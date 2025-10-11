// package com.example.userservice.controller;

// import com.example.userservice.dto.OAuth2UserDto;
// import com.example.userservice.dto.UserDto;
// import com.example.userservice.service.OAuth2UserService;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import java.util.HashMap;
// import java.util.Map;

// @RestController
// @RequestMapping("/users/oauth2")
// public class OAuth2UserController {

//     private static final Logger logger = LoggerFactory.getLogger(OAuth2UserController.class);

//     @Autowired
//     private OAuth2UserService oauth2UserService;

//     /**
//      * Kiểm tra user OAuth2 đã tồn tại hay chưa
//      * POST /users/oauth2/check
//      */
//     @PostMapping("/check")
//     public ResponseEntity<Map<String, Object>> checkOAuth2User(@RequestBody Map<String, String> request) {
//         try {
//             String email = request.get("email");
//             String provider = request.get("provider");
//             String providerId = request.get("providerId");

//             logger.info("Checking OAuth2 user existence: email={}, provider={}", email, provider);

//             UserDto user = oauth2UserService.findOAuth2User(email, provider, providerId);

//             if (user != null) {
//                 Map<String, Object> response = new HashMap<>();
//                 response.put("exists", true);
//                 response.put("user", user);
//                 response.put("id", user.getId());
//                 response.put("email", user.getEmail());
//                 response.put("provider", user.getOauthProvider());

//                 return ResponseEntity.ok(response);
//             } else {
//                 return ResponseEntity.notFound().build();
//             }

//         } catch (Exception e) {
//             logger.error("Error checking OAuth2 user", e);
//             Map<String, Object> errorResponse = new HashMap<>();
//             errorResponse.put("error", "Failed to check user existence");
//             errorResponse.put("message", e.getMessage());
//             return ResponseEntity.badRequest().body(errorResponse);
//         }
//     }

//     /**
//      * Tạo user mới từ OAuth2
//      * POST /users/oauth2/create
//      */
//     @PostMapping("/create")
//     public ResponseEntity<Map<String, Object>> createOAuth2User(@RequestBody OAuth2UserDto oauth2UserDto) {
//         try {
//             logger.info("Creating OAuth2 user: email={}, provider={}",
//                     oauth2UserDto.getEmail(), oauth2UserDto.getProvider());

//             UserDto newUser = oauth2UserService.createOAuth2User(oauth2UserDto);

//             Map<String, Object> response = new HashMap<>();
//             response.put("success", true);
//             response.put("message", "OAuth2 user created successfully");
//             response.put("user", newUser);
//             response.put("id", newUser.getId());
//             response.put("email", newUser.getEmail());
//             response.put("provider", newUser.getOauthProvider());

//             return ResponseEntity.status(HttpStatus.CREATED).body(response);

//         } catch (Exception e) {
//             logger.error("Error creating OAuth2 user", e);
//             Map<String, Object> errorResponse = new HashMap<>();
//             errorResponse.put("success", false);
//             errorResponse.put("error", "Failed to create OAuth2 user");
//             errorResponse.put("message", e.getMessage());
//             return ResponseEntity.badRequest().body(errorResponse);
//         }
//     }

//     /**
//      * Cập nhật user OAuth2
//      * PUT /users/oauth2/update/{userId}
//      */
//     @PutMapping("/update/{userId}")
//     public ResponseEntity<Map<String, Object>> updateOAuth2User(
//             @PathVariable("userId") Long userId,
//             @RequestBody OAuth2UserDto oauth2UserDto) {
//         try {
//             logger.info("Updating OAuth2 user: userId={}, email={}", userId, oauth2UserDto.getEmail());

//             UserDto updatedUser = oauth2UserService.updateOAuth2User(userId, oauth2UserDto);

//             Map<String, Object> response = new HashMap<>();
//             response.put("success", true);
//             response.put("message", "OAuth2 user updated successfully");
//             response.put("user", updatedUser);
//             response.put("id", updatedUser.getId());
//             response.put("email", updatedUser.getEmail());
//             response.put("provider", updatedUser.getOauthProvider());

//             return ResponseEntity.ok(response);

//         } catch (Exception e) {
//             logger.error("Error updating OAuth2 user for userId: {}", userId, e);
//             Map<String, Object> errorResponse = new HashMap<>();
//             errorResponse.put("success", false);
//             errorResponse.put("error", "Failed to update OAuth2 user");
//             errorResponse.put("message", e.getMessage());
//             return ResponseEntity.badRequest().body(errorResponse);
//         }
//     }

//     /**
//      * Lấy user theo email và provider
//      * GET /users/oauth2/find?email={email}&provider={provider}
//      */
//     @GetMapping("/find")
//     public ResponseEntity<Map<String, Object>> findUserByEmailAndProvider(
//             @RequestParam String email,
//             @RequestParam String provider) {
//         try {
//             logger.info("Finding user by email and provider: email={}, provider={}", email, provider);

//             UserDto user = oauth2UserService.findByEmailAndProvider(email, provider);

//             if (user != null) {
//                 Map<String, Object> response = new HashMap<>();
//                 response.put("found", true);
//                 response.put("user", user);
//                 return ResponseEntity.ok(response);
//             } else {
//                 Map<String, Object> response = new HashMap<>();
//                 response.put("found", false);
//                 response.put("message", "User not found");
//                 return ResponseEntity.notFound().build();
//             }

//         } catch (Exception e) {
//             logger.error("Error finding user by email and provider", e);
//             Map<String, Object> errorResponse = new HashMap<>();
//             errorResponse.put("error", "Failed to find user");
//             errorResponse.put("message", e.getMessage());
//             return ResponseEntity.badRequest().body(errorResponse);
//         }
//     }
// }