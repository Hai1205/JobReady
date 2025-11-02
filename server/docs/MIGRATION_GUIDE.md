# 🚀 Migration Guide: Từ Old RabbitRPC sang Improved Architecture

## 📋 Overview

Guide này hướng dẫn bạn migrate từ RabbitMQ architecture cũ (blocking, shared queue) sang architecture mới (non-blocking, Direct Reply-To, idempotency).

---

## 🎯 Phase 1: Setup Dependencies và Redis

### Step 1.1: Thêm Dependencies vào `pom.xml`

```xml
<!-- Redis cho Idempotency -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>

<!-- Lettuce connection pool -->
<dependency>
    <groupId>io.lettuce</groupId>
    <artifactId>lettuce-core</artifactId>
</dependency>

<!-- Resilience4j Circuit Breaker -->
<dependency>
    <groupId>io.github.resilience4j</groupId>
    <artifactId>resilience4j-spring-boot3</artifactId>
    <version>2.1.0</version>
</dependency>

<!-- Actuator cho monitoring -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>

<!-- Micrometer for metrics -->
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

### Step 1.2: Cấu hình Redis

```yaml
# application.yml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      password: # để trống nếu không có password
```

### Step 1.3: Verify Redis đang chạy

```powershell
# Check Redis
docker run -d -p 6379:6379 --name redis redis:alpine

# Test connection
docker exec -it redis redis-cli ping
# Expected: PONG
```

---

## 🎯 Phase 2: Migrate RabbitRPCService

### Step 2.1: Add ImprovedRabbitRPCService vào rabbit-common

File đã được tạo: `rabbit-common/src/main/java/com/example/rabbitmq/services/ImprovedRabbitRPCService.java`

### Step 2.2: Update RabbitHeader.java để support setter

```java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RabbitHeader {
    private String correlationId;
    private String replyTo;
    private String replyExchange;
    private Long timestamp;
    private String sourceService;
    private String targetService;
    private String status;

    // ✅ Đảm bảo có setter để ImprovedRabbitRPCService có thể set correlationId
}
```

### Step 2.3: Refactor UserProducer (auth-service)

**Before:**

```java
@Service
@RequiredArgsConstructor
public class UserProducer {
    private final RabbitRPCService rpcService; // ❌ Old

    public UserDto findUserByEmail(String email) {
        // Blocking call
        return rpcService.sendAndReceive(...);
    }
}
```

**After:**

```java
@Service
@RequiredArgsConstructor
public class UserProducer {
    private final ImprovedRabbitRPCService rpcService; // ✅ New

    // Option 1: Synchronous (backward compatible)
    public UserDto findUserByEmail(String email) {
        RabbitHeader header = RabbitHeader.builder()
            .timestamp(System.currentTimeMillis())
            .sourceService("auth-service")
            .targetService("user-service")
            .build();

        Map<String, Object> params = Map.of("email", email);

        return rpcService.sendAndReceive(
            RabbitConstants.USER_EXCHANGE,
            RabbitConstants.USER_FIND_BY_EMAIL,
            header,
            params,
            UserDto.class
        );
    }

    // Option 2: Asynchronous (recommended)
    public CompletableFuture<UserDto> findUserByEmailAsync(String email) {
        RabbitHeader header = RabbitHeader.builder()
            .timestamp(System.currentTimeMillis())
            .sourceService("auth-service")
            .targetService("user-service")
            .build();

        Map<String, Object> params = Map.of("email", email);

        return rpcService.sendAndReceiveAsync(
            RabbitConstants.USER_EXCHANGE,
            RabbitConstants.USER_FIND_BY_EMAIL,
            header,
            params,
            UserDto.class
        );
    }
}
```

### Step 2.4: Update AuthService để dùng async

```java
@Service
public class AuthService {
    private final UserProducer userProducer;

