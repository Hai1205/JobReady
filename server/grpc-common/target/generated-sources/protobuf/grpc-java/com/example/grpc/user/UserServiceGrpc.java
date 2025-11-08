package com.example.grpc.user;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 * <pre>
 * User Service Definition
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.58.0)",
    comments = "Source: user_service.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class UserServiceGrpc {

  private UserServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "user.UserService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.example.grpc.user.FindUserByIdRequest,
      com.example.grpc.user.UserResponse> getFindUserByIdMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "FindUserById",
      requestType = com.example.grpc.user.FindUserByIdRequest.class,
      responseType = com.example.grpc.user.UserResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.example.grpc.user.FindUserByIdRequest,
      com.example.grpc.user.UserResponse> getFindUserByIdMethod() {
    io.grpc.MethodDescriptor<com.example.grpc.user.FindUserByIdRequest, com.example.grpc.user.UserResponse> getFindUserByIdMethod;
    if ((getFindUserByIdMethod = UserServiceGrpc.getFindUserByIdMethod) == null) {
      synchronized (UserServiceGrpc.class) {
        if ((getFindUserByIdMethod = UserServiceGrpc.getFindUserByIdMethod) == null) {
          UserServiceGrpc.getFindUserByIdMethod = getFindUserByIdMethod =
              io.grpc.MethodDescriptor.<com.example.grpc.user.FindUserByIdRequest, com.example.grpc.user.UserResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "FindUserById"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.grpc.user.FindUserByIdRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.grpc.user.UserResponse.getDefaultInstance()))
              .setSchemaDescriptor(new UserServiceMethodDescriptorSupplier("FindUserById"))
              .build();
        }
      }
    }
    return getFindUserByIdMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.example.grpc.user.FindUserByEmailRequest,
      com.example.grpc.user.UserResponse> getFindUserByEmailMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "FindUserByEmail",
      requestType = com.example.grpc.user.FindUserByEmailRequest.class,
      responseType = com.example.grpc.user.UserResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.example.grpc.user.FindUserByEmailRequest,
      com.example.grpc.user.UserResponse> getFindUserByEmailMethod() {
    io.grpc.MethodDescriptor<com.example.grpc.user.FindUserByEmailRequest, com.example.grpc.user.UserResponse> getFindUserByEmailMethod;
    if ((getFindUserByEmailMethod = UserServiceGrpc.getFindUserByEmailMethod) == null) {
      synchronized (UserServiceGrpc.class) {
        if ((getFindUserByEmailMethod = UserServiceGrpc.getFindUserByEmailMethod) == null) {
          UserServiceGrpc.getFindUserByEmailMethod = getFindUserByEmailMethod =
              io.grpc.MethodDescriptor.<com.example.grpc.user.FindUserByEmailRequest, com.example.grpc.user.UserResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "FindUserByEmail"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.grpc.user.FindUserByEmailRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.grpc.user.UserResponse.getDefaultInstance()))
              .setSchemaDescriptor(new UserServiceMethodDescriptorSupplier("FindUserByEmail"))
              .build();
        }
      }
    }
    return getFindUserByEmailMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.example.grpc.user.FindUserByIdentifierRequest,
      com.example.grpc.user.UserResponse> getFindUserByIdentifierMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "FindUserByIdentifier",
      requestType = com.example.grpc.user.FindUserByIdentifierRequest.class,
      responseType = com.example.grpc.user.UserResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.example.grpc.user.FindUserByIdentifierRequest,
      com.example.grpc.user.UserResponse> getFindUserByIdentifierMethod() {
    io.grpc.MethodDescriptor<com.example.grpc.user.FindUserByIdentifierRequest, com.example.grpc.user.UserResponse> getFindUserByIdentifierMethod;
    if ((getFindUserByIdentifierMethod = UserServiceGrpc.getFindUserByIdentifierMethod) == null) {
      synchronized (UserServiceGrpc.class) {
        if ((getFindUserByIdentifierMethod = UserServiceGrpc.getFindUserByIdentifierMethod) == null) {
          UserServiceGrpc.getFindUserByIdentifierMethod = getFindUserByIdentifierMethod =
              io.grpc.MethodDescriptor.<com.example.grpc.user.FindUserByIdentifierRequest, com.example.grpc.user.UserResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "FindUserByIdentifier"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.grpc.user.FindUserByIdentifierRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.grpc.user.UserResponse.getDefaultInstance()))
              .setSchemaDescriptor(new UserServiceMethodDescriptorSupplier("FindUserByIdentifier"))
              .build();
        }
      }
    }
    return getFindUserByIdentifierMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.example.grpc.user.CreateUserRequest,
      com.example.grpc.user.UserResponse> getCreateUserMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CreateUser",
      requestType = com.example.grpc.user.CreateUserRequest.class,
      responseType = com.example.grpc.user.UserResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.example.grpc.user.CreateUserRequest,
      com.example.grpc.user.UserResponse> getCreateUserMethod() {
    io.grpc.MethodDescriptor<com.example.grpc.user.CreateUserRequest, com.example.grpc.user.UserResponse> getCreateUserMethod;
    if ((getCreateUserMethod = UserServiceGrpc.getCreateUserMethod) == null) {
      synchronized (UserServiceGrpc.class) {
        if ((getCreateUserMethod = UserServiceGrpc.getCreateUserMethod) == null) {
          UserServiceGrpc.getCreateUserMethod = getCreateUserMethod =
              io.grpc.MethodDescriptor.<com.example.grpc.user.CreateUserRequest, com.example.grpc.user.UserResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "CreateUser"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.grpc.user.CreateUserRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.grpc.user.UserResponse.getDefaultInstance()))
              .setSchemaDescriptor(new UserServiceMethodDescriptorSupplier("CreateUser"))
              .build();
        }
      }
    }
    return getCreateUserMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.example.grpc.user.ActivateUserRequest,
      com.example.grpc.user.UserResponse> getActivateUserMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ActivateUser",
      requestType = com.example.grpc.user.ActivateUserRequest.class,
      responseType = com.example.grpc.user.UserResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.example.grpc.user.ActivateUserRequest,
      com.example.grpc.user.UserResponse> getActivateUserMethod() {
    io.grpc.MethodDescriptor<com.example.grpc.user.ActivateUserRequest, com.example.grpc.user.UserResponse> getActivateUserMethod;
    if ((getActivateUserMethod = UserServiceGrpc.getActivateUserMethod) == null) {
      synchronized (UserServiceGrpc.class) {
        if ((getActivateUserMethod = UserServiceGrpc.getActivateUserMethod) == null) {
          UserServiceGrpc.getActivateUserMethod = getActivateUserMethod =
              io.grpc.MethodDescriptor.<com.example.grpc.user.ActivateUserRequest, com.example.grpc.user.UserResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ActivateUser"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.grpc.user.ActivateUserRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.grpc.user.UserResponse.getDefaultInstance()))
              .setSchemaDescriptor(new UserServiceMethodDescriptorSupplier("ActivateUser"))
              .build();
        }
      }
    }
    return getActivateUserMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.example.grpc.user.AuthenticateUserRequest,
      com.example.grpc.user.UserResponse> getAuthenticateUserMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "AuthenticateUser",
      requestType = com.example.grpc.user.AuthenticateUserRequest.class,
      responseType = com.example.grpc.user.UserResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.example.grpc.user.AuthenticateUserRequest,
      com.example.grpc.user.UserResponse> getAuthenticateUserMethod() {
    io.grpc.MethodDescriptor<com.example.grpc.user.AuthenticateUserRequest, com.example.grpc.user.UserResponse> getAuthenticateUserMethod;
    if ((getAuthenticateUserMethod = UserServiceGrpc.getAuthenticateUserMethod) == null) {
      synchronized (UserServiceGrpc.class) {
        if ((getAuthenticateUserMethod = UserServiceGrpc.getAuthenticateUserMethod) == null) {
          UserServiceGrpc.getAuthenticateUserMethod = getAuthenticateUserMethod =
              io.grpc.MethodDescriptor.<com.example.grpc.user.AuthenticateUserRequest, com.example.grpc.user.UserResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "AuthenticateUser"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.grpc.user.AuthenticateUserRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.grpc.user.UserResponse.getDefaultInstance()))
              .setSchemaDescriptor(new UserServiceMethodDescriptorSupplier("AuthenticateUser"))
              .build();
        }
      }
    }
    return getAuthenticateUserMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.example.grpc.user.ChangePasswordRequest,
      com.example.grpc.user.UserResponse> getChangePasswordMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ChangePassword",
      requestType = com.example.grpc.user.ChangePasswordRequest.class,
      responseType = com.example.grpc.user.UserResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.example.grpc.user.ChangePasswordRequest,
      com.example.grpc.user.UserResponse> getChangePasswordMethod() {
    io.grpc.MethodDescriptor<com.example.grpc.user.ChangePasswordRequest, com.example.grpc.user.UserResponse> getChangePasswordMethod;
    if ((getChangePasswordMethod = UserServiceGrpc.getChangePasswordMethod) == null) {
      synchronized (UserServiceGrpc.class) {
        if ((getChangePasswordMethod = UserServiceGrpc.getChangePasswordMethod) == null) {
          UserServiceGrpc.getChangePasswordMethod = getChangePasswordMethod =
              io.grpc.MethodDescriptor.<com.example.grpc.user.ChangePasswordRequest, com.example.grpc.user.UserResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ChangePassword"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.grpc.user.ChangePasswordRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.grpc.user.UserResponse.getDefaultInstance()))
              .setSchemaDescriptor(new UserServiceMethodDescriptorSupplier("ChangePassword"))
              .build();
        }
      }
    }
    return getChangePasswordMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.example.grpc.user.ForgotPasswordRequest,
      com.example.grpc.user.UserResponse> getForgotPasswordMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ForgotPassword",
      requestType = com.example.grpc.user.ForgotPasswordRequest.class,
      responseType = com.example.grpc.user.UserResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.example.grpc.user.ForgotPasswordRequest,
      com.example.grpc.user.UserResponse> getForgotPasswordMethod() {
    io.grpc.MethodDescriptor<com.example.grpc.user.ForgotPasswordRequest, com.example.grpc.user.UserResponse> getForgotPasswordMethod;
    if ((getForgotPasswordMethod = UserServiceGrpc.getForgotPasswordMethod) == null) {
      synchronized (UserServiceGrpc.class) {
        if ((getForgotPasswordMethod = UserServiceGrpc.getForgotPasswordMethod) == null) {
          UserServiceGrpc.getForgotPasswordMethod = getForgotPasswordMethod =
              io.grpc.MethodDescriptor.<com.example.grpc.user.ForgotPasswordRequest, com.example.grpc.user.UserResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ForgotPassword"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.grpc.user.ForgotPasswordRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.grpc.user.UserResponse.getDefaultInstance()))
              .setSchemaDescriptor(new UserServiceMethodDescriptorSupplier("ForgotPassword"))
              .build();
        }
      }
    }
    return getForgotPasswordMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.example.grpc.user.ResetPasswordRequest,
      com.example.grpc.user.ResetPasswordResponse> getResetPasswordMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ResetPassword",
      requestType = com.example.grpc.user.ResetPasswordRequest.class,
      responseType = com.example.grpc.user.ResetPasswordResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.example.grpc.user.ResetPasswordRequest,
      com.example.grpc.user.ResetPasswordResponse> getResetPasswordMethod() {
    io.grpc.MethodDescriptor<com.example.grpc.user.ResetPasswordRequest, com.example.grpc.user.ResetPasswordResponse> getResetPasswordMethod;
    if ((getResetPasswordMethod = UserServiceGrpc.getResetPasswordMethod) == null) {
      synchronized (UserServiceGrpc.class) {
        if ((getResetPasswordMethod = UserServiceGrpc.getResetPasswordMethod) == null) {
          UserServiceGrpc.getResetPasswordMethod = getResetPasswordMethod =
              io.grpc.MethodDescriptor.<com.example.grpc.user.ResetPasswordRequest, com.example.grpc.user.ResetPasswordResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ResetPassword"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.grpc.user.ResetPasswordRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.example.grpc.user.ResetPasswordResponse.getDefaultInstance()))
              .setSchemaDescriptor(new UserServiceMethodDescriptorSupplier("ResetPassword"))
              .build();
        }
      }
    }
    return getResetPasswordMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static UserServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<UserServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<UserServiceStub>() {
        @java.lang.Override
        public UserServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new UserServiceStub(channel, callOptions);
        }
      };
    return UserServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static UserServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<UserServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<UserServiceBlockingStub>() {
        @java.lang.Override
        public UserServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new UserServiceBlockingStub(channel, callOptions);
        }
      };
    return UserServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static UserServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<UserServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<UserServiceFutureStub>() {
        @java.lang.Override
        public UserServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new UserServiceFutureStub(channel, callOptions);
        }
      };
    return UserServiceFutureStub.newStub(factory, channel);
  }

  /**
   * <pre>
   * User Service Definition
   * </pre>
   */
  public interface AsyncService {

    /**
     * <pre>
     * Find user by ID
     * </pre>
     */
    default void findUserById(com.example.grpc.user.FindUserByIdRequest request,
        io.grpc.stub.StreamObserver<com.example.grpc.user.UserResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getFindUserByIdMethod(), responseObserver);
    }

    /**
     * <pre>
     * Find user by email
     * </pre>
     */
    default void findUserByEmail(com.example.grpc.user.FindUserByEmailRequest request,
        io.grpc.stub.StreamObserver<com.example.grpc.user.UserResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getFindUserByEmailMethod(), responseObserver);
    }

    /**
     * <pre>
     * Find user by identifier (username or email)
     * </pre>
     */
    default void findUserByIdentifier(com.example.grpc.user.FindUserByIdentifierRequest request,
        io.grpc.stub.StreamObserver<com.example.grpc.user.UserResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getFindUserByIdentifierMethod(), responseObserver);
    }

    /**
     * <pre>
     * Create new user
     * </pre>
     */
    default void createUser(com.example.grpc.user.CreateUserRequest request,
        io.grpc.stub.StreamObserver<com.example.grpc.user.UserResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getCreateUserMethod(), responseObserver);
    }

    /**
     * <pre>
     * Activate user account
     * </pre>
     */
    default void activateUser(com.example.grpc.user.ActivateUserRequest request,
        io.grpc.stub.StreamObserver<com.example.grpc.user.UserResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getActivateUserMethod(), responseObserver);
    }

    /**
     * <pre>
     * Authenticate user
     * </pre>
     */
    default void authenticateUser(com.example.grpc.user.AuthenticateUserRequest request,
        io.grpc.stub.StreamObserver<com.example.grpc.user.UserResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getAuthenticateUserMethod(), responseObserver);
    }

    /**
     * <pre>
     * Change user password
     * </pre>
     */
    default void changePassword(com.example.grpc.user.ChangePasswordRequest request,
        io.grpc.stub.StreamObserver<com.example.grpc.user.UserResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getChangePasswordMethod(), responseObserver);
    }

    /**
     * <pre>
     * Forgot password
     * </pre>
     */
    default void forgotPassword(com.example.grpc.user.ForgotPasswordRequest request,
        io.grpc.stub.StreamObserver<com.example.grpc.user.UserResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getForgotPasswordMethod(), responseObserver);
    }

    /**
     * <pre>
     * Reset password
     * </pre>
     */
    default void resetPassword(com.example.grpc.user.ResetPasswordRequest request,
        io.grpc.stub.StreamObserver<com.example.grpc.user.ResetPasswordResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getResetPasswordMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service UserService.
   * <pre>
   * User Service Definition
   * </pre>
   */
  public static abstract class UserServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return UserServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service UserService.
   * <pre>
   * User Service Definition
   * </pre>
   */
  public static final class UserServiceStub
      extends io.grpc.stub.AbstractAsyncStub<UserServiceStub> {
    private UserServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected UserServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new UserServiceStub(channel, callOptions);
    }

    /**
     * <pre>
     * Find user by ID
     * </pre>
     */
    public void findUserById(com.example.grpc.user.FindUserByIdRequest request,
        io.grpc.stub.StreamObserver<com.example.grpc.user.UserResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getFindUserByIdMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Find user by email
     * </pre>
     */
    public void findUserByEmail(com.example.grpc.user.FindUserByEmailRequest request,
        io.grpc.stub.StreamObserver<com.example.grpc.user.UserResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getFindUserByEmailMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Find user by identifier (username or email)
     * </pre>
     */
    public void findUserByIdentifier(com.example.grpc.user.FindUserByIdentifierRequest request,
        io.grpc.stub.StreamObserver<com.example.grpc.user.UserResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getFindUserByIdentifierMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Create new user
     * </pre>
     */
    public void createUser(com.example.grpc.user.CreateUserRequest request,
        io.grpc.stub.StreamObserver<com.example.grpc.user.UserResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getCreateUserMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Activate user account
     * </pre>
     */
    public void activateUser(com.example.grpc.user.ActivateUserRequest request,
        io.grpc.stub.StreamObserver<com.example.grpc.user.UserResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getActivateUserMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Authenticate user
     * </pre>
     */
    public void authenticateUser(com.example.grpc.user.AuthenticateUserRequest request,
        io.grpc.stub.StreamObserver<com.example.grpc.user.UserResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getAuthenticateUserMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Change user password
     * </pre>
     */
    public void changePassword(com.example.grpc.user.ChangePasswordRequest request,
        io.grpc.stub.StreamObserver<com.example.grpc.user.UserResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getChangePasswordMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Forgot password
     * </pre>
     */
    public void forgotPassword(com.example.grpc.user.ForgotPasswordRequest request,
        io.grpc.stub.StreamObserver<com.example.grpc.user.UserResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getForgotPasswordMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * Reset password
     * </pre>
     */
    public void resetPassword(com.example.grpc.user.ResetPasswordRequest request,
        io.grpc.stub.StreamObserver<com.example.grpc.user.ResetPasswordResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getResetPasswordMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service UserService.
   * <pre>
   * User Service Definition
   * </pre>
   */
  public static final class UserServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<UserServiceBlockingStub> {
    private UserServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected UserServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new UserServiceBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * Find user by ID
     * </pre>
     */
    public com.example.grpc.user.UserResponse findUserById(com.example.grpc.user.FindUserByIdRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getFindUserByIdMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Find user by email
     * </pre>
     */
    public com.example.grpc.user.UserResponse findUserByEmail(com.example.grpc.user.FindUserByEmailRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getFindUserByEmailMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Find user by identifier (username or email)
     * </pre>
     */
    public com.example.grpc.user.UserResponse findUserByIdentifier(com.example.grpc.user.FindUserByIdentifierRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getFindUserByIdentifierMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Create new user
     * </pre>
     */
    public com.example.grpc.user.UserResponse createUser(com.example.grpc.user.CreateUserRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCreateUserMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Activate user account
     * </pre>
     */
    public com.example.grpc.user.UserResponse activateUser(com.example.grpc.user.ActivateUserRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getActivateUserMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Authenticate user
     * </pre>
     */
    public com.example.grpc.user.UserResponse authenticateUser(com.example.grpc.user.AuthenticateUserRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getAuthenticateUserMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Change user password
     * </pre>
     */
    public com.example.grpc.user.UserResponse changePassword(com.example.grpc.user.ChangePasswordRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getChangePasswordMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Forgot password
     * </pre>
     */
    public com.example.grpc.user.UserResponse forgotPassword(com.example.grpc.user.ForgotPasswordRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getForgotPasswordMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * Reset password
     * </pre>
     */
    public com.example.grpc.user.ResetPasswordResponse resetPassword(com.example.grpc.user.ResetPasswordRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getResetPasswordMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service UserService.
   * <pre>
   * User Service Definition
   * </pre>
   */
  public static final class UserServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<UserServiceFutureStub> {
    private UserServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected UserServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new UserServiceFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * Find user by ID
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.example.grpc.user.UserResponse> findUserById(
        com.example.grpc.user.FindUserByIdRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getFindUserByIdMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Find user by email
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.example.grpc.user.UserResponse> findUserByEmail(
        com.example.grpc.user.FindUserByEmailRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getFindUserByEmailMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Find user by identifier (username or email)
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.example.grpc.user.UserResponse> findUserByIdentifier(
        com.example.grpc.user.FindUserByIdentifierRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getFindUserByIdentifierMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Create new user
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.example.grpc.user.UserResponse> createUser(
        com.example.grpc.user.CreateUserRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getCreateUserMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Activate user account
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.example.grpc.user.UserResponse> activateUser(
        com.example.grpc.user.ActivateUserRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getActivateUserMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Authenticate user
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.example.grpc.user.UserResponse> authenticateUser(
        com.example.grpc.user.AuthenticateUserRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getAuthenticateUserMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Change user password
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.example.grpc.user.UserResponse> changePassword(
        com.example.grpc.user.ChangePasswordRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getChangePasswordMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Forgot password
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.example.grpc.user.UserResponse> forgotPassword(
        com.example.grpc.user.ForgotPasswordRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getForgotPasswordMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * Reset password
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.example.grpc.user.ResetPasswordResponse> resetPassword(
        com.example.grpc.user.ResetPasswordRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getResetPasswordMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_FIND_USER_BY_ID = 0;
  private static final int METHODID_FIND_USER_BY_EMAIL = 1;
  private static final int METHODID_FIND_USER_BY_IDENTIFIER = 2;
  private static final int METHODID_CREATE_USER = 3;
  private static final int METHODID_ACTIVATE_USER = 4;
  private static final int METHODID_AUTHENTICATE_USER = 5;
  private static final int METHODID_CHANGE_PASSWORD = 6;
  private static final int METHODID_FORGOT_PASSWORD = 7;
  private static final int METHODID_RESET_PASSWORD = 8;

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
        case METHODID_FIND_USER_BY_ID:
          serviceImpl.findUserById((com.example.grpc.user.FindUserByIdRequest) request,
              (io.grpc.stub.StreamObserver<com.example.grpc.user.UserResponse>) responseObserver);
          break;
        case METHODID_FIND_USER_BY_EMAIL:
          serviceImpl.findUserByEmail((com.example.grpc.user.FindUserByEmailRequest) request,
              (io.grpc.stub.StreamObserver<com.example.grpc.user.UserResponse>) responseObserver);
          break;
        case METHODID_FIND_USER_BY_IDENTIFIER:
          serviceImpl.findUserByIdentifier((com.example.grpc.user.FindUserByIdentifierRequest) request,
              (io.grpc.stub.StreamObserver<com.example.grpc.user.UserResponse>) responseObserver);
          break;
        case METHODID_CREATE_USER:
          serviceImpl.createUser((com.example.grpc.user.CreateUserRequest) request,
              (io.grpc.stub.StreamObserver<com.example.grpc.user.UserResponse>) responseObserver);
          break;
        case METHODID_ACTIVATE_USER:
          serviceImpl.activateUser((com.example.grpc.user.ActivateUserRequest) request,
              (io.grpc.stub.StreamObserver<com.example.grpc.user.UserResponse>) responseObserver);
          break;
        case METHODID_AUTHENTICATE_USER:
          serviceImpl.authenticateUser((com.example.grpc.user.AuthenticateUserRequest) request,
              (io.grpc.stub.StreamObserver<com.example.grpc.user.UserResponse>) responseObserver);
          break;
        case METHODID_CHANGE_PASSWORD:
          serviceImpl.changePassword((com.example.grpc.user.ChangePasswordRequest) request,
              (io.grpc.stub.StreamObserver<com.example.grpc.user.UserResponse>) responseObserver);
          break;
        case METHODID_FORGOT_PASSWORD:
          serviceImpl.forgotPassword((com.example.grpc.user.ForgotPasswordRequest) request,
              (io.grpc.stub.StreamObserver<com.example.grpc.user.UserResponse>) responseObserver);
          break;
        case METHODID_RESET_PASSWORD:
          serviceImpl.resetPassword((com.example.grpc.user.ResetPasswordRequest) request,
              (io.grpc.stub.StreamObserver<com.example.grpc.user.ResetPasswordResponse>) responseObserver);
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
          getFindUserByIdMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.example.grpc.user.FindUserByIdRequest,
              com.example.grpc.user.UserResponse>(
                service, METHODID_FIND_USER_BY_ID)))
        .addMethod(
          getFindUserByEmailMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.example.grpc.user.FindUserByEmailRequest,
              com.example.grpc.user.UserResponse>(
                service, METHODID_FIND_USER_BY_EMAIL)))
        .addMethod(
          getFindUserByIdentifierMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.example.grpc.user.FindUserByIdentifierRequest,
              com.example.grpc.user.UserResponse>(
                service, METHODID_FIND_USER_BY_IDENTIFIER)))
        .addMethod(
          getCreateUserMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.example.grpc.user.CreateUserRequest,
              com.example.grpc.user.UserResponse>(
                service, METHODID_CREATE_USER)))
        .addMethod(
          getActivateUserMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.example.grpc.user.ActivateUserRequest,
              com.example.grpc.user.UserResponse>(
                service, METHODID_ACTIVATE_USER)))
        .addMethod(
          getAuthenticateUserMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.example.grpc.user.AuthenticateUserRequest,
              com.example.grpc.user.UserResponse>(
                service, METHODID_AUTHENTICATE_USER)))
        .addMethod(
          getChangePasswordMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.example.grpc.user.ChangePasswordRequest,
              com.example.grpc.user.UserResponse>(
                service, METHODID_CHANGE_PASSWORD)))
        .addMethod(
          getForgotPasswordMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.example.grpc.user.ForgotPasswordRequest,
              com.example.grpc.user.UserResponse>(
                service, METHODID_FORGOT_PASSWORD)))
        .addMethod(
          getResetPasswordMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.example.grpc.user.ResetPasswordRequest,
              com.example.grpc.user.ResetPasswordResponse>(
                service, METHODID_RESET_PASSWORD)))
        .build();
  }

  private static abstract class UserServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    UserServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.example.grpc.user.UserServiceProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("UserService");
    }
  }

  private static final class UserServiceFileDescriptorSupplier
      extends UserServiceBaseDescriptorSupplier {
    UserServiceFileDescriptorSupplier() {}
  }

  private static final class UserServiceMethodDescriptorSupplier
      extends UserServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    UserServiceMethodDescriptorSupplier(java.lang.String methodName) {
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
      synchronized (UserServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new UserServiceFileDescriptorSupplier())
              .addMethod(getFindUserByIdMethod())
              .addMethod(getFindUserByEmailMethod())
              .addMethod(getFindUserByIdentifierMethod())
              .addMethod(getCreateUserMethod())
              .addMethod(getActivateUserMethod())
              .addMethod(getAuthenticateUserMethod())
              .addMethod(getChangePasswordMethod())
              .addMethod(getForgotPasswordMethod())
              .addMethod(getResetPasswordMethod())
              .build();
        }
      }
    }
    return result;
  }
}
