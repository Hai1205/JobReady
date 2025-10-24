# 📊 Sequence Diagrams - RabbitMQ Architecture

## 1. Current Architecture (Problematic) - Register Flow

```mermaid
sequenceDiagram
    participant Client
    participant AuthService
    participant SharedQueue as auth.reply.queue
    participant UserService
    
    Note over Client,UserService: Request 1: Register user1@test.com
    
    Client->>AuthService: POST /register (user1@test.com)
    activate AuthService
    
    AuthService->>UserService: RPC: createUser (corrId: ABC123)
    activate UserService
    
    Note over AuthService: BLOCKING receiveAndConvert()<br/>Thread stuck, polling queue
    
    UserService->>UserService: Create user in DB
    UserService->>SharedQueue: Response (corrId: ABC123)
    deactivate UserService
    
    Note over Client,UserService: Request 2: Register user1@test.com (duplicate)
    
    Client->>AuthService: POST /register (user1@test.com) - concurrent
    
    AuthService->>UserService: RPC: createUser (corrId: XYZ789)
    activate UserService
    
    Note over AuthService: BLOCKING receiveAndConvert()<br/>Another thread stuck
    
    UserService->>UserService: Create user in DB
    Note over UserService: ❌ Duplicate! But no check
    
    UserService->>SharedQueue: Response (corrId: XYZ789)
    deactivate UserService
    
    Note over AuthService: ❌ Request 1 picks up Response XYZ789<br/>❌ Request 2 picks up Response ABC123<br/>WRONG CORRELATION!
    
    SharedQueue-->>AuthService: Response (wrong corrId)
    deactivate AuthService
    
    AuthService-->>Client: 200 OK (WRONG!)
    
    Note over Client,UserService: Request 1 might timeout or get wrong response<br/>Only discover duplicate on login!
```

---

## 2. Improved Architecture - Register Flow

```mermaid
sequenceDiagram
    participant Client
    participant AuthService
    participant DirectReplyTo as Direct Reply-To<br/>(per-connection)
    participant UserService
    participant Redis
    
    Note over Client,UserService: Request 1: Register user1@test.com
    
    Client->>AuthService: POST /register (user1@test.com)
    activate AuthService
    
    AuthService->>AuthService: Generate corrId: ABC123
    AuthService->>DirectReplyTo: Store Future[ABC123] in cache
    
    AuthService->>UserService: RPC: createUser<br/>(corrId: ABC123, replyTo: amq.rabbitmq.reply-to)
    
    Note over AuthService: ✅ NON-BLOCKING<br/>CompletableFuture with timeout
    
    activate UserService
    
    UserService->>Redis: Check idempotency key: user:create:ABC123
    Redis-->>UserService: Not exists
    
    UserService->>Redis: SET user:create:ABC123 = "PROCESSING"
    
    UserService->>UserService: Create user in DB
    
    UserService->>Redis: UPDATE user:create:ABC123 = {result}
    
    UserService->>DirectReplyTo: Response (corrId: ABC123)
    deactivate UserService
    
    DirectReplyTo-->>AuthService: ✅ Exact match corrId: ABC123
    AuthService->>AuthService: Complete Future[ABC123]
    
    deactivate AuthService
    AuthService-->>Client: 200 OK (user created)
    
    Note over Client,UserService: Request 2: Register user1@test.com (duplicate)
    
    Client->>AuthService: POST /register (user1@test.com)
    activate AuthService
    
    AuthService->>AuthService: Generate corrId: XYZ789
    AuthService->>DirectReplyTo: Store Future[XYZ789] in cache
    
    AuthService->>UserService: RPC: createUser<br/>(corrId: XYZ789)
    
    activate UserService
    
    UserService->>Redis: Check idempotency key: user:create:XYZ789
    Redis-->>UserService: Not exists
    
    UserService->>Redis: SET user:create:XYZ789 = "PROCESSING"
    
    UserService->>UserService: Try create user
    Note over UserService: ❌ DB constraint error: duplicate email
    
    UserService->>Redis: MARK user:create:XYZ789 = ERROR
    
    UserService->>DirectReplyTo: Error Response (corrId: XYZ789)
    deactivate UserService
    
    DirectReplyTo-->>AuthService: ✅ Exact match corrId: XYZ789
    AuthService->>AuthService: Complete Future[XYZ789] with error
    
    deactivate AuthService
    AuthService-->>Client: 409 Conflict (duplicate email) ✅
    
    Note over Client,UserService: ✅ Immediate error detection<br/>✅ No race condition<br/>✅ Correct correlation
```

