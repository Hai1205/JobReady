package com.example.authservice.services.grpcs;

import com.example.authservice.dtos.UserDto;
import com.example.grpc.user.*;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
public class UserGrpcClient {

    @GrpcClient("user-service")
    private UserServiceGrpc.UserServiceBlockingStub userServiceStub;

    public UserDto findUserByEmail(String email) {
        try {
            FindUserByEmailRequest request = FindUserByEmailRequest.newBuilder()
                    .setEmail(email)
                    .build();

            UserResponse response = userServiceStub.findUserByEmail(request);

            if (response.getCode() == 200 && response.hasUser()) {
                return convertToUserDto(response.getUser());
            }
            return null;
        } catch (StatusRuntimeException e) {
            System.err.println("gRPC call failed: " + e.getStatus());
            return null;
        }
    }

    public UserDto findUserByIdentifier(String identifier) {
        try {
            FindUserByIdentifierRequest request = FindUserByIdentifierRequest.newBuilder()
                    .setIdentifier(identifier)
                    .build();

            UserResponse response = userServiceStub.findUserByIdentifier(request);

            if (response.getCode() == 200 && response.hasUser()) {
                return convertToUserDto(response.getUser());
            }
            return null;
        } catch (StatusRuntimeException e) {
            System.err.println("gRPC call failed: " + e.getStatus());
            return null;
        }
    }

    public UserDto createUser(String username, String email, String password, String fullname) {
        try {
            CreateUserRequest request = CreateUserRequest.newBuilder()
                    .setUsername(username)
                    .setEmail(email)
                    .setPassword(password)
                    .setFullname(fullname)
                    .build();

            UserResponse response = userServiceStub.createUser(request);

            if (response.getCode() == 200 && response.hasUser()) {
                return convertToUserDto(response.getUser());
            }
            return null;
        } catch (StatusRuntimeException e) {
            System.err.println("gRPC call failed: " + e.getStatus());
            return null;
        }
    }

    public UserDto activateUser(String email) {
        try {
            ActivateUserRequest request = ActivateUserRequest.newBuilder()
                    .setEmail(email)
                    .build();

            UserResponse response = userServiceStub.activateUser(request);

            if (response.getCode() == 200 && response.hasUser()) {
                return convertToUserDto(response.getUser());
            }
            return null;
        } catch (StatusRuntimeException e) {
            System.err.println("gRPC call failed: " + e.getStatus());
            return null;
        }
    }

    public UserDto authenticateUser(String identifier, String password) {
        try {
            AuthenticateUserRequest request = AuthenticateUserRequest.newBuilder()
                    .setIdentifier(identifier)
                    .setPassword(password)
                    .build();

            UserResponse response = userServiceStub.authenticateUser(request);

            if (response.getCode() == 200 && response.hasUser()) {
                return convertToUserDto(response.getUser());
            }
            return null;
        } catch (StatusRuntimeException e) {
            System.err.println("gRPC call failed: " + e.getStatus());
            return null;
        }
    }

    public UserDto changePassword(String identifier, String currentPassword, String newPassword) {
        try {
            ChangePasswordRequest request = ChangePasswordRequest.newBuilder()
                    .setIdentifier(identifier)
                    .setCurrentPassword(currentPassword)
                    .setNewPassword(newPassword)
                    .build();

            UserResponse response = userServiceStub.changePassword(request);

            if (response.getCode() == 200 && response.hasUser()) {
                return convertToUserDto(response.getUser());
            }
            return null;
        } catch (StatusRuntimeException e) {
            System.err.println("gRPC call failed: " + e.getStatus());
            return null;
        }
    }

    public UserDto forgotPassword(String email, String newPassword) {
        try {
            ForgotPasswordRequest request = ForgotPasswordRequest.newBuilder()
                    .setEmail(email)
                    .setNewPassword(newPassword)
                    .build();

            UserResponse response = userServiceStub.forgotPassword(request);

            if (response.getCode() == 200 && response.hasUser()) {
                return convertToUserDto(response.getUser());
            }
            return null;
        } catch (StatusRuntimeException e) {
            System.err.println("gRPC call failed: " + e.getStatus());
            return null;
        }
    }

    public String resetPassword(String email) {
        try {
            ResetPasswordRequest request = ResetPasswordRequest.newBuilder()
                    .setEmail(email)
                    .build();

            ResetPasswordResponse response = userServiceStub.resetPassword(request);

            if (response.getCode() == 200) {
                return response.getNewPassword();
            }
            return null;
        } catch (StatusRuntimeException e) {
            System.err.println("gRPC call failed: " + e.getStatus());
            return null;
        }
    }

    private UserDto convertToUserDto(User protoUser) {
        UserDto userDto = new UserDto();
        userDto.setId(java.util.UUID.fromString(protoUser.getId()));
        userDto.setUsername(protoUser.getUsername());
        userDto.setEmail(protoUser.getEmail());
        userDto.setFullname(protoUser.getFullname());
        userDto.setRole(protoUser.getRole());
        userDto.setStatus(protoUser.getStatus());

        if (!protoUser.getAvatar().isEmpty()) {
            userDto.setAvatarUrl(protoUser.getAvatar());
        }

        return userDto;
    }
}
