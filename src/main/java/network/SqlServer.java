package network;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.ListenableFuture;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import lcoal.MySQL;
import network.proto.DdbServiceGrpc;
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
import network.proto.DdbServiceGrpc.DdbServiceFutureStub;
import network.proto.DdbServiceGrpc.DdbServiceImplBase;

public class SqlServer {
    private Server server;

    public static String site = "";
    private boolean isPortUsing(int port) throws UnknownHostException {
        boolean flag = false;
        InetAddress localAddress = InetAddress.getLocalHost();
        try {
            Socket socket = new Socket(localAddress, port);
            flag = true;
            socket.close();
        } catch (IOException ignoredException) { }
        return flag;
    }

    private String getIpAddress() {
	    try {
	      Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
	      InetAddress ip = null;
	      while (allNetInterfaces.hasMoreElements()) {
	        NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
	        if (netInterface.isLoopback() || netInterface.isVirtual() || !netInterface.isUp()) {
	          continue;
	        } else {
	          Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
	          while (addresses.hasMoreElements()) {
	            ip = addresses.nextElement();
	            if (ip != null && ip instanceof Inet4Address) {
	              return ip.getHostAddress();
	            }
	          }
	        }
	      }
	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
        return "";
    }

    public void start() throws IOException {
        for (int offset = 0; offset < 65536; offset++) {
            int port = Constants.SERVER_PORT + offset;
            if (false == isPortUsing(port)) {
                server = ServerBuilder.forPort(port)
                                      .addService(new DdbServiceImpl())
                                      .build()
                                      .start();
                try (EtcdClient client = new EtcdClient(Constants.ETCD_ENDPOINTS)) {
                    client.lock();
                    String sites = client.get(Constants.SITES_KEY);
                    String ip = getIpAddress();
                    StringBuffer siteBuffer = new StringBuffer();
                    siteBuffer.append(ip)
                              .append(":")
                              .append(port);
                    site = siteBuffer.toString();
                    if (false == sites.contains(site)) {
                        if (false == sites.isEmpty()) {
                            sites = sites + ",";
                        }
                        sites = sites + site;
                        client.put(Constants.SITES_KEY, sites);
                    }
                    client.unlock();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
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
    private ListenableFuture<TableResponse> getDataFromChild(String address, String tempTableName) {
        ManagedChannel channel = ManagedChannelBuilder.forTarget(address)
                                                      .usePlaintext()
                                                      .build();
        DdbServiceFutureStub stub = DdbServiceGrpc.newFutureStub(channel);
        TableRequest tableRequest = TableRequest.newBuilder()
                                                .setTempTableName(tempTableName)
                                                .build();
        ListenableFuture<TableResponse> response = stub.requestTable(tableRequest);
        channel.shutdown();
        return response;
    }

    private List<String> waitForData(ListenableFuture<TableResponse> response) {
        TableResponse tableResponse = null;
        try {
            tableResponse = response.get();
        } catch (CancellationException ce) {
            System.out.println("the computation was cancelled");
        } catch (ExecutionException ee) {
            System.out.println("the computation threw an exception");
        } catch (InterruptedException ie) {
            System.out.println("the current thread was interrupted while waiting");
        }
        List<String> queryResult = new ArrayList<>();
        queryResult.add(tableResponse.getAttributeMeta());
        for (int j = 0; j < tableResponse.getAttributeValuesCount(); j++) {
            queryResult.add(tableResponse.getAttributeValues(j));
        }
        return queryResult;
    }

    private void insert(String tableName, List<String> queryResult) {
        if (queryResult.size() > 1) {
            StringBuffer sqlBuffer = new StringBuffer();
            sqlBuffer.append("insert into `")
                     .append(tableName)
                     .append("` values (")
                     .append(queryResult.get(1))
                     .append(')');
            for (int j = 2; j < queryResult.size(); j++) {
                sqlBuffer.append(", (")
                         .append(queryResult.get(j))
                         .append(')');
            }
            String sql = sqlBuffer.toString();
            MySQL.executeNonQuery(sql, null);
        }
    }

    private TableResponse select(String tableName, String project, String condition) {
        TableResponse.Builder builder = TableResponse.newBuilder();
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append(String.format("select %s from `%s`", project, tableName));
        if (condition != null && !condition.isEmpty()) {
            sqlBuffer.append(String.format(" where %s", condition));
        }
        final String sql = sqlBuffer.toString();
        MySQL.executeQuery(sql, builder);
        TableResponse tableResponse = builder.build();
        return tableResponse;
    }

    private TableResponse select(String tableName1, String tableName2,
                                 String joinAttribute, String project, String condition) {
        TableResponse.Builder builder = TableResponse.newBuilder();
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append(String.format("select %s from ", project));
        sqlBuffer.append(String.format("`%s` join `%s` ", tableName1, tableName2));
        sqlBuffer.append(String.format("on %s", joinAttribute));
        if (condition != null && !condition.isEmpty()) {
            sqlBuffer.append(String.format(" where %s", condition));
        }
        String sql = sqlBuffer.toString();
        MySQL.executeQuery(sql, builder);
        TableResponse tableResponse = builder.build();
        return tableResponse;
    }


    @Override
    public void saveTable(SaveTableRequest saveTableRequest,
                         StreamObserver<SaveTableResponse> responseObserver) {
        String attributeMeta = saveTableRequest.getAttributeMeta();
        String tableName = saveTableRequest.getTableName();
        String toCreateTale = 
            String.format("create table if not exists `%s` (%s) ENGINE=InnoDB DEFAULT CHARSET=utf8", 
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
                                //  try {
								// 	Thread.sleep(5000);
								// } catch (InterruptedException e1) {
								// 	// TODO Auto-generated catch block
								// 	e1.printStackTrace();
								// }
        // 从获取ETCD中获取要执行的结点并删除
        final String tempTableName = tableRequest.getTempTableName();
        TempTable tempTable = null;
        try (EtcdClient etcdClient = new EtcdClient(Constants.ETCD_ENDPOINTS)) {
            String json = etcdClient.get(tempTableName);
            tempTable = TempTable.fromJson(json);
            etcdClient.remove(tempTableName);
        } catch (Exception e) {
            e.printStackTrace();;
        }

        if (tempTable.isLeaf) {
            // 叶子结点直接在本地执行SQL语句
            final String tableName = tempTable.tableName;
            TableResponse tableResponse = select(tableName, tempTable.project, tempTable.condition);
            responseObserver.onNext(tableResponse);
            responseObserver.onCompleted();
        } else {
            // 非叶子结点
            final List<String> addresses = tempTable.addresses;
            final List<String> children = tempTable.children;
            if (addresses.size() != children.size() || addresses.size() <= 0) {
                return;
            }
            final int n = addresses.size();
        
            if (tempTable.isUnion) {
                // union

                // 从子结点获取数据并保存到同一张表中
                List<ListenableFuture<TableResponse>> responses = new ArrayList<>();
                final long start = System.currentTimeMillis();
                for (int i = 0; i < n; i++) {
                    responses.add(getDataFromChild(addresses.get(i), children.get(i)));
                }
                List<String> queryResult = waitForData(responses.get(0));
                if (queryResult.size() == 0) {
                    return;
                }
                final CountDownLatch countDownLatch = new CountDownLatch(n);
                for (int i = 0; i < n; i++) {
                    final int p = i;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            List<String> queryResult = waitForData(responses.get(p));
                            long size = 0;
                            for (String data : queryResult) {
                                size += data.length();
                            }
                            try (EtcdClient client = new EtcdClient(Constants.ETCD_ENDPOINTS)) {
                                long finish = System.currentTimeMillis();
                                String accumulateSize = client.get(addresses.get(p) + "_SIZE");
                                if (accumulateSize.isEmpty()) {
                                    accumulateSize = "0";
                                }
                                String accumulateTime = client.get(addresses.get(p) + "_TIME");
                                if (accumulateTime.isEmpty()) {
                                    accumulateTime = "0";
                                }
                                client.put(addresses.get(p) + "_SIZE", Long.toString(Long.parseLong(accumulateSize) + size));
                                client.put(addresses.get(p) + "_TIME", Long.toString(Long.parseLong(accumulateTime) + finish - start));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            String sql = String.format("create table if not exists `%s` (%s) ENGINE=InnoDB DEFAULT CHARSET=utf8",
                                                       tempTableName, queryResult.get(0));
                            MySQL.executeNonQuery(sql, null);
                            insert(tempTableName, queryResult);
                            countDownLatch.countDown();
                        }
                    }).start();
                }
                try {
                    countDownLatch.await();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // 把数据返回给父节点
                TableResponse tableResponse = select(tempTableName, tempTable.project,
                                                     tempTable.condition);
                responseObserver.onNext(tableResponse);

                // 删除暂存的临时表
                String sql = String.format("drop table if exists `%s`;", tempTableName);
                MySQL.executeNonQuery(sql, null);
                responseObserver.onCompleted();
            } else {
                // join
                if (n != 2) {
                    return;
                }

                // 从子结点获取数据并保存到不同的表中
                List<ListenableFuture<TableResponse>> responses = new ArrayList<>();
                final long start = System.currentTimeMillis();
                for (int i = 0; i < n; i++) {
                    responses.add(getDataFromChild(addresses.get(i), children.get(i)));
                }
                final CountDownLatch countDownLatch = new CountDownLatch(n);
                for (int i = 0; i < n; i++) {   
                    final int p = i;
                    new Thread(new Runnable(){
                        @Override
                        public void run() {
                            List<String> queryResult = waitForData(responses.get(p));
                            if (queryResult.size() == 0) {
                                return;
                            }
                            long size = 0;
                            for (String data : queryResult) {
                                size += data.length();
                            }
                            try (EtcdClient client = new EtcdClient(Constants.ETCD_ENDPOINTS)) {
                                long finish = System.currentTimeMillis();
                                String accumulateSize = client.get(addresses.get(p) + "_SIZE");
                                if (accumulateSize.isEmpty()) {
                                    accumulateSize = "0";
                                }
                                String accumulateTime = client.get(addresses.get(p) + "_TIME");
                                if (accumulateTime.isEmpty()) {
                                    accumulateTime = "0";
                                }
                                client.put(addresses.get(p) + "_SIZE", Long.toString(Long.parseLong(accumulateSize) + size));
                                client.put(addresses.get(p) + "_TIME", Long.toString(Long.parseLong(accumulateTime) + finish - start));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            String sql = String.format("drop table if exists `%s`;", children.get(p));
                            MySQL.executeNonQuery(sql, null);
                            sql = String.format("create table `%s` (%s) ENGINE=InnoDB DEFAULT CHARSET=utf8",
                                                children.get(p), queryResult.get(0));
                            MySQL.executeNonQuery(sql, null);
                            insert(children.get(p), queryResult);
                            countDownLatch.countDown();
                        }
                    }).start();
                }
                try {
                    countDownLatch.await();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // 把数据返回给父节点
                TableResponse tableResponse = select(children.get(0), children.get(1),
                                                     tempTable.joinAttribute, tempTable.project, 
                                                     tempTable.condition);
                responseObserver.onNext(tableResponse);

                // 删除暂存的临时表
                for (int i = 0; i < n; i++) {
                    String sql = String.format("drop table if exists `%s`;", children.get(i));
                    MySQL.executeNonQuery(sql, null);
                }
                responseObserver.onCompleted();
            }
        }
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