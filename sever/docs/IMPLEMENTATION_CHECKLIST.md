# ✅ Implementation Checklist - RabbitMQ Architecture Improvements

## 📋 Overview

Checklist để track tiến độ implement các cải tiến cho RabbitMQ architecture.

---

## 🎯 Phase 1: Setup & Dependencies (Week 1)

### Infrastructure Setup

- [ ] **Redis Installation**

  - [ ] Install Redis via Docker: `docker run -d -p 6379:6379 --name redis redis:alpine`
  - [ ] Test connection: `docker exec -it redis redis-cli ping`
  - [ ] Setup Redis password (production)
  - [ ] Configure Redis persistence (AOF/RDB)

- [ ] **RabbitMQ Verification**
  - [ ] Access management UI: http://localhost:15672
  - [ ] Verify all queues are healthy
  - [ ] Check for message backlogs
  - [ ] Document current queue structure

### Dependencies

- [ ] **rabbit-common module**

  - [ ] Add spring-boot-starter-data-redis
  - [ ] Add lettuce-core
  - [ ] Add resilience4j-spring-boot3
  - [ ] Add micrometer-registry-prometheus
  - [ ] Run `mvn clean install`

- [ ] **auth-service module**

  - [ ] Add spring-boot-starter-actuator
  - [ ] Add spring-boot-starter-aop
  - [ ] Run `mvn clean install`

- [ ] **user-service module**

  - [ ] Add spring-boot-starter-actuator
  - [ ] Add spring-boot-starter-aop
  - [ ] Add spring-boot-starter-data-jpa (if not exists)
  - [ ] Run `mvn clean install`

- [ ] **cv-service module**
  - [ ] Add spring-boot-starter-actuator
  - [ ] Run `mvn clean install`

### Configuration Files

- [ ] **application.yml updates**

  - [ ] Add Redis configuration (all services)
  - [ ] Add Resilience4j configuration
  - [ ] Add RabbitMQ enhanced settings
  - [ ] Add actuator endpoints configuration

- [ ] **Environment Variables**
  - [ ] Setup REDIS_HOST
  - [ ] Setup REDIS_PORT
  - [ ] Setup REDIS_PASSWORD
  - [ ] Update docker-compose.yml với Redis

---

## 🎯 Phase 2: Core RPC Improvements (Week 2)

### ImprovedRabbitRPCService

- [ ] **Implementation**

  - [ ] Copy ImprovedRabbitRPCService.java to rabbit-common
  - [ ] Verify Direct Reply-To configuration
  - [ ] Test CompletableFuture mechanism
  - [ ] Test timeout handling
  - [ ] Test correlation ID matching

- [ ] **Testing**
  - [ ] Unit test: single request
  - [ ] Unit test: concurrent requests (10+)
  - [ ] Unit test: timeout scenario
  - [ ] Integration test: full RPC flow
  - [ ] Load test: 100+ concurrent requests

### IdempotencyService

- [ ] **Implementation**

  - [ ] Copy IdempotencyService.java to rabbit-common
  - [ ] Test Redis connection
  - [ ] Test SET NX operation
  - [ ] Test TTL configuration
  - [ ] Test cache retrieval

- [ ] **Testing**
  - [ ] Unit test: first request detection
  - [ ] Unit test: duplicate request detection
  - [ ] Unit test: cache result storage
  - [ ] Unit test: TTL expiration
  - [ ] Integration test: concurrent duplicate requests

### Producer Updates

- [ ] **auth-service: UserProducer**

  - [ ] Replace RabbitRPCService with ImprovedRabbitRPCService
  - [ ] Update findUserByEmail() method
  - [ ] Update createUser() method
  - [ ] Add async variants (optional)
  - [ ] Test all methods

- [ ] **cv-service: UserProducer** (if exists)
  - [ ] Same updates as auth-service
  - [ ] Test all methods

---

## 🎯 Phase 3: Consumer Improvements (Week 2)

### UserMessageHandler

- [ ] **Implementation**
  ### UserMessageHandler

