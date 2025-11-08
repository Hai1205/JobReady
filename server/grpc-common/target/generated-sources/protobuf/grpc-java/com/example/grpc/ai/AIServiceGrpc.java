package com.example.grpc.ai;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 * <pre>
 * AI Service Definition
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.58.0)",
    comments = "Source: ai_service.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class AIServiceGrpc {

  private AIServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "ai.AIService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.example.grpc.ai.AnalyzeCVRequest,
      com.example.grpc.ai.AIResponse> getAnalyzeCVMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "AnalyzeCV",
      requestType = com.example.grpc.ai.AnalyzeCVRequest.class,
      responseType = com.example.grpc.ai.AIResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.example.grpc.ai.AnalyzeCVRequest,
      com.example.grpc.ai.AIResponse> getAnalyzeCVMethod() {
    io.grpc.MethodDescriptor<com.example.grpc.ai.AnalyzeCVRequest, com.example.grpc.ai.AIResponse> getAnalyzeCVMethod;
    if ((getAnalyzeCVMethod = AIServiceGrpc.getAnalyzeCVMethod) == null) {
      synchronized (AIServiceGrpc.class) {
        if ((getAnalyzeCVMethod = AIServiceGrpc.getAnalyzeCVMethod) == null) {
          AIServiceGrpc.getAnalyzeCVMethod = getAnalyzeCVMethod =
              io.grpc.MethodDescriptor.<com.example.grpc.ai.AnalyzeCVRequest, com.example.grpc.ai.AIResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "AnalyzeCV"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.grpc.ai.AnalyzeCVRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.grpc.ai.AIResponse.getDefaultInstance()))
              .setSchemaDescriptor(new AIServiceMethodDescriptorSupplier("AnalyzeCV"))
              .build();
        }
      }
    }
    return getAnalyzeCVMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.example.grpc.ai.ImproveCVRequest,
      com.example.grpc.ai.AIResponse> getImproveCVMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ImproveCV",
      requestType = com.example.grpc.ai.ImproveCVRequest.class,
      responseType = com.example.grpc.ai.AIResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.example.grpc.ai.ImproveCVRequest,
      com.example.grpc.ai.AIResponse> getImproveCVMethod() {
    io.grpc.MethodDescriptor<com.example.grpc.ai.ImproveCVRequest, com.example.grpc.ai.AIResponse> getImproveCVMethod;
    if ((getImproveCVMethod = AIServiceGrpc.getImproveCVMethod) == null) {
      synchronized (AIServiceGrpc.class) {
        if ((getImproveCVMethod = AIServiceGrpc.getImproveCVMethod) == null) {
          AIServiceGrpc.getImproveCVMethod = getImproveCVMethod =
              io.grpc.MethodDescriptor.<com.example.grpc.ai.ImproveCVRequest, com.example.grpc.ai.AIResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ImproveCV"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.grpc.ai.ImproveCVRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.grpc.ai.AIResponse.getDefaultInstance()))
              .setSchemaDescriptor(new AIServiceMethodDescriptorSupplier("ImproveCV"))
              .build();
        }
      }
    }
    return getImproveCVMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.example.grpc.ai.AnalyzeCVWithJDRequest,
      com.example.grpc.ai.AIResponse> getAnalyzeCVWithJobDescriptionMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "AnalyzeCVWithJobDescription",
      requestType = com.example.grpc.ai.AnalyzeCVWithJDRequest.class,
      responseType = com.example.grpc.ai.AIResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.example.grpc.ai.AnalyzeCVWithJDRequest,
      com.example.grpc.ai.AIResponse> getAnalyzeCVWithJobDescriptionMethod() {
    io.grpc.MethodDescriptor<com.example.grpc.ai.AnalyzeCVWithJDRequest, com.example.grpc.ai.AIResponse> getAnalyzeCVWithJobDescriptionMethod;
    if ((getAnalyzeCVWithJobDescriptionMethod = AIServiceGrpc.getAnalyzeCVWithJobDescriptionMethod) == null) {
      synchronized (AIServiceGrpc.class) {
        if ((getAnalyzeCVWithJobDescriptionMethod = AIServiceGrpc.getAnalyzeCVWithJobDescriptionMethod) == null) {
          AIServiceGrpc.getAnalyzeCVWithJobDescriptionMethod = getAnalyzeCVWithJobDescriptionMethod =
              io.grpc.MethodDescriptor.<com.example.grpc.ai.AnalyzeCVWithJDRequest, com.example.grpc.ai.AIResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "AnalyzeCVWithJobDescription"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.grpc.ai.AnalyzeCVWithJDRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.grpc.ai.AIResponse.getDefaultInstance()))
              .setSchemaDescriptor(new AIServiceMethodDescriptorSupplier("AnalyzeCVWithJobDescription"))
              .build();
        }
      }
    }
    return getAnalyzeCVWithJobDescriptionMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static AIServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<AIServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<AIServiceStub>() {
        @java.lang.Override
        public AIServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new AIServiceStub(channel, callOptions);
        }
      };
    return AIServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static AIServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<AIServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<AIServiceBlockingStub>() {
        @java.lang.Override
        public AIServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new AIServiceBlockingStub(channel, callOptions);
        }
      };
    return AIServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static AIServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<AIServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<AIServiceFutureStub>() {
        @java.lang.Override
        public AIServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new AIServiceFutureStub(channel, callOptions);
        }
      };
    return AIServiceFutureStub.newStub(factory, channel);
  }

  /**
   * <pre>
   * AI Service Definition
   * </pre>
   */
  public interface AsyncService {

    /**
     * <pre>
     * Analyze CV
     * </pre>
     */
    default void analyzeCV(com.example.grpc.ai.AnalyzeCVRequest request,
        io.grpc.stub.StreamObserver<com.example.grpc.ai.AIResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getAnalyzeCVMethod(), responseObserver);
    }

    /**
     * <pre>
     * Improve CV section
     * </pre>
     */
    default void improveCV(com.example.grpc.ai.ImproveCVRequest request,
        io.grpc.stub.StreamObserver<com.example.grpc.ai.AIResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getImproveCVMethod(), responseObserver);
    }

    /**
     * <pre>
     * Analyze CV with Job Description
     * </pre>
     */
    default void analyzeCVWithJobDescription(com.example.grpc.ai.AnalyzeCVWithJDRequest request,
        io.grpc.stub.StreamObserver<com.example.grpc.ai.AIResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getAnalyzeCVWithJobDescriptionMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service AIService.
   * <pre>
   * AI Service Definition
   * </pre>
   */
  public static abstract class AIServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return AIServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service AIService.
   * <pre>
   * AI Service Definition
   * </pre>
   */
  public static final class AIServiceStub
      extends io.grpc.stub.AbstractAsyncStub<AIServiceStub> {
    private AIServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected AIServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new AIServiceStub(channel, callOptions);
    }

    /**
     * <pre>
     * Analyze CV
     * </pre>
     */
    public void analyzeCV(com.example.grpc.ai.AnalyzeCVRequest request,
        io.grpc.stub.StreamObserver<com.example.grpc.ai.AIResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getAnalyzeCVMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Improve CV section
     * </pre>
     */
    public void improveCV(com.example.grpc.ai.ImproveCVRequest request,
        io.grpc.stub.StreamObserver<com.example.grpc.ai.AIResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getImproveCVMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Analyze CV with Job Description
     * </pre>
     */
    public void analyzeCVWithJobDescription(com.example.grpc.ai.AnalyzeCVWithJDRequest request,
        io.grpc.stub.StreamObserver<com.example.grpc.ai.AIResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getAnalyzeCVWithJobDescriptionMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service AIService.
   * <pre>
   * AI Service Definition
   * </pre>
   */
  public static final class AIServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<AIServiceBlockingStub> {
    private AIServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected AIServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new AIServiceBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * Analyze CV
     * </pre>
     */
    public com.example.grpc.ai.AIResponse analyzeCV(com.example.grpc.ai.AnalyzeCVRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getAnalyzeCVMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Improve CV section
     * </pre>
     */
    public com.example.grpc.ai.AIResponse improveCV(com.example.grpc.ai.ImproveCVRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getImproveCVMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Analyze CV with Job Description
     * </pre>
     */
    public com.example.grpc.ai.AIResponse analyzeCVWithJobDescription(com.example.grpc.ai.AnalyzeCVWithJDRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getAnalyzeCVWithJobDescriptionMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service AIService.
   * <pre>
   * AI Service Definition
   * </pre>
   */
  public static final class AIServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<AIServiceFutureStub> {
    private AIServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected AIServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new AIServiceFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * Analyze CV
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.example.grpc.ai.AIResponse> analyzeCV(
        com.example.grpc.ai.AnalyzeCVRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getAnalyzeCVMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Improve CV section
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.example.grpc.ai.AIResponse> improveCV(
        com.example.grpc.ai.ImproveCVRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getImproveCVMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Analyze CV with Job Description
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.example.grpc.ai.AIResponse> analyzeCVWithJobDescription(
        com.example.grpc.ai.AnalyzeCVWithJDRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getAnalyzeCVWithJobDescriptionMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_ANALYZE_CV = 0;
  private static final int METHODID_IMPROVE_CV = 1;
  private static final int METHODID_ANALYZE_CVWITH_JOB_DESCRIPTION = 2;

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
        case METHODID_ANALYZE_CV:
          serviceImpl.analyzeCV((com.example.grpc.ai.AnalyzeCVRequest) request,
              (io.grpc.stub.StreamObserver<com.example.grpc.ai.AIResponse>) responseObserver);
          break;
        case METHODID_IMPROVE_CV:
          serviceImpl.improveCV((com.example.grpc.ai.ImproveCVRequest) request,
              (io.grpc.stub.StreamObserver<com.example.grpc.ai.AIResponse>) responseObserver);
          break;
        case METHODID_ANALYZE_CVWITH_JOB_DESCRIPTION:
          serviceImpl.analyzeCVWithJobDescription((com.example.grpc.ai.AnalyzeCVWithJDRequest) request,
              (io.grpc.stub.StreamObserver<com.example.grpc.ai.AIResponse>) responseObserver);
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
          getAnalyzeCVMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.example.grpc.ai.AnalyzeCVRequest,
              com.example.grpc.ai.AIResponse>(
                service, METHODID_ANALYZE_CV)))
        .addMethod(
          getImproveCVMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.example.grpc.ai.ImproveCVRequest,
              com.example.grpc.ai.AIResponse>(
                service, METHODID_IMPROVE_CV)))
        .addMethod(
          getAnalyzeCVWithJobDescriptionMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.example.grpc.ai.AnalyzeCVWithJDRequest,
              com.example.grpc.ai.AIResponse>(
                service, METHODID_ANALYZE_CVWITH_JOB_DESCRIPTION)))
        .build();
  }

  private static abstract class AIServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    AIServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.example.grpc.ai.AIServiceProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("AIService");
    }
  }

  private static final class AIServiceFileDescriptorSupplier
      extends AIServiceBaseDescriptorSupplier {
    AIServiceFileDescriptorSupplier() {}
  }

  private static final class AIServiceMethodDescriptorSupplier
      extends AIServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    AIServiceMethodDescriptorSupplier(java.lang.String methodName) {
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
      synchronized (AIServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new AIServiceFileDescriptorSupplier())
              .addMethod(getAnalyzeCVMethod())
              .addMethod(getImproveCVMethod())
              .addMethod(getAnalyzeCVWithJobDescriptionMethod())
              .build();
        }
      }
    }
    return result;
  }
}
