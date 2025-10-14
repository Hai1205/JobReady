package com.example.authservice.service;

// import com.example.authservice.dto.OAuth2UserDto;
// import com.example.authservice.dto.messages.ChangePasswordMessage;
// import com.example.authservice.dto.messages.ChangeStatusMessage;
// import com.example.authservice.dto.messages.EmailMessage;
// import com.example.authservice.dto.messages.ForgotPasswordMessage;
// import com.example.authservice.dto.messages.OtpVerificationMessage;
// import com.example.authservice.dto.messages.ResetPasswordMessage;
// import com.example.authservice.dto.requests.*;
// import com.example.authservice.dto.responses.MessageResponse;
// import com.example.authservice.dto.results.UserResult;

// import java.util.Map;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    // private static final Logger logger = LoggerFactory.getLogger(UserService.class);


    // public UserResult findUserByEmail(String email) {
    //     try {
    //         logger.info("Finding user by email: {}", email);

    //         // Tạo request để tìm user
    //         EmailMessage request = new EmailMessage(email);

    //         // Sử dụng RabbitRpcClient để gửi và nhận kết quả
    //         UserResult response = rpcClient.sendAndReceive(
    //                 RabbitConfig.USER_EXCHANGE,
    //                 RabbitConfig.USER_FIND_BY_EMAIL_ROUTING_KEY,
    //                 request,
    //                 RabbitConfig.USER_FIND_BY_EMAIL_REPLY_QUEUE,
    //                 UserResult.class);

    //         if (response != null && response.isSuccess()) {
    //             logger.info("User found for email: {}", email);
    //             return response;
    //         } else {
    //             logger.warn("User not found for email: {}", email);
    //             throw new NotFoundException("User not found");
    //         }
    //     } catch (RabbitRpcException e) {
    //         if (e.isTimeout()) {
    //             logger.error("Timeout finding user by email: {}", email);
    //             throw new NotFoundException("Service timed out while finding user");
    //         } else {
    //             logger.error("Error finding user by email: {}", email, e);
    //             throw new BadRequestException("Failed to find user: " + e.getMessage());
    //         }
    //     } catch (NotFoundException e) {
    //         throw e;
    //     } catch (Exception e) {
    //         logger.error("Error finding user by email: {}", email, e);
    //         throw new BadRequestException("Failed to find user: " + e.getMessage());
    //     }
    // }

    // /**
    //  * Đặt lại mật khẩu cho user sử dụng RabbitMQ
    //  * 
    //  * @param request ResetPasswordRequest chứa thông tin đặt lại mật khẩu
    //  * @return UserResult kết quả đặt lại mật khẩu
    //  */
    // public UserResult resetPassword(ResetPasswordMessage request) {
    //     try {
    //         logger.info("Resetting password for user: {}", request.getUserId());

    //         // Sử dụng RabbitRpcClient để gửi và nhận kết quả
    //         UserResult response = rpcClient.sendAndReceive(
    //                 RabbitConfig.USER_EXCHANGE,
    //                 RabbitConfig.USER_RESET_PASSWORD_ROUTING_KEY,
    //                 request,
    //                 RabbitConfig.USER_RESET_PASSWORD_REPLY_QUEUE,
    //                 UserResult.class);

    //         if (response != null && response.isSuccess()) {
    //             logger.info("Password reset successful for userId: {}", request.getUserId());
    //             return response;
    //         } else {
    //             logger.error("Failed to reset password for userId: {}", request.getUserId());
    //             throw new BadRequestException("Failed to reset password");
    //         }
    //     } catch (RabbitRpcException e) {
    //         if (e.isTimeout()) {
    //             logger.error("Timeout resetting password for userId: {}", request.getUserId());
    //             throw new BadRequestException("Timeout resetting password");
    //         } else {
    //             logger.error("Error resetting password for userId: {}", request.getUserId(), e);
    //             throw new BadRequestException("Failed to reset password: " + e.getMessage());
    //         }
    //     } catch (Exception e) {
    //         logger.error("Error resetting password for userId: {}", request.getUserId(), e);
    //         throw new BadRequestException("Failed to reset password: " + e.getMessage());
    //     }
    // }

    // /**
    //  * Thay đổi mật khẩu cho user sử dụng RabbitMQ
    //  * 
    //  * @param request ChangePasswordRequest chứa thông tin thay đổi mật khẩu
    //  * @return UserResult kết quả thay đổi mật khẩu
    //  */
    // public UserResult changePassword(ChangePasswordMessage request) {
    //     try {
    //         logger.info("Changing password for user: {}", request.getUserId());

    //         // Sử dụng RabbitRpcClient để gửi và nhận kết quả
    //         UserResult response = rpcClient.sendAndReceive(
    //                 RabbitConfig.USER_EXCHANGE,
    //                 RabbitConfig.USER_CHANGE_PASSWORD_ROUTING_KEY,
    //                 request,
    //                 RabbitConfig.USER_CHANGE_PASSWORD_REPLY_QUEUE,
    //                 UserResult.class);

    //         if (response != null && response.isSuccess()) {
    //             logger.info("Password change successful for userId: {}", request.getUserId());
    //             return response;
    //         } else {
    //             logger.error("Failed to change password for userId: {}", request.getUserId());
    //             throw new BadRequestException("Failed to change password");
    //         }
    //     } catch (RabbitRpcException e) {
    //         if (e.isTimeout()) {
    //             logger.error("Timeout changing password for userId: {}", request.getUserId());
    //             throw new BadRequestException("Timeout changing password");
    //         } else {
    //             logger.error("Error changing password for userId: {}", request.getUserId(), e);
    //             throw new BadRequestException("Failed to change password: " + e.getMessage());
    //         }
    //     } catch (Exception e) {
    //         logger.error("Error changing password for userId: {}", request.getUserId(), e);
    //         throw new BadRequestException("Failed to change password: " + e.getMessage());
    //     }
    // }

    // /**
    //  * Thay đổi trạng thái user sử dụng RabbitMQ
    //  * 
    //  * @param request ChangeStatusRequest chứa thông tin thay đổi trạng thái
    //  * @return UserResult kết quả thay đổi trạng thái
    //  */
    // public UserResult changeStatus(ChangeStatusMessage request) {
    //     try {
    //         logger.info("Changing status for user: {} to {}", request.getUserId(), request.getStatus());

    //         // Sử dụng RabbitRpcClient để gửi và nhận kết quả
    //         UserResult response = rpcClient.sendAndReceive(
    //                 RabbitConfig.USER_EXCHANGE,
    //                 RabbitConfig.USER_CHANGE_STATUS_ROUTING_KEY,
    //                 request,
    //                 RabbitConfig.USER_CHANGE_STATUS_REPLY_QUEUE,
    //                 UserResult.class);

    //         if (response != null && response.isSuccess()) {
    //             logger.info("Status change successful for userId: {}", request.getUserId());
    //             return response;
    //         } else {
    //             logger.error("Failed to change status for userId: {}", request.getUserId());
    //             throw new BadRequestException("Failed to change user status");
    //         }
    //     } catch (RabbitRpcException e) {
    //         if (e.isTimeout()) {
    //             logger.error("Timeout changing status for userId: {}", request.getUserId());
    //             throw new BadRequestException("Timeout changing user status");
    //         } else {
    //             logger.error("Error changing status for userId: {}", request.getUserId(), e);
    //             throw new BadRequestException("Failed to change user status: " + e.getMessage());
    //         }
    //     } catch (Exception e) {
    //         logger.error("Error changing status for userId: {}", request.getUserId(), e);
    //         throw new BadRequestException("Failed to change user status: " + e.getMessage());
    //     }
    // }

    // /**
    //  * Gửi yêu cầu quên mật khẩu và tạo OTP
    //  * 
    //  * @param request ForgotPasswordRequest chứa email
    //  * @return MessageResponse kết quả gửi OTP
    //  */
    // public MessageResponse forgotPassword(ForgotPasswordMessage request) {
    //     try {
    //         // Tìm kiếm người dùng theo email
    //         findUserByEmail(request.getEmail());

    //         // Tạo OTP và lưu vào Redis với key là email
    //         String otp = otpService.generateOtp(request.getEmail());

    //         // Gửi email OTP (trong thực tế, đây là nơi bạn sẽ gửi email)
    //         logger.info("OTP for password reset sent to: {} with code: {}", request.getEmail(), otp);

    //         // Trả về thông báo thành công
    //         return new MessageResponse(true, "OTP has been sent to your email");
    //     } catch (NotFoundException e) {
    //         // Trả về lỗi khi không tìm thấy email
    //         logger.error("Email not found for forgot password: {}", request.getEmail());
    //         throw new NotFoundException("Email not found");
    //     } catch (Exception e) {
    //         logger.error("Error processing forgot password for email: {}", request.getEmail(), e);
    //         throw new BadRequestException("Failed to process forgot password: " + e.getMessage());
    //     }
    // }

    // /**
    //  * Xác thực OTP cho quá trình đặt lại mật khẩu
    //  * 
    //  * @param request OtpVerificationRequest chứa email và OTP
    //  * @return MessageResponse kết quả xác thực
    //  */
    // public MessageResponse verifyOtp(OtpVerificationMessage request) {
    //     try {
    //         boolean isValid = otpService.validateOtp(request.getEmail(), request.getOtp());

    //         if (isValid) {
    //             return new MessageResponse(true, "OTP verification successful");
    //         } else {
    //             throw new BadRequestException("Invalid OTP");
    //         }
    //     } catch (Exception e) {
    //         logger.error("Error verifying OTP for email: {}", request.getEmail(), e);
    //         throw new BadRequestException("Failed to verify OTP: " + e.getMessage());
    //     }
    // }

    // /**
    //  * Xử lý thông tin người dùng đăng nhập bằng OAuth2
    //  * 
    //  * @param oauth2UserDto thông tin user từ OAuth2 provider
    //  * @return Map chứa thông tin user sau khi xử lý
    //  */
    // public Map<String, Object> processOAuth2User(OAuth2UserDto oauth2UserDto) {
    //     try {
    //         logger.info("Processing OAuth2 user: {} from provider: {}",
    //                 oauth2UserDto.getEmail(), oauth2UserDto.getProvider());

    //         // Gửi thông tin OAuth2 user đến user-service qua RabbitMQ
    //         UserResult response = rpcClient.sendAndReceive(
    //                 RabbitConfig.USER_EXCHANGE,
    //                 RabbitConfig.USER_PROCESS_OAUTH2_ROUTING_KEY,
    //                 oauth2UserDto,
    //                 RabbitConfig.USER_PROCESS_OAUTH2_REPLY_QUEUE,
    //                 UserResult.class);

    //         if (response != null && response.isSuccess()) {
    //             logger.info("Successfully processed OAuth2 user: {}", oauth2UserDto.getEmail());
    //             return response.getData();
    //         } else {
    //             logger.error("Failed to process OAuth2 user: {}", oauth2UserDto.getEmail());
    //             throw new BadRequestException("Failed to process OAuth2 user");
    //         }
    //     } catch (RabbitRpcException e) {
    //         if (e.isTimeout()) {
    //             logger.error("Timeout processing OAuth2 user: {}", oauth2UserDto.getEmail());
    //             throw new BadRequestException("Service timed out while processing OAuth2 user");
    //         } else {
    //             logger.error("Error processing OAuth2 user: {}", oauth2UserDto.getEmail(), e);
    //             throw new BadRequestException("Failed to process OAuth2 user: " + e.getMessage());
    //         }
    //     } catch (Exception e) {
    //         logger.error("Error processing OAuth2 user: {}", oauth2UserDto.getEmail(), e);
    //         throw new BadRequestException("Failed to process OAuth2 user: " + e.getMessage());
    //     }
    // }
}