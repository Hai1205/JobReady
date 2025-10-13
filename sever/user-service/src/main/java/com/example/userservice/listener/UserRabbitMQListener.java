package com.example.userservice.listener;

import com.example.userservice.config.RabbitConfig;
import com.example.userservice.dto.results.*;
import com.example.userservice.dto.requests.*;
import com.example.userservice.exception.*;
import com.example.userservice.entity.User;
import com.example.userservice.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class UserRabbitMQListener {

    private static final Logger logger = LoggerFactory.getLogger(UserRabbitMQListener.class);

    private final UserService userService;

    public UserRabbitMQListener(UserService userService) {
        this.userService = userService;
    }

    /**
     * Lắng nghe yêu cầu tìm kiếm user theo email
     * 
     * @param request EmailRequest từ auth-service
     * @return UserResult chứa thông tin user nếu tìm thấy
     */
    @RabbitListener(queues = RabbitConfig.USER_FIND_BY_EMAIL_QUEUE)
    public UserResult findUserByEmail(EmailRequest request) {
        try {
            logger.info("Received request to find user by email: {}", request.getEmail());

            User user = userService.findByEmail(request.getEmail());

            if (user != null) {
                UserResult result = new UserResult();
                result.setId(user.getId());
                result.setEmail(user.getEmail());
                // Tên đầy đủ có thể được chia thành firstName và lastName
                String[] names = user.getFullname() != null ? user.getFullname().split(" ", 2)
                        : new String[] { "", "" };
                result.setFirstName(names.length > 0 ? names[0] : "");
                result.setLastName(names.length > 1 ? names[1] : "");
                result.setEnabled(user.getStatus() == User.UserStatus.ACTIVE);
                result.setRole(user.getRole().toString());
                result.setSuccess(true);
                result.setMessage("User found successfully");

                logger.info("User found with email: {}", request.getEmail());
                return result;
            } else {
                logger.warn("User not found with email: {}", request.getEmail());
                UserResult result = new UserResult();
                result.setSuccess(false);
                result.setMessage("User not found");
                return result;
            }
        } catch (Exception e) {
            logger.error("Error finding user by email: {}", request.getEmail(), e);
            UserResult result = new UserResult();
            result.setSuccess(false);
            result.setMessage("Error finding user: " + e.getMessage());
            return result;
        }
    }

    /**
     * Lắng nghe yêu cầu đổi mật khẩu
     * 
     * @param request ChangePasswordRequest từ auth-service
     * @return UserResult kết quả thay đổi mật khẩu
     */
    @RabbitListener(queues = RabbitConfig.USER_CHANGE_PASSWORD_QUEUE)
    public UserResult changePassword(ChangePasswordRequest request) {
        try {
            logger.info("Received request to change password for userId: {}", request.getUserId());

            User user = userService.changePassword(
                    request);

            UserResult result = new UserResult();
            result.setId(user.getId());
            result.setEmail(user.getEmail());
            result.setSuccess(true);
            result.setMessage("Password changed successfully");

            logger.info("Password changed successfully for userId: {}", request.getUserId());
            return result;
        } catch (NotFoundException e) {
            logger.error("User not found for password change: {}", request.getUserId());
            UserResult result = new UserResult();
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            return result;
        } catch (BadRequestException e) {
            logger.error("Invalid current password for userId: {}", request.getUserId());
            UserResult result = new UserResult();
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            return result;
        } catch (Exception e) {
            logger.error("Error changing password for userId: {}", request.getUserId(), e);
            UserResult result = new UserResult();
            result.setSuccess(false);
            result.setMessage("Error changing password: " + e.getMessage());
            return result;
        }
    }

    /**
     * Lắng nghe yêu cầu đặt lại mật khẩu
     * 
     * @param request ResetPasswordRequest từ auth-service
     * @return UserResult kết quả đặt lại mật khẩu
     */
    @RabbitListener(queues = RabbitConfig.USER_RESET_PASSWORD_QUEUE)
    public UserResult resetPassword(ResetPasswordRequest request) {
        try {
            logger.info("Received request to reset password for userId: {}", request.getUserId());

            User user = userService.resetPassword(
                    request);

            UserResult result = new UserResult();
            result.setId(user.getId());
            result.setEmail(user.getEmail());
            result.setSuccess(true);
            result.setMessage("Password reset successfully");

            logger.info("Password reset successfully for userId: {}", request.getUserId());
            return result;
        } catch (NotFoundException e) {
            logger.error("User not found for password reset: {}", request.getUserId());
            UserResult result = new UserResult();
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            return result;
        } catch (Exception e) {
            logger.error("Error resetting password for userId: {}", request.getUserId(), e);
            UserResult result = new UserResult();
            result.setSuccess(false);
            result.setMessage("Error resetting password: " + e.getMessage());
            return result;
        }
    }

    /**
     * Lắng nghe yêu cầu thay đổi trạng thái user
     * 
     * @param request ChangeStatusRequest từ auth-service
     * @return UserResult kết quả thay đổi trạng thái
     */
    @RabbitListener(queues = RabbitConfig.USER_CHANGE_STATUS_QUEUE)
    public UserResult changeStatus(ChangeStatusRequest request) {
        try {
            logger.info("Received request to change status for userId: {} to {}",
                    request.getUserId(), request.getStatus());

            User user = userService.changeStatus(request);

            UserResult result = new UserResult();
            result.setId(user.getId());
            result.setEmail(user.getEmail());
            result.setEnabled(user.getStatus() == User.UserStatus.ACTIVE);
            result.setSuccess(true);
            result.setMessage("User status changed successfully");

            logger.info("Status changed successfully for userId: {} to {}",
                    request.getUserId(), user.getStatus());
            return result;
        } catch (NotFoundException e) {
            logger.error("User not found for status change: {}", request.getUserId());
            UserResult result = new UserResult();
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            return result;
        } catch (Exception e) {
            logger.error("Error changing status for userId: {}", request.getUserId(), e);
            UserResult result = new UserResult();
            result.setSuccess(false);
            result.setMessage("Error changing user status: " + e.getMessage());
            return result;
        }
    }

    /**
     * Lắng nghe yêu cầu quên mật khẩu
     * 
     * @param request ForgotPasswordRequest từ auth-service
     * @return UserResult kết quả quên mật khẩu
     */
    @RabbitListener(queues = RabbitConfig.USER_FORGOT_PASSWORD_QUEUE)
    public UserResult forgotPassword(ForgotPasswordRequest request) {
        try {
            logger.info("Received forgot password request for email: {}", request.getEmail());

            User user = userService.resetPasswordByEmail(request.getEmail(), request.getNewPassword());

            UserResult result = new UserResult();
            result.setId(user.getId());
            result.setEmail(user.getEmail());
            result.setSuccess(true);
            result.setMessage("Password reset successfully");

            logger.info("Password reset successfully for email: {}", request.getEmail());
            return result;
        } catch (NotFoundException e) {
            logger.error("User not found for forgot password: {}", request.getEmail());
            UserResult result = new UserResult();
            result.setSuccess(false);
            result.setMessage(e.getMessage());
            return result;
        } catch (Exception e) {
            logger.error("Error processing forgot password for email: {}", request.getEmail(), e);
            UserResult result = new UserResult();
            result.setSuccess(false);
            result.setMessage("Error resetting password: " + e.getMessage());
            return result;
        }
    }
}