````bash
  - [ ] Copy UserMessageHandler.java to user-service
  - [ ] Update handleCreateUser() với idempotency
  - [ ] Update handleActivateUser() với idempotency
  - [ ] Update handleFindByEmail() với logging
  - [ ] Update handleAuthenticateUser() với logging
  - [ ] Add structured logging (correlationId)

- [ ] **Testing**
  - [ ] Test single createUser request
  - [ ] Test duplicate createUser requests
  - [ ] Test concurrent createUser requests (same email)
  - [ ] Test idempotency cache hit
  - [ ] Test error scenarios

### Backward Compatibility
- [ ] **Gradual Migration**
  - [ ] Keep old UserConsumer as backup
  - [ ] Rename: UserConsumer.java → UserConsumer.old.java
  - [ ] Enable UserMessageHandler
  - [ ] Monitor logs for issues
  - [ ] Rollback plan documented

---

## 🎯 Phase 4: Dead Letter Queue (Week 3)

### DLQ Configuration
- [ ] **DeadLetterQueueConfig**
  - [ ] Copy DeadLetterQueueConfig.java to rabbit-common
  - [ ] Create DLX exchange
  - [ ] Create DLQ queues for each operation
  - [ ] Create poison queue
  - [ ] Setup bindings

- [ ] **Queue Updates**
  - [ ] Update user.create.queue với DLX args
  - [ ] Update user.activate.queue với DLX args
  - [ ] Update all other queues với DLX args
  - [ ] Test DLX routing

### DLQ Retry Listener
- [ ] **DLQRetryListener**
  - [ ] Copy DLQRetryListener.java to user-service
  - [ ] Test retry mechanism
  - [ ] Test exponential backoff (5s, 10s, 20s)
  - [ ] Test max retry enforcement
  - [ ] Test poison queue routing

- [ ] **Testing**
  - [ ] Force error in consumer
  - [ ] Verify message goes to DLQ
  - [ ] Verify retry attempts
  - [ ] Verify poison queue after max retry
  - [ ] Test DLQ monitoring

### Monitoring & Alerts
- [ ] **Setup Alerts**
  - [ ] DLQ size threshold alert (> 10 messages)
  - [ ] Poison queue alert (any message)
  - [ ] Email notification setup
  - [ ] Slack webhook setup (optional)

---

## 🎯 Phase 5: Circuit Breaker (Week 3)

### Resilience4j Configuration
- [ ] **application.yml**
  - [ ] Add circuit breaker config for userService
  - [ ] Add circuit breaker config for cvService
  - [ ] Add time limiter config
  - [ ] Add retry config
  - [ ] Test configuration loading

### Circuit Breaker Implementation
- [ ] **auth-service: UserProducer**
  - [ ] Add @CircuitBreaker annotation to methods
  - [ ] Add @TimeLimiter annotation
  - [ ] Implement fallback methods
  - [ ] Test fallback behavior

- [ ] **Testing**
  - [ ] Test circuit CLOSED state (normal)
  - [ ] Test circuit OPEN state (failure)
  - [ ] Test circuit HALF_OPEN state (recovery)
  - [ ] Test fallback method invocation
  - [ ] Test timeout scenarios

### Monitoring
- [ ] **Actuator Endpoints**
  - [ ] Enable circuitbreakers endpoint
  - [ ] Enable circuitbreakerevents endpoint
  - [ ] Test: GET /actuator/circuitbreakers
  - [ ] Test: GET /actuator/health
  - [ ] Setup Prometheus scraping

---

## 🎯 Phase 6: Event-Driven Architecture (Week 4)

### Event Infrastructure
- [ ] **Event Classes**
  - [ ] Copy UserCreatedEvent.java
  - [ ] Create UserActivatedEvent.java (if needed)
  - [ ] Create UserDeletedEvent.java (if needed)
  - [ ] Create CVCreatedEvent.java (if needed)

- [ ] **EventPublisher Service**
  - [ ] Copy EventPublisher.java to rabbit-common
  - [ ] Test publishEvent() method
  - [ ] Test error handling

