# 📨 User Service - RabbitMQ Consumers

## 📋 Overview

Thư mục này chứa tất cả RabbitMQ consumers cho User Service, phân chia theo chức năng rõ ràng.

---

## 🗂️ File Structure

```
consumers/
├── UserMessageHandler.java       ⭐ MAIN CONSUMER (dùng từ giờ)
├── DLQRetryListener.java         ⭐ AUTO RETRY (dùng từ giờ)
└── UserConsumer.java             ❌ LEGACY (XÓA ĐI!)
```

---

## 📁 File Descriptions

### 1️⃣ **UserMessageHandler.java** ⭐ MAIN CONSUMER

**Chức năng:**
- Consumer chính xử lý TẤT CẢ RPC requests từ các services khác
- Idempotency check với Redis (chống duplicate)
- Error handling tự động routing sang DLQ
- Structured logging với correlation ID

**Queues xử lý:**
```java
@RabbitListener(queues = RabbitConstants.USER_FIND_BY_EMAIL)
public void handleFindByEmail(Message message) { ... }

@RabbitListener(queues = RabbitConstants.USER_CREATE_QUEUE)
public void handleCreateUser(Message message) { ... }

@RabbitListener(queues = RabbitConstants.USER_ACTIVATE_QUEUE)
public void handleActivateUser(Message message) { ... }

@RabbitListener(queues = RabbitConstants.USER_GET_BY_ID_QUEUE)
public void handleGetUserById(Message message) { ... }

@RabbitListener(queues = RabbitConstants.USER_UPDATE_QUEUE)
public void handleUpdateUser(Message message) { ... }

@RabbitListener(queues = RabbitConstants.USER_DELETE_QUEUE)
public void handleDeleteUser(Message message) { ... }
```

**Features:**
- ✅ Redis idempotency check (prevent duplicate)
- ✅ Cached response cho duplicate requests
- ✅ Automatic DLQ routing on exception
- ✅ Correlation ID tracking
- ✅ Structured logging

**Khi nào dùng:**
- **TẤT CẢ business logic mới viết ở đây**
- Mỗi operation cần method riêng với `@RabbitListener`
- Throw exception để message tự động vào DLQ

**Example:**
```java
@RabbitListener(queues = RabbitConstants.USER_VERIFY_EMAIL)
public void handleVerifyEmail(Message message) {
    String correlationId = message.getMessageProperties().getCorrelationId();
    
    try {
        // 1. Check idempotency
        if (!idempotencyService.isFirstRequest(correlationId)) {
            var cached = idempotencyService.getCachedResult(correlationId);
            if (cached.isPresent()) {
                sendResponse(replyTo, correlationId, cached.get());
                return;
            }
        }
        
        // 2. Parse request
        String email = objectMapper.readValue(message.getBody(), String.class);
        
        // 3. Business logic
        User user = userService.verifyEmail(email);
        
        // 4. Cache result
        String result = objectMapper.writeValueAsString(user);
        idempotencyService.updateResult(correlationId, result);
        
        // 5. Send response
        sendResponse(replyTo, correlationId, result);
        
    } catch (Exception e) {
        log.error("Error verifying email: {}", correlationId, e);
        idempotencyService.markAsFailed(correlationId, e.getMessage());
        
        // ⚠️ Throw để message vào DLQ
        throw new RuntimeException("Failed to verify email", e);
    }
}
```

---

### 2️⃣ **DLQRetryListener.java** ⭐ AUTO RETRY

**Chức năng:**
- TỰ ĐỘNG nhận messages từ Dead Letter Queue (DLQ)
- Retry với exponential backoff (5s, 10s, 20s, 40s)
- Move to poison queue sau 3 lần retry
- Alert admin khi có poison message

**Queues xử lý:**
```java
@RabbitListener(queues = DeadLetterQueueConfig.USER_CREATE_DLQ)
public void handleUserCreateDLQ(Message message) { ... }

@RabbitListener(queues = DeadLetterQueueConfig.USER_ACTIVATE_DLQ)
public void handleUserActivateDLQ(Message message) { ... }

@RabbitListener(queues = DeadLetterQueueConfig.POISON_QUEUE)
public void monitorPoisonQueue(Message message) { ... }
```

**Features:**
- ✅ Exponential backoff (5s → 10s → 20s)
- ✅ Max 3 retries
- ✅ Poison queue isolation
- ✅ Admin notification
- ✅ Automatic retry scheduling

**Khi nào hoạt động:**
- `UserMessageHandler` throw exception
- Database timeout
- Network error
- Service restart
- Bất kỳ lỗi nào