---

## 3. Hybrid Flow: Sync RPC + Async Events

```mermaid
sequenceDiagram
    participant Client
    participant AuthService
    participant UserService
    participant EventExchange
    participant CVService
    participant NotificationService
    
    Note over Client,NotificationService: Register Flow - Hybrid Sync + Async
    
    Client->>AuthService: POST /register
    activate AuthService
    
    Note over AuthService,UserService: SYNC RPC - cần response ngay
    
    AuthService->>UserService: RPC: createUser (with Direct Reply-To)
    activate UserService
    
    UserService->>UserService: Create user in DB
    UserService->>UserService: Store UserCreatedEvent in Outbox table<br/>(SAME transaction)
    
    UserService-->>AuthService: Response: { userId, email }
    deactivate UserService
    
    Note over AuthService: ✅ Got userId ngay lập tức
    
    AuthService-->>Client: 200 OK { userId, email }
    deactivate AuthService
    
    Note over Client: ✅ Client nhận response < 200ms<br/>✅ Có thể login ngay
    
    Note over UserService,NotificationService: ASYNC Events - xử lý background
    
    activate UserService
    UserService->>UserService: Outbox Processor (scheduled @5s)
    UserService->>EventExchange: Publish UserCreatedEvent<br/>(fanout to multiple queues)
    deactivate UserService
    
    EventExchange->>CVService: UserCreatedEvent (routing: user.created)
    activate CVService
    CVService->>CVService: Create default CV template
    CVService-->>EventExchange: ACK
    deactivate CVService
    
    EventExchange->>NotificationService: UserCreatedEvent
    activate NotificationService
    NotificationService->>NotificationService: Send welcome email
    NotificationService-->>EventExchange: ACK
    deactivate NotificationService
    
    Note over CVService,NotificationService: ✅ Parallel processing<br/>✅ No blocking<br/>✅ Eventual consistency
```

---

## 4. Dead Letter Queue (DLQ) Flow

```mermaid
sequenceDiagram
    participant Producer as Auth Service
    participant MainQueue as user.create.queue
    participant Consumer as User Service
    participant DLX as Dead Letter Exchange
    participant DLQ as DLQ Queue
    participant DLQListener as DLQ Retry Listener
    participant PoisonQueue as Poison Queue
    
    Note over Producer,PoisonQueue: Scenario: Consumer fails to process message
    
    Producer->>MainQueue: Send createUser message
    
    MainQueue->>Consumer: Deliver message
    activate Consumer
    
    Consumer->>Consumer: Process message
    Note over Consumer: ❌ Exception thrown<br/>(DB down, validation error, etc.)
    
    Consumer-->>MainQueue: NACK (reject)
    deactivate Consumer
    
    Note over MainQueue: Message has x-message-ttl<br/>or max retry reached
    
    MainQueue->>DLX: Route to Dead Letter Exchange
    DLX->>DLQ: Route to DLQ (routing key: dlq.user.create)
    
    Note over DLQ: Message waiting in DLQ
    
    DLQ->>DLQListener: Deliver message
    activate DLQListener
    
    DLQListener->>DLQListener: Check retry count: 0
    Note over DLQListener: Retry count < MAX_RETRY (3)
    
    DLQListener->>DLQListener: Calculate backoff: 2^0 * 5 = 5s
    
    Note over DLQListener: ⏳ Wait 5 seconds
    
    DLQListener->>MainQueue: Resend message<br/>(retry count: 1)
    deactivate DLQListener
    
    MainQueue->>Consumer: Deliver message (attempt 2)
    activate Consumer
    
    Consumer->>Consumer: Process message
    Note over Consumer: ❌ Still failing
    
    Consumer-->>MainQueue: NACK
    deactivate Consumer
    
    MainQueue->>DLX: Route to DLX again
    DLX->>DLQ: Back to DLQ
    
    DLQ->>DLQListener: Deliver message
    activate DLQListener
    
    DLQListener->>DLQListener: Check retry count: 1
    DLQListener->>DLQListener: Calculate backoff: 2^1 * 5 = 10s
    
    Note over DLQListener: ⏳ Wait 10 seconds
    
    DLQListener->>MainQueue: Resend message (retry count: 2)
    deactivate DLQListener
    
    Note over Consumer: ... Same process ...
    
    MainQueue->>Consumer: Deliver message (attempt 3)
    activate Consumer
    Consumer-->>MainQueue: NACK (still failing)
    deactivate Consumer
    
    MainQueue->>DLX: Route to DLX
    DLX->>DLQ: Back to DLQ
    
    DLQ->>DLQListener: Deliver message
    activate DLQListener
    
    DLQListener->>DLQListener: Check retry count: 2
    DLQListener->>DLQListener: Calculate backoff: 2^2 * 5 = 20s
    
    Note over DLQListener: ⏳ Wait 20 seconds
    
    DLQListener->>MainQueue: Resend message (retry count: 3)
    deactivate DLQListener
    
    MainQueue->>Consumer: Deliver message (attempt 4)
    activate Consumer
    Consumer-->>MainQueue: NACK (FINAL failure)
    deactivate Consumer
    
    MainQueue->>DLX: Route to DLX
    DLX->>DLQ: Back to DLQ
    
    DLQ->>DLQListener: Deliver message
    activate DLQListener
    
    DLQListener->>DLQListener: Check retry count: 3
    Note over DLQListener: ❌ MAX_RETRY exceeded!
    
    DLQListener->>PoisonQueue: Move to Poison Queue
    DLQListener->>DLQListener: 🚨 Alert Admin<br/>(Email, Slack, PagerDuty)
    deactivate DLQListener
    
    Note over PoisonQueue: ☠️ Message stuck here<br/>Requires manual intervention
```

