package network;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import lcoal.MySQL;
import network.proto.DeleteTempTableRequest;
import network.proto.DeleteTempTableResponse;
import network.proto.ExecuteNonQueryRequest;
import network.proto.ExecuteNonQueryResponse;
import network.proto.ExecuteQueryRequest;
import network.proto.ExecuteQueryResponse;
import network.proto.SaveTableRequest;
import network.proto.SaveTableResponse;
import network.proto.TableRequest;
import network.proto.TableResponse;
import network.proto.DdbServiceGrpc.DdbServiceImplBase;

public class SqlServer {
    private Server server;

    public void start() throws IOException {
        int port = 50051;
        server = ServerBuilder.forPort(port)
                              .addService(new DdbServiceImpl())
                              .build()
                              .start();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    SqlServer.this.stop();
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
            }
        });
    }

    private void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }
}

class DdbServiceImpl extends DdbServiceImplBase {
    @Override
    public void saveTable(SaveTableRequest saveTableRequest,
                         StreamObserver<SaveTableResponse> responseObserver) {
        String attributeMeta = saveTableRequest.getAttributeMeta();
        String tableName = saveTableRequest.getTableName();
        String toCreateTale = String.format("create table if not exists `%s` (%s) ENGINE=InnoDB DEFAULT CHARSET=utf8", 
                                            tableName, attributeMeta);
        MySQL.executeNonQuery(toCreateTale, null);
        if (saveTableRequest.getAttributeValuesCount() > 0) {
            StringBuffer toInsertDataBuffer = new StringBuffer();
            toInsertDataBuffer.append("insert into `")
                              .append(tableName)
                              .append("` values (")
                              .append(saveTableRequest.getAttributeValues(0))
                              .append(')');
            for (int i = 1; i < saveTableRequest.getAttributeValuesCount(); i++) {
                toInsertDataBuffer.append(", (")
                                  .append(saveTableRequest.getAttributeValues(i))
                                  .append(')');
            }
            String toInsertData = toInsertDataBuffer.toString();
            MySQL.executeNonQuery(toInsertData, null);
        }
        SaveTableResponse saveTableResponse = SaveTableResponse.newBuilder()
                                                               .setSuccess(true)
                                                               .build();
        responseObserver.onNext(saveTableResponse);
        responseObserver.onCompleted();
    }


    @Override
    public void requestTable(TableRequest tableRequest, 
                             StreamObserver<TableResponse> responseObserver) {
        // TODO
    }

    @Override
    public void executeQuery(ExecuteQueryRequest queryRequest,
                             StreamObserver<ExecuteQueryResponse> responseObserver) {
        String query = queryRequest.getSql();
        ExecuteQueryResponse.Builder builder = ExecuteQueryResponse.newBuilder();
        MySQL.executeQuery(query, builder);
        ExecuteQueryResponse executeQueryResponse = builder.build();
        responseObserver.onNext(executeQueryResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void executeNonQuery(ExecuteNonQueryRequest nonQueryRequest,
                                StreamObserver<ExecuteNonQueryResponse> responseObserver) {
        String nonQuery = nonQueryRequest.getSql();
        ExecuteNonQueryResponse.Builder builder = ExecuteNonQueryResponse.newBuilder();
        MySQL.executeNonQuery(nonQuery, builder);
        ExecuteNonQueryResponse executeNonQueryResponse = builder.build();
        responseObserver.onNext(executeNonQueryResponse);
        responseObserver.onCompleted();
    }

    @Override
    public void deleteTable(DeleteTempTableRequest request,
                            StreamObserver<DeleteTempTableResponse> responseObserver) {
        String tempTableName = request.getTempTableName();
        String toDeleteTable = String.format("drop table if exists `%s`;", tempTableName);
        MySQL.executeNonQuery(toDeleteTable, null);
        DeleteTempTableResponse deleteTempTableResponse = DeleteTempTableResponse.newBuilder()
                                                                                 .setSuccess(true)
                                                                                 .build();
        responseObserver.onNext(deleteTempTableResponse);
        responseObserver.onCompleted();
    }
}