package com.example.cvservice.services.grpcs.clients;

import com.example.cvservice.dtos.UserDto;
import com.example.grpc.user.*;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserGrpcClient {

    @GrpcClient("user-service")
    private UserServiceGrpc.UserServiceBlockingStub userServiceStub;

    public UserDto findUserById(UUID userId) {
        try {
            FindUserByIdRequest request = FindUserByIdRequest.newBuilder()
                    .setUserId(userId.toString())
                    .build();

            UserResponse response = userServiceStub.findUserById(request);

            if (response.getCode() == 200 && response.hasUser()) {
                return convertToUserDto(response.getUser());
            }
            return null;
        } catch (StatusRuntimeException e) {
            System.err.println("gRPC call failed: " + e.getStatus());
            return null;
        }
    }

    private UserDto convertToUserDto(User protoUser) {
        UserDto userDto = new UserDto();
        userDto.setId(UUID.fromString(protoUser.getId()));
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