---

## 5. Circuit Breaker Flow

```mermaid
sequenceDiagram
    participant Client
    participant AuthService
    participant CircuitBreaker
    participant UserService
    
    Note over Client,UserService: Normal State: CLOSED (All requests pass through)
    
    loop 5 successful requests
        Client->>AuthService: Request
        AuthService->>CircuitBreaker: Call user service
        CircuitBreaker->>UserService: Forward request
        UserService-->>CircuitBreaker: Success response
        CircuitBreaker-->>AuthService: Return response
        AuthService-->>Client: 200 OK
    end
    
    Note over UserService: ❌ User Service starts failing
    
    loop 5 failed requests
        Client->>AuthService: Request
        AuthService->>CircuitBreaker: Call user service
        CircuitBreaker->>UserService: Forward request
        UserService-->>CircuitBreaker: ❌ Timeout / Error
        CircuitBreaker->>CircuitBreaker: Increment failure count
        CircuitBreaker-->>AuthService: Error
        AuthService-->>Client: 500 Error
    end
    
    Note over CircuitBreaker: ⚡ Failure rate > 50%<br/>Circuit OPENS!
    
    Client->>AuthService: Request
    AuthService->>CircuitBreaker: Call user service
    
    Note over CircuitBreaker: Circuit is OPEN<br/>Fail fast!
    
    CircuitBreaker->>CircuitBreaker: Call fallback method
    CircuitBreaker-->>AuthService: Fallback response<br/>(cached data or error)
    AuthService-->>Client: 503 Service Unavailable<br/>(immediate, no waiting)
    
    Note over Client,UserService: ✅ No wasted time waiting for timeout<br/>✅ System remains responsive
    
    Note over CircuitBreaker: ⏳ Wait 30 seconds...
    
    Note over CircuitBreaker: Circuit transitions to HALF_OPEN<br/>Allow limited test requests
    
    loop 3 test requests
        Client->>AuthService: Request
        AuthService->>CircuitBreaker: Call user service
        
        alt User Service healthy
            CircuitBreaker->>UserService: Test request
            UserService-->>CircuitBreaker: ✅ Success
            CircuitBreaker->>CircuitBreaker: Increment success count
            CircuitBreaker-->>AuthService: Success
            AuthService-->>Client: 200 OK
        else User Service still down
            CircuitBreaker->>UserService: Test request
            UserService-->>CircuitBreaker: ❌ Still failing
            CircuitBreaker->>CircuitBreaker: Back to OPEN state
            CircuitBreaker-->>AuthService: Fallback
            AuthService-->>Client: 503 Error
        end
    end
    
    Note over CircuitBreaker: ✅ All 3 test requests successful<br/>Circuit CLOSES!
    
    Note over Client,UserService: System back to normal operation
    
    Client->>AuthService: Request
    AuthService->>CircuitBreaker: Call user service
    CircuitBreaker->>UserService: Forward request
    UserService-->>CircuitBreaker: Success
    CircuitBreaker-->>AuthService: Return response
    AuthService-->>Client: 200 OK
```

