package network.proto;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.41.0)",
    comments = "Source: ddb.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class DdbServiceGrpc {

  private DdbServiceGrpc() {}

  public static final String SERVICE_NAME = "DdbService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<network.proto.TableRequest,
      network.proto.TableResponse> getRequestTableMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "RequestTable",
      requestType = network.proto.TableRequest.class,
      responseType = network.proto.TableResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<network.proto.TableRequest,
      network.proto.TableResponse> getRequestTableMethod() {
    io.grpc.MethodDescriptor<network.proto.TableRequest, network.proto.TableResponse> getRequestTableMethod;
    if ((getRequestTableMethod = DdbServiceGrpc.getRequestTableMethod) == null) {
      synchronized (DdbServiceGrpc.class) {
        if ((getRequestTableMethod = DdbServiceGrpc.getRequestTableMethod) == null) {
          DdbServiceGrpc.getRequestTableMethod = getRequestTableMethod =
              io.grpc.MethodDescriptor.<network.proto.TableRequest, network.proto.TableResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "RequestTable"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  network.proto.TableRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  network.proto.TableResponse.getDefaultInstance()))
              .setSchemaDescriptor(new DdbServiceMethodDescriptorSupplier("RequestTable"))
              .build();
        }
      }
    }
    return getRequestTableMethod;
  }

  private static volatile io.grpc.MethodDescriptor<network.proto.SaveTableRequest,
      network.proto.SaveTableResponse> getSaveTableMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "SaveTable",
      requestType = network.proto.SaveTableRequest.class,
      responseType = network.proto.SaveTableResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<network.proto.SaveTableRequest,
      network.proto.SaveTableResponse> getSaveTableMethod() {
    io.grpc.MethodDescriptor<network.proto.SaveTableRequest, network.proto.SaveTableResponse> getSaveTableMethod;
    if ((getSaveTableMethod = DdbServiceGrpc.getSaveTableMethod) == null) {
      synchronized (DdbServiceGrpc.class) {
        if ((getSaveTableMethod = DdbServiceGrpc.getSaveTableMethod) == null) {
          DdbServiceGrpc.getSaveTableMethod = getSaveTableMethod =
              io.grpc.MethodDescriptor.<network.proto.SaveTableRequest, network.proto.SaveTableResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "SaveTable"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  network.proto.SaveTableRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  network.proto.SaveTableResponse.getDefaultInstance()))
              .setSchemaDescriptor(new DdbServiceMethodDescriptorSupplier("SaveTable"))
              .build();
        }
      }
    }
    return getSaveTableMethod;
  }

  private static volatile io.grpc.MethodDescriptor<network.proto.DeleteTempTableRequest,
      network.proto.DeleteTempTableResponse> getDeleteTableMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "DeleteTable",
      requestType = network.proto.DeleteTempTableRequest.class,
      responseType = network.proto.DeleteTempTableResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<network.proto.DeleteTempTableRequest,
      network.proto.DeleteTempTableResponse> getDeleteTableMethod() {
    io.grpc.MethodDescriptor<network.proto.DeleteTempTableRequest, network.proto.DeleteTempTableResponse> getDeleteTableMethod;
    if ((getDeleteTableMethod = DdbServiceGrpc.getDeleteTableMethod) == null) {
      synchronized (DdbServiceGrpc.class) {
        if ((getDeleteTableMethod = DdbServiceGrpc.getDeleteTableMethod) == null) {
          DdbServiceGrpc.getDeleteTableMethod = getDeleteTableMethod =
              io.grpc.MethodDescriptor.<network.proto.DeleteTempTableRequest, network.proto.DeleteTempTableResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "DeleteTable"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  network.proto.DeleteTempTableRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  network.proto.DeleteTempTableResponse.getDefaultInstance()))
              .setSchemaDescriptor(new DdbServiceMethodDescriptorSupplier("DeleteTable"))
              .build();
        }
      }
    }
    return getDeleteTableMethod;
  }

  private static volatile io.grpc.MethodDescriptor<network.proto.ExecuteNonQueryRequest,
      network.proto.ExecuteNonQueryResponse> getExecuteNonQueryMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ExecuteNonQuery",
      requestType = network.proto.ExecuteNonQueryRequest.class,
      responseType = network.proto.ExecuteNonQueryResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<network.proto.ExecuteNonQueryRequest,
      network.proto.ExecuteNonQueryResponse> getExecuteNonQueryMethod() {
    io.grpc.MethodDescriptor<network.proto.ExecuteNonQueryRequest, network.proto.ExecuteNonQueryResponse> getExecuteNonQueryMethod;
    if ((getExecuteNonQueryMethod = DdbServiceGrpc.getExecuteNonQueryMethod) == null) {
      synchronized (DdbServiceGrpc.class) {
        if ((getExecuteNonQueryMethod = DdbServiceGrpc.getExecuteNonQueryMethod) == null) {
          DdbServiceGrpc.getExecuteNonQueryMethod = getExecuteNonQueryMethod =
              io.grpc.MethodDescriptor.<network.proto.ExecuteNonQueryRequest, network.proto.ExecuteNonQueryResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ExecuteNonQuery"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  network.proto.ExecuteNonQueryRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  network.proto.ExecuteNonQueryResponse.getDefaultInstance()))
              .setSchemaDescriptor(new DdbServiceMethodDescriptorSupplier("ExecuteNonQuery"))
              .build();
        }
      }
    }
    return getExecuteNonQueryMethod;
  }

  private static volatile io.grpc.MethodDescriptor<network.proto.ExecuteQueryRequest,
      network.proto.ExecuteQueryResponse> getExecuteQueryMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ExecuteQuery",
      requestType = network.proto.ExecuteQueryRequest.class,
      responseType = network.proto.ExecuteQueryResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<network.proto.ExecuteQueryRequest,
      network.proto.ExecuteQueryResponse> getExecuteQueryMethod() {
    io.grpc.MethodDescriptor<network.proto.ExecuteQueryRequest, network.proto.ExecuteQueryResponse> getExecuteQueryMethod;
    if ((getExecuteQueryMethod = DdbServiceGrpc.getExecuteQueryMethod) == null) {
      synchronized (DdbServiceGrpc.class) {
        if ((getExecuteQueryMethod = DdbServiceGrpc.getExecuteQueryMethod) == null) {
          DdbServiceGrpc.getExecuteQueryMethod = getExecuteQueryMethod =
              io.grpc.MethodDescriptor.<network.proto.ExecuteQueryRequest, network.proto.ExecuteQueryResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ExecuteQuery"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  network.proto.ExecuteQueryRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  network.proto.ExecuteQueryResponse.getDefaultInstance()))
              .setSchemaDescriptor(new DdbServiceMethodDescriptorSupplier("ExecuteQuery"))
              .build();
        }
      }
    }
    return getExecuteQueryMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static DdbServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<DdbServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<DdbServiceStub>() {
        @java.lang.Override
        public DdbServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new DdbServiceStub(channel, callOptions);
        }
      };
    return DdbServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static DdbServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<DdbServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<DdbServiceBlockingStub>() {
        @java.lang.Override
        public DdbServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new DdbServiceBlockingStub(channel, callOptions);
        }
      };
    return DdbServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static DdbServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<DdbServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<DdbServiceFutureStub>() {
        @java.lang.Override
        public DdbServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new DdbServiceFutureStub(channel, callOptions);
        }
      };
    return DdbServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class DdbServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void requestTable(network.proto.TableRequest request,
        io.grpc.stub.StreamObserver<network.proto.TableResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getRequestTableMethod(), responseObserver);
    }

    /**
     */
    public void saveTable(network.proto.SaveTableRequest request,
        io.grpc.stub.StreamObserver<network.proto.SaveTableResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getSaveTableMethod(), responseObserver);
    }

    /**
     */
    public void deleteTable(network.proto.DeleteTempTableRequest request,
        io.grpc.stub.StreamObserver<network.proto.DeleteTempTableResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getDeleteTableMethod(), responseObserver);
    }

    /**
     */
    public void executeNonQuery(network.proto.ExecuteNonQueryRequest request,
        io.grpc.stub.StreamObserver<network.proto.ExecuteNonQueryResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getExecuteNonQueryMethod(), responseObserver);
    }

    /**
     */
    public void executeQuery(network.proto.ExecuteQueryRequest request,
        io.grpc.stub.StreamObserver<network.proto.ExecuteQueryResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getExecuteQueryMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getRequestTableMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                network.proto.TableRequest,
                network.proto.TableResponse>(
                  this, METHODID_REQUEST_TABLE)))
          .addMethod(
            getSaveTableMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                network.proto.SaveTableRequest,
                network.proto.SaveTableResponse>(
                  this, METHODID_SAVE_TABLE)))
          .addMethod(
            getDeleteTableMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                network.proto.DeleteTempTableRequest,
                network.proto.DeleteTempTableResponse>(
                  this, METHODID_DELETE_TABLE)))
          .addMethod(
            getExecuteNonQueryMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                network.proto.ExecuteNonQueryRequest,
                network.proto.ExecuteNonQueryResponse>(
                  this, METHODID_EXECUTE_NON_QUERY)))
          .addMethod(
            getExecuteQueryMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                network.proto.ExecuteQueryRequest,
                network.proto.ExecuteQueryResponse>(
                  this, METHODID_EXECUTE_QUERY)))
          .build();
    }
  }

  /**
   */
  public static final class DdbServiceStub extends io.grpc.stub.AbstractAsyncStub<DdbServiceStub> {
    private DdbServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected DdbServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new DdbServiceStub(channel, callOptions);
    }

    /**
     */
    public void requestTable(network.proto.TableRequest request,
        io.grpc.stub.StreamObserver<network.proto.TableResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getRequestTableMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void saveTable(network.proto.SaveTableRequest request,
        io.grpc.stub.StreamObserver<network.proto.SaveTableResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getSaveTableMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void deleteTable(network.proto.DeleteTempTableRequest request,
        io.grpc.stub.StreamObserver<network.proto.DeleteTempTableResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getDeleteTableMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void executeNonQuery(network.proto.ExecuteNonQueryRequest request,
        io.grpc.stub.StreamObserver<network.proto.ExecuteNonQueryResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getExecuteNonQueryMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void executeQuery(network.proto.ExecuteQueryRequest request,
        io.grpc.stub.StreamObserver<network.proto.ExecuteQueryResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getExecuteQueryMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class DdbServiceBlockingStub extends io.grpc.stub.AbstractBlockingStub<DdbServiceBlockingStub> {
    private DdbServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected DdbServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new DdbServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public network.proto.TableResponse requestTable(network.proto.TableRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getRequestTableMethod(), getCallOptions(), request);
    }

    /**
     */
    public network.proto.SaveTableResponse saveTable(network.proto.SaveTableRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getSaveTableMethod(), getCallOptions(), request);
    }

    /**
     */
    public network.proto.DeleteTempTableResponse deleteTable(network.proto.DeleteTempTableRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getDeleteTableMethod(), getCallOptions(), request);
    }

    /**
     */
    public network.proto.ExecuteNonQueryResponse executeNonQuery(network.proto.ExecuteNonQueryRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getExecuteNonQueryMethod(), getCallOptions(), request);
    }

    /**
     */
    public network.proto.ExecuteQueryResponse executeQuery(network.proto.ExecuteQueryRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getExecuteQueryMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class DdbServiceFutureStub extends io.grpc.stub.AbstractFutureStub<DdbServiceFutureStub> {
    private DdbServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected DdbServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new DdbServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<network.proto.TableResponse> requestTable(
        network.proto.TableRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getRequestTableMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<network.proto.SaveTableResponse> saveTable(
        network.proto.SaveTableRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getSaveTableMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<network.proto.DeleteTempTableResponse> deleteTable(
        network.proto.DeleteTempTableRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getDeleteTableMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<network.proto.ExecuteNonQueryResponse> executeNonQuery(
        network.proto.ExecuteNonQueryRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getExecuteNonQueryMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<network.proto.ExecuteQueryResponse> executeQuery(
        network.proto.ExecuteQueryRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getExecuteQueryMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_REQUEST_TABLE = 0;
  private static final int METHODID_SAVE_TABLE = 1;
  private static final int METHODID_DELETE_TABLE = 2;
  private static final int METHODID_EXECUTE_NON_QUERY = 3;
  private static final int METHODID_EXECUTE_QUERY = 4;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final DdbServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(DdbServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_REQUEST_TABLE:
          serviceImpl.requestTable((network.proto.TableRequest) request,
              (io.grpc.stub.StreamObserver<network.proto.TableResponse>) responseObserver);
          break;
        case METHODID_SAVE_TABLE:
          serviceImpl.saveTable((network.proto.SaveTableRequest) request,
              (io.grpc.stub.StreamObserver<network.proto.SaveTableResponse>) responseObserver);
          break;
        case METHODID_DELETE_TABLE:
          serviceImpl.deleteTable((network.proto.DeleteTempTableRequest) request,
              (io.grpc.stub.StreamObserver<network.proto.DeleteTempTableResponse>) responseObserver);
          break;
        case METHODID_EXECUTE_NON_QUERY:
          serviceImpl.executeNonQuery((network.proto.ExecuteNonQueryRequest) request,
              (io.grpc.stub.StreamObserver<network.proto.ExecuteNonQueryResponse>) responseObserver);
          break;
        case METHODID_EXECUTE_QUERY:
          serviceImpl.executeQuery((network.proto.ExecuteQueryRequest) request,
              (io.grpc.stub.StreamObserver<network.proto.ExecuteQueryResponse>) responseObserver);
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

  private static abstract class DdbServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    DdbServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return network.proto.DdbProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("DdbService");
    }
  }

  private static final class DdbServiceFileDescriptorSupplier
      extends DdbServiceBaseDescriptorSupplier {
    DdbServiceFileDescriptorSupplier() {}
  }

  private static final class DdbServiceMethodDescriptorSupplier
      extends DdbServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    DdbServiceMethodDescriptorSupplier(String methodName) {
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
      synchronized (DdbServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new DdbServiceFileDescriptorSupplier())
              .addMethod(getRequestTableMethod())
              .addMethod(getSaveTableMethod())
              .addMethod(getDeleteTableMethod())
              .addMethod(getExecuteNonQueryMethod())
              .addMethod(getExecuteQueryMethod())
              .build();
        }
      }
    }
    return result;
  }
}
