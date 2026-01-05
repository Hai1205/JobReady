package com.example.paymentservice.services.apis;

import com.example.paymentservice.dtos.*;
import com.example.paymentservice.dtos.RevenueStatsDto.DailyRevenueDto;
import com.example.paymentservice.dtos.RevenueStatsDto.MonthlyRevenueDto;
import com.example.paymentservice.dtos.requests.*;
import com.example.paymentservice.dtos.requests.vnpay.VNPayPaymentRequest;
import com.example.paymentservice.dtos.requests.momo.*;
import com.example.paymentservice.dtos.requests.paypal.PayPalPaymentRequest;
import com.example.paymentservice.dtos.response.Response;
import com.example.paymentservice.dtos.response.vnpay.VNPayPaymentResponse;
import com.example.paymentservice.dtos.response.momo.*;
import com.example.paymentservice.dtos.response.paypal.*;
import com.example.paymentservice.entities.Invoice.InvoiceStatus;
import com.example.paymentservice.exceptions.OurException;
import com.example.paymentservice.mappers.InvoiceMapper;
import com.example.paymentservice.repositories.*;
import com.example.paymentservice.configs.*;
import com.example.paymentservice.services.utils.*;
import com.example.paymentservice.services.feigns.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paypal.api.payments.*;
import com.paypal.base.rest.*;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.*;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.io.entity.*;
import org.apache.hc.core5.http.ContentType;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.example.paymentservice.entities.Invoice;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PaymentApi extends BaseApi {

    private final SimpleInvoiceRepository simpleInvoiceRepository;
    private final InvoiceQueryRepository invoiceQueryRepository;
    private final InvoiceCommandRepository invoiceCommandRepository;
    private final InvoiceMapper invoiceMapper;
    private final ObjectMapper objectMapper;
    private final MoMoConfig moMoConfig;
    private final PayPalConfig payPalConfig;
    private final VNPayConfig vnPayConfig;
    private final UserFeignClient userFeignClient;

    public PaymentApi(
            SimpleInvoiceRepository simpleInvoiceRepository,
            InvoiceQueryRepository invoiceQueryRepository,
            InvoiceCommandRepository invoiceCommandRepository,
            InvoiceMapper invoiceMapper,
            MoMoConfig moMoConfig,
            PayPalConfig payPalConfig,
            VNPayConfig vnPayConfig,
            UserFeignClient userFeignClient) {
        this.simpleInvoiceRepository = simpleInvoiceRepository;
        this.invoiceQueryRepository = invoiceQueryRepository;
        this.invoiceCommandRepository = invoiceCommandRepository;
        this.invoiceMapper = invoiceMapper;
        this.moMoConfig = moMoConfig;
        this.payPalConfig = payPalConfig;
        this.vnPayConfig = vnPayConfig;
        this.userFeignClient = userFeignClient;
        this.objectMapper = new ObjectMapper();
    }

    public List<InvoiceDto> handleGetAllInvoices() {
        try {
            return invoiceQueryRepository.findAllInvoices(Pageable.unpaged()).stream()
                    .map(invoiceMapper::toDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error in handleGetAllInvoices: {}", e.getMessage(), e);
            throw new OurException("Failed to retrieve invoices", 500);
        }
    }

    public Response getUserInvoices(UUID userId) {
        Response response = new Response();

        try {
            List<InvoiceDto> invoiceDtos = handleGetUserInvoices(userId);
            response.setMessage("Invoices retrieved successfully");
            response.setInvoices(invoiceDtos);
            return response;
        } catch (OurException e) {
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return buildErrorResponse(500, e.getMessage());
        }
    }

    public List<InvoiceDto> handleGetUserInvoices(UUID userId) {
        try {
            return invoiceQueryRepository.findByUserId(userId).stream()
                    .map(invoiceMapper::toDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error in handleGetAllInvoices: {}", e.getMessage(), e);
            throw new OurException("Failed to retrieve invoices", 500);
        }
    }

    public Response getAllInvoices() {
        Response response = new Response();

        try {
            List<InvoiceDto> invoiceDtos = handleGetAllInvoices();

            response.setMessage("Invoices retrieved successfully");
            response.setInvoices(invoiceDtos);
            return response;
        } catch (OurException e) {
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return buildErrorResponse(500, e.getMessage());
        }
    }

    public InvoiceDto handleGetInvoiceById(UUID invoiceId) {
        try {
            Invoice invoice = invoiceQueryRepository.findInvoiceById(invoiceId)
                    .orElseThrow(() -> new OurException("Invoice not found", 404));
            return invoiceMapper.toDto(invoice);
        } catch (OurException e) {
            logger.error("Error in handleGetInvoiceById: {}", e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error in handleGetInvoiceById: {}", e.getMessage(), e);
            throw new OurException("Failed to retrieve user", 500);
        }
    }

    public Response getInvoiceById(UUID invoiceId) {
        Response response = new Response();

        try {
            InvoiceDto invoiceDto = handleGetInvoiceById(invoiceId);
            response.setMessage("Invoice retrieved successfully");
            response.setInvoice(invoiceDto);
            return response;
        } catch (OurException e) {
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return buildErrorResponse(500, e.getMessage());
        }
    }

    public Response createVnPayPayment(UUID userId, String dataJson) {
        logger.info("Creating new VNPay payment");
        Response response = new Response();

        try {
            CreatePaymentRequest request = objectMapper.readValue(dataJson, CreatePaymentRequest.class);
            String planTitle = request.getPlanTitle();
            Long price = request.getPrice();

            VNPayPaymentResponse res = handleCreateVnPayPayment(userId, price, planTitle);

            response.setStatusCode(201);
            response.setMessage("VNPay payment created successfully");
            response.setPaymentUrl(res.getPaymentUrl());
            response.setOrderId(res.getOrderId());
            response.setTxnRef(res.getTxnRef());
            logger.info("VNPay payment creation completed successfully");
            return response;
        } catch (OurException e) {
            logger.error("VNPay payment creation failed with OurException: {}", e.getMessage());
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            logger.error("VNPay payment creation failed with unexpected error", e);
            e.printStackTrace();
            return buildErrorResponse(500, e.getMessage());
        }
    }

    public VNPayPaymentResponse handleCreateVnPayPayment(UUID userId, Long price, String planTitle) {
        try {
            String orderInfo = "Payment for plan " + planTitle;

            if (price == null || price <= 0) {
                throw new OurException("Số tiền không hợp lệ", 400);
            }

            // Tạo txnRef (order ID)
            String txnRef = "VNP_" + System.currentTimeMillis();

            // Tạo các tham số
            Map<String, String> vnpParams = new TreeMap<>(); // Dùng TreeMap để tự động sort
            vnpParams.put("vnp_Version", vnPayConfig.getVersion());
            vnpParams.put("vnp_Command", vnPayConfig.getCommand());
            vnpParams.put("vnp_TmnCode", vnPayConfig.getTmnCode());
            vnpParams.put("vnp_Amount", String.valueOf(price * 100)); // VNPay yêu cầu amount * 100
            vnpParams.put("vnp_CurrCode", "VND");
            vnpParams.put("vnp_TxnRef", txnRef);
            vnpParams.put("vnp_OrderInfo", orderInfo);
            vnpParams.put("vnp_OrderType", vnPayConfig.getOrderType());
            // vnpParams.put("vnp_OrderType",
            // request.getOrderType() != null ? request.getOrderType() :
            // vnPayConfig.getOrderType());
            // vnpParams.put("vnp_Locale", request.getLocale() != null ? request.getLocale()
            // : "vn");
            vnpParams.put("vnp_Locale", "vn");
            vnpParams.put("vnp_ReturnUrl", vnPayConfig.getReturnUrl());
            vnpParams.put("vnp_IpAddr", "127.0.0.1");

            // Thêm thời gian
            Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String vnpCreateDate = formatter.format(cld.getTime());
            vnpParams.put("vnp_CreateDate", vnpCreateDate);

            cld.add(Calendar.MINUTE, 15); // Hết hạn sau 15 phút
            String vnpExpireDate = formatter.format(cld.getTime());
            vnpParams.put("vnp_ExpireDate", vnpExpireDate);

            logger.info("TxnRef: {}", txnRef);
            logger.info("Amount: {} VND (sent as: {})", price, price * 100);
            logger.info("Order Info: {}", orderInfo);
            logger.info("TMN Code: {}", vnPayConfig.getTmnCode());
            logger.info("Hash Secret: {}", handleMaskSecret(vnPayConfig.getHashSecret()));
            logger.info("Return URL: {}", vnPayConfig.getReturnUrl());
            logger.info("Create Date: {}", vnpCreateDate);
            logger.info("Expire Date: {}", vnpExpireDate);

            // Log all params (sorted)
            logger.info("\nAll Params (sorted):");
            vnpParams.forEach((key, value) -> logger.info("   {} = {}", key, value));

            // Tạo hash data (KHÔNG encode)
            String hashData = VNPayUtil.buildHashData(vnpParams);
            logger.info("\nHash Data (RAW - KHÔNG encode):");
            logger.info("   {}", hashData);

            // Tạo secure hash
            String secureHash = VNPayUtil.hmacSHA512(vnPayConfig.getHashSecret(), hashData);
            logger.info("\nSecure Hash: {}", secureHash);

            // Build query string (CÓ encode)
            String queryString = VNPayUtil.buildQueryString(vnpParams);
            logger.info("\nQuery String (encoded):");
            logger.info("   {}", queryString);

            // Add vnp_SecureHash vào cuối URL
            String paymentUrl = vnPayConfig.getPaymentUrl() + "?" + queryString + "&vnp_SecureHash=" + secureHash;

            logger.info("\nTạo payment URL thành công!");
            logger.info("Payment URL:");
            logger.info("   {}", paymentUrl);

            // Lưu invoice vào database
            Invoice invoice = invoiceCommandRepository.insertInvoice(
                    userId,
                    planTitle,
                    price,
                    "VND",
                    InvoiceStatus.pending,
                    "VNPay",
                    txnRef,
                    LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    orderInfo);
            logger.info("Invoice saved with ID: {}", invoice.getId());

            return VNPayPaymentResponse.builder()
                    .paymentUrl(paymentUrl)
                    .orderId(txnRef)
                    .txnRef(txnRef)
                    .build();

        } catch (Exception e) {
            logger.error("Lỗi khi tạo thanh toán VNPay", e);
            throw new RuntimeException("Lỗi khi tạo thanh toán VNPay", e);
        }
    }

    public Response verifyVnPayCallback(Map<String, String> params) {
        logger.info("Verifying VNPay callback");
        Response response = new Response();

        try {
            boolean isValid = handleVerifyVnPayCallback(params);

            response.setStatusCode(200);
            response.setSuccess(isValid);
            if (isValid) {
                response.setMessage("VNPay callback verified successfully");
            } else {
                response.setMessage("VNPay callback verification failed - invalid signature");
            }
            logger.info("VNPay callback verification completed. Valid: {}", isValid);
            return response;
        } catch (OurException e) {
            logger.error("VNPay callback verification failed with OurException: {}", e.getMessage());
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            logger.error("VNPay callback verification failed with unexpected error", e);
            e.printStackTrace();
            return buildErrorResponse(500, e.getMessage());
        }
    }

    public boolean handleVerifyVnPayCallback(Map<String, String> params) {
        try {
            String vnpSecureHash = params.get("vnp_SecureHash");
            logger.info("VNPay Secure Hash: {}", vnpSecureHash);

            // Remove hash params
            Map<String, String> verifyParams = new TreeMap<>(params);
            verifyParams.remove("vnp_SecureHash");
            verifyParams.remove("vnp_SecureHashType");

            // Log params
            logger.info("\nCallback Params (sorted):");
            verifyParams.forEach((key, value) -> logger.info("   {} = {}", key, value));

            // Build hash data
            String hashData = VNPayUtil.buildHashData(verifyParams);
            logger.info("\nHash Data for verification:");
            logger.info("   {}", hashData);

            // Calculate hash
            String calculatedHash = VNPayUtil.hmacSHA512(vnPayConfig.getHashSecret(), hashData);
            logger.info("\nCalculated Hash: {}", calculatedHash);
            logger.info("VNPay Hash:      {}", vnpSecureHash);

            boolean isValid = calculatedHash.equalsIgnoreCase(vnpSecureHash);
            logger.info("\nSignature Valid: {}", isValid ? "YES" : "NO");

            // Cập nhật trạng thái invoice nếu signature hợp lệ
            if (isValid) {
                String txnRef = params.get("vnp_TxnRef");
                String responseCode = params.get("vnp_ResponseCode");

                InvoiceStatus newStatus = "00".equals(responseCode) ? InvoiceStatus.paid : InvoiceStatus.failed;
                int updated = invoiceCommandRepository.updateStatusByTransactionId(txnRef, newStatus);

                if (updated > 0) {
                    logger.info("Invoice updated to {} for txnRef: {}", newStatus, txnRef);

                    // Update user plan if payment success
                    if ("00".equals(responseCode)) {
                        try {
                            Invoice invoice = invoiceQueryRepository.findByTransactionId(txnRef)
                                    .orElse(null);
                            if (invoice != null) {
                                handleUpdateUserPlanAfterPayment(invoice);
                            }
                        } catch (Exception e) {
                            logger.error("Failed to update user plan after VNPay payment", e);
                        }
                    }
                } else {
                    logger.warn("Invoice not found for txnRef: {}", txnRef);
                }
            }

            return isValid;
        } catch (Exception e) {
            logger.error("Error verifying VNPay callback", e);
            return false;
        }
    }

    private String handleMaskSecret(String secret) {
        if (secret == null || secret.length() < 8) {
            return "****";
        }
        return secret.substring(0, 4) + "****" + secret.substring(secret.length() - 4);
    }

    public Response createMoMoPayment(UUID userId, String dataJson) {
        logger.info("Creating new MoMo payment");
        Response response = new Response();

        try {
            CreatePaymentRequest request = objectMapper.readValue(dataJson, CreatePaymentRequest.class);
            String planTitle = request.getPlanTitle();
            Long price = request.getPrice();

            MoMoPaymentResponse res = handleCreateMoMoPayment(userId, planTitle, price);

            response.setStatusCode(201);
            response.setMessage("MoMo payment created successfully");
            response.setSuccess(res.getResultCode() == 0);
            response.setPaymentUrl(res.getPayUrl());
            response.setOrderId(res.getOrderId());
            logger.info("MoMo payment creation completed successfully. Order ID: {}", res.getOrderId());
            return response;
        } catch (OurException e) {
            logger.error("MoMo payment creation failed with OurException: {}", e.getMessage());
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            logger.error("MoMo payment creation failed with unexpected error", e);
            e.printStackTrace();
            return buildErrorResponse(500, e.getMessage());
        }
    }

    public MoMoPaymentResponse handleCreateMoMoPayment(UUID userId, String planTitle, Long price) {
        try {
            // 1: Tạo các thông tin cần thiết
            logger.info("1: Chuẩn bị thông tin thanh toán");
            String requestId = UUID.randomUUID().toString();
            String orderId = "ORDER_" + System.currentTimeMillis();

            logger.info("Request ID: {}", requestId);
            logger.info("Order ID: {}", orderId);
            logger.info("Số tiền: {} VND", price);
            logger.info("Thông tin đơn hàng: {}", planTitle);
            logger.info("Extra Data: {}", planTitle);

            // 2: Tạo raw data để ký
            logger.info("\n2: Tạo chữ ký số (Signature)");
            String orderType = "momo_wallet";
            String rawData = MoMoSignatureUtil.buildRawDataForPayment(
                    moMoConfig.getAccessKey(),
                    price,
                    planTitle,
                    moMoConfig.getIpnUrl(),
                    orderId,
                    planTitle,
                    moMoConfig.getPartnerCode(),
                    moMoConfig.getRedirectUrl(),
                    requestId,
                    moMoConfig.getRequestType());

            logger.info("Partner Code: {}", moMoConfig.getPartnerCode());
            logger.info("Access Key: {}", moMoConfig.getAccessKey());
            logger.info("Redirect URL: {}", moMoConfig.getRedirectUrl());
            logger.info("IPN URL: {}", moMoConfig.getIpnUrl());
            logger.info("Request Type: {}", moMoConfig.getRequestType());

            // 3: Tạo signature
            String signature = MoMoSignatureUtil.createSignature(moMoConfig.getSecretKey(), rawData);
            logger.info("Signature đã tạo: {}", signature);

            // 4: Tạo payment request object
            logger.info("\n3: Tạo Payment Request Object");
            MoMoPaymentRequest paymentRequest = MoMoPaymentRequest.builder()
                    .partnerCode(moMoConfig.getPartnerCode())
                    .accessKey(moMoConfig.getAccessKey())
                    .requestId(requestId)
                    .amount(price)
                    .orderId(orderId)
                    .orderInfo(planTitle)
                    .redirectUrl(moMoConfig.getRedirectUrl())
                    .ipnUrl(moMoConfig.getIpnUrl())
                    .requestType(moMoConfig.getRequestType())
                    .extraData(planTitle)
                    .orderType(orderType)
                    .lang("vi")
                    .signature(signature)
                    .build();

            String jsonRequest = objectMapper.writeValueAsString(paymentRequest);
            logger.debug("JSON Request: {}", jsonRequest);

            // 5: Gửi request đến MoMo
            logger.info("\n4: Gửi request đến MoMo API");
            logger.info("Payment URL: {}", moMoConfig.getPaymentUrl());

            MoMoPaymentResponse response = handleSendMoMoHttpRequest(
                    moMoConfig.getPaymentUrl(),
                    jsonRequest,
                    MoMoPaymentResponse.class);

            // 6: Xử lý response
            logger.info("\n5: Nhận và xử lý response từ MoMo");
            logger.info("Result Code: {}", response.getResultCode());
            logger.info("Message: {}", response.getMessage());

            if (response.getResultCode() == 0) {
                logger.info("Tạo thanh toán THÀNH CÔNG!");
                logger.info("Pay URL: {}", response.getPayUrl());
                logger.info("QR Code URL: {}", response.getQrCodeUrl());
                logger.info("Deeplink: {}", response.getDeeplink());

                // Lưu invoice vào database
                UUID invoiceId = UUID.randomUUID();
                invoiceCommandRepository.insertInvoice(
                        userId,
                        planTitle,
                        price,
                        "VND",
                        InvoiceStatus.pending,
                        "MoMo",
                        orderId,
                        LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                        planTitle);
                logger.info("Invoice saved with ID: {}", invoiceId);
            } else {
                logger.error("Tạo thanh toán THẤT BẠI!");
                logger.error("Result Code: {}", response.getResultCode());
                logger.error("Message: {}", response.getMessage());
            }

            return response;

        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo thanh toán MoMo", e);
        }
    }

    public Response createMoMoIPN(String dataJson) {
        logger.info("Processing MoMo IPN");
        Response response = new Response();

        try {
            MoMoIPNRequest ipnRequest = objectMapper.readValue(dataJson, MoMoIPNRequest.class);
            MoMoIPNResponse res = handleCreateMoMoIPN(ipnRequest);

            response.setStatusCode(200);
            response.setMessage("MoMo IPN processed successfully");
            response.setSuccess(res.getResultCode() == 0);
            response.setOrderId(res.getOrderId());
            logger.info("MoMo IPN processing completed. Order ID: {}", res.getOrderId());
            return response;
        } catch (OurException e) {
            logger.error("MoMo IPN processing failed with OurException: {}", e.getMessage());
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            logger.error("MoMo IPN processing failed with unexpected error", e);
            e.printStackTrace();
            return buildErrorResponse(500, e.getMessage());
        }
    }

    public MoMoIPNResponse handleCreateMoMoIPN(MoMoIPNRequest ipnRequest) {
        try {
            // 1: Log thông tin IPN nhận được
            logger.info("1: Thông tin IPN nhận được");
            logger.info("Partner Code: {}", ipnRequest.getPartnerCode());
            logger.info("Order ID: {}", ipnRequest.getOrderId());
            logger.info("Request ID: {}", ipnRequest.getRequestId());
            logger.info("Amount: {} VND", ipnRequest.getAmount());
            logger.info("Order Info: {}", ipnRequest.getOrderInfo());
            logger.info("Trans ID: {}", ipnRequest.getTransId());
            logger.info("Result Code: {}", ipnRequest.getResultCode());
            logger.info("Message: {}", ipnRequest.getMessage());
            logger.info("Pay Type: {}", ipnRequest.getPayType());
            logger.info("Response Time: {}", ipnRequest.getResponseTime());

            // 2: Verify signature
            logger.info("\n2: Xác thực chữ ký từ MoMo");
            String rawData = MoMoSignatureUtil.buildRawDataForIPN(
                    moMoConfig.getAccessKey(),
                    ipnRequest.getAmount(),
                    ipnRequest.getExtraData() != null ? ipnRequest.getExtraData() : "",
                    ipnRequest.getMessage(),
                    ipnRequest.getOrderId(),
                    ipnRequest.getOrderInfo(),
                    ipnRequest.getOrderType(),
                    ipnRequest.getPartnerCode(),
                    ipnRequest.getPayType(),
                    ipnRequest.getRequestId(),
                    ipnRequest.getResponseTime(),
                    ipnRequest.getResultCode(),
                    ipnRequest.getTransId());

            boolean isValidSignature = MoMoSignatureUtil.verifySignature(
                    moMoConfig.getSecretKey(),
                    rawData,
                    ipnRequest.getSignature());

            if (!isValidSignature) {
                logger.error("Chữ ký không hợp lệ! IPN có thể bị giả mạo!");
                return handleBuildMoMoIPNResponse(ipnRequest, 1, "Invalid signature");
            }

            logger.info("Chữ ký hợp lệ");

            // 3: Xử lý kết quả thanh toán
            logger.info("\n3: Xử lý kết quả thanh toán");
            if (ipnRequest.getResultCode() == 0) {
                logger.info("THANH TOÁN THÀNH CÔNG!");
                logger.info("Order ID: {}", ipnRequest.getOrderId());
                logger.info("Trans ID: {}", ipnRequest.getTransId());
                logger.info("Amount: {} VND", ipnRequest.getAmount());
                logger.info("Pay Type: {}", ipnRequest.getPayType());

                // Cập nhật invoice trong database
                int updated = invoiceCommandRepository.updateStatusByTransactionId(
                        ipnRequest.getOrderId(),
                        InvoiceStatus.paid);

                if (updated > 0) {
                    logger.info("Invoice updated to PAID for orderId: {}", ipnRequest.getOrderId());

                    // Update user plan after payment success
                    try {
                        Invoice invoice = invoiceQueryRepository.findByTransactionId(ipnRequest.getOrderId())
                                .orElse(null);
                        if (invoice != null) {
                            handleUpdateUserPlanAfterPayment(invoice);
                        }
                    } catch (Exception e) {
                        logger.error("Failed to update user plan after payment", e);
                    }
                } else {
                    logger.warn("Invoice not found for orderId: {}", ipnRequest.getOrderId());
                }

            } else {
                logger.warn("THANH TOÁN THẤT BẠI!");
                logger.warn("Result Code: {}", ipnRequest.getResultCode());
                logger.warn("Message: {}", ipnRequest.getMessage());

                // Cập nhật invoice thất bại trong database
                int updated = invoiceCommandRepository.updateStatusByTransactionId(
                        ipnRequest.getOrderId(),
                        InvoiceStatus.failed);

                if (updated > 0) {
                    logger.info("Invoice updated to FAILED for orderId: {}", ipnRequest.getOrderId());
                } else {
                    logger.warn("Invoice not found for orderId: {}", ipnRequest.getOrderId());
                }
            }

            // 4: Tạo response trả về cho MoMo
            logger.info("\n4: Tạo response trả về cho MoMo");
            MoMoIPNResponse response = handleBuildMoMoIPNResponse(ipnRequest, 0, "Success");

            logger.info("Result Code: {}", response.getResultCode());
            logger.info("Message: {}", response.getMessage());

            return response;

        } catch (Exception e) {
            logger.error("Exception: {}", e.getMessage(), e);
            return handleBuildMoMoIPNResponse(ipnRequest, 1, "Internal error");
        }
    }

    public Response queryMoMoTransaction(String orderId) {
        logger.info("Querying MoMo transaction");
        Response response = new Response();

        try {
            MoMoQueryResponse res = handleQueryMoMoTransaction(orderId);

            response.setStatusCode(200);
            response.setMessage("MoMo transaction queried successfully");
            response.setSuccess(res.getResultCode() == 0);
            response.setOrderId(res.getOrderId());
            logger.info("MoMo transaction query completed. Order ID: {}", res.getOrderId());
            return response;
        } catch (OurException e) {
            logger.error("MoMo transaction query failed with OurException: {}", e.getMessage());
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            logger.error("MoMo transaction query failed with unexpected error", e);
            e.printStackTrace();
            return buildErrorResponse(500, e.getMessage());
        }
    }

    public MoMoQueryResponse handleQueryMoMoTransaction(String orderId) {
        try {
            // 1: Chuẩn bị thông tin query
            logger.info("1: Chuẩn bị thông tin query");
            String requestId = UUID.randomUUID().toString();

            logger.info("Order ID: {}", orderId);
            logger.info("Request ID: {}", requestId);

            // 2: Tạo signature
            logger.info("\n2: Tạo chữ ký cho query request");
            String rawData = MoMoSignatureUtil.buildRawDataForQuery(
                    moMoConfig.getAccessKey(),
                    orderId,
                    moMoConfig.getPartnerCode(),
                    requestId);

            String signature = MoMoSignatureUtil.createSignature(moMoConfig.getSecretKey(), rawData);
            logger.info("Signature: {}", signature);

            // 3: Tạo query request
            logger.info("\n3: Tạo Query Request Object");
            MoMoQueryRequest queryRequest = MoMoQueryRequest.builder()
                    .partnerCode(moMoConfig.getPartnerCode())
                    .accessKey(moMoConfig.getAccessKey())
                    .requestId(requestId)
                    .orderId(orderId)
                    .lang("vi")
                    .signature(signature)
                    .build();

            String jsonRequest = objectMapper.writeValueAsString(queryRequest);
            logger.debug("JSON Request: {}", jsonRequest);

            // 4: Gửi request
            logger.info("\n4: Gửi query request đến MoMo API");
            logger.info("Query URL: {}", moMoConfig.getQueryUrl());

            MoMoQueryResponse response = handleSendMoMoHttpRequest(
                    moMoConfig.getQueryUrl(),
                    jsonRequest,
                    MoMoQueryResponse.class);

            // 5: Xử lý response
            logger.info("\n5: Nhận và xử lý response");
            logger.info("Result Code: {}", response.getResultCode());
            logger.info("Message: {}", response.getMessage());

            if (response.getResultCode() == 0) {
                logger.info("Query THÀNH CÔNG!");
                logger.info("Trans ID: {}", response.getTransId());
                logger.info("Amount: {} VND", response.getAmount());
                logger.info("Pay Type: {}", response.getPayType());
            } else {
                logger.warn("Query THẤT BẠI hoặc giao dịch không tồn tại");
                logger.warn("Result Code: {}", response.getResultCode());
                logger.warn("Message: {}", response.getMessage());
            }

            return response;

        } catch (Exception e) {
            logger.error("Exception: {}", e.getMessage(), e);
            throw new RuntimeException("Lỗi khi query giao dịch MoMo", e);
        }
    }

    private <T> T handleSendMoMoHttpRequest(String url, String jsonBody, Class<T> responseType) throws Exception {
        logger.debug("Preparing HTTP POST request");
        logger.debug("URL: {}", url);
        logger.debug("Request Body: {}", jsonBody);

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setEntity(new StringEntity(jsonBody, ContentType.APPLICATION_JSON));

            logger.debug("Sending request...");

            HttpClientContext context = HttpClientContext.create();
            try (CloseableHttpResponse response = httpClient.execute(httpPost, context)) {
                int statusCode = response.getCode();
                String responseBody = EntityUtils.toString(response.getEntity(), "UTF-8");

                logger.debug("HTTP Status Code: {}", statusCode);
                logger.debug("Response Body: {}", responseBody);

                if (statusCode == 200) {
                    logger.debug("HTTP request successful");
                    return objectMapper.readValue(responseBody, responseType);
                } else {
                    logger.error("HTTP request failed with status code: {}", statusCode);
                    throw new RuntimeException("HTTP request failed: " + statusCode);
                }
            }
        }
    }

    private void handleUpdateUserPlanAfterPayment(Invoice invoice) {
        try {
            logger.info("Updating user plan after successful payment for userId: {}", invoice.getUserId());

            // Extract plan type from planTitle
            String planType = extractPlanTypeFromTitle(invoice.getPlanTitle());

            // Create update plan request - server will auto-set expiration to 30 days from
            // now
            // unless plan is free (no expiration)
            Map<String, Object> updatePlanData = new HashMap<>();
            updatePlanData.put("planType", planType);
            // Let server handle auto-setting expiration

            String updatePlanJson = objectMapper.writeValueAsString(updatePlanData);

            // Call user service to update plan
            Response response = userFeignClient.updateUserPlan(invoice.getUserId(), updatePlanJson);

            if (response.getStatusCode() == 200 || response.getStatusCode() == 201) {
                logger.info("User plan updated successfully after payment for userId: {}", invoice.getUserId());
            } else {
                logger.warn("Failed to update user plan after payment for userId: {}. Response: {}",
                        invoice.getUserId(), response.getMessage());
            }
        } catch (Exception e) {
            logger.error("Error updating user plan after payment for userId: {}", invoice.getUserId(), e);
            // Log but don't throw - payment already succeeded
        }
    }

    private String extractPlanTypeFromTitle(String planTitle) {
        if (planTitle == null) {
            return "free";
        }

        String title = planTitle.toLowerCase().trim();
        if (title.contains("ultra")) {
            return "ultra";
        } else if (title.contains("pro")) {
            return "pro";
        } else {
            return "free";
        }
    }

    private MoMoIPNResponse handleBuildMoMoIPNResponse(MoMoIPNRequest ipnRequest, int resultCode, String message) {
        String rawData = "partnerCode=" + moMoConfig.getPartnerCode() +
                "&requestId=" + ipnRequest.getRequestId() +
                "&orderId=" + ipnRequest.getOrderId() +
                "&resultCode=" + resultCode +
                "&message=" + message +
                "&responseTime=" + System.currentTimeMillis() +
                "&extraData=";

        String signature = MoMoSignatureUtil.createSignature(moMoConfig.getSecretKey(), rawData);

        return MoMoIPNResponse.builder()
                .partnerCode(moMoConfig.getPartnerCode())
                .requestId(ipnRequest.getRequestId())
                .orderId(ipnRequest.getOrderId())
                .resultCode(resultCode)
                .message(message)
                .responseTime(System.currentTimeMillis())
                .extraData("")
                .signature(signature)
                .build();
    }

    public Response createPayPalPayment(UUID userId, String dataJson) {
        logger.info("Creating new PayPal payment");
        Response response = new Response();

        try {
            CreatePaymentRequest request = objectMapper.readValue(dataJson, CreatePaymentRequest.class);
            String planTitle = request.getPlanTitle();
            Long price = request.getPrice();

            PayPalPaymentResponse res = handleCreatePayPalPayment(userId, planTitle, price);

            response.setStatusCode(201);
            response.setMessage("PayPal payment created successfully");
            response.setPaymentUrl(res.getApprovalUrl());
            response.setOrderId(res.getPaymentId());
            logger.info("PayPal payment creation completed successfully. Payment ID: {}", res.getPaymentId());
            return response;
        } catch (OurException e) {
            logger.error("PayPal payment creation failed with OurException: {}", e.getMessage());
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            logger.error("PayPal payment creation failed with unexpected error", e);
            e.printStackTrace();
            return buildErrorResponse(500, e.getMessage());
        }
    }

    public PayPalPaymentResponse handleCreatePayPalPayment(UUID userId, String planTitle, Long price) {
        try {
            String currency = "VND";
            String orderInfo = "Payment for plan " + planTitle;
            String method = "paypal";
            String intent = "sale";

            // Tạo APIContext mới
            APIContext apiContext = payPalConfig.createApiContext();

            // 1: Tạo Amount
            logger.info("1: Chuẩn bị thông tin thanh toán");
            Amount amount = new Amount();
            amount.setCurrency(currency);
            amount.setTotal(String.format(Locale.US, "%.2f", price));
            logger.info("Số tiền: {} {}", amount.getTotal(), currency);

            // 2: Tạo Transaction
            Transaction transaction = new Transaction();
            transaction.setDescription(orderInfo);
            transaction.setAmount(amount);
            logger.info("Mô tả: {}", orderInfo);

            List<Transaction> transactions = new ArrayList<>();
            transactions.add(transaction);

            // 3: Tạo Payer
            logger.info("\n2: Thiết lập phương thức thanh toán");
            Payer payer = new Payer();
            payer.setPaymentMethod(method);
            logger.info("Phương thức: {}", method);

            // 4: Tạo Payment
            Payment payment = new Payment();
            payment.setIntent(intent);
            payment.setPayer(payer);
            payment.setTransactions(transactions);
            logger.info("Intent: {}", intent);

            // 5: Thiết lập Redirect URLs
            logger.info("\n3: Thiết lập URLs");
            RedirectUrls redirectUrls = new RedirectUrls();
            redirectUrls.setCancelUrl(
                    payPalConfig.getCancelUrl());
            redirectUrls.setReturnUrl(
                    payPalConfig.getSuccessUrl());
            payment.setRedirectUrls(redirectUrls);
            logger.info("Success URL: {}", redirectUrls.getReturnUrl());
            logger.info("Cancel URL: {}", redirectUrls.getCancelUrl());

            // 6: Tạo Payment
            logger.info("\n4: Gửi request tới PayPal");
            Payment createdPayment = payment.create(apiContext);
            logger.info("Payment đã được tạo với ID: {}", createdPayment.getId());

            // 7: Tìm approval URL
            String approvalUrl = null;
            for (Links link : createdPayment.getLinks()) {
                if (link.getRel().equals("approval_url")) {
                    approvalUrl = link.getHref();
                    break;
                }
            }

            logger.info("Approval URL: {}", approvalUrl);

            // Lưu invoice vào database
            UUID invoiceId = UUID.randomUUID();
            invoiceCommandRepository.insertInvoice(
                    userId,
                    planTitle,
                    (price * 100),
                    currency,
                    InvoiceStatus.pending,
                    "PayPal",
                    createdPayment.getId(),
                    LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    orderInfo);
            logger.info("Invoice saved with ID: {}", invoiceId);

            return PayPalPaymentResponse.builder()
                    .paymentId(createdPayment.getId())
                    .status(createdPayment.getState())
                    .approvalUrl(approvalUrl)
                    .message("Payment created successfully")
                    .build();

        } catch (PayPalRESTException e) {
            logger.error("Lỗi khi tạo PayPal payment: {}", e.getMessage(), e);
            return PayPalPaymentResponse.builder()
                    .status("error")
                    .message("Error creating payment: " + e.getMessage())
                    .build();
        } catch (Exception e) {
            logger.error("Lỗi: {}", e.getMessage(), e);
            return PayPalPaymentResponse.builder()
                    .status("error")
                    .message(e.getMessage())
                    .build();
        }
    }

    public Response executePayPalPayment(String paymentId, String payerId) {
        logger.info("Executing PayPal payment");
        Response response = new Response();

        try {
            PayPalExecuteResponse res = handleExecutePayPalPayment(paymentId, payerId);

            response.setStatusCode(200);
            response.setMessage("PayPal payment executed successfully");
            response.setSuccess("approved".equals(res.getState()));
            response.setOrderId(res.getPaymentId());
            logger.info("PayPal payment execution completed. Payment ID: {}, State: {}", res.getPaymentId(),
                    res.getState());
            return response;
        } catch (OurException e) {
            logger.error("PayPal payment execution failed with OurException: {}", e.getMessage());
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            logger.error("PayPal payment execution failed with unexpected error", e);
            e.printStackTrace();
            return buildErrorResponse(500, e.getMessage());
        }
    }

    public PayPalExecuteResponse handleExecutePayPalPayment(String paymentId, String payerId) {
        try {
            // Tạo APIContext mới
            APIContext apiContext = payPalConfig.createApiContext();

            logger.info("Thông tin thanh toán");
            logger.info("Payment ID: {}", paymentId);
            logger.info("Payer ID: {}", payerId);

            // 1: Lấy thông tin payment
            Payment payment = Payment.get(apiContext, paymentId);
            logger.info("Payment state: {}", payment.getState());

            // 2: Tạo PaymentExecution
            PaymentExecution paymentExecution = new PaymentExecution();
            paymentExecution.setPayerId(payerId);

            // 3: Thực hiện payment
            logger.info("\nĐang thực hiện thanh toán...");
            Payment executedPayment = payment.execute(apiContext, paymentExecution);
            logger.info("Thanh toán hoàn tất");

            // Lấy thông tin transaction
            Transaction transaction = executedPayment.getTransactions().get(0);
            Amount amount = transaction.getAmount();

            logger.info("State: {}", executedPayment.getState());
            logger.info("Amount: {} {}", amount.getTotal(), amount.getCurrency());

            // Cập nhật invoice trong database
            InvoiceStatus newStatus = "approved".equals(executedPayment.getState())
                    ? InvoiceStatus.paid
                    : InvoiceStatus.failed;

            int updated = invoiceCommandRepository.updateStatusByTransactionId(paymentId, newStatus);

            if (updated > 0) {
                logger.info("Invoice updated to {} for paymentId: {}", newStatus, paymentId);

                // Update user plan if payment approved
                if ("approved".equals(executedPayment.getState())) {
                    try {
                        Invoice invoice = invoiceQueryRepository.findByTransactionId(paymentId)
                                .orElse(null);
                        if (invoice != null) {
                            handleUpdateUserPlanAfterPayment(invoice);
                        }
                    } catch (Exception e) {
                        logger.error("Failed to update user plan after PayPal payment", e);
                    }
                }
            } else {
                logger.warn("Invoice not found for paymentId: {}", paymentId);
            }

            return PayPalExecuteResponse.builder()
                    .paymentId(executedPayment.getId())
                    .payerId(payerId)
                    .state(executedPayment.getState())
                    .amount(amount.getTotal())
                    .currency(amount.getCurrency())
                    .description(transaction.getDescription())
                    .message("Payment executed successfully")
                    .build();

        } catch (PayPalRESTException e) {
            logger.error("Lỗi khi thực hiện payment: {}", e.getMessage(), e);
            return PayPalExecuteResponse.builder()
                    .paymentId(paymentId)
                    .payerId(payerId)
                    .state("error")
                    .message("Error executing payment: " + e.getMessage())
                    .build();
        } catch (Exception e) {
            logger.error("Lỗi: {}", e.getMessage(), e);
            return PayPalExecuteResponse.builder()
                    .paymentId(paymentId)
                    .payerId(payerId)
                    .state("error")
                    .message(e.getMessage())
                    .build();
        }
    }

    public Response getPayPalPaymentDetails(String paymentId) {
        logger.info("Getting PayPal payment details");
        Response response = new Response();

        try {
            Payment payment = handleGetPayPalPaymentDetails(paymentId);

            if (payment != null) {
                response.setStatusCode(200);
                response.setMessage("PayPal payment details retrieved successfully");
                response.setSuccess(true);
                // Có thể thêm payment details vào additionalData nếu cần
                logger.info("PayPal payment details retrieved successfully. Payment ID: {}", paymentId);
            } else {
                response.setStatusCode(404);
                response.setMessage("PayPal payment not found");
                response.setSuccess(false);
                logger.warn("PayPal payment not found. Payment ID: {}", paymentId);
            }
            return response;
        } catch (OurException e) {
            logger.error("PayPal payment details retrieval failed with OurException: {}", e.getMessage());
            return buildErrorResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            logger.error("PayPal payment details retrieval failed with unexpected error", e);
            e.printStackTrace();
            return buildErrorResponse(500, e.getMessage());
        }
    }

    public Payment handleGetPayPalPaymentDetails(String paymentId) {
        try {
            APIContext apiContext = payPalConfig.createApiContext();
            logger.info("Lấy thông tin payment ID: {}", paymentId);
            return Payment.get(apiContext, paymentId);
        } catch (PayPalRESTException e) {
            logger.error("Lỗi khi lấy thông tin payment: {}", e.getMessage(), e);
            return null;
        } catch (Exception e) {
            logger.error("Lỗi: {}", e.getMessage(), e);
            return null;
        }
    }

    public Response getRevenueStats() {
        logger.info("Fetching revenue statistics");
        Response response = new Response();

        try {
            // Lấy tất cả invoice đã thanh toán thành công
            var paidInvoices = invoiceQueryRepository.findByStatus(InvoiceStatus.paid);

            // Tổng doanh thu
            BigDecimal totalRevenue = paidInvoices.stream()
                    .map(inv -> BigDecimal.valueOf(inv.getAmount()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            // Doanh thu tháng này
            LocalDate now = LocalDate.now();
            LocalDate startOfMonth = now.withDayOfMonth(1);
            LocalDate startOfLastMonth = startOfMonth.minusMonths(1);
            LocalDate endOfLastMonth = startOfMonth.minusDays(1);

            BigDecimal thisMonthRevenue = paidInvoices.stream()
                    .filter(inv -> {
                        LocalDate billingDate = LocalDate.parse(inv.getBillingDate().substring(0, 10));
                        return !billingDate.isBefore(startOfMonth) && !billingDate.isAfter(now);
                    })
                    .map(inv -> BigDecimal.valueOf(inv.getAmount()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            // Doanh thu tháng trước
            BigDecimal lastMonthRevenue = paidInvoices.stream()
                    .filter(inv -> {
                        LocalDate billingDate = LocalDate.parse(inv.getBillingDate().substring(0, 10));
                        return !billingDate.isBefore(startOfLastMonth) && !billingDate.isAfter(endOfLastMonth);
                    })
                    .map(inv -> BigDecimal.valueOf(inv.getAmount()))
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            // Tính tỷ lệ tăng trưởng
            Double growthRate = 0.0;
            if (lastMonthRevenue.compareTo(BigDecimal.ZERO) > 0) {
                growthRate = thisMonthRevenue.subtract(lastMonthRevenue)
                        .divide(lastMonthRevenue, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .doubleValue();
            }

            // Thống kê số lượng giao dịch
            long successfulTransactions = invoiceQueryRepository.countByStatus(InvoiceStatus.paid);
            long failedTransactions = invoiceQueryRepository.countByStatus(InvoiceStatus.failed);
            long pendingTransactions = invoiceQueryRepository.countByStatus(InvoiceStatus.pending);

            // Doanh thu theo phương thức thanh toán
            Map<String, BigDecimal> revenueByPaymentMethod = paidInvoices.stream()
                    .collect(Collectors.groupingBy(
                            inv -> inv.getPaymentMethod() != null ? inv.getPaymentMethod() : "Unknown",
                            Collectors.reducing(
                                    BigDecimal.ZERO,
                                    inv -> BigDecimal.valueOf(inv.getAmount()).divide(BigDecimal.valueOf(100), 2,
                                            RoundingMode.HALF_UP),
                                    BigDecimal::add)));

            // Doanh thu theo gói
            Map<String, BigDecimal> revenueByPlan = paidInvoices.stream()
                    .filter(inv -> inv.getPlanTitle() != null)
                    .collect(Collectors.groupingBy(
                            inv -> inv.getPlanTitle(),
                            Collectors.reducing(
                                    BigDecimal.ZERO,
                                    inv -> BigDecimal.valueOf(inv.getAmount()).divide(BigDecimal.valueOf(100), 2,
                                            RoundingMode.HALF_UP),
                                    BigDecimal::add)));

            // Doanh thu theo ngày (30 ngày gần nhất)
            List<DailyRevenueDto> dailyRevenue = new ArrayList<>();
            for (int i = 29; i >= 0; i--) {
                LocalDate date = now.minusDays(i);
                String dateStr = date.format(DateTimeFormatter.ISO_LOCAL_DATE);

                BigDecimal dayRevenue = paidInvoices.stream()
                        .filter(inv -> inv.getBillingDate().startsWith(dateStr))
                        .map(inv -> BigDecimal.valueOf(inv.getAmount()))
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

                long dayTransactions = paidInvoices.stream()
                        .filter(inv -> inv.getBillingDate().startsWith(dateStr))
                        .count();

                dailyRevenue.add(DailyRevenueDto.builder()
                        .date(dateStr)
                        .revenue(dayRevenue)
                        .transactions(dayTransactions)
                        .build());
            }

            // Doanh thu theo tháng (12 tháng gần nhất)
            List<MonthlyRevenueDto> monthlyRevenue = new ArrayList<>();
            for (int i = 11; i >= 0; i--) {
                LocalDate monthDate = now.minusMonths(i).withDayOfMonth(1);
                String monthStr = monthDate.format(DateTimeFormatter.ofPattern("yyyy-MM"));

                BigDecimal monthRevenue = paidInvoices.stream()
                        .filter(inv -> inv.getBillingDate().startsWith(monthStr))
                        .map(inv -> BigDecimal.valueOf(inv.getAmount()))
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

                long monthTransactions = paidInvoices.stream()
                        .filter(inv -> inv.getBillingDate().startsWith(monthStr))
                        .count();

                monthlyRevenue.add(MonthlyRevenueDto.builder()
                        .month(monthStr)
                        .revenue(monthRevenue)
                        .transactions(monthTransactions)
                        .build());
            }

            RevenueStatsDto revenueStats = RevenueStatsDto.builder()
                    .totalRevenue(totalRevenue)
                    .thisMonthRevenue(thisMonthRevenue)
                    .lastMonthRevenue(lastMonthRevenue)
                    .growthRate(growthRate)
                    .successfulTransactions(successfulTransactions)
                    .failedTransactions(failedTransactions)
                    .pendingTransactions(pendingTransactions)
                    .revenueByPaymentMethod(revenueByPaymentMethod)
                    .revenueByPlan(revenueByPlan)
                    .dailyRevenue(dailyRevenue)
                    .monthlyRevenue(monthlyRevenue)
                    .build();

            logger.info("Revenue statistics fetched successfully");
            response.setStatusCode(200);
            response.setMessage("Revenue statistics fetched successfully");
            response.setRevenueStats(revenueStats);
            return response;

        } catch (Exception e) {
            logger.error("Error fetching revenue statistics", e);
            throw new RuntimeException("Failed to fetch revenue statistics", e);
        }
    }
}