    public Response login(String dataJson, HttpServletResponse httpServletResponse) {
        LoginRequest request = objectMapper.readValue(dataJson, LoginRequest.class);

        // ✅ Async call
        CompletableFuture<UserDto> userFuture = userProducer.findUserByEmailAsync(request.getEmail());

        // ✅ Get result (block chỉ khi cần)
        UserDto userDto = userFuture.get(5, TimeUnit.SECONDS);

        // Rest of login logic...
    }
}
```

---

## 🎯 Phase 3: Add Idempotency

### Step 3.1: Add IdempotencyService vào rabbit-common

File đã được tạo: `rabbit-common/src/main/java/com/example/rabbitmq/services/IdempotencyService.java`

### Step 3.2: Update UserConsumer với Idempotency

**Option 1: Use UserMessageHandler**

File đã được tạo: `user-service/.../UserMessageHandler.java`

Rename `UserConsumer.java` → `UserConsumer.old.java` và enable `UserMessageHandler.java`

**Option 2: Patch existing UserConsumer**

```java
@Component
@RequiredArgsConstructor
public class UserConsumer extends BaseConsumer {

    private final UserService userService;
    private final IdempotencyService idempotencyService; // ✅ Add
    private final ImprovedRabbitRPCService rpcService; // ✅ Change to Improved

    @RabbitListener(queues = RabbitConstants.USER_CREATE_QUEUE)
    public void handleCreateUser(Message message) {
        RabbitHeader header = extractHeader(message);
        String correlationId = header.getCorrelationId();

        try {
            // ✅ Check idempotency
            String idempotencyKey = "user:create:" + correlationId;

            // Check cached result
            Optional<String> cachedResult = idempotencyService.getCachedResult(idempotencyKey);
            if (cachedResult.isPresent()) {
                log.info("♻️ Duplicate request, returning cached result");
                RabbitResponse<?> response = objectMapper.readValue(cachedResult.get(), RabbitResponse.class);
                rpcService.sendReply(header.getReplyTo(), correlationId, response);
                return;
            }

            // ✅ Acquire lock
            if (!idempotencyService.isFirstRequest(idempotencyKey)) {
                log.warn("⚠️ Concurrent request detected");
                return;
            }

            // Extract và process
            Map<String, Object> params = extractPayload(message, new TypeReference<>() {});
            Map<String, Object> payload = (Map<String, Object>) params.get("payload");

            String username = (String) payload.get("username");
            String email = (String) payload.get("email");
            String password = (String) payload.get("password");
            String fullname = (String) payload.get("fullname");

            UserDto user = userService.handleCreateUser(username, email, password, fullname, "", "", null);

            var response = RabbitResponse.<UserDto>builder()
                .code(200)
                .message("Success")
                .data(user)
                .build();

            // ✅ Cache result
            String resultJson = objectMapper.writeValueAsString(response);
            idempotencyService.updateResult(idempotencyKey, resultJson);

            // Send reply
            rpcService.sendReply(header.getReplyTo(), correlationId, response);

        } catch (Exception e) {
            log.error("❌ Error creating user", e);

            var errorResponse = RabbitResponse.builder()
                .code(500)
                .message(e.getMessage())
                .data(null)
                .build();

            rpcService.sendReply(header.getReplyTo(), correlationId, errorResponse);
        }
    }
}
```

### Step 3.3: Test Idempotency

```java
// Test case
@Test
void testDuplicateUserCreation() {
    String email = "test@example.com";

    // Request 1
    UserDto user1 = userProducer.createUser("test", email, "password", "Test User");
    assertNotNull(user1);

    // Request 2 (duplicate) - should return cached result or error ngay
    assertThrows(Exception.class, () -> {
        userProducer.createUser("test", email, "password", "Test User");
    });

    // Verify chỉ 1 user được tạo
    List<User> users = userRepository.findByEmail(email);
    assertEquals(1, users.size());
}
```

---

## 🎯 Phase 4: Setup Dead Letter Queue

### Step 4.1: Add DeadLetterQueueConfig

File đã được tạo: `rabbit-common/.../config/DeadLetterQueueConfig.java`

### Step 4.2: Update UserRabbitConfig với DLX

```java
@Configuration
public class UserRabbitConfig extends BaseRabbitConfig {