**Flow:**
```
UserMessageHandler throw exception
        ↓
Message → user.create.dlq (Dead Letter Queue)
        ↓
DLQRetryListener nhận message
        ↓
Check retry count < 3?
        ├─ YES: Schedule retry (5s, 10s, 20s)
        │       ↓
        │   Gửi lại vào user.create.queue
        │       ↓
        │   UserMessageHandler xử lý lại
        │
        └─ NO: Move to poison.queue
                ↓
            Alert admin
```

**⚠️ QUAN TRỌNG:**
- KHÔNG cần gọi trực tiếp class này
- RabbitMQ tự động routing message vào DLQ khi có exception
- DLQRetryListener tự động nhận và xử lý

---

### 3️⃣ **UserConsumer.java** ❌ LEGACY CODE

**Chức năng:**
- Code cũ không có idempotency
- Có race condition
- Không có retry logic
- Đang bị thay thế

**⚠️ ACTION REQUIRED:**
```bash
# XÓA FILE NÀY ĐI!
rm UserConsumer.java

# Hoặc backup:
mv UserConsumer.java UserConsumer.old.java
```

**Tại sao phải xóa:**
- ❌ Không có idempotency check
- ❌ Duplicate requests không được xử lý
- ❌ Race condition với shared reply queue
- ❌ Không có automatic retry
- ❌ Message loss on failure

**Đã được thay thế bởi:**
- `UserMessageHandler.java` (business logic)
- `DLQRetryListener.java` (retry logic)

---

## 🔄 Message Flow

### Happy Path (Thành công)

```
1. Auth-service gửi createUser request
        ↓
2. user.create.queue
        ↓
3. UserMessageHandler.handleCreateUser()
        ├─ Check idempotency ✅
        ├─ Create user ✅
        ├─ Cache result ✅
        └─ Send response ✅
        ↓
4. Auth-service nhận response
```

### Error Path (Có lỗi)

```
1. Auth-service gửi createUser request
        ↓
2. user.create.queue
        ↓
3. UserMessageHandler.handleCreateUser()
        ├─ Check idempotency ✅
        ├─ Create user ❌ DATABASE TIMEOUT
        └─ throw RuntimeException
        ↓
4. RabbitMQ routing → user.create.dlq
        ↓
5. DLQRetryListener.handleUserCreateDLQ()
        ├─ Retry count = 0
        ├─ Delay = 5s
        └─ Schedule retry
        ↓
6. (After 5s) Gửi lại vào user.create.queue
        ↓
7. UserMessageHandler.handleCreateUser() (lần 2)
        ├─ Check idempotency ✅ (duplicate, skip)
        ├─ Create user ✅ (DB đã hồi phục)
        ├─ Cache result ✅
        └─ Send response ✅
        ↓
8. Auth-service nhận response (sau 5.5s)
```

### Poison Path (Fail hết 3 lần)

```
1-7. (Same as Error Path, nhưng fail 3 lần)
        ↓
8. DLQRetryListener: retry count = 3 >= MAX_RETRY
        ├─ moveToPoisonQueue() ☠️
        ├─ notifyAdmin() 🚨
        └─ Store to poison.queue
        ↓
9. DLQRetryListener.monitorPoisonQueue()
        └─ Log error, manual review required
```

---

## 📝 Best Practices

### ✅ DO

```java
// ✅ Viết consumer mới trong UserMessageHandler
@RabbitListener(queues = "user.new.operation")
public void handleNewOperation(Message message) {
    // Check idempotency
    // Process business logic
    // Cache result
    // Send response
    // Throw exception on error (auto DLQ)
}

// ✅ Throw exception để trigger DLQ
throw new RuntimeException("Error message");

// ✅ Check idempotency cho mọi request
if (!idempotencyService.isFirstRequest(correlationId)) {
    // Return cached result
}
```

### ❌ DON'T

```java
// ❌ KHÔNG viết consumer mới trong UserConsumer.java
// (File này sẽ bị xóa)

// ❌ KHÔNG catch exception mà không re-throw
try {
    userService.createUser();
} catch (Exception e) {
    log.error("Error", e);
    // ← Message sẽ bị ACK và mất vĩnh viễn!
}

// ❌ KHÔNG gọi DLQRetryListener trực tiếp
dlqRetryListener.handleUserCreateDLQ(message); // ← WRONG!
// RabbitMQ tự động routing, không cần gọi manual

// ❌ KHÔNG skip idempotency check
// Mọi operation PHẢI check idempotency
```

---

## 🚀 Adding New Consumer

**Step-by-step:**

1. **Define routing key trong RabbitConstants**
```java
public static final String USER_NEW_OPERATION = "user.new.operation";
public static final String USER_NEW_OPERATION_QUEUE = "user.new.operation.queue";
```

