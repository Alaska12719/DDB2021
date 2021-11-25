package network;

import io.etcd.jetcd.ByteSequence;

public class Constants {
    public static final long TTL = 10;
    public static final ByteSequence LOCK_NAME = EtcdClient.bytesOf("LOCK");
    public static final int PORT = 50051;
    public static final int SERVER_PORT = 50051;
    public static final String ETCD_ENDPOINTS = "http://192.168.31.101:2379,http://192.168.31.102:2379,http://192.168.31.103:2379";
}