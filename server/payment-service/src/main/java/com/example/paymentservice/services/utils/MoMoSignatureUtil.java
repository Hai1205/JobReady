package com.example.paymentservice.services.utils;

import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MoMoSignatureUtil {

    private static final Logger logger = LoggerFactory.getLogger(MoMoSignatureUtil.class);

    public static String createSignature(String secretKey, String rawData) {
        logger.info("=== BẮT ĐẦU TẠO SIGNATURE ===");
        logger.debug("Secret Key: {}", secretKey);
        logger.debug("Raw Data: {}", rawData);

        try {
            String signature = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, secretKey)
                    .hmacHex(rawData);

            logger.info("Tạo signature thành công");
            logger.debug("Signature: {}", signature);
            logger.info("=== KẾT THÚC TẠO SIGNATURE ===");

            return signature;
        } catch (Exception e) {
            logger.error("Lỗi khi tạo signature", e);
            throw new RuntimeException("Không thể tạo signature", e);
        }
    }

    public static boolean verifySignature(String secretKey, String rawData, String signature) {
        logger.info("=== BẮT ĐẦU XÁC THỰC SIGNATURE ===");
        logger.debug("Secret Key: {}", secretKey);
        logger.debug("Raw Data: {}", rawData);
        logger.debug("Signature nhận được: {}", signature);

        try {
            String expectedSignature = createSignature(secretKey, rawData);
            boolean isValid = expectedSignature.equals(signature);

            if (isValid) {
                logger.info("Signature hợp lệ");
            } else {
                logger.warn("Signature không hợp lệ!");
                logger.debug("Signature mong đợi: {}", expectedSignature);
                logger.debug("Signature nhận được: {}", signature);
            }

            logger.info("=== KẾT THÚC XÁC THỰC SIGNATURE ===");
            return isValid;
        } catch (Exception e) {
            logger.error("Lỗi khi xác thực signature", e);
            return false;
        }
    }

    public static String buildRawDataForPayment(String accessKey, Long amount, String extraData,
            String ipnUrl, String orderId, String orderInfo,
            String partnerCode, String redirectUrl,
            String requestId, String requestType) {
        logger.debug("=== Tạo Raw Data cho Payment ===");

        // QUAN TRỌNG: Các tham số PHẢI được sắp xếp theo thứ tự alphabet
        // orderType KHÔNG được đưa vào signature theo yêu cầu của MoMo
        String rawData = "accessKey=" + accessKey +
                "&amount=" + amount +
                "&extraData=" + extraData +
                "&ipnUrl=" + ipnUrl +
                "&orderId=" + orderId +
                "&orderInfo=" + orderInfo +
                "&partnerCode=" + partnerCode +
                "&redirectUrl=" + redirectUrl +
                "&requestId=" + requestId +
                "&requestType=" + requestType;

        logger.debug("Raw Data: {}", rawData);
        return rawData;
    }

    public static String buildRawDataForQuery(String accessKey, String orderId,
            String partnerCode, String requestId) {
        logger.debug("=== Tạo Raw Data cho Query ===");

        String rawData = "accessKey=" + accessKey +
                "&orderId=" + orderId +
                "&partnerCode=" + partnerCode +
                "&requestId=" + requestId;

        logger.debug("Raw Data: {}", rawData);
        return rawData;
    }

    public static String buildRawDataForIPN(String accessKey, Long amount, String extraData,
            String message, String orderId, String orderInfo,
            String orderType, String partnerCode, String payType,
            String requestId, Long responseTime, Integer resultCode,
            Long transId) {
        logger.debug("=== Tạo Raw Data cho IPN Verification ===");

        String rawData = "accessKey=" + accessKey +
                "&amount=" + amount +
                "&extraData=" + extraData +
                "&message=" + message +
                "&orderId=" + orderId +
                "&orderInfo=" + orderInfo +
                "&orderType=" + orderType +
                "&partnerCode=" + partnerCode +
                "&payType=" + payType +
                "&requestId=" + requestId +
                "&responseTime=" + responseTime +
                "&resultCode=" + resultCode +
                "&transId=" + transId;

        logger.debug("Raw Data: {}", rawData);
        return rawData;
    }
}