2. **Add method trong UserMessageHandler**
```java
@RabbitListener(queues = RabbitConstants.USER_NEW_OPERATION_QUEUE)
public void handleNewOperation(Message message) {
    String correlationId = message.getMessageProperties().getCorrelationId();
    String replyTo = message.getMessageProperties().getReplyTo();
    
    try {
        // 1. Idempotency check
        if (!idempotencyService.isFirstRequest(correlationId)) {
            var cached = idempotencyService.getCachedResult(correlationId);
            if (cached.isPresent()) {
                sendResponse(replyTo, correlationId, cached.get());
                return;
            }
        }
        
        // 2. Parse request
        YourRequest request = objectMapper.readValue(
            message.getBody(), 
            YourRequest.class
        );
        
        // 3. Business logic
        YourResult result = yourService.doSomething(request);
        
        // 4. Cache result
        String resultJson = objectMapper.writeValueAsString(result);
        idempotencyService.updateResult(correlationId, resultJson);
        
        // 5. Send response
        sendResponse(replyTo, correlationId, resultJson);
        
        log.info("✅ Operation success - correlationId: {}", correlationId);
        
    } catch (Exception e) {
        log.error("❌ Operation failed - correlationId: {}", correlationId, e);
        idempotencyService.markAsFailed(correlationId, e.getMessage());
        throw new RuntimeException("Operation failed: " + e.getMessage(), e);
    }
}
```

3. **Add DLQ handler trong DLQRetryListener**
```java
@RabbitListener(queues = DeadLetterQueueConfig.USER_NEW_OPERATION_DLQ)
public void handleNewOperationDLQ(Message message) {
    retryMessage(
        message,
        "NewOperation",
        RabbitConstants.USER_EXCHANGE,
        RabbitConstants.USER_NEW_OPERATION
    );
}
```

4. **Config queue trong DeadLetterQueueConfig**
```java
public static final String USER_NEW_OPERATION_DLQ = "user.new.operation.dlq";

@Bean
Queue userNewOperationQueue() {
    return QueueBuilder.durable(RabbitConstants.USER_NEW_OPERATION_QUEUE)
        .withArgument("x-dead-letter-exchange", DEAD_LETTER_EXCHANGE)
        .withArgument("x-dead-letter-routing-key", "dlq.user.new.operation")
        .build();
}

@Bean
Queue userNewOperationDLQ() {
    return QueueBuilder.durable(USER_NEW_OPERATION_DLQ).build();
}

@Bean
Binding userNewOperationDLQBinding() {
    return BindingBuilder
        .bind(userNewOperationDLQ())
        .to(deadLetterExchange())
        .with("dlq.user.new.operation");
}
```

**Done!** 🎉

---

## 📊 Monitoring

### Logs to watch:

```bash
# Success
✅ [USER-CREATE] Success - userId: 123, correlationId: abc

# Duplicate (idempotency)
🔄 [USER-CREATE] Duplicate request detected - correlationId: abc

# Error (going to DLQ)
❌ [USER-CREATE] Error - correlationId: abc

# DLQ retry
📥 [DLQ-UserCreate] Message received - retryCount: 1/3
⏳ [DLQ-UserCreate] Scheduling retry in 10s
🔄 [DLQ-UserCreate] Message resent - attempt: 2

# Poison queue
🚨 [DLQ-UserCreate] Max retry exceeded
☠️ [DLQ-UserCreate] Message moved to poison queue
```

### Metrics to track:

- `rabbitmq.consumer.success` - Success rate
- `rabbitmq.consumer.duplicate` - Duplicate requests
- `rabbitmq.dlq.retry` - Retry count
- `rabbitmq.poison.count` - Poison messages (should be 0!)

---

## 🎯 Summary

| File | Purpose | Khi nào dùng |
|------|---------|--------------|
| **UserMessageHandler** | Main business logic | Viết TẤT CẢ consumer mới ở đây |
| **DLQRetryListener** | Auto retry failed messages | Tự động, không cần gọi |
| **UserConsumer** | Legacy code | ❌ XÓA ĐI! |

**Remember:**
- ✅ Tất cả business logic → `UserMessageHandler`
- ✅ Throw exception để trigger DLQ → `DLQRetryListener` tự động xử lý
- ✅ Always check idempotency
- ❌ Xóa `UserConsumer.java`

---

## 📚 References

- [RABBITMQ_ARCHITECTURE_IMPROVEMENT.md](../../../docs/RABBITMQ_ARCHITECTURE_IMPROVEMENT.md) - Kiến trúc tổng thể
- [MIGRATION_GUIDE.md](../../../docs/MIGRATION_GUIDE.md) - Hướng dẫn migration
- [SEQUENCE_DIAGRAMS.md](../../../docs/SEQUENCE_DIAGRAMS.md) - Flow diagrams

**Last Updated:** October 23, 2025
