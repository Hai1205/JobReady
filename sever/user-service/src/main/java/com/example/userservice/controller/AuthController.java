package com.example.userservice.controller;

import com.example.userservice.dto.Response;
import com.example.userservice.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @deprecated This controller is deprecated and should be removed in future
 *             versions.
 *             Authentication functionality has been moved to the auth-service's
 *             AuthController.
 *             User verification functionality has been moved to
 *             UserVerificationController.
 * 
 *             Please update your API calls to use the appropriate endpoints.
 */
@Deprecated
@RestController
@RequestMapping("/users")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserService userService;

    /**
     * Endpoint để xác thực người dùng và trả về thông tin trạng thái
     * 
     * @param credentials Map chứa username và password
     * @return Thông tin người dùng nếu xác thực thành công
     */
    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticateUser(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        if (username == null || password == null) {
            return ResponseEntity.badRequest().body("Username and password are required");
        }

        Response response = userService.authenticateUser(username, password);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    /**
     * Endpoint để kích hoạt tài khoản người dùng
     * 
     * @param username Username của người dùng
     * @param token    Token xác thực
     * @return Thông báo kích hoạt thành công
     */
    @PostMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestParam String username, @RequestParam String token) {
        try {
            // Kiểm tra token xác thực (có thể thêm logic phức tạp hơn)
            // Đây là mô phỏng đơn giản, trong thực tế cần xác thực token từ email

            // Kiểm tra xem user có tồn tại không
            Response statusResponse = userService.getUserStatus(username);

            if (statusResponse.getStatusCode() != 200) {
                return ResponseEntity.status(statusResponse.getStatusCode()).body(statusResponse);
            }

            String userStatus = (String) statusResponse.getData().getStatus();

            if ("PENDING".equals(userStatus)) {
                // Kích hoạt tài khoản
                Response activationResponse = userService.activateUser(username);
                return ResponseEntity.status(activationResponse.getStatusCode()).body(activationResponse);
            } else if ("ACTIVE".equals(userStatus)) {
                Response response = new Response();
                response.setStatusCode(200);
                response.setMessage("Account is already verified");
                return ResponseEntity.ok(response);
            } else {
                Response response = new Response();
                response.setStatusCode(400);
                response.setMessage("Invalid account status");
                return ResponseEntity.status(400).body(response);
            }
        } catch (Exception e) {
            logger.error("Error during account verification: {}", e.getMessage());
            Response response = new Response();
            response.setStatusCode(500);
            response.setMessage("Verification failed: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}