### Event Exchange Configuration
- [ ] **RabbitMQ Setup**
  - [ ] Create events.exchange (Topic)
  - [ ] Create queue: cv-service.user.created.queue
  - [ ] Create queue: notification-service.user.created.queue (future)
  - [ ] Setup bindings với routing keys
  - [ ] Test fanout behavior

### Event Publishing
- [ ] **user-service: UserService**
  - [ ] Inject EventPublisher
  - [ ] Publish UserCreatedEvent after user creation
  - [ ] Publish UserActivatedEvent after activation
  - [ ] Test event publishing

### Event Consuming
- [ ] **cv-service: UserEventConsumer**
  - [ ] Create UserEventConsumer class
  - [ ] Implement handleUserCreated() method
  - [ ] Auto-create CV template
  - [ ] Test event consumption

- [ ] **Testing**
  - [ ] Test event publishing
  - [ ] Test event routing
  - [ ] Test multiple consumers
  - [ ] Test event failure handling

---

## 🎯 Phase 7: Outbox Pattern (Week 4-5)

### Database Schema
- [ ] **Create Outbox Table**
  ```sql
  CREATE TABLE outbox_events (
    id VARCHAR(255) PRIMARY KEY,
    aggregate_type VARCHAR(50),
    aggregate_id VARCHAR(255),
    event_type VARCHAR(50),
    payload TEXT,
    created_at TIMESTAMP,
    processed_at TIMESTAMP,
    status VARCHAR(20),
    retry_count INT
  );
````

- [ ] Run migration script
- [ ] Create indexes (aggregate_id, status)
- [ ] Verify table structure

### Outbox Entity & Repository

- [ ] **user-service**
  - [ ] Create OutboxEvent.java entity
  - [ ] Create OutboxEventRepository.java
  - [ ] Test repository methods

### Outbox Integration

- [ ] **UserService Updates**
  - [ ] Store events in Outbox table
  - [ ] Use @Transactional for atomicity
  - [ ] Test transactional behavior

### Outbox Processor

- [ ] **OutboxEventProcessor**

  - [ ] Create scheduled job (@Scheduled)
  - [ ] Poll pending events (every 5s)
  - [ ] Publish to EventExchange
  - [ ] Mark as processed
  - [ ] Handle failures

- [ ] **Testing**
  - [ ] Test event storage
  - [ ] Test event processing
  - [ ] Test retry mechanism
  - [ ] Test failure scenarios
  - [ ] Test event ordering

---

## 🎯 Phase 8: Testing & Validation (Week 5)

### Unit Tests

- [ ] **ImprovedRabbitRPCService**

  - [ ] Test sendAndReceiveAsync()
  - [ ] Test handleReply()
  - [ ] Test timeout
  - [ ] Test correlation matching

- [ ] **IdempotencyService**

  - [ ] Test isFirstRequest()
  - [ ] Test getCachedResult()
  - [ ] Test concurrent access

- [ ] **DLQRetryListener**
  - [ ] Test retry logic
  - [ ] Test exponential backoff
  - [ ] Test poison queue

### Integration Tests

- [ ] **Full Register Flow**

  - [ ] Test normal registration
  - [ ] Test duplicate email
  - [ ] Test concurrent registrations
  - [ ] Test event publishing
  - [ ] Test CV creation

- [ ] **Full Login Flow**
  - [ ] Test successful login
  - [ ] Test failed login
  - [ ] Test timeout scenario

### Load Tests

- [ ] **Apache Bench / JMeter**
  - [ ] 100 concurrent requests
  - [ ] 1000 total requests
  - [ ] Measure response times
  - [ ] Measure error rates
  - [ ] Check for race conditions

### Performance Tests

- [ ] **Metrics Collection**
  - [ ] Response time p50, p95, p99
  - [ ] Throughput (req/s)
  - [ ] Error rate
  - [ ] Circuit breaker state changes
  - [ ] DLQ message count

### Benchmark Comparison

- [ ] **Before vs After**
  - [ ] Response time improvement
  - [ ] Concurrent request handling
  - [ ] Error rate reduction
  - [ ] Resource usage (CPU, memory)

---

## 🎯 Phase 9: Monitoring & Observability (Week 6)

### Prometheus & Grafana

- [ ] **Setup Prometheus**

  - [ ] Install Prometheus
  - [ ] Configure scraping endpoints
  - [ ] Test metrics collection

- [ ] **Setup Grafana**
  - [ ] Install Grafana
  - [ ] Add Prometheus datasource
  - [ ] Create dashboard for RabbitMQ metrics
  - [ ] Create dashboard for Circuit Breaker
  - [ ] Create dashboard for DLQ

### Logging

- [ ] **Structured Logging**
  - [ ] All logs have correlationId
  - [ ] Use log levels correctly (INFO, WARN, ERROR)
  - [ ] Add request/response logging
  - [ ] Setup log aggregation (ELK stack optional)

### Alerts

- [ ] **Setup Alerting Rules**
  - [ ] DLQ size > threshold
  - [ ] Circuit breaker OPEN
  - [ ] Error rate > 5%
  - [ ] Response time > 1s
  - [ ] Redis connection issues

---

## 🎯 Phase 10: Documentation & Cleanup (Week 6)

### Documentation

- [ ] **Update README.md**

  - [ ] Architecture diagram
  - [ ] Setup instructions
  - [ ] Configuration guide
  - [ ] Troubleshooting section

- [ ] **API Documentation**

  - [ ] Document all RPC operations
  - [ ] Document all events
  - [ ] Document error codes

- [ ] **Runbook**
  - [ ] How to check system health
  - [ ] How to investigate issues
  - [ ] How to process poison queue
  - [ ] How to manually trigger circuit breaker

### Code Cleanup

- [ ] **Remove Old Code**

  - [ ] Delete old RabbitRPCService
  - [ ] Delete old UserConsumer
  - [ ] Delete shared reply queues config
  - [ ] Clean up unused imports

- [ ] **Code Review**
  - [ ] Review all new code
  - [ ] Check for security issues
  - [ ] Check for performance issues
  - [ ] Verify error handling

### Deployment

- [ ] **Staging Environment**

  - [ ] Deploy to staging
  - [ ] Run smoke tests
  - [ ] Monitor for 24 hours
  - [ ] Fix any issues

- [ ] **Production Deployment**
  - [ ] Create deployment plan
  - [ ] Schedule maintenance window
  - [ ] Deploy to production
  - [ ] Monitor closely
  - [ ] Have rollback plan ready

---

## 📊 Success Metrics

Track these metrics to verify improvements:

### Performance

- [ ] **Response Time**

  - Before: 500-2000ms
  - After: < 200ms
  - Target: ✅ < 200ms (p95)

- [ ] **Throughput**
  - Before: ~10 req/s
  - After: > 100 req/s
  - Target: ✅ > 100 req/s

### Reliability

- [ ] **Error Rate**

  - Before: ~5%
  - After: < 1%
  - Target: ✅ < 1%

- [ ] **Message Loss**
  - Before: Possible
  - After: 0 (with DLQ + Outbox)
  - Target: ✅ 0 message loss

### Scalability

- [ ] **Concurrent Requests**
  - Before: ~10-20
  - After: > 1000
  - Target: ✅ > 500 concurrent

---

## 🚨 Rollback Checklist

If major issues occur:

- [ ] Stop all services
- [ ] Revert code to previous version
- [ ] Clear Redis cache
- [ ] Recreate shared reply queues (if needed)
- [ ] Restart services
- [ ] Monitor logs
- [ ] Verify system health
- [ ] Post-mortem meeting

---

## 📝 Notes

- Update this checklist as you progress
- Mark completed items with [x]
- Document any issues encountered
- Keep team informed of progress
- Celebrate milestones! 🎉

---

## 📞 Support Contacts

- **RabbitMQ Issues**: [Team Lead]
- **Redis Issues**: [DevOps Team]
- **Code Reviews**: [Senior Developer]
- **Production Deployment**: [Release Manager]

---

**Last Updated**: [Date]
**Progress**: [0%] Complete
