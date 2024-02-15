package game;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.61.1)",
    comments = "Source: game_service.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class GameStateServiceGrpc {

  private GameStateServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "game.GameStateService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<game.GameService.PlayerActionUpdates,
      game.GameService.PlayerActionUpdate> getStreamGameStateMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "StreamGameState",
      requestType = game.GameService.PlayerActionUpdates.class,
      responseType = game.GameService.PlayerActionUpdate.class,
      methodType = io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
  public static io.grpc.MethodDescriptor<game.GameService.PlayerActionUpdates,
      game.GameService.PlayerActionUpdate> getStreamGameStateMethod() {
    io.grpc.MethodDescriptor<game.GameService.PlayerActionUpdates, game.GameService.PlayerActionUpdate> getStreamGameStateMethod;
    if ((getStreamGameStateMethod = GameStateServiceGrpc.getStreamGameStateMethod) == null) {
      synchronized (GameStateServiceGrpc.class) {
        if ((getStreamGameStateMethod = GameStateServiceGrpc.getStreamGameStateMethod) == null) {
          GameStateServiceGrpc.getStreamGameStateMethod = getStreamGameStateMethod =
              io.grpc.MethodDescriptor.<game.GameService.PlayerActionUpdates, game.GameService.PlayerActionUpdate>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "StreamGameState"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  game.GameService.PlayerActionUpdates.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  game.GameService.PlayerActionUpdate.getDefaultInstance()))
              .setSchemaDescriptor(new GameStateServiceMethodDescriptorSupplier("StreamGameState"))
              .build();
        }
      }
    }
    return getStreamGameStateMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static GameStateServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<GameStateServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<GameStateServiceStub>() {
        @java.lang.Override
        public GameStateServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new GameStateServiceStub(channel, callOptions);
        }
      };
    return GameStateServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static GameStateServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<GameStateServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<GameStateServiceBlockingStub>() {
        @java.lang.Override
        public GameStateServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new GameStateServiceBlockingStub(channel, callOptions);
        }
      };
    return GameStateServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static GameStateServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<GameStateServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<GameStateServiceFutureStub>() {
        @java.lang.Override
        public GameStateServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new GameStateServiceFutureStub(channel, callOptions);
        }
      };
    return GameStateServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     */
    default io.grpc.stub.StreamObserver<game.GameService.PlayerActionUpdates> streamGameState(
        io.grpc.stub.StreamObserver<game.GameService.PlayerActionUpdate> responseObserver) {
      return io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall(getStreamGameStateMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service GameStateService.
   */
  public static abstract class GameStateServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return GameStateServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service GameStateService.
   */
  public static final class GameStateServiceStub
      extends io.grpc.stub.AbstractAsyncStub<GameStateServiceStub> {
    private GameStateServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GameStateServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new GameStateServiceStub(channel, callOptions);
    }

    /**
     */
    public io.grpc.stub.StreamObserver<game.GameService.PlayerActionUpdates> streamGameState(
        io.grpc.stub.StreamObserver<game.GameService.PlayerActionUpdate> responseObserver) {
      return io.grpc.stub.ClientCalls.asyncBidiStreamingCall(
          getChannel().newCall(getStreamGameStateMethod(), getCallOptions()), responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service GameStateService.
   */
  public static final class GameStateServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<GameStateServiceBlockingStub> {
    private GameStateServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GameStateServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new GameStateServiceBlockingStub(channel, callOptions);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service GameStateService.
   */
  public static final class GameStateServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<GameStateServiceFutureStub> {
    private GameStateServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected GameStateServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new GameStateServiceFutureStub(channel, callOptions);
    }
  }

  private static final int METHODID_STREAM_GAME_STATE = 0;

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
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_STREAM_GAME_STATE:
          return (io.grpc.stub.StreamObserver<Req>) serviceImpl.streamGameState(
              (io.grpc.stub.StreamObserver<game.GameService.PlayerActionUpdate>) responseObserver);
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getStreamGameStateMethod(),
          io.grpc.stub.ServerCalls.asyncBidiStreamingCall(
            new MethodHandlers<
              game.GameService.PlayerActionUpdates,
              game.GameService.PlayerActionUpdate>(
                service, METHODID_STREAM_GAME_STATE)))
        .build();
  }

  private static abstract class GameStateServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    GameStateServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return game.GameService.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("GameStateService");
    }
  }

  private static final class GameStateServiceFileDescriptorSupplier
      extends GameStateServiceBaseDescriptorSupplier {
    GameStateServiceFileDescriptorSupplier() {}
  }

  private static final class GameStateServiceMethodDescriptorSupplier
      extends GameStateServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    GameStateServiceMethodDescriptorSupplier(java.lang.String methodName) {
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
      synchronized (GameStateServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new GameStateServiceFileDescriptorSupplier())
              .addMethod(getStreamGameStateMethod())
              .build();
        }
      }
    }
    return result;
  }
}
