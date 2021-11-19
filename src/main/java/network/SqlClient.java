package network;

import java.util.ArrayList;
import java.util.List;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import network.proto.DdbServiceGrpc;
import network.proto.ExecuteNonQueryRequest;
import network.proto.ExecuteNonQueryResponse;
import network.proto.ExecuteQueryRequest;
import network.proto.ExecuteQueryResponse;
import network.proto.SaveTableRequest;
import network.proto.SaveTableResponse;
import network.proto.TableRequest;
import network.proto.TableResponse;
import network.proto.DdbServiceGrpc.DdbServiceBlockingStub;
import network.proto.SaveTableRequest.Builder;

public class SqlClient {

    private DdbServiceBlockingStub stub;
    
    public SqlClient(String ip) {
        String target = ip + ":" + Constants.SERVER_PORT;
        ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
                                                      .usePlaintext()
                                                      .build();
        stub = DdbServiceGrpc.newBlockingStub(channel);
    }

    public boolean saveTable(String tableName, String attributeMeta, List<String> attributeValues) {
        Builder saveTableRequestBuilder = SaveTableRequest.newBuilder()
                                                          .setTableName(tableName)
                                                          .setAttributeMeta(attributeMeta);
        for (String attributeValue : attributeValues) {
            saveTableRequestBuilder.addAttributeValues(attributeValue);
        }
        SaveTableRequest saveTableRequest = saveTableRequestBuilder.build();
        SaveTableResponse saveTableResponse = stub.saveTable(saveTableRequest);
        return saveTableResponse.getSuccess();
    }
    
    public List<String> requestTable(String tempTableName) {
        TableRequest tableRequest = TableRequest.newBuilder()
                                                .setTempTableName(tempTableName)
                                                .build();
        TableResponse tableResponse = stub.requestTable(tableRequest);
        List<String> results = new ArrayList<>();
        results.add(tableResponse.getAttributeMeta());
        for (int i = 0; i < tableResponse.getAttributeValuesCount(); i++) {
            results.add(tableResponse.getAttributeValues(i));
        }
        return results;
    }

    public List<String> executeQuery(String query) {
        ExecuteQueryRequest queryRequest = ExecuteQueryRequest.newBuilder()
                                                              .setSql(query)
                                                              .build();
        ExecuteQueryResponse queryResponse = stub.executeQuery(queryRequest);
        List<String> results = new ArrayList<>();
        results.add(queryResponse.getAttributeMeta());
        for (int i = 0; i < queryResponse.getAttributeValuesCount(); i++) {
            results.add(queryResponse.getAttributeValues(i));
        }
        return results;
    }

    public boolean executeNonQuery(String nonQuery) {
        ExecuteNonQueryRequest nonQueryRequest = ExecuteNonQueryRequest.newBuilder()
                                                                       .setSql(nonQuery)
                                                                       .build();
        ExecuteNonQueryResponse nonQueryResponse = stub.executeNonQuery(nonQueryRequest);
        return nonQueryResponse.getSuccess();
    }

    public void close() {
        ManagedChannel channel = (ManagedChannel) stub.getChannel();
        channel.shutdown();
    }

}