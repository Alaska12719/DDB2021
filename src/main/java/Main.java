import java.util.ArrayList;

import network.Constants;
import network.EtcdClient;
import network.SqlClient;
import network.SqlServer;
import network.TempTable;
import parser.Parser;

public class Main {
    public static void main(String[] args) {
        SqlServer server = new SqlServer();
        try {
            server.start();
            Parser parser = new Parser();
            // parser.deleteAllEtcd();
            // parser.parseInput("drop table Publisher");
            // parser.parseInput("create table Publisher(id int key, name text, nation text)");
            // parser.parseInput("fragment Publisher horizontally into id < 104000 and nation='PRC', id < 104000 and nation = 'USA', id >= 104000 and nation ='PRC'");
            // parser.parseInput("allocate Publisher-0 to 192.168.31.101:31100");
            // parser.parseInput("allocate Publisher-1 to 192.168.31.102:31100");
            // parser.parseInput("allocate Publisher-2 to 192.168.31.103:31100");
            // parser.parseInput("insert into Publisher values(1,'abc','USA')");
            // parser.parseInput("insert into Publisher values(2,'BCD','PRC')");
            parser.parseInput("select * from Publisher");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
               
        // TempTable t111 = new TempTable();
        // t111.project = "*";
        // t111.isLeaf = true;
        // t111.tableName = "t1";

        // TempTable t112 = new TempTable();
        // t112.project = "*";
        // t112.isLeaf = true;
        // t112.tableName = "t2";

        // TempTable t121 = new TempTable();
        // t121.project = "*";
        // t121.isLeaf = true;
        // t121.tableName = "t1";

        // TempTable t122 = new TempTable();
        // t122.project = "*";
        // t122.isLeaf = true;
        // t122.tableName = "t2";

        // TempTable t11 = new TempTable();
        // t11.project = "t111.id, t111.name, t112.age";
        // t11.isLeaf = false;
        // t11.isUnion = false;
        // t11.joinAttribute = "t111.id=t112.id";
        // t11.addresses = new ArrayList<>();
        // t11.addresses.add("192.168.31.102:31100");
        // t11.addresses.add("192.168.31.102:31100");
        // t11.children = new ArrayList<>();
        // t11.children.add("t111");
        // t11.children.add("t112");

        // TempTable t12 = new TempTable();
        // t12.project = "t121.id, t121.name, t122.age";
        // t12.isLeaf = false;
        // t12.isUnion = false;
        // t12.joinAttribute = "t121.id=t122.id";
        // t12.addresses = new ArrayList<>();
        // t12.addresses.add("192.168.31.103:31100");
        // t12.addresses.add("192.168.31.103:31100");
        // t12.children = new ArrayList<>();
        // t12.children.add("t121");
        // t12.children.add("t122");

        // TempTable t1 = new TempTable();
        // t1.project = "*";
        // t1.isLeaf = false;
        // t1.isUnion = true;
        // t1.addresses = new ArrayList<>();
        // t1.addresses.add("192.168.31.102:31100");
        // t1.addresses.add("192.168.31.103:31100");
        // t1.children = new ArrayList<>();
        // t1.children.add("t11");
        // t1.children.add("t12");

        // EtcdClient client = new EtcdClient(Constants.ETCD_ENDPOINTS);
        // try {
        //     client.put("t1", t1.toJson());
        //     client.put("t11", t11.toJson());
        //     client.put("t12", t12.toJson());
        //     client.put("t111", t111.toJson());
        //     client.put("t112", t112.toJson());
        //     client.put("t121", t121.toJson());
        //     client.put("t122", t122.toJson());
        // } catch (Exception e) {
        //     e.printStackTrace();
        // }
        // client.close();

        // SqlClient client2 = new SqlClient("192.168.31.101:31100");
        // System.out.println(client2.requestTable("t1"));
        // client2.close();

        while (true) {

        }
    }
}