    @Bean
    public Declarables userExchangeConfigWithDLX() {
        // Main exchange
        TopicExchange userExchange = new TopicExchange(RabbitConstants.USER_EXCHANGE, true, false);

        // User Create Queue với DLX
        Queue userCreateQueue = DeadLetterQueueConfig.createQueueWithDLX(
            RabbitConstants.USER_CREATE_QUEUE,
            DeadLetterQueueConfig.USER_CREATE_DLQ_ROUTING_KEY,
            30000, // 30s timeout
            null   // no max retries at queue level
        );

        Binding userCreateBinding = BindingBuilder
            .bind(userCreateQueue)
            .to(userExchange)
            .with(RabbitConstants.USER_CREATE);

        // User Activate Queue với DLX
        Queue userActivateQueue = DeadLetterQueueConfig.createQueueWithDLX(
            RabbitConstants.USER_ACTIVATE_QUEUE,
            DeadLetterQueueConfig.USER_ACTIVATE_DLQ_ROUTING_KEY,
            30000,
            null
        );

        Binding userActivateBinding = BindingBuilder
            .bind(userActivateQueue)
            .to(userExchange)
            .with(RabbitConstants.USER_ACTIVATE);

        return new Declarables(
            userExchange,
            userCreateQueue, userCreateBinding,
            userActivateQueue, userActivateBinding
        );
    }
}
```

### Step 4.3: Add DLQRetryListener

File đã được tạo: `user-service/.../DLQRetryListener.java`

### Step 4.4: Test DLQ

Để test DLQ, force throw exception trong consumer:

```java
@RabbitListener(queues = RabbitConstants.USER_CREATE_QUEUE)
public void handleCreateUser(Message message) {
    // Force error để test DLQ
    if (true) {
        throw new RuntimeException("Simulated error for DLQ testing");
    }

    // Normal logic...
}
```

Expected behavior:

1. Message fail → vào DLQ
2. DLQ listener retry sau 5s, 10s, 20s
3. Sau 3 lần → move to poison queue
4. Admin nhận alert

---

## 🎯 Phase 5: Add Circuit Breaker

### Step 5.1: Thêm Resilience4j config vào application.yml

Đã có trong `config/development/rabbitmq-enhanced.yml`

### Step 5.2: Apply Circuit Breaker vào UserProducer

```java
@Service
@RequiredArgsConstructor
public class UserProducer {

    private final ImprovedRabbitRPCService rpcService;

    @CircuitBreaker(name = "userService", fallbackMethod = "findUserByEmailFallback")
    @TimeLimiter(name = "userService")
    public CompletableFuture<UserDto> findUserByEmailAsync(String email) {
        RabbitHeader header = RabbitHeader.builder()
            .timestamp(System.currentTimeMillis())
            .sourceService("auth-service")
            .targetService("user-service")
            .build();

        Map<String, Object> params = Map.of("email", email);

        return rpcService.sendAndReceiveAsync(
            RabbitConstants.USER_EXCHANGE,
            RabbitConstants.USER_FIND_BY_EMAIL,
            header,
            params,
            UserDto.class
        );
    }

    // Fallback method
    private CompletableFuture<UserDto> findUserByEmailFallback(String email, Exception e) {
        log.error("⚡ Circuit breaker fallback triggered for email: {}", email, e);

        // Option 1: Return null
        return CompletableFuture.completedFuture(null);

        // Option 2: Return cached data từ Redis
        // return getCachedUser(email);

        // Option 3: Throw custom exception
        // return CompletableFuture.failedFuture(
        //     new ServiceUnavailableException("User service is temporarily unavailable")
        // );
    }
}
```

### Step 5.3: Monitor Circuit Breaker

```powershell
# Check circuit breaker state
curl http://localhost:8080/actuator/circuitbreakers

