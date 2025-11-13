package com.example.cvservice.services.grpcs.servers;

import com.example.cvservice.repositoryies.CVRepository;
import com.example.grpc.cv.*;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.time.Instant;
import java.util.stream.Collectors;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class CVGrpcServer extends CVServiceGrpc.CVServiceImplBase {

    private final CVRepository cvRepository;

    @Override
    public void getTotalCVs(GetTotalCVsRequest request, StreamObserver<GetTotalCVsResponse> responseObserver) {
        try {
            long total = cvRepository.count();

            GetTotalCVsResponse response = GetTotalCVsResponse.newBuilder()
                    .setCode(200)
                    .setMessage("Success")
                    .setTotal(total)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

            log.info("getTotalCVs: {}", total);
        } catch (Exception e) {
            log.error("Error getting total CVs", e);
            responseObserver.onError(e);
        }
    }

    @Override
    public void getCVsByVisibility(GetCVsByVisibilityRequest request,
            StreamObserver<GetCVsByVisibilityResponse> responseObserver) {
        try {
            long count = cvRepository.findAll().stream()
                    .filter(cv -> cv.getIsVisibility().equals(request.getIsVisibility()))
                    .count();

            GetCVsByVisibilityResponse response = GetCVsByVisibilityResponse.newBuilder()
                    .setCode(200)
                    .setMessage("Success")
                    .setCount(count)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

            log.info("getCVsByVisibility: visibility={}, count={}", request.getIsVisibility(), count);
        } catch (Exception e) {
            log.error("Error getting CVs by visibility", e);
            responseObserver.onError(e);
        }
    }

    @Override
    public void getCVsCreatedInRange(GetCVsCreatedInRangeRequest request,
            StreamObserver<GetCVsCreatedInRangeResponse> responseObserver) {
        try {
            Instant startDate = Instant.parse(request.getStartDate());
            Instant endDate = Instant.parse(request.getEndDate());

            long count = cvRepository.findAll().stream()
                    .filter(cv -> cv.getCreatedAt() != null &&
                            !cv.getCreatedAt().isBefore(startDate) &&
                            !cv.getCreatedAt().isAfter(endDate))
                    .count();

            GetCVsCreatedInRangeResponse response = GetCVsCreatedInRangeResponse.newBuilder()
                    .setCode(200)
                    .setMessage("Success")
                    .setCount(count)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

            log.info("getCVsCreatedInRange: start={}, end={}, count={}",
                    request.getStartDate(), request.getEndDate(), count);
        } catch (Exception e) {
            log.error("Error getting CVs created in range", e);
            responseObserver.onError(e);
        }
    }

    @Override
    public void getRecentCVs(GetRecentCVsRequest request,
            StreamObserver<GetRecentCVsResponse> responseObserver) {
        try {
            var recentCVs = cvRepository.findAll().stream()
                    .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                    .limit(request.getLimit())
                    .map(cv -> CVInfo.newBuilder()
                            .setId(cv.getId().toString())
                            .setTitle(cv.getTitle())
                            .setUserId(cv.getUserId().toString())
                            .setCreatedAt(cv.getCreatedAt().toString())
                            .setIsVisibility(cv.getIsVisibility())
                            .build())
                    .collect(Collectors.toList());

            GetRecentCVsResponse response = GetRecentCVsResponse.newBuilder()
                    .setCode(200)
                    .setMessage("Success")
                    .addAllCvs(recentCVs)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

            log.info("getRecentCVs: limit={}, found={}", request.getLimit(), recentCVs.size());
        } catch (Exception e) {
            log.error("Error getting recent CVs", e);
            responseObserver.onError(e);
        }
    }
}
