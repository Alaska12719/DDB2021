
package network;

import static com.google.common.base.Charsets.UTF_8;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.KeyValue;
import io.etcd.jetcd.Lease;
import io.etcd.jetcd.Lock;
import io.etcd.jetcd.Txn;
import io.etcd.jetcd.kv.DeleteResponse;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.kv.TxnResponse;
import io.etcd.jetcd.lease.LeaseGrantResponse;
import io.etcd.jetcd.lease.LeaseKeepAliveResponse;
import io.etcd.jetcd.op.Op;
import io.etcd.jetcd.op.Op.GetOp;
import io.etcd.jetcd.op.Op.PutOp;
import io.etcd.jetcd.options.DeleteOption;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import io.grpc.stub.StreamObserver;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class EtcdClient {
    private long leaseId;
    private final Client client;

    public static ByteSequence bytesOf(String s) {
        return ByteSequence.from(s, UTF_8);
    }

    public EtcdClient(String endPoints) {
        String[] l = endPoints.split(",");
        client = Client.builder().endpoints(endPoints.split(",")).build();
    }

    public void put(String key, String value) throws ExecutionException, InterruptedException {
        KV kv = client.getKVClient();
        kv.put(bytesOf(key), bytesOf(value)).get();
    }

    public void put(Map<String, String> keyValues) {
        KV kv = client.getKVClient();
        int i = 0;
        PutOp[] putOps = new PutOp[keyValues.size()];
        Iterator<Map.Entry<String, String>> iter = keyValues.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, String> entry = iter.next();
            String key = entry.getKey();
            String value = entry.getValue();
            putOps[i++] = Op.put(bytesOf(key), bytesOf(value), PutOption.DEFAULT);
        }
        Txn txn = kv.txn();
        txn.If().Then(putOps).commit();
    }

    public String get(String key) throws ExecutionException, InterruptedException {
        KV kv = client.getKVClient();
        GetResponse getResponse = kv.get(bytesOf(key)).get();
        if (getResponse.getKvs().size() > 0) {
            String value = getResponse.getKvs().get(0).getValue().toString(UTF_8);
            return value;
        }
        return null;
    }

    public Map<String, String> get(List<String> keys) throws ExecutionException, InterruptedException {
        KV kv = client.getKVClient();
        GetOp[] getOps = new GetOp[keys.size()];
        for (int i = 0; i < keys.size(); i++) {
            getOps[i] = Op.get(bytesOf(keys.get(i)), GetOption.DEFAULT);
        }
        Txn txn = kv.txn();
        TxnResponse txnResponse = txn.If().Then(getOps).commit().get();
        if (txnResponse.isSucceeded()) {
            List<GetResponse> getResponses = txnResponse.getGetResponses();
            if (getResponses.size() > 0) {
                Map<String, String> keyValues = new HashMap<>();
                for (int i = 0; i < getResponses.size(); i++) {
                    GetResponse getResponse = getResponses.get(i);
                    if (getResponse.getKvs().size() > 0) {
                        keyValues.put(getResponse.getKvs().get(0).getKey().toString(UTF_8),
                                getResponse.getKvs().get(0).getValue().toString(UTF_8));
                    }
                }
                return keyValues;
            }
        }
        return null;
    }

    public Map<String, String> getByPrefix(String prefix) throws ExecutionException, InterruptedException {
        KV kv = client.getKVClient();
        GetResponse getResponse = kv.get(bytesOf(prefix), GetOption.newBuilder().isPrefix(true).build()).get();
        if (getResponse.getCount() > 0) {
            Map<String, String> keyValues = new HashMap<>();
            for (KeyValue keyValue : getResponse.getKvs()) {
                keyValues.put(keyValue.getKey().toString(UTF_8), keyValue.getValue().toString(UTF_8));
            }
            return keyValues;
        }
        return null;
    }

    public long remove(String key) throws ExecutionException, InterruptedException {
        KV kv = client.getKVClient();
        DeleteResponse deleteResponse = kv.delete(bytesOf(key)).get();
        return deleteResponse.getDeleted();
    }

    public long removeByPrefix(String prefix) throws ExecutionException, InterruptedException {
        KV kv = client.getKVClient();
        DeleteResponse deleteResponse = kv.delete(bytesOf(prefix), DeleteOption.newBuilder().isPrefix(true).build())
                .get();
        return deleteResponse.getDeleted();
    }

    public void close() {
        client.close();
    }

    public void lock() throws InterruptedException, ExecutionException, TimeoutException {
        Lease lease = client.getLeaseClient();
        Lock lock = client.getLockClient();
        LeaseGrantResponse LeaseGrantResponse = lease.grant(1).get(1, TimeUnit.SECONDS);
        // System.out.println("x");
        leaseId = LeaseGrantResponse.getID();
        StreamObserver<LeaseKeepAliveResponse> observer = new StreamObserver<LeaseKeepAliveResponse>() {

            @Override
            public void onCompleted() {}

            @Override
            public void onError(Throwable t) {}

            @Override
            public void onNext(LeaseKeepAliveResponse value) {}
            
        };
        lease.keepAlive(leaseId, observer);
        lock.lock(Constants.LOCK_NAME, leaseId).get();
    }

    public void unlock() { 
        Lease lease = client.getLeaseClient();
        Lock lock = client.getLockClient();
        lock.unlock(Constants.LOCK_NAME);
        lease.revoke(leaseId);
    }
}
