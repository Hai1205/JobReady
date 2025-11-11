package com.example.userservice.services.grpc;

import com.example.grpc.user.*;
import com.example.userservice.dtos.UserDto;
import com.example.userservice.services.apis.UserService;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

@GrpcService
public class UserGrpcServer extends UserServiceGrpc.UserServiceImplBase {

    @Autowired
    private UserService userService;

    @Override
    public void findUserById(FindUserByIdRequest request, StreamObserver<UserResponse> responseObserver) {
        try {
            UUID userId = UUID.fromString(request.getUserId());
            UserDto userData = userService.handleFindById(userId);

            UserResponse response;
            if (userData != null) {
                response = UserResponse.newBuilder()
                        .setCode(200)
                        .setMessage("User found successfully")
                        .setUser(convertToProtoUser(userData))
                        .build();
            } else {
                response = UserResponse.newBuilder()
                        .setCode(404)
                        .setMessage("User not found")
                        .build();
            }

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onNext(UserResponse.newBuilder()
                    .setCode(500)
                    .setMessage("Error finding user by ID: " + e.getMessage())
                    .build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void findUserByEmail(FindUserByEmailRequest request, StreamObserver<UserResponse> responseObserver) {
        try {
            UserDto userData = userService.handleFindByEmail(request.getEmail());

            UserResponse response;
            if (userData != null) {
                response = UserResponse.newBuilder()
                        .setCode(200)
                        .setMessage("User found successfully")
                        .setUser(convertToProtoUser(userData))
                        .build();
            } else {
                response = UserResponse.newBuilder()
                        .setCode(404)
                        .setMessage("User not found")
                        .build();
            }

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onNext(UserResponse.newBuilder()
                    .setCode(500)
                    .setMessage("Error finding user by email: " + e.getMessage())
                    .build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void findUserByIdentifier(FindUserByIdentifierRequest request,
            StreamObserver<UserResponse> responseObserver) {
        try {
            UserDto userData = userService.handleFindByIdentifier(request.getIdentifier());

            UserResponse response;
            if (userData != null) {
                response = UserResponse.newBuilder()
                        .setCode(200)
                        .setMessage("User found successfully")
                        .setUser(convertToProtoUser(userData))
                        .build();
            } else {
                response = UserResponse.newBuilder()
                        .setCode(404)
                        .setMessage("User not found")
                        .build();
            }

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onNext(UserResponse.newBuilder()
                    .setCode(500)
                    .setMessage("Error finding user by identifier: " + e.getMessage())
                    .build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void createUser(CreateUserRequest request, StreamObserver<UserResponse> responseObserver) {
        try {
            UserDto userData = userService.handleCreateUser(
                    request.getUsername(),
                    request.getEmail(),
                    request.getPassword(),
                    request.getFullname(),
                    "user",
                    "pending",
                    null);

            UserResponse response;
            if (userData != null) {
                response = UserResponse.newBuilder()
                        .setCode(200)
                        .setMessage("User created successfully")
                        .setUser(convertToProtoUser(userData))
                        .build();
            } else {
                response = UserResponse.newBuilder()
                        .setCode(400)
                        .setMessage("User creation failed")
                        .build();
            }

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onNext(UserResponse.newBuilder()
                    .setCode(500)
                    .setMessage("Error creating user: " + e.getMessage())
                    .build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void activateUser(ActivateUserRequest request, StreamObserver<UserResponse> responseObserver) {
        try {
            UserDto userData = userService.handleActivateUser(request.getEmail());

            UserResponse response;
            if (userData != null) {
                response = UserResponse.newBuilder()
                        .setCode(200)
                        .setMessage("User activated successfully")
                        .setUser(convertToProtoUser(userData))
                        .build();
            } else {
                response = UserResponse.newBuilder()
                        .setCode(404)
                        .setMessage("User not found")
                        .build();
            }

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onNext(UserResponse.newBuilder()
                    .setCode(500)
                    .setMessage("Error activating user: " + e.getMessage())
                    .build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void authenticateUser(AuthenticateUserRequest request, StreamObserver<UserResponse> responseObserver) {
        try {
            UserDto userData = userService.handleAuthenticateUser(
                    request.getIdentifier(),
                    request.getPassword());

            UserResponse response;
            if (userData != null) {
                response = UserResponse.newBuilder()
                        .setCode(200)
                        .setMessage("Authentication successful")
                        .setUser(convertToProtoUser(userData))
                        .build();
            } else {
                response = UserResponse.newBuilder()
                        .setCode(404)
                        .setMessage("User not found")
                        .build();
            }

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onNext(UserResponse.newBuilder()
                    .setCode(500)
                    .setMessage("Error authenticating user: " + e.getMessage())
                    .build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void changePassword(ChangePasswordRequest request, StreamObserver<UserResponse> responseObserver) {
        try {
            UserDto userData = userService.handleChangePasswordUser(
                    request.getIdentifier(),
                    request.getCurrentPassword(),
                    request.getNewPassword());

            UserResponse response;
            if (userData != null) {
                response = UserResponse.newBuilder()
                        .setCode(200)
                        .setMessage("Password changed successfully")
                        .setUser(convertToProtoUser(userData))
                        .build();
            } else {
                response = UserResponse.newBuilder()
                        .setCode(404)
                        .setMessage("User not found")
                        .build();
            }

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onNext(UserResponse.newBuilder()
                    .setCode(500)
                    .setMessage("Error changing password: " + e.getMessage())
                    .build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest request, StreamObserver<UserResponse> responseObserver) {
        try {
            UserDto userData = userService.handleForgotPasswordUser(
                    request.getEmail(),
                    request.getNewPassword());

            UserResponse response;
            if (userData != null) {
                response = UserResponse.newBuilder()
                        .setCode(200)
                        .setMessage("Password reset successful")
                        .setUser(convertToProtoUser(userData))
                        .build();
            } else {
                response = UserResponse.newBuilder()
                        .setCode(404)
                        .setMessage("User not found")
                        .build();
            }

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onNext(UserResponse.newBuilder()
                    .setCode(500)
                    .setMessage("Error resetting password: " + e.getMessage())
                    .build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void resetPassword(ResetPasswordRequest request, StreamObserver<ResetPasswordResponse> responseObserver) {
        try {
            String newPassword = userService.handleResetPasswordUser(request.getEmail());

            ResetPasswordResponse response;
            if (newPassword != null) {
                response = ResetPasswordResponse.newBuilder()
                        .setCode(200)
                        .setMessage("Password reset successful")
                        .setNewPassword(newPassword)
                        .build();
            } else {
                response = ResetPasswordResponse.newBuilder()
                        .setCode(404)
                        .setMessage("User not found")
                        .build();
            }

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onNext(ResetPasswordResponse.newBuilder()
                    .setCode(500)
                    .setMessage("Error resetting password: " + e.getMessage())
                    .build());
            responseObserver.onCompleted();
        }
    }

    private User convertToProtoUser(UserDto userDto) {
        User.Builder userBuilder = User.newBuilder()
                .setId(userDto.getId().toString())
                .setUsername(userDto.getUsername())
                .setEmail(userDto.getEmail())
                .setFullname(userDto.getFullname())
                .setRole(userDto.getRole())
                .setStatus(userDto.getStatus());

        if (userDto.getAvatarUrl() != null) {
            userBuilder.setAvatar(userDto.getAvatarUrl());
        }
        // if (userDto.getCreatedAt() != null) {
        // userBuilder.setCreatedAt(userDto.getCreatedAt().toString());
        // }
        // if (userDto.getUpdatedAt() != null) {
        // userBuilder.setUpdatedAt(userDto.getUpdatedAt().toString());
        // }

        return userBuilder.build();
    }
}
