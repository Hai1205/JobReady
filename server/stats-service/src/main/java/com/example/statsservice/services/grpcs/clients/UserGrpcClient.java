package com.example.statsservice.services.grpcs.clients;

import com.example.grpc.user.*;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
public class UserGrpcClient {

    @GrpcClient("user-service")
    private UserServiceGrpc.UserServiceBlockingStub userServiceStub;

    public GetUserStatsResponse getUserStats() {
        try {
            GetUserStatsRequest request = GetUserStatsRequest.newBuilder().build();
            return userServiceStub.getUserStats(request);
        } catch (StatusRuntimeException e) {
            System.err.println("gRPC call failed: " + e.getStatus());
            throw e;
        }
    }

    public GetUsersCreatedInRangeResponse getUsersCreatedInRange(String startDate, String endDate) {
        try {
            GetUsersCreatedInRangeRequest request = GetUsersCreatedInRangeRequest.newBuilder()
                    .setStartDate(startDate)
                    .setEndDate(endDate)
                    .build();
            return userServiceStub.getUsersCreatedInRange(request);
        } catch (StatusRuntimeException e) {
            System.err.println("gRPC call failed: " + e.getStatus());
            throw e;
        }
    }

    public GetRecentUsersResponse getRecentUsers(int limit) {
        try {
            GetRecentUsersRequest request = GetRecentUsersRequest.newBuilder()
                    .setLimit(limit)
                    .build();
            return userServiceStub.getRecentUsers(request);
        } catch (StatusRuntimeException e) {
            System.err.println("gRPC call failed: " + e.getStatus());
            throw e;
        }
    }
}