---

## 6. Full Register Flow với All Improvements

```mermaid
sequenceDiagram
    participant Client
    participant AuthService
    participant Redis
    participant UserService
    participant DLQ
    participant EventExchange
    participant CVService
    
    Client->>AuthService: POST /register { email, password }
    activate AuthService
    
    Note over AuthService: 1. Generate correlationId: ABC123
    
    AuthService->>Redis: Check rate limit for IP
    Redis-->>AuthService: OK (under limit)
    
    AuthService->>UserService: RPC createUser<br/>(Direct Reply-To, corrId: ABC123)
    
    Note over AuthService: CompletableFuture.get(10s)
    
    activate UserService
    
    Note over UserService: 2. Idempotency Check
    
    UserService->>Redis: GET idempotency:user:create:ABC123
    Redis-->>UserService: Key not found
    
    UserService->>Redis: SET idempotency:user:create:ABC123 = "PROCESSING"
    
    Note over UserService: 3. Business Logic
    
    UserService->>UserService: Validate email format
    UserService->>UserService: Hash password
    
    UserService->>UserService: BEGIN TRANSACTION
    UserService->>UserService: INSERT into users table
    UserService->>UserService: INSERT into outbox_events table<br/>(UserCreatedEvent)
    UserService->>UserService: COMMIT TRANSACTION
    
    Note over UserService: 4. Cache Result
    
    UserService->>Redis: SET idempotency:user:create:ABC123 = {result}<br/>TTL: 24h
    
    Note over UserService: 5. Send Response
    
    UserService-->>AuthService: RPC Response { userId, email }
    deactivate UserService
    
    Note over AuthService: 6. Process Response
    
    AuthService->>AuthService: Generate JWT tokens
    AuthService->>AuthService: Set cookies
    
    deactivate AuthService
    AuthService-->>Client: 200 OK { user, tokens }
    
    Note over Client: ✅ User can login immediately<br/>Response time: ~200ms
    
    Note over UserService,CVService: ASYNC Background Processing
    
    activate UserService
    Note over UserService: 7. Outbox Processor (every 5s)
    
    UserService->>UserService: SELECT pending events from outbox
    UserService->>EventExchange: Publish UserCreatedEvent
    UserService->>UserService: Mark event as PROCESSED
    deactivate UserService
    
    Note over EventExchange: 8. Fan-out to Consumers
    
    EventExchange->>CVService: UserCreatedEvent
    activate CVService
    CVService->>CVService: Create default CV template
    CVService-->>EventExchange: ACK
    deactivate CVService
    
    Note over CVService: ✅ CV ready in ~5 seconds
    
    Note over Client,CVService: What if UserService fails?
    
    Client->>AuthService: POST /register (same email)
    activate AuthService
    
    AuthService->>UserService: RPC createUser (corrId: XYZ789)
    
    activate UserService
    UserService->>Redis: GET idempotency:user:create:XYZ789
    Redis-->>UserService: Not found (different corrId)
    
    UserService->>UserService: Try INSERT user
    Note over UserService: ❌ DB Error: duplicate email
    
    UserService->>UserService: Catch exception
    UserService->>Redis: MARK idempotency:user:create:XYZ789 = ERROR
    
    alt Retry < MAX_RETRY
        UserService-->>DLQ: NACK → Send to DLQ
        Note over DLQ: Exponential backoff retry
    else MAX_RETRY exceeded
        UserService-->>AuthService: Error Response
        deactivate UserService
        
        deactivate AuthService
        AuthService-->>Client: 409 Conflict (duplicate email)
    end
```

Tất cả các diagrams này minh họa:
- ✅ **Fixed race conditions** với Direct Reply-To
- ✅ **Idempotency** với Redis
- ✅ **Resilience** với DLQ và Circuit Breaker
- ✅ **Performance** với Async events
- ✅ **Reliability** với Outbox pattern
