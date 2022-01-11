package network;

import io.etcd.jetcd.ByteSequence;

public class Constants {
    public static final long TTL = 10;
    public static final ByteSequence LOCK_KEY = EtcdClient.bytesOf("LOCK");
    public static final ByteSequence LOCK_VALUE = EtcdClient.bytesOf("0");
    public static final String SITES_KEY = "sites";
    public static final int SERVER_PORT = 31100;
    public static final String ETCD_ENDPOINTS = "http://10.77.70.68:2379,http://10.77.70.68:2379,http://10.77.70.68:2379";
    // public static final String ETCD_ENDPOINTS = "http://192.168.31.101:2379";
}