# Check metrics
curl http://localhost:8080/actuator/metrics/resilience4j.circuitbreaker.state
```

---

## 🎯 Phase 6: Testing & Verification

### Test 1: Concurrent Requests

```java
@Test
void testConcurrentUserCreation() throws Exception {
    String email = "test@example.com";
    int threadCount = 10;

    ExecutorService executor = Executors.newFixedThreadPool(threadCount);
    CountDownLatch latch = new CountDownLatch(threadCount);

    AtomicInteger successCount = new AtomicInteger(0);
    AtomicInteger errorCount = new AtomicInteger(0);

    for (int i = 0; i < threadCount; i++) {
        executor.submit(() -> {
            try {
                userProducer.createUser("test", email, "password", "Test User");
                successCount.incrementAndGet();
            } catch (Exception e) {
                errorCount.incrementAndGet();
            } finally {
                latch.countDown();
            }
        });
    }

    latch.await(30, TimeUnit.SECONDS);

    // Verify: chỉ 1 success, còn lại error hoặc cached
    assertEquals(1, successCount.get());
    assertEquals(threadCount - 1, errorCount.get());

    // Verify DB: chỉ 1 user
    List<User> users = userRepository.findByEmail(email);
    assertEquals(1, users.size());
}
```

### Test 2: Response Time

```java
@Test
void testResponseTime() {
    long startTime = System.currentTimeMillis();

    UserDto user = userProducer.findUserByEmail("test@example.com");

    long duration = System.currentTimeMillis() - startTime;

    // Verify < 500ms
    assertTrue(duration < 500, "Response time too slow: " + duration + "ms");
}
```

### Test 3: Load Test

```powershell
# Dùng Apache Bench
ab -n 1000 -c 100 -p register.json -T application/json http://localhost:8080/api/auth/register

# Expected:
# - Requests per second > 200
# - No errors
# - No duplicate users
```

---

## 🎯 Phase 7: Cleanup Old Code

Sau khi verify tất cả tests pass:

1. **Delete old RabbitRPCService**

   ```powershell
   # Rename to backup
   mv rabbit-common/src/.../RabbitRPCService.java RabbitRPCService.old.java

   # Rename improved to main
   mv ImprovedRabbitRPCService.java RabbitRPCService.java
   ```

2. **Remove shared reply queues**

   ```java
   // Delete from RabbitConstants.java
   // public static final String AUTH_REPLY_QUEUE = "auth.reply.queue";
   // public static final String USER_REPLY_QUEUE = "user.reply.queue";
   ```

3. **Remove old reply queue configs**

   ```java
   // Delete from AuthRabbitConfig.java
   // @Bean
   // public Declarables authReplyQueueConfig() { ... }
   ```

4. **Delete RabbitMQ queues**

   ```powershell
   # Access RabbitMQ management
   # http://localhost:15672

   # Delete queues:
   # - auth.reply.queue
   # - user.reply.queue
   # - cv.reply.queue
   ```

---

## 📊 Monitoring Checklist

Sau khi deploy, check các metrics:

### RabbitMQ Metrics

- [ ] Queue depth < 100 messages
- [ ] Consumer count > 0
- [ ] No unacked messages piling up
- [ ] DLQ count = 0 (hoặc very low)

### Redis Metrics

- [ ] Connected clients < 100
- [ ] Memory usage stable
- [ ] Keys count tăng dần (idempotency keys)

### Application Metrics

- [ ] Response time < 500ms (p95)
- [ ] Error rate < 1%
- [ ] Circuit breaker CLOSED
- [ ] No memory leaks

### Logs

- [ ] No `TimeoutException`
- [ ] No `race condition` warnings
- [ ] Idempotency hits logged (♻️)
- [ ] DLQ retries logged (🔄)

---

## 🚨 Rollback Plan

Nếu có vấn đề nghiêm trọng:

1. **Revert code**

   ```powershell
   git revert <commit-hash>
   git push
   ```

2. **Switch back to old RabbitRPCService**

   ```java
   // In UserProducer
   private final RabbitRPCService rpcService; // Use old
   ```

3. **Recreate shared reply queues**

   ```powershell
   # Via RabbitMQ management or restart services
   ```

4. **Clear Redis cache**
   ```powershell
   docker exec -it redis redis-cli FLUSHDB
   ```

---

## 🎓 Best Practices

### DO's ✅

- Use async calls khi có thể
- Always set timeout
- Cache idempotency results
- Monitor DLQ size
- Set up alerts
- Log với correlation ID
- Use circuit breaker cho external calls

### DON'Ts ❌

- Không dùng shared reply queue
- Không block thread quá lâu
- Không skip idempotency check
- Không ignore DLQ messages
- Không hardcode timeout values
- Không log sensitive data

---

## 📞 Support

Nếu gặp vấn đề trong quá trình migration:

1. Check logs với correlation ID
2. Check RabbitMQ management console
3. Check Redis keys: `redis-cli KEYS "idempotency:*"`
4. Check circuit breaker state: `/actuator/circuitbreakers`
5. Open issue hoặc contact team

**Good luck với migration!** 🚀
