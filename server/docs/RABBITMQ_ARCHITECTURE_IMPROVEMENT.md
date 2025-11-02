# 🚀 Kiến Trúc RabbitMQ Cải Tiến - JobReady Microservices

## 📋 Mục Lục
1. [Phân Tích Vấn Đề Hiện Tại](#1-phân-tích-vấn-đề-hiện-tại)
2. [Kiến Trúc Đề Xuất](#2-kiến-trúc-đề-xuất)
3. [Chi Tiết Giải Pháp](#3-chi-tiết-giải-pháp)
4. [Roadmap Triển Khai](#4-roadmap-triển-khai)
5. [Sequence Diagrams](#5-sequence-diagrams)

---

## 1. Phân Tích Vấn Đề Hiện Tại

### 🔴 **Vấn Đề Nghiêm Trọng Nhất: Blocking RPC với `receiveAndConvert()`**

```java
// ❌ VẤN ĐỀ CHÍNH TRONG RabbitRPCService.java
Object response = rabbitTemplate.receiveAndConvert(header.getReplyTo(), 8000);
```

**Tại sao đây là vấn đề lớn:**

1. **Blocking Call**: `receiveAndConvert()` **CHẶN thread hiện tại** và **POLLING** liên tục vào queue trong 8 giây
2. **Tốn kém tài nguyên**: Mỗi request giữ 1 thread cho đến khi có response hoặc timeout
3. **Race Condition**: Nhiều request cùng lúc vào cùng `AUTH_REPLY_QUEUE` → lấy nhầm response của nhau
4. **Không có correlation**: Không có cơ chế matching `correlationId` đúng → lấy message đầu tiên trong queue
5. **Không scalable**: Với 100 concurrent requests → 100 threads bị block

### 🐛 **Các Vấn Đề Khác**

#### **A. Shared Reply Queue (Anti-pattern)**
```java
// ❌ TẤT CẢ requests từ auth-service đều dùng CHUNG 1 queue
public static final String AUTH_REPLY_QUEUE = "auth.reply.queue";
```

**Hậu quả:**
- Request A gửi đi, nhận về response của Request B (đến trước)
- Khi có 2 requests `createUser` liên tiếp cùng email:
  - Request 1: gửi đi, đang đợi response
  - Request 2: gửi đi, đang đợi response
  - Response 1: về trước → Request 2 nhận nhầm → trả về success
  - Response 2: về sau → Request 1 nhận nhầm → có thể báo lỗi hoặc timeout

#### **B. Không Có Idempotency**
```java
// ❌ Không có cơ chế check duplicate
@RabbitListener(queues = RabbitConstants.USER_CREATE_QUEUE)
public void handleCreateUser(Message message) {
    // Nếu message bị gửi lại (retry) → tạo user trùng
    UserDto user = userService.handleCreateUser(...);
}
```

#### **C. Không Có Retry & Dead Letter Queue**
- Message thất bại → mất luôn, không có cơ chế xử lý lại
- Không có DLQ để debug các message lỗi

#### **D. Không Có Circuit Breaker**
- Khi user-service chậm/die → auth-service timeout liên tục (8s/request)
- Tốn tài nguyên và làm chậm toàn hệ thống

#### **E. Synchronous Everywhere**
- Tất cả operations đều dùng RPC → cần phản hồi ngay
- Một số operations như "create CV" không cần sync nhưng vẫn đợi

---

## 2. Kiến Trúc Đề Xuất

### 🎯 **Mô Hình Hybrid: Sync + Async + Event-Driven**

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         RABBITMQ ARCHITECTURE                            │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  ┌──────────────┐     SYNC RPC      ┌──────────────┐                   │
│  │              │ ──────────────────>│              │                   │
│  │ Auth Service │                    │ User Service │                   │
│  │              │ <──────────────────│              │                   │
│  └──────┬───────┘  (Direct Reply-To) └──────┬───────┘                   │
│         │                                    │                          │
│         │                                    │ ASYNC EVENT              │
│         │                                    v                          │
│         │                           ┌─────────────────┐                 │
│         │                           │  Event Exchange │                 │
│         │                           │   (Topic)       │                 │
│         │                           └────────┬────────┘                 │
│         │                                    │                          │
│         │          ┌─────────────────────────┼─────────────┐            │
│         │          │                         │             │            │
│         │          v                         v             v            │
│         │   user.created.queue       cv.created.queue  notification.*   │
│         │          │                         │             │            │
│         │          v                         v             v            │
│         │   ┌──────────────┐         ┌──────────────┐  ┌────────────┐  │
│         └──>│  CV Service  │         │ Notification │  │  Analytics │  │
│             │              │         │   Service    │  │  Service   │  │
│             └──────────────┘         └──────────────┘  └────────────┘  │
│                                                                          │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │                      INFRASTRUCTURE LAYER                        │   │
│  ├─────────────────────────────────────────────────────────────────┤   │
│  │  • Dead Letter Exchange (DLX) + DLQ for all queues              │   │
│  │  • Redis for Distributed Lock & Idempotency Token               │   │
│  │  • Outbox Pattern Table for reliable event publishing           │   │
│  │  • Circuit Breaker (Resilience4j) for RPC calls                 │   │
│  └─────────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────┘
```

### 📊 **Phân Loại Operations: Sync vs Async**

| Operation | Pattern | Lý do |
|-----------|---------|-------|
| **Login** | Sync RPC | Cần response ngay để trả JWT token |
| **Register** | Sync + Async | Sync: tạo user, trả về user ID ngay<br>Async: gửi email, tạo CV template |
| **Find User** | Sync RPC | Cần data ngay để validate |
| **Create CV** | Async Event | Không cần đợi, xử lý background |
| **Send Notification** | Async Event | Fire-and-forget |
| **Password Reset** | Sync + Async | Sync: đổi password<br>Async: gửi email |

---

## 3. Chi Tiết Giải Pháp

### 🔧 **Solution 1: Sửa RPC với Direct Reply-To Pattern**

**Vấn đề:** `receiveAndConvert()` blocking + shared queue

**Giải pháp:** Dùng **RabbitMQ Direct Reply-To** + **Correlation ID** đúng cách

```java
// ✅ RabbitRPCService.java - IMPROVED VERSION
@Service
@RequiredArgsConstructor
public class RabbitRPCService {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    
    // Cache để lưu pending requests với correlationId
    private final ConcurrentHashMap<String, CompletableFuture<Object>> pendingRequests = 
        new ConcurrentHashMap<>();
    
    /**
     * Non-blocking RPC với Direct Reply-To
     */
    public <R> CompletableFuture<R> sendAndReceiveAsync(
            String exchange, 
            String routingKey, 
            RabbitHeader header, 
            Object payload,
            Class<R> responseType) {
        
        CompletableFuture<R> future = new CompletableFuture<>();
        
        try {
            // Generate unique correlation ID
            String correlationId = UUID.randomUUID().toString();
            
            if (header == null) {
                header = RabbitHeader.builder()
                    .correlationId(correlationId)
                    .timestamp(System.currentTimeMillis())
                    .sourceService("current-service")
                    .targetService("target-service")
                    .build();
            } else {
                header.setCorrelationId(correlationId);
            }
            
            // Store future in cache
            pendingRequests.put(correlationId, (CompletableFuture<Object>) future);
            
            // Set timeout để tự động remove khỏi cache
            CompletableFuture.delayedExecutor(10, TimeUnit.SECONDS).execute(() -> {
                CompletableFuture<Object> removed = pendingRequests.remove(correlationId);
                if (removed != null && !removed.isDone()) {
                    removed.completeExceptionally(
                        new TimeoutException("RPC timeout after 10s for correlationId: " + correlationId)
                    );
                }
            });
            
            // Create message
            var wrapper = Map.of("header", header, "payload", payload);
            String json = objectMapper.writeValueAsString(wrapper);
            
            Message message = MessageBuilder
                .withBody(json.getBytes())
                .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                .setCorrelationId(correlationId)
                .setReplyTo(Address.AMQ_RABBITMQ_REPLY_TO) // ✅ Direct Reply-To
                .build();
            
            // Send message
            rabbitTemplate.send(exchange, routingKey, message);
            
            log.debug("📤 RPC Request sent - correlationId: {}, routing: {}", 
                correlationId, routingKey);
            
        } catch (Exception e) {
            future.completeExceptionally(e);
        }
        
        return future;
    }
    
    /**
     * Xử lý reply từ Direct Reply-To queue
     * Spring AMQP tự động route replies về đây
     */
    @RabbitListener(queues = "amq.rabbitmq.reply-to")
    public void handleReply(Message message) {
        try {
            String correlationId = message.getMessageProperties().getCorrelationId();
            
            CompletableFuture<Object> future = pendingRequests.remove(correlationId);
            
            if (future != null) {
                // Parse response
                String body = new String(message.getBody());
                Map<String, Object> wrapper = objectMapper.readValue(body, Map.class);
                Object responsePayload = wrapper.get("payload");
                
                RabbitResponse<?> response = objectMapper.convertValue(
                    responsePayload, 
                    RabbitResponse.class
                );
                
                if (response.getCode() == 200) {
                    future.complete(response.getData());
                    log.debug("✅ RPC Response received - correlationId: {}", correlationId);
                } else {
                    future.completeExceptionally(
                        new RuntimeException("Remote error: " + response.getMessage())
                    );
                }
            } else {
                log.warn("⚠️ Received reply for unknown correlationId: {}", correlationId);
            }
            
        } catch (Exception e) {
            log.error("❌ Error handling reply: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Synchronous wrapper (cho backward compatibility)
     */
    public <R> R sendAndReceive(
            String exchange, 
            String routingKey, 
            RabbitHeader header, 
            Object payload,
            Class<R> responseType) {
        try {
            return sendAndReceiveAsync(exchange, routingKey, header, payload, responseType)
                .get(10, TimeUnit.SECONDS); // ✅ Block chỉ khi cần
        } catch (Exception e) {
            throw new RuntimeException("RPC call failed: " + e.getMessage(), e);
        }
    }
}
```

**Lợi ích:**
- ✅ **Không còn shared queue** → mỗi request có reply queue riêng (tự động tạo bởi RabbitMQ)
- ✅ **Correlation ID matching** → đúng response cho đúng request
- ✅ **Non-blocking option** → có thể dùng `CompletableFuture` cho async
- ✅ **Auto cleanup** → timeout tự động xóa pending requests
- ✅ **Scalable** → không limit số concurrent requests

---

### 🔧 **Solution 2: Idempotency với Redis**

**Vấn đề:** Duplicate requests tạo duplicate records

**Giải pháp:** Idempotency Token + Redis Lock

```java
// ✅ IdempotencyService.java
@Service
@RequiredArgsConstructor
public class IdempotencyService {
    
    private final StringRedisTemplate redisTemplate;
    private static final String IDEMPOTENCY_PREFIX = "idempotency:";
    private static final long EXPIRATION_SECONDS = 24 * 60 * 60; // 24 hours
    
    /**
     * Check và store idempotency key
     * @return true nếu đây là lần đầu xử lý request
     */
    public boolean isFirstRequest(String key, String value) {
        String redisKey = IDEMPOTENCY_PREFIX + key;
        Boolean success = redisTemplate.opsForValue()
            .setIfAbsent(redisKey, value, Duration.ofSeconds(EXPIRATION_SECONDS));
        return Boolean.TRUE.equals(success);
    }
    
    /**
     * Lấy result đã cache từ lần xử lý trước
     */
    public Optional<String> getCachedResult(String key) {
        String redisKey = IDEMPOTENCY_PREFIX + key;
        String value = redisTemplate.opsForValue().get(redisKey);
        return Optional.ofNullable(value);
    }
    
    /**
     * Update result sau khi xử lý xong
     */
    public void updateResult(String key, String result) {
        String redisKey = IDEMPOTENCY_PREFIX + key;
        redisTemplate.opsForValue().set(
            redisKey, 
            result, 
            Duration.ofSeconds(EXPIRATION_SECONDS)
        );
    }
}

// ✅ UserConsumer.java - Apply Idempotency
@Component
@RequiredArgsConstructor
public class UserConsumer extends BaseConsumer {
    
    private final UserService userService;
    private final IdempotencyService idempotencyService;
    private final ObjectMapper objectMapper;
    
    @RabbitListener(queues = RabbitConstants.USER_CREATE_QUEUE)
    public void handleCreateUser(Message message) {
        RabbitHeader header = extractHeader(message);
        String correlationId = header.getCorrelationId();
        
        try {
            // ✅ Check idempotency
            String idempotencyKey = "user:create:" + correlationId;
            
            Optional<String> cachedResult = idempotencyService.getCachedResult(idempotencyKey);
            if (cachedResult.isPresent()) {
                log.info("🔄 Duplicate request detected, returning cached result");
                sendReplyFromCache(header, cachedResult.get());
                return;
            }
            
            // ✅ Acquire distributed lock
            if (!idempotencyService.isFirstRequest(idempotencyKey, "processing")) {
                log.warn("⚠️ Concurrent request detected, skipping");
                return;
            }
            
            // Extract payload
            Map<String, Object> params = extractPayload(message, new TypeReference<>() {});
            Map<String, Object> payload = (Map<String, Object>) params.get("payload");
            
            String username = (String) payload.get("username");
            String email = (String) payload.get("email");
            String password = (String) payload.get("password");
            String fullname = (String) payload.get("fullname");
            
            // Process
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
            sendReply(header, response);
            
        } catch (Exception e) {
            log.error("❌ Error creating user", e);
            sendErrorReply(header, e.getMessage());
        }
    }
    
    private void sendReplyFromCache(RabbitHeader header, String cachedJson) {
        try {
            RabbitResponse<?> response = objectMapper.readValue(cachedJson, RabbitResponse.class);
            sendReply(header, response);
        } catch (Exception e) {
            log.error("❌ Error sending cached reply", e);
        }
    }
}
```

**Lợi ích:**
- ✅ Chống duplicate request (cùng correlationId)
- ✅ Cache kết quả → reply ngay lập tức cho duplicate
- ✅ TTL 24h → tự động cleanup
- ✅ Distributed lock → work với multi-instance

---

### 🔧 **Solution 3: Dead Letter Queue + Retry Mechanism**

```java
// ✅ RabbitMQConfig.java - Setup DLQ
@Configuration
public class UserRabbitConfig extends BaseRabbitConfig {
    
    @Bean
    public Declarables userQueuesWithDLQ() {
        // Main queue với DLX config
        Queue userCreateQueue = QueueBuilder
            .durable(RabbitConstants.USER_CREATE_QUEUE)
            .withArgument("x-dead-letter-exchange", "dlx.exchange")
            .withArgument("x-dead-letter-routing-key", "dlq.user.create")
            .withArgument("x-message-ttl", 30000) // 30s timeout
            .build();
        
        // Dead Letter Queue
        Queue userCreateDLQ = QueueBuilder
            .durable("dlq.user.create.queue")
            .build();
        
        // DLX Exchange
        TopicExchange dlxExchange = new TopicExchange("dlx.exchange", true, false);
        
        Binding dlqBinding = BindingBuilder
            .bind(userCreateDLQ)
            .to(dlxExchange)
            .with("dlq.user.create");
        
        // Main exchange và binding
        TopicExchange mainExchange = new TopicExchange(RabbitConstants.USER_EXCHANGE, true, false);
        Binding mainBinding = BindingBuilder
            .bind(userCreateQueue)
            .to(mainExchange)
            .with(RabbitConstants.USER_CREATE);
        
        return new Declarables(
            userCreateQueue, userCreateDLQ,
            mainExchange, dlxExchange,
            mainBinding, dlqBinding
        );
    }
}

// ✅ Retry Listener
@Component
@RequiredArgsConstructor
public class DLQRetryListener {
    
    private final RabbitTemplate rabbitTemplate;
    private static final int MAX_RETRY = 3;
    
    @RabbitListener(queues = "dlq.user.create.queue")
    public void handleDLQMessage(Message message) {
        try {
            MessageProperties props = message.getMessageProperties();
            
            // Check retry count
            Integer retryCount = (Integer) props.getHeader("x-retry-count");
            if (retryCount == null) retryCount = 0;
            
            if (retryCount < MAX_RETRY) {
                log.info("🔄 Retrying message (attempt {}/{})", retryCount + 1, MAX_RETRY);
                
                // Increment retry count
                MessageProperties newProps = new MessageProperties();
                newProps.setContentType(props.getContentType());
                newProps.setCorrelationId(props.getCorrelationId());
                newProps.setHeader("x-retry-count", retryCount + 1);
                
                Message retryMessage = new Message(message.getBody(), newProps);
                
                // Exponential backoff: 5s, 10s, 20s
                int delaySeconds = (int) Math.pow(2, retryCount) * 5;
                CompletableFuture.delayedExecutor(delaySeconds, TimeUnit.SECONDS).execute(() -> {
                    rabbitTemplate.send(
                        RabbitConstants.USER_EXCHANGE,
                        RabbitConstants.USER_CREATE,
                        retryMessage
                    );
                });
                
            } else {
                log.error("❌ Max retry exceeded, moving to poison queue");
                // Move to poison queue hoặc alert admin
                notifyAdmin(message);
            }
            
        } catch (Exception e) {
            log.error("❌ Error processing DLQ message", e);
        }
    }
    
    private void notifyAdmin(Message message) {
        // Send alert qua email/Slack
        log.error("🚨 POISON MESSAGE: {}", new String(message.getBody()));
    }
}
```

**Lợi ích:**
- ✅ Message thất bại không mất → vào DLQ
- ✅ Auto retry với exponential backoff
- ✅ Limit retry count → tránh infinite loop
- ✅ Poison queue cho messages không thể xử lý

---

### 🔧 **Solution 4: Event-Driven cho Async Operations**

```java
// ✅ Event Model
@Data
@Builder
public class UserCreatedEvent {
    private String eventId;
    private String userId;
    private String username;
    private String email;
    private String fullname;
    private LocalDateTime createdAt;
    private Map<String, Object> metadata;
}

// ✅ Event Publisher
@Service
@RequiredArgsConstructor
public class EventPublisher {
    
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    
    private static final String EVENT_EXCHANGE = "events.exchange";
    
    public void publishUserCreatedEvent(UserDto user) {
        try {
            UserCreatedEvent event = UserCreatedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .userId(user.getId().toString())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullname(user.getFullname())
                .createdAt(LocalDateTime.now())
                .build();
            
            String json = objectMapper.writeValueAsString(event);
            
            rabbitTemplate.convertAndSend(
                EVENT_EXCHANGE,
                "user.created", // routing key
                json
            );
            
            log.info("📣 Published UserCreatedEvent for userId: {}", user.getId());
            
        } catch (Exception e) {
            log.error("❌ Failed to publish event", e);
            // Fallback: store in Outbox table
        }
    }
}

// ✅ Event Consumer trong CV Service
@Component
@RequiredArgsConstructor
public class UserEventConsumer {
    
    private final CVService cvService;
    
    @RabbitListener(queues = "cv-service.user.created.queue")
    public void handleUserCreated(String eventJson) {
        try {
            UserCreatedEvent event = new ObjectMapper()
                .readValue(eventJson, UserCreatedEvent.class);
            
            log.info("📨 Received UserCreatedEvent for userId: {}", event.getUserId());
            
            // Tạo CV template cho user mới
            cvService.createDefaultCVTemplate(event.getUserId(), event.getUsername());
            
            log.info("✅ Created default CV for userId: {}", event.getUserId());
            
        } catch (Exception e) {
            log.error("❌ Error handling UserCreatedEvent", e);
            throw new AmqpRejectAndDontRequeueException("Cannot process event", e);
        }
    }
}

// ✅ Config Event Exchange
@Bean
public Declarables eventExchangeConfig() {
    TopicExchange eventExchange = new TopicExchange("events.exchange", true, false);
    
    // Queue cho CV service
    Queue cvServiceQueue = QueueBuilder
        .durable("cv-service.user.created.queue")
        .withArgument("x-dead-letter-exchange", "dlx.exchange")
        .build();
    
    Binding cvBinding = BindingBuilder
        .bind(cvServiceQueue)
        .to(eventExchange)
        .with("user.created");
    
    // Có thể add thêm queues cho các services khác
    // Queue notificationQueue = ...
    // Binding notificationBinding = ...
    
    return new Declarables(eventExchange, cvServiceQueue, cvBinding);
}
```

**Lợi ích:**
- ✅ Decoupling: CV service không cần biết về User service
- ✅ Fanout: 1 event → nhiều consumers (notification, analytics, ...)
- ✅ Non-blocking: User creation trả về ngay, CV tạo sau
- ✅ Resilient: CV service die → event vẫn trong queue → xử lý khi service sống lại

---

### 🔧 **Solution 5: Circuit Breaker cho RPC Calls**

```java
// ✅ pom.xml - Add Resilience4j
<dependency>
    <groupId>io.github.resilience4j</groupId>
    <artifactId>resilience4j-spring-boot3</artifactId>
    <version>2.1.0</version>
</dependency>

// ✅ application.yml
resilience4j:
  circuitbreaker:
    instances:
      userService:
        registerHealthIndicator: true
        slidingWindowSize: 10
        minimumNumberOfCalls: 5
        failureRateThreshold: 50
        waitDurationInOpenState: 10s
        permittedNumberOfCallsInHalfOpenState: 3
        automaticTransitionFromOpenToHalfOpenEnabled: true
  timelimiter:
    instances:
      userService:
        timeoutDuration: 5s

// ✅ UserProducer.java - Apply Circuit Breaker
@Service
@RequiredArgsConstructor
public class UserProducer {
    
    private final RabbitRPCService rpcService;
    private final CircuitBreakerRegistry circuitBreakerRegistry;
    
    @CircuitBreaker(name = "userService", fallbackMethod = "findUserByEmailFallback")
    @TimeLimiter(name = "userService")
    public CompletableFuture<UserDto> findUserByEmail(String email) {
        RabbitHeader header = RabbitHeader.builder()
            .correlationId(UUID.randomUUID().toString())
            .replyTo(RabbitConstants.AUTH_REPLY_QUEUE)
            .replyExchange(RabbitConstants.AUTH_EXCHANGE)
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
    
    // Fallback method khi Circuit open hoặc timeout
    private CompletableFuture<UserDto> findUserByEmailFallback(String email, Exception e) {
        log.error("⚡ Circuit breaker fallback triggered for email: {}", email, e);
        
        // Option 1: Return cached data từ Redis
        // Option 2: Return default value
        // Option 3: Throw custom exception
        
        return CompletableFuture.failedFuture(
            new ServiceUnavailableException("User service is temporarily unavailable")
        );
    }
}
```

**Lợi ích:**
- ✅ Tự động detect service chậm/die
- ✅ Fail fast: không đợi timeout khi service die
- ✅ Auto recovery: tự test lại service sau 10s
- ✅ Metrics: track failure rate, response time

---

### 🔧 **Solution 6: Outbox Pattern cho Reliable Events**

```java
// ✅ Entity
@Entity
@Table(name = "outbox_events")
@Data
public class OutboxEvent {
    @Id
    private String id;
    
    private String aggregateType; // "USER", "CV"
    private String aggregateId;
    private String eventType; // "CREATED", "UPDATED"
    
    @Column(columnDefinition = "TEXT")
    private String payload;
    
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
    private String status; // PENDING, PROCESSED, FAILED
    private Integer retryCount;
}

// ✅ UserService - Store to Outbox
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final OutboxEventRepository outboxRepository;
    private final ObjectMapper objectMapper;
    
    public UserDto handleCreateUser(...) {
        // 1. Create user trong DB
        User user = new User(...);
        user = userRepository.save(user);
        
        // 2. Store event trong Outbox (CÙNG transaction)
        UserCreatedEvent event = UserCreatedEvent.builder()
            .eventId(UUID.randomUUID().toString())
            .userId(user.getId().toString())
            .username(user.getUsername())
            .email(user.getEmail())
            .build();
        
        OutboxEvent outboxEvent = new OutboxEvent();
        outboxEvent.setId(UUID.randomUUID().toString());
        outboxEvent.setAggregateType("USER");
        outboxEvent.setAggregateId(user.getId().toString());
        outboxEvent.setEventType("CREATED");
        outboxEvent.setPayload(objectMapper.writeValueAsString(event));
        outboxEvent.setCreatedAt(LocalDateTime.now());
        outboxEvent.setStatus("PENDING");
        outboxEvent.setRetryCount(0);
        
        outboxRepository.save(outboxEvent);
        
        // 3. Return user ngay
        return UserMapper.toDto(user);
    }
}

// ✅ Outbox Processor - Scheduled job
@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxEventProcessor {
    
    private final OutboxEventRepository outboxRepository;
    private final EventPublisher eventPublisher;
    
    @Scheduled(fixedDelay = 5000) // Every 5 seconds
    public void processOutboxEvents() {
        List<OutboxEvent> pendingEvents = outboxRepository
            .findByStatusAndRetryCountLessThan("PENDING", 5);
        
        for (OutboxEvent event : pendingEvents) {
            try {
                // Publish to RabbitMQ
                eventPublisher.publishEvent(
                    event.getEventType(),
                    event.getPayload()
                );
                
                // Mark as processed
                event.setStatus("PROCESSED");
                event.setProcessedAt(LocalDateTime.now());
                outboxRepository.save(event);
                
                log.info("✅ Processed outbox event: {}", event.getId());
                
            } catch (Exception e) {
                log.error("❌ Failed to process outbox event: {}", event.getId(), e);
                
                // Increment retry count
                event.setRetryCount(event.getRetryCount() + 1);
                if (event.getRetryCount() >= 5) {
                    event.setStatus("FAILED");
                }
                outboxRepository.save(event);
            }
        }
    }
}
```

**Lợi ích:**
- ✅ **Guaranteed delivery**: Event chắc chắn được publish (eventual consistency)
- ✅ **Atomicity**: User tạo thành công <=> Event stored
- ✅ **Resilient**: RabbitMQ die → event vẫn trong DB → publish lại sau
- ✅ **Debugging**: Track tất cả events, replay khi cần

---

## 4. Roadmap Triển Khai

### 📅 **Phase 1: Fix Critical Issues (Week 1-2)**

**Mục tiêu:** Fix vấn đề blocking RPC và race condition

1. ✅ **Refactor RabbitRPCService**
   - Implement Direct Reply-To pattern
   - Add CompletableFuture support
   - Add proper correlation ID matching
   - Add timeout handling

2. ✅ **Setup Redis**
   - Add Redis dependency
   - Config Redis connection
   - Implement IdempotencyService
   - Apply to critical endpoints (createUser, login)

3. ✅ **Testing**
   - Test concurrent requests (100+ simultaneous)
   - Test duplicate email scenario
   - Load testing với JMeter
   - Verify no more response mixing

**Expected Results:**
- Response time stable < 500ms
- No duplicate users
- Handle 100+ concurrent requests

---

### 📅 **Phase 2: Add Resilience (Week 3-4)**

**Mục tiêu:** Thêm fault tolerance và error handling

1. ✅ **Dead Letter Queue**
   - Setup DLX cho tất cả queues
   - Implement retry mechanism
   - Add poison queue

2. ✅ **Circuit Breaker**
   - Add Resilience4j
   - Config circuit breaker cho RPC calls
   - Implement fallback methods

3. ✅ **Monitoring**
   - Add metrics (success rate, latency)
   - Setup alerts (DLQ size, circuit open)

**Expected Results:**
- No message loss
- Graceful degradation khi service die
- Auto recovery

---

### 📅 **Phase 3: Event-Driven Architecture (Week 5-6)**

**Mục tiêu:** Tách sync/async operations

1. ✅ **Setup Event Exchange**
   - Create topic exchange
   - Define event schemas
   - Setup queues cho consumers

2. ✅ **Refactor Register Flow**
   - Sync: create user, return user ID
   - Async: send email, create CV
   - Publish UserCreatedEvent

3. ✅ **Implement CV Service Consumer**
   - Listen to UserCreatedEvent
   - Auto create CV template

**Expected Results:**
- Register response time < 200ms
- CV tạo trong background (< 5s)
- System dễ mở rộng

---

### 📅 **Phase 4: Outbox Pattern (Week 7-8)**

**Mục tiêu:** Guaranteed event delivery

1. ✅ **Create Outbox Table**
   - Define schema
   - Add repository

2. ✅ **Refactor UserService**
   - Store events in Outbox
   - Transactional write

3. ✅ **Implement Outbox Processor**
   - Scheduled job
   - Publish pending events
   - Retry failed events

**Expected Results:**
- No lost events
- Eventual consistency guaranteed

---

## 5. Sequence Diagrams

### 📊 **Current Flow (❌ Problematic)**

```
┌────────┐          ┌──────────────┐          ┌──────────────┐
│ Client │          │ Auth Service │          │ User Service │
└───┬────┘          └──────┬───────┘          └──────┬───────┘
    │                      │                         │
    │  POST /register      │                         │
    ├─────────────────────>│                         │
    │                      │                         │
    │                      │  RPC: createUser       │
    │                      ├────────────────────────>│
    │                      │                         │
    │                      │  receiveAndConvert()   │
    │                      │  (BLOCKING 8s)         │
    │                      │◄───┐                    │
    │                      │    │                    │
    │                      │    │ POLLING            │
    │                      │    │ QUEUE              │
    │                      │    │                    │
    │                      │<───┘                    │
    │                      │                         │
    │                      │       Response          │
    │                      │ (might be wrong!)       │
    │                      │◄────────────────────────┤
    │                      │                         │
    │    Response (200)    │                         │
    │◄─────────────────────┤                         │
    │                      │                         │
    │  POST /register      │                         │
    │  (same email)        │                         │
    ├─────────────────────>│                         │
    │                      │                         │
    │                      │  RPC: createUser       │
    │                      ├────────────────────────>│
    │                      │                         │
    │                      │  receiveAndConvert()   │
    │                      │  (gets response from    │
    │                      │   PREVIOUS request!)    │
    │                      │◄────────────────────────┤
    │                      │                         │
    │    Response (200) ❌  │                         │
    │◄─────────────────────┤                         │
    │                      │                         │
    │  POST /login         │                         │
    ├─────────────────────>│                         │
    │                      │                         │
    │                      │  RPC: authenticateUser │
    │                      ├────────────────────────>│
    │                      │                         │
    │                      │     Error (duplicate)   │
    │                      │◄────────────────────────┤
    │                      │                         │
    │    Error (409) ❌     │                         │
    │◄─────────────────────┤                         │
```

**Problems:**
- ❌ Request 2 nhận response của Request 1
- ❌ Blocking threads
- ❌ Lỗi chỉ phát hiện khi login

---

### 📊 **Improved Flow với Direct Reply-To (✅ Fixed)**

```
┌────────┐          ┌──────────────┐          ┌──────────────┐
│ Client │          │ Auth Service │          │ User Service │
└───┬────┘          └──────┬───────┘          └──────┬───────┘
    │                      │                         │
    │  POST /register      │                         │
    ├─────────────────────>│                         │
    │                      │                         │
    │                      │ 1. Generate correlationId: ABC123
    │                      │ 2. Store Future in cache
    │                      │ 3. Send with replyTo=amq.rabbitmq.reply-to
    │                      │                         │
    │                      │  Request (corrId=ABC123)│
    │                      ├────────────────────────>│
    │                      │                         │
    │                      │  (NON-BLOCKING)        │
    │                      │  Future.get()          │
    │                      │  with timeout          │
    │                      │                         │
    │                      │                         │ Check Redis
    │                      │                         │ (idempotency)
    │                      │                         │
    │                      │                         │ Create user
    │                      │                         │ in DB
    │                      │                         │
    │                      │                         │ Cache result
    │                      │                         │
    │                      │  Response (corrId=ABC123)│
    │                      │◄────────────────────────┤
    │                      │                         │
    │                      │ Match correlationId      │
    │                      │ Complete Future         │
    │                      │                         │
    │    Response (200) ✅  │                         │
    │◄─────────────────────┤                         │
    │                      │                         │
    │                      │                         │
    │  POST /register      │                         │
    │  (same email)        │                         │
    ├─────────────────────>│                         │
    │                      │                         │
    │                      │ Generate correlationId: XYZ789
    │                      │                         │
    │                      │  Request (corrId=XYZ789)│
    │                      ├────────────────────────>│
    │                      │                         │
    │                      │                         │ Check Redis
    │                      │                         │ Found! ABC123
    │                      │                         │
    │                      │                         │ Return cached
    │                      │                         │ OR error
    │                      │                         │
    │                      │  Response (corrId=XYZ789)│
    │                      │  Error: duplicate email │
    │                      │◄────────────────────────┤
    │                      │                         │
    │    Error (409) ✅     │                         │
    │◄─────────────────────┤                         │
```

**Improvements:**
- ✅ Correct correlation ID matching
- ✅ Non-blocking with timeout
- ✅ Idempotency check
- ✅ Immediate error detection

---

### 📊 **Hybrid Flow: Sync + Async (✅ Recommended)**

```
┌────────┐     ┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│ Client │     │ Auth Service │     │ User Service │     │  CV Service  │
└───┬────┘     └──────┬───────┘     └──────┬───────┘     └──────┬───────┘
    │                 │                     │                     │
    │  POST /register │                     │                     │
    ├────────────────>│                     │                     │
    │                 │                     │                     │
    │                 │ SYNC RPC: createUser│                     │
    │                 ├────────────────────>│                     │
    │                 │                     │                     │
    │                 │                     │ 1. Create user      │
    │                 │                     │ 2. Store in Outbox  │
    │                 │                     │ (same transaction)  │
    │                 │                     │                     │
    │                 │  Response (user ID) │                     │
    │                 │◄────────────────────┤                     │
    │                 │                     │                     │
    │  Response (200) │                     │                     │
    │  with user ID ✅ │                     │                     │
    │◄────────────────┤                     │                     │
    │                 │                     │                     │
    │                 │                     │                     │
    │                 │                     │ ASYNC: Outbox       │
    │                 │                     │ Processor           │
    │                 │                     ├─┐                   │
    │                 │                     │ │ Read pending      │
    │                 │                     │ │ events            │
    │                 │                     │◄┘                   │
    │                 │                     │                     │
    │                 │                     │ Publish UserCreated │
    │                 │                     │ Event               │
    │                 │                     ├────────────────────>│
    │                 │                     │                     │
    │                 │                     │                     │ Create CV
    │                 │                     │                     │ template
    │                 │                     │                     │
    │                 │                     │                     │ Send welcome
    │                 │                     │                     │ email
    │                 │                     │                     │
    │  (User can login immediately)        │                     │
    │  (CV ready in ~5s)                   │                     │
```

**Benefits:**
- ✅ Fast response (< 200ms)
- ✅ User can login ngay
- ✅ Background tasks không block
- ✅ Guaranteed event delivery

---

## 📝 Summary & Recommendations

### 🎯 **Ưu Tiên Cao Nhất**

1. **Fix RabbitRPCService với Direct Reply-To** → Giải quyết race condition
2. **Add Idempotency với Redis** → Chống duplicate
3. **Add Dead Letter Queue** → Tránh mất message

### 🎯 **Trung Hạn**

4. **Circuit Breaker** → Fault tolerance
5. **Event-Driven cho async operations** → Scalability

### 🎯 **Dài Hạn**

6. **Outbox Pattern** → Guaranteed delivery
7. **Monitoring & Alerting** → Observability

### 📊 **Expected Performance**

| Metric | Before | After |
|--------|--------|-------|
| Register Response Time | 500-2000ms | < 200ms |
| Concurrent Requests | ~10 (blocking) | 1000+ (non-blocking) |
| Duplicate Prevention | ❌ None | ✅ Redis Lock |
| Message Loss | ❌ Possible | ✅ DLQ + Outbox |
| Scalability | ⚠️ Limited | ✅ Event-driven |

---

## 🚀 Next Steps

1. **Review** kiến trúc này với team
2. **Chọn Phase** để bắt đầu (recommend: Phase 1)
3. **Setup environment** (Redis, monitoring tools)
4. **Implement từng solution** theo roadmap
5. **Test kỹ** mỗi phase trước khi qua phase tiếp

**Tôi sẵn sàng hỗ trợ implement chi tiết từng phần!** 🔥
