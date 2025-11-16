package com.example.grpc.cv;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 * <pre>
 * CV Service Definition
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.58.0)",
    comments = "Source: cv_service.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class CVServiceGrpc {

  private CVServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "cv.CVService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.example.grpc.cv.GetTotalCVsRequest,
      com.example.grpc.cv.GetTotalCVsResponse> getGetTotalCVsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetTotalCVs",
      requestType = com.example.grpc.cv.GetTotalCVsRequest.class,
      responseType = com.example.grpc.cv.GetTotalCVsResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.example.grpc.cv.GetTotalCVsRequest,
      com.example.grpc.cv.GetTotalCVsResponse> getGetTotalCVsMethod() {
    io.grpc.MethodDescriptor<com.example.grpc.cv.GetTotalCVsRequest, com.example.grpc.cv.GetTotalCVsResponse> getGetTotalCVsMethod;
    if ((getGetTotalCVsMethod = CVServiceGrpc.getGetTotalCVsMethod) == null) {
      synchronized (CVServiceGrpc.class) {
        if ((getGetTotalCVsMethod = CVServiceGrpc.getGetTotalCVsMethod) == null) {
          CVServiceGrpc.getGetTotalCVsMethod = getGetTotalCVsMethod =
              io.grpc.MethodDescriptor.<com.example.grpc.cv.GetTotalCVsRequest, com.example.grpc.cv.GetTotalCVsResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetTotalCVs"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.grpc.cv.GetTotalCVsRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.grpc.cv.GetTotalCVsResponse.getDefaultInstance()))
              .setSchemaDescriptor(new CVServiceMethodDescriptorSupplier("GetTotalCVs"))
              .build();
        }
      }
    }
    return getGetTotalCVsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.example.grpc.cv.GetCVsByVisibilityRequest,
      com.example.grpc.cv.GetCVsByVisibilityResponse> getGetCVsByVisibilityMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetCVsByVisibility",
      requestType = com.example.grpc.cv.GetCVsByVisibilityRequest.class,
      responseType = com.example.grpc.cv.GetCVsByVisibilityResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.example.grpc.cv.GetCVsByVisibilityRequest,
      com.example.grpc.cv.GetCVsByVisibilityResponse> getGetCVsByVisibilityMethod() {
    io.grpc.MethodDescriptor<com.example.grpc.cv.GetCVsByVisibilityRequest, com.example.grpc.cv.GetCVsByVisibilityResponse> getGetCVsByVisibilityMethod;
    if ((getGetCVsByVisibilityMethod = CVServiceGrpc.getGetCVsByVisibilityMethod) == null) {
      synchronized (CVServiceGrpc.class) {
        if ((getGetCVsByVisibilityMethod = CVServiceGrpc.getGetCVsByVisibilityMethod) == null) {
          CVServiceGrpc.getGetCVsByVisibilityMethod = getGetCVsByVisibilityMethod =
              io.grpc.MethodDescriptor.<com.example.grpc.cv.GetCVsByVisibilityRequest, com.example.grpc.cv.GetCVsByVisibilityResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetCVsByVisibility"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.grpc.cv.GetCVsByVisibilityRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.grpc.cv.GetCVsByVisibilityResponse.getDefaultInstance()))
              .setSchemaDescriptor(new CVServiceMethodDescriptorSupplier("GetCVsByVisibility"))
              .build();
        }
      }
    }
    return getGetCVsByVisibilityMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.example.grpc.cv.GetCVsCreatedInRangeRequest,
      com.example.grpc.cv.GetCVsCreatedInRangeResponse> getGetCVsCreatedInRangeMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetCVsCreatedInRange",
      requestType = com.example.grpc.cv.GetCVsCreatedInRangeRequest.class,
      responseType = com.example.grpc.cv.GetCVsCreatedInRangeResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.example.grpc.cv.GetCVsCreatedInRangeRequest,
      com.example.grpc.cv.GetCVsCreatedInRangeResponse> getGetCVsCreatedInRangeMethod() {
    io.grpc.MethodDescriptor<com.example.grpc.cv.GetCVsCreatedInRangeRequest, com.example.grpc.cv.GetCVsCreatedInRangeResponse> getGetCVsCreatedInRangeMethod;
    if ((getGetCVsCreatedInRangeMethod = CVServiceGrpc.getGetCVsCreatedInRangeMethod) == null) {
      synchronized (CVServiceGrpc.class) {
        if ((getGetCVsCreatedInRangeMethod = CVServiceGrpc.getGetCVsCreatedInRangeMethod) == null) {
          CVServiceGrpc.getGetCVsCreatedInRangeMethod = getGetCVsCreatedInRangeMethod =
              io.grpc.MethodDescriptor.<com.example.grpc.cv.GetCVsCreatedInRangeRequest, com.example.grpc.cv.GetCVsCreatedInRangeResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetCVsCreatedInRange"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.grpc.cv.GetCVsCreatedInRangeRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.grpc.cv.GetCVsCreatedInRangeResponse.getDefaultInstance()))
              .setSchemaDescriptor(new CVServiceMethodDescriptorSupplier("GetCVsCreatedInRange"))
              .build();
        }
      }
    }
    return getGetCVsCreatedInRangeMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.example.grpc.cv.GetRecentCVsRequest,
      com.example.grpc.cv.GetRecentCVsResponse> getGetRecentCVsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetRecentCVs",
      requestType = com.example.grpc.cv.GetRecentCVsRequest.class,
      responseType = com.example.grpc.cv.GetRecentCVsResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.example.grpc.cv.GetRecentCVsRequest,
      com.example.grpc.cv.GetRecentCVsResponse> getGetRecentCVsMethod() {
    io.grpc.MethodDescriptor<com.example.grpc.cv.GetRecentCVsRequest, com.example.grpc.cv.GetRecentCVsResponse> getGetRecentCVsMethod;
    if ((getGetRecentCVsMethod = CVServiceGrpc.getGetRecentCVsMethod) == null) {
      synchronized (CVServiceGrpc.class) {
        if ((getGetRecentCVsMethod = CVServiceGrpc.getGetRecentCVsMethod) == null) {
          CVServiceGrpc.getGetRecentCVsMethod = getGetRecentCVsMethod =
              io.grpc.MethodDescriptor.<com.example.grpc.cv.GetRecentCVsRequest, com.example.grpc.cv.GetRecentCVsResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetRecentCVs"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.grpc.cv.GetRecentCVsRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.grpc.cv.GetRecentCVsResponse.getDefaultInstance()))
              .setSchemaDescriptor(new CVServiceMethodDescriptorSupplier("GetRecentCVs"))
              .build();
        }
      }
    }
    return getGetRecentCVsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.example.grpc.cv.CreateCVRequest,
      com.example.grpc.cv.CreateCVResponse> getCreateCVMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CreateCV",
      requestType = com.example.grpc.cv.CreateCVRequest.class,
      responseType = com.example.grpc.cv.CreateCVResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.example.grpc.cv.CreateCVRequest,
      com.example.grpc.cv.CreateCVResponse> getCreateCVMethod() {
    io.grpc.MethodDescriptor<com.example.grpc.cv.CreateCVRequest, com.example.grpc.cv.CreateCVResponse> getCreateCVMethod;
    if ((getCreateCVMethod = CVServiceGrpc.getCreateCVMethod) == null) {
      synchronized (CVServiceGrpc.class) {
        if ((getCreateCVMethod = CVServiceGrpc.getCreateCVMethod) == null) {
          CVServiceGrpc.getCreateCVMethod = getCreateCVMethod =
              io.grpc.MethodDescriptor.<com.example.grpc.cv.CreateCVRequest, com.example.grpc.cv.CreateCVResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "CreateCV"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.grpc.cv.CreateCVRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.grpc.cv.CreateCVResponse.getDefaultInstance()))
              .setSchemaDescriptor(new CVServiceMethodDescriptorSupplier("CreateCV"))
              .build();
        }
      }
    }
    return getCreateCVMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static CVServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<CVServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<CVServiceStub>() {
        @java.lang.Override
        public CVServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new CVServiceStub(channel, callOptions);
        }
      };
    return CVServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static CVServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<CVServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<CVServiceBlockingStub>() {
        @java.lang.Override
        public CVServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new CVServiceBlockingStub(channel, callOptions);
        }
      };
    return CVServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static CVServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<CVServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<CVServiceFutureStub>() {
        @java.lang.Override
        public CVServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new CVServiceFutureStub(channel, callOptions);
        }
      };
    return CVServiceFutureStub.newStub(factory, channel);
  }

  /**
   * <pre>
   * CV Service Definition
   * </pre>
   */
  public interface AsyncService {

    /**
     * <pre>
     * Get total CVs count
     * </pre>
     */
    default void getTotalCVs(com.example.grpc.cv.GetTotalCVsRequest request,
        io.grpc.stub.StreamObserver<com.example.grpc.cv.GetTotalCVsResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetTotalCVsMethod(), responseObserver);
    }

    /**
     * <pre>
     * Get CVs by visibility
     * </pre>
     */
    default void getCVsByVisibility(com.example.grpc.cv.GetCVsByVisibilityRequest request,
        io.grpc.stub.StreamObserver<com.example.grpc.cv.GetCVsByVisibilityResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetCVsByVisibilityMethod(), responseObserver);
    }

    /**
     * <pre>
     * Get CVs created in date range
     * </pre>
     */
    default void getCVsCreatedInRange(com.example.grpc.cv.GetCVsCreatedInRangeRequest request,
        io.grpc.stub.StreamObserver<com.example.grpc.cv.GetCVsCreatedInRangeResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetCVsCreatedInRangeMethod(), responseObserver);
    }

    /**
     * <pre>
     * Get recent CVs
     * </pre>
     */
    default void getRecentCVs(com.example.grpc.cv.GetRecentCVsRequest request,
        io.grpc.stub.StreamObserver<com.example.grpc.cv.GetRecentCVsResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetRecentCVsMethod(), responseObserver);
    }

    /**
     * <pre>
     * Create a new CV
     * </pre>
     */
    default void createCV(com.example.grpc.cv.CreateCVRequest request,
        io.grpc.stub.StreamObserver<com.example.grpc.cv.CreateCVResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getCreateCVMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service CVService.
   * <pre>
   * CV Service Definition
   * </pre>
   */
  public static abstract class CVServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return CVServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service CVService.
   * <pre>
   * CV Service Definition
   * </pre>
   */
  public static final class CVServiceStub
      extends io.grpc.stub.AbstractAsyncStub<CVServiceStub> {
    private CVServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CVServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new CVServiceStub(channel, callOptions);
    }

    /**
     * <pre>
     * Get total CVs count
     * </pre>
     */
    public void getTotalCVs(com.example.grpc.cv.GetTotalCVsRequest request,
        io.grpc.stub.StreamObserver<com.example.grpc.cv.GetTotalCVsResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetTotalCVsMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Get CVs by visibility
     * </pre>
     */
    public void getCVsByVisibility(com.example.grpc.cv.GetCVsByVisibilityRequest request,
        io.grpc.stub.StreamObserver<com.example.grpc.cv.GetCVsByVisibilityResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetCVsByVisibilityMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Get CVs created in date range
     * </pre>
     */
    public void getCVsCreatedInRange(com.example.grpc.cv.GetCVsCreatedInRangeRequest request,
        io.grpc.stub.StreamObserver<com.example.grpc.cv.GetCVsCreatedInRangeResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetCVsCreatedInRangeMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Get recent CVs
     * </pre>
     */
    public void getRecentCVs(com.example.grpc.cv.GetRecentCVsRequest request,
        io.grpc.stub.StreamObserver<com.example.grpc.cv.GetRecentCVsResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetRecentCVsMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Create a new CV
     * </pre>
     */
    public void createCV(com.example.grpc.cv.CreateCVRequest request,
        io.grpc.stub.StreamObserver<com.example.grpc.cv.CreateCVResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getCreateCVMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service CVService.
   * <pre>
   * CV Service Definition
   * </pre>
   */
  public static final class CVServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<CVServiceBlockingStub> {
    private CVServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CVServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new CVServiceBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * Get total CVs count
     * </pre>
     */
    public com.example.grpc.cv.GetTotalCVsResponse getTotalCVs(com.example.grpc.cv.GetTotalCVsRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetTotalCVsMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Get CVs by visibility
     * </pre>
     */
    public com.example.grpc.cv.GetCVsByVisibilityResponse getCVsByVisibility(com.example.grpc.cv.GetCVsByVisibilityRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetCVsByVisibilityMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Get CVs created in date range
     * </pre>
     */
    public com.example.grpc.cv.GetCVsCreatedInRangeResponse getCVsCreatedInRange(com.example.grpc.cv.GetCVsCreatedInRangeRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetCVsCreatedInRangeMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Get recent CVs
     * </pre>
     */
    public com.example.grpc.cv.GetRecentCVsResponse getRecentCVs(com.example.grpc.cv.GetRecentCVsRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetRecentCVsMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Create a new CV
     * </pre>
     */
    public com.example.grpc.cv.CreateCVResponse createCV(com.example.grpc.cv.CreateCVRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCreateCVMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service CVService.
   * <pre>
   * CV Service Definition
   * </pre>
   */
  public static final class CVServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<CVServiceFutureStub> {
    private CVServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CVServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new CVServiceFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * Get total CVs count
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.example.grpc.cv.GetTotalCVsResponse> getTotalCVs(
        com.example.grpc.cv.GetTotalCVsRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetTotalCVsMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Get CVs by visibility
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.example.grpc.cv.GetCVsByVisibilityResponse> getCVsByVisibility(
        com.example.grpc.cv.GetCVsByVisibilityRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetCVsByVisibilityMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Get CVs created in date range
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.example.grpc.cv.GetCVsCreatedInRangeResponse> getCVsCreatedInRange(
        com.example.grpc.cv.GetCVsCreatedInRangeRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetCVsCreatedInRangeMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Get recent CVs
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.example.grpc.cv.GetRecentCVsResponse> getRecentCVs(
        com.example.grpc.cv.GetRecentCVsRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetRecentCVsMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Create a new CV
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.example.grpc.cv.CreateCVResponse> createCV(
        com.example.grpc.cv.CreateCVRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getCreateCVMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_GET_TOTAL_CVS = 0;
  private static final int METHODID_GET_CVS_BY_VISIBILITY = 1;
  private static final int METHODID_GET_CVS_CREATED_IN_RANGE = 2;
  private static final int METHODID_GET_RECENT_CVS = 3;
  private static final int METHODID_CREATE_CV = 4;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_GET_TOTAL_CVS:
          serviceImpl.getTotalCVs((com.example.grpc.cv.GetTotalCVsRequest) request,
              (io.grpc.stub.StreamObserver<com.example.grpc.cv.GetTotalCVsResponse>) responseObserver);
          break;
        case METHODID_GET_CVS_BY_VISIBILITY:
          serviceImpl.getCVsByVisibility((com.example.grpc.cv.GetCVsByVisibilityRequest) request,
              (io.grpc.stub.StreamObserver<com.example.grpc.cv.GetCVsByVisibilityResponse>) responseObserver);
          break;
        case METHODID_GET_CVS_CREATED_IN_RANGE:
          serviceImpl.getCVsCreatedInRange((com.example.grpc.cv.GetCVsCreatedInRangeRequest) request,
              (io.grpc.stub.StreamObserver<com.example.grpc.cv.GetCVsCreatedInRangeResponse>) responseObserver);
          break;
        case METHODID_GET_RECENT_CVS:
          serviceImpl.getRecentCVs((com.example.grpc.cv.GetRecentCVsRequest) request,
              (io.grpc.stub.StreamObserver<com.example.grpc.cv.GetRecentCVsResponse>) responseObserver);
          break;
        case METHODID_CREATE_CV:
          serviceImpl.createCV((com.example.grpc.cv.CreateCVRequest) request,
              (io.grpc.stub.StreamObserver<com.example.grpc.cv.CreateCVResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getGetTotalCVsMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.example.grpc.cv.GetTotalCVsRequest,
              com.example.grpc.cv.GetTotalCVsResponse>(
                service, METHODID_GET_TOTAL_CVS)))
        .addMethod(
          getGetCVsByVisibilityMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.example.grpc.cv.GetCVsByVisibilityRequest,
              com.example.grpc.cv.GetCVsByVisibilityResponse>(
                service, METHODID_GET_CVS_BY_VISIBILITY)))
        .addMethod(
          getGetCVsCreatedInRangeMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.example.grpc.cv.GetCVsCreatedInRangeRequest,
              com.example.grpc.cv.GetCVsCreatedInRangeResponse>(
                service, METHODID_GET_CVS_CREATED_IN_RANGE)))
        .addMethod(
          getGetRecentCVsMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.example.grpc.cv.GetRecentCVsRequest,
              com.example.grpc.cv.GetRecentCVsResponse>(
                service, METHODID_GET_RECENT_CVS)))
        .addMethod(
          getCreateCVMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.example.grpc.cv.CreateCVRequest,
              com.example.grpc.cv.CreateCVResponse>(
                service, METHODID_CREATE_CV)))
        .build();
  }

  private static abstract class CVServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    CVServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.example.grpc.cv.CVServiceProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("CVService");
    }
  }

  private static final class CVServiceFileDescriptorSupplier
      extends CVServiceBaseDescriptorSupplier {
    CVServiceFileDescriptorSupplier() {}
  }

  private static final class CVServiceMethodDescriptorSupplier
      extends CVServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    CVServiceMethodDescriptorSupplier(java.lang.String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (CVServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new CVServiceFileDescriptorSupplier())
              .addMethod(getGetTotalCVsMethod())
              .addMethod(getGetCVsByVisibilityMethod())
              .addMethod(getGetCVsCreatedInRangeMethod())
              .addMethod(getGetRecentCVsMethod())
              .addMethod(getCreateCVMethod())
              .build();
        }
      }
    }
    return result;
  }
}
