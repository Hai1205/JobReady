package com.example.statsservice.services.grpcs.clients;

import com.example.grpc.cv.*;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
public class CVGrpcClient {

    @GrpcClient("cv-service")
    private CVServiceGrpc.CVServiceBlockingStub cvServiceStub;

    public GetTotalCVsResponse getTotalCVs() {
        try {
            GetTotalCVsRequest request = GetTotalCVsRequest.newBuilder().build();
            return cvServiceStub.getTotalCVs(request);
        } catch (StatusRuntimeException e) {
            System.err.println("gRPC call failed: " + e.getStatus());
            throw e;
        }
    }

    public GetCVsByVisibilityResponse getCVsByVisibility(boolean isVisibility) {
        try {
            GetCVsByVisibilityRequest request = GetCVsByVisibilityRequest.newBuilder()
                    .setIsVisibility(isVisibility)
                    .build();
            return cvServiceStub.getCVsByVisibility(request);
        } catch (StatusRuntimeException e) {
            System.err.println("gRPC call failed: " + e.getStatus());
            throw e;
        }
    }

    public GetCVsCreatedInRangeResponse getCVsCreatedInRange(String startDate, String endDate) {
        try {
            GetCVsCreatedInRangeRequest request = GetCVsCreatedInRangeRequest.newBuilder()
                    .setStartDate(startDate)
                    .setEndDate(endDate)
                    .build();
            return cvServiceStub.getCVsCreatedInRange(request);
        } catch (StatusRuntimeException e) {
            System.err.println("gRPC call failed: " + e.getStatus());
            throw e;
        }
    }

    public GetRecentCVsResponse getRecentCVs(int limit) {
        try {
            GetRecentCVsRequest request = GetRecentCVsRequest.newBuilder()
                    .setLimit(limit)
                    .build();
            return cvServiceStub.getRecentCVs(request);
        } catch (StatusRuntimeException e) {
            System.err.println("gRPC call failed: " + e.getStatus());
            throw e;
        }
    }
}
