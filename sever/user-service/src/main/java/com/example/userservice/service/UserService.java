package com.example.userservice.service;

import com.example.userservice.dto.UserDto;
import com.example.userservice.dto.response.Response;
import com.example.userservice.dto.response.ResponseData;
import com.example.userservice.entity.User;
import com.example.userservice.mapper.UserMapper;
import com.example.userservice.exception.OurException;
import com.example.userservice.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    public Response createUser(UserDto userDto) {
        Response response = new Response();

        try {
            if (userRepository.existsByUsername(userDto.getUsername())) {
                throw new RuntimeException("Username already exists");
            }
            if (userRepository.existsByEmail(userDto.getEmail())) {
                throw new RuntimeException("Email already exists");
            }

            User user = new User(
                    userDto.getUsername(),
                    passwordEncoder.encode(userDto.getPassword()), // Encoding password
                    userDto.getEmail(),
                    userDto.getFullname());

            // Ensure status is set to PENDING for new users
            user.setStatus(User.UserStatus.PENDING);

            User savedUser = userRepository.save(user);
            UserDto savedUserDto = userMapper.toDto(savedUser);

            ResponseData data = new ResponseData();
            data.setUser(savedUserDto);

            response.setStatusCode(201);
            response.setMessage("User created successfully");
            response.setData(data);
        } catch (OurException e) {
            response.setStatusCode(e.getStatusCode());
            response.setMessage(e.getMessage());
            System.out.println(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
            System.out.println(e.getMessage());
        }

        return response;
    }

    public Response getAllUsers() {
        Response response = new Response();

        try {
            List<UserDto> userDtos = userRepository.findAll().stream()
                    .map(userMapper::toDto)
                    .collect(Collectors.toList());

            ResponseData data = new ResponseData();
            data.setUsers(userDtos);

            response.setStatusCode(200);
            response.setMessage("Users retrieved successfully");
            response.setData(data);
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
            System.out.println(e.getMessage());
        }

        return response;
    }

    public Response getUserById(UUID id) {
        Response response = new Response();

        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            UserDto userDto = userMapper.toDto(user);

            ResponseData data = new ResponseData();
            data.setUser(userDto);

            response.setStatusCode(200);
            response.setMessage("User retrieved successfully");
            response.setData(data);
        } catch (IllegalArgumentException e) {
            response.setStatusCode(400);
            response.setMessage("Invalid user ID format");
            System.out.println(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
            System.out.println(e.getMessage());
        }

        return response;
    }

    public Response updateUser(UUID id, UserDto userDto) {
        Response response = new Response();

        try {
            User existingUser = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            existingUser.setEmail(userDto.getEmail());
            existingUser.setFullname(userDto.getFullname());

            if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
            }

            User updatedUser = userRepository.save(existingUser);
            UserDto updatedUserDto = userMapper.toDto(updatedUser);

            ResponseData data = new ResponseData();
            data.setUser(updatedUserDto);

            response.setStatusCode(200);
            response.setMessage("User updated successfully");
            response.setData(data);
        } catch (OurException e) {
            response.setStatusCode(e.getStatusCode());
            response.setMessage(e.getMessage());
            System.out.println(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
            System.out.println(e.getMessage());
        }

        return response;
    }

    public Response deleteUser(UUID id) {
        Response response = new Response();

        try {
            UUID uuid = id;
            if (!userRepository.existsById(uuid)) {
                throw new RuntimeException("User not found");
            }

            userRepository.deleteById(uuid);

            response.setStatusCode(200);
            response.setMessage("User deleted successfully");
        } catch (OurException e) {
            response.setStatusCode(e.getStatusCode());
            response.setMessage(e.getMessage());
            System.out.println(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
            System.out.println(e.getMessage());
        }

        return response;
    }

    public UserDto findByEmail(String email) {
        logger.info("Finding user by email: {}", email);
        return userRepository.findByEmail(email)
                .map(userMapper::toDto)
                .orElse(null);
    }

        // public Response authenticateUser(String username, String password) {
    //     Response response = new Response();

    //     try {
    //         boolean isAuthenticated = userRepository.findByUsername(username)
    //                 .map(user -> {
    //                     if (user.getStatus() == User.UserStatus.PENDING) {
    //                         throw new OurException(
    //                                 "User account is not verified. Please verify your account first.", 403);
    //                     }
    //                     return passwordEncoder.matches(password, user.getPassword());
    //                 })
    //                 .orElse(false);

    //         if (!isAuthenticated) {
    //             throw new RuntimeException("Invalid username or password");
    //         }

    //         User user = userRepository.findByUsername(username).get(); // We know it exists at this point
    //         UserDto userDto = userMapper.toDto(user);

    //         ResponseData data = new ResponseData();
    //         data.setUser(userDto);
    //         data.setAuthenticated(true);

    //         response.setStatusCode(200);
    //         response.setMessage("User authenticated successfully");
    //         response.setData(data);
    //     } catch (OurException e) {
    //         response.setStatusCode(e.getStatusCode());
    //         response.setMessage(e.getMessage());
    //         System.out.println(e.getMessage());
    //     } catch (Exception e) {
    //         response.setStatusCode(500);
    //         response.setMessage(e.getMessage());
    //         System.out.println(e.getMessage());
    //     }

    //     return response;
    // }

    // /**
    //  * Authenticate a user by email and password
    //  * This method is specifically for RabbitMQ authentication requests
    //  * 
    //  * @param email    User email
    //  * @param password User password
    //  * @return UserDto if authenticated, null otherwise
    //  */
    // public UserDto authenticateUserByEmail(String email, String password) {
    //     try {
    //         User user = userRepository.findByEmail(email)
    //                 .orElseThrow(() -> new RuntimeException("User not found"));

    //         // Check if the account is pending verification
    //         if (user.getStatus() == User.UserStatus.PENDING) {
    //             throw new OurException(
    //                     "User account is not verified. Please verify your account first.");
    //         }

    //         // Verify password
    //         if (!passwordEncoder.matches(password, user.getPassword())) {
    //             throw new RuntimeException("Invalid email or password");
    //         }

    //         // Return the user data if authentication is successful
    //         return userMapper.toDto(user);
    //     } catch (Exception e) {
    //         System.out.println("Authentication failed: " + e.getMessage());
    //         return null;
    //     }
    // }

    // public Response activateUser(String username) {
    //     Response response = new Response();

    //     try {
    //         User user = userRepository.findByUsername(username)
    //                 .orElseThrow(() -> new RuntimeException("User not found"));

    //         if (user.getStatus() == User.UserStatus.PENDING) {
    //             user.setStatus(User.UserStatus.ACTIVE);
    //             userRepository.save(user);
    //         }

    //         UserDto userDto = userMapper.toDto(user);

    //         ResponseData data = new ResponseData();
    //         data.setUser(userDto);

    //         response.setStatusCode(200);
    //         response.setMessage("User activated successfully");
    //         response.setData(data);
    //     } catch (OurException e) {
    //         response.setStatusCode(e.getStatusCode());
    //         response.setMessage(e.getMessage());
    //         System.out.println(e.getMessage());
    //     } catch (Exception e) {
    //         response.setStatusCode(500);
    //         response.setMessage(e.getMessage());
    //         System.out.println(e.getMessage());
    //     }

    //     return response;
    // }

    // public Response getUserStatus(String username) {
    //     Response response = new Response();

    //     try {
    //         User.UserStatus status = userRepository.findByUsername(username)
    //                 .map(User::getStatus)
    //                 .orElseThrow(() -> new RuntimeException("User not found"));

    //         ResponseData data = new ResponseData();
    //         data.setStatus(status.toString());

    //         response.setStatusCode(200);
    //         response.setMessage("User status retrieved successfully");
    //         response.setData(data);
    //     } catch (OurException e) {
    //         response.setStatusCode(e.getStatusCode());
    //         response.setMessage(e.getMessage());
    //         System.out.println(e.getMessage());
    //     } catch (Exception e) {
    //         response.setStatusCode(500);
    //         response.setMessage(e.getMessage());
    //         System.out.println(e.getMessage());
    //     }

    //     return response;
    // }

    // /**
    // * Thay đổi mật khẩu cho người dùng
    // *
    // * @param userId ID của người dùng
    // * @param currentPassword Mật khẩu hiện tại
    // * @param newPassword Mật khẩu mới
    // * @return User đã cập nhật
    // */
    // @Transactional
    // public User changePassword(ChangePasswordRequest request) {
    // logger.info("Changing password for user ID: {}", request.getUserId());

    // User user = userRepository.findById(request.getUserId())
    // .orElseThrow(() -> new OurException("User not found with ID: " +
    // request.getUserId(), 404));

    // // Kiểm tra mật khẩu hiện tại
    // if (!passwordEncoder.matches(request.getCurrentPassword(),
    // user.getPassword())) {
    // throw new OurException("Current password is incorrect", 400);
    // }

    // // Cập nhật mật khẩu mới
    // user.setPassword(passwordEncoder.encode(request.getNewPassword()));
    // return userRepository.save(user);
    // }

    // /**
    // * Đặt lại mật khẩu cho người dùng
    // *
    // * @param userId ID của người dùng
    // * @param newPassword Mật khẩu mới
    // * @return User đã cập nhật
    // */
    // @Transactional
    // public User resetPassword(ResetPasswordRequest request) {
    // logger.info("Resetting password for user ID: {}", request.getUserId());

    // User user = userRepository.findById(request.getUserId())
    // .orElseThrow(() -> new OurException("User not found with ID: " +
    // request.getUserId(), 404));

    // // Cập nhật mật khẩu mới
    // user.setPassword(passwordEncoder.encode(request.getNewPassword()));
    // return userRepository.save(user);
    // }

    // /**
    // * Đặt lại mật khẩu bằng email
    // *
    // * @param email Email của người dùng
    // * @param newPassword Mật khẩu mới
    // * @return User đã cập nhật
    // */
    // @Transactional
    // public User resetPasswordByEmail(String email, String newPassword) {
    // logger.info("Resetting password for email: {}", email);

    // User user = userRepository.findByEmail(email)
    // .orElseThrow(() -> new OurException("User not found with email: " + email,
    // 404));

    // // Cập nhật mật khẩu mới
    // user.setPassword(passwordEncoder.encode(newPassword));
    // return userRepository.save(user);
    // }

    // /**
    // * Thay đổi trạng thái người dùng (kích hoạt/vô hiệu hóa)
    // *
    // * @param userId ID của người dùng
    // * @param status Trạng thái mới (true: kích hoạt, false: vô hiệu hóa)
    // * @return User đã cập nhật
    // */
    // @Transactional
    // public User changeStatus(ChangeStatusRequest request) {
    // logger.info("Changing status for user ID: {} to {}", request.getUserId(),
    // request.getStatus());

    // User user = userRepository.findById(request.getUserId())
    // .orElseThrow(() -> new OurException("User not found with ID: " +
    // request.getUserId(), 404));

    // // Cập nhật trạng thái
    // boolean status = "enable".equalsIgnoreCase(request.getStatus());
    // if (status) {
    // user.setStatus(User.UserStatus.ACTIVE);
    // } else {
    // user.setStatus(User.UserStatus.INACTIVE);
    // }

    // return userRepository.save(user);
    // }
}