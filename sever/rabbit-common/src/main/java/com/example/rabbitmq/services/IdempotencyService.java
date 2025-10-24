package com.example.rabbitmq.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

/**
 * Service để xử lý Idempotency với Redis
 * 
 * Giải quyết vấn đề:
 * - Duplicate requests (cùng correlationId)
 * - Race condition khi concurrent requests
 * - Retry requests (network issues)
 * 
 * Cách hoạt động:
 * 1. Client gửi request với unique idempotency key (thường là correlationId)
 * 2. Service check Redis xem key đã tồn tại chưa
 * 3. Nếu chưa: xử lý request, cache result
 * 4. Nếu rồi: trả về cached result ngay lập tức
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IdempotencyService {

    private final StringRedisTemplate redisTemplate;

    private static final String IDEMPOTENCY_PREFIX = "idempotency:";
    private static final String PROCESSING_VALUE = "PROCESSING";
    private static final long DEFAULT_EXPIRATION_HOURS = 24;

    /**
     * Check xem đây có phải request đầu tiên không
     * Dùng Redis SET NX (set if not exists) để atomic check-and-set
     * 
     * @param key   Idempotency key (thường là correlationId)
     * @param value Initial value (thường là "PROCESSING")
     * @return true nếu đây là request đầu tiên, false nếu đã có request trước
     */
    public boolean isFirstRequest(String key, String value) {
        String redisKey = IDEMPOTENCY_PREFIX + key;

        Boolean success = redisTemplate.opsForValue()
                .setIfAbsent(redisKey, value, Duration.ofHours(DEFAULT_EXPIRATION_HOURS));

        boolean isFirst = Boolean.TRUE.equals(success);

        if (isFirst) {
            log.debug("🔐 New idempotency key acquired: {}", key);
        } else {
            log.info("♻️ Duplicate request detected for key: {}", key);
        }

        return isFirst;
    }

    /**
     * Overload với default "PROCESSING" value
     */
    public boolean isFirstRequest(String key) {
        return isFirstRequest(key, PROCESSING_VALUE);
    }

    /**
     * Lấy cached result từ Redis
     * 
     * @param key Idempotency key
     * @return Optional chứa cached result, empty nếu chưa có
     */
    public Optional<String> getCachedResult(String key) {
        String redisKey = IDEMPOTENCY_PREFIX + key;
        String value = redisTemplate.opsForValue().get(redisKey);

        if (value != null && !PROCESSING_VALUE.equals(value)) {
            log.debug("📦 Cache hit for key: {}", key);
            return Optional.of(value);
        }

        return Optional.empty();
    }

    /**
     * Check xem request có đang được xử lý không
     * 
     * @param key Idempotency key
     * @return true nếu đang xử lý (value = "PROCESSING")
     */
    public boolean isProcessing(String key) {
        String redisKey = IDEMPOTENCY_PREFIX + key;
        String value = redisTemplate.opsForValue().get(redisKey);
        return PROCESSING_VALUE.equals(value);
    }

    /**
     * Update result sau khi xử lý xong
     * 
     * @param key    Idempotency key
     * @param result Result data (JSON string)
     */
    public void updateResult(String key, String result) {
        String redisKey = IDEMPOTENCY_PREFIX + key;

        redisTemplate.opsForValue().set(
                redisKey,
                result,
                Duration.ofHours(DEFAULT_EXPIRATION_HOURS));

        log.debug("💾 Result cached for key: {}", key);
    }

    /**
     * Update result với custom TTL
     * 
     * @param key       Idempotency key
     * @param result    Result data
     * @param ttlHours  TTL in hours
     */
    public void updateResult(String key, String result, long ttlHours) {
        String redisKey = IDEMPOTENCY_PREFIX + key;

        redisTemplate.opsForValue().set(
                redisKey,
                result,
                Duration.ofHours(ttlHours));

        log.debug("💾 Result cached for key: {} with TTL: {}h", key, ttlHours);
    }

    /**
     * Mark request as failed
     * Store error message để tránh retry liên tục
     * 
     * @param key   Idempotency key
     * @param error Error message
     */
    public void markAsFailed(String key, String error) {
        String redisKey = IDEMPOTENCY_PREFIX + key;
        String errorValue = "ERROR:" + error;

        // Shorter TTL cho failed requests (1 hour)
        redisTemplate.opsForValue().set(
                redisKey,
                errorValue,
                Duration.ofHours(1));

        log.warn("❌ Request marked as failed for key: {}", key);
    }

    /**
     * Get error message nếu request đã failed
     * 
     * @param key Idempotency key
     * @return Optional chứa error message
     */
    public Optional<String> getErrorMessage(String key) {
        String redisKey = IDEMPOTENCY_PREFIX + key;
        String value = redisTemplate.opsForValue().get(redisKey);

        if (value != null && value.startsWith("ERROR:")) {
            return Optional.of(value.substring(6)); // Remove "ERROR:" prefix
        }

        return Optional.empty();
    }

    /**
     * Xóa idempotency key (dùng cho testing hoặc manual cleanup)
     * 
     * @param key Idempotency key
     * @return true nếu xóa thành công
     */
    public boolean delete(String key) {
        String redisKey = IDEMPOTENCY_PREFIX + key;
        Boolean deleted = redisTemplate.delete(redisKey);
        return Boolean.TRUE.equals(deleted);
    }

    /**
     * Check xem key có tồn tại không
     * 
     * @param key Idempotency key
     * @return true nếu tồn tại
     */
    public boolean exists(String key) {
        String redisKey = IDEMPOTENCY_PREFIX + key;
        Boolean exists = redisTemplate.hasKey(redisKey);
        return Boolean.TRUE.equals(exists);
    }

    /**
     * Get TTL remaining của key (in seconds)
     * 
     * @param key Idempotency key
     * @return TTL in seconds, -1 nếu không có TTL, -2 nếu key không tồn tại
     */
    public long getTTL(String key) {
        String redisKey = IDEMPOTENCY_PREFIX + key;
        Long ttl = redisTemplate.getExpire(redisKey);
        return ttl != null ? ttl : -2;
    }
}
