import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

import network.SqlClient;
import network.SqlServer;

public class Main {
    public static void main(String[] args) {
        SqlServer server = new SqlServer();
        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // SqlClient client67 = new SqlClient("10.77.70.67"); //10.77.70.68 127.0.0.1
        // List<String> t67 = new ArrayList<>();
        // t67.add("1, 'abc'");
        // t67.add("2, 'def'");
        // client67.saveTable("t", "id int, name varchar(6)", t67);
        // System.out.println("10.77.70.67");
        // System.out.println(client67.executeQuery("select * from t"));
        // client67.executeNonQuery("delete from t");
        // client67.executeNonQuery("drop table t");
        // System.out.println();

        // SqlClient client68 = new SqlClient("10.77.70.68"); //10.77.70.68 127.0.0.1
        // List<String> t68 = new ArrayList<>();
        // t68.add("3, 'abc'");
        // t68.add("4, 'def'");
        // client68.saveTable("t", "id int, name varchar(6)", t68);
        // System.out.println("10.77.70.68");
        // System.out.println(client68.executeQuery("select * from t"));
        // client68.executeNonQuery("delete from t");
        // client68.executeNonQuery("drop table t");
        // System.out.println();

        // SqlClient client69 = new SqlClient("10.77.70.69"); //10.77.70.68 127.0.0.1
        // List<String> t69 = new ArrayList<>();
        // t69.add("5, 'abc'");
        // t69.add("6, 'def'");
        // client69.saveTable("t", "id int, name varchar(6)", t69);
        // System.out.println("10.77.70.69");
        // System.out.println(client69.executeQuery("select * from t"));
        // client69.executeNonQuery("delete from t");
        // client69.executeNonQuery("drop table t");
        // System.out.println();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String ip = scanner.next();
            SqlClient client = new SqlClient(ip);
            int type = scanner.nextInt();
            if (type == 0) {
                scanner.nextLine();
                String sql1 = scanner.nextLine();
                client.executeNonQuery(sql1);
            } else if (type == 1) {
                scanner.nextLine();
                String sql2 = scanner.nextLine();
                List<String> l = client.executeQuery(sql2);
                for (String s : l) {
                    System.out.println(s);
                }
            } else if (type == 2) {
                List<String> t = new ArrayList<>();
                t.add("1, 'abc'");
                t.add("2, 'def'");
                client.saveTable("t", "id int, name varchar(6)", t);
            } else {
                break;
            }
        }

        SqlClient client = new SqlClient("127.0.0.1"); //10.77.70.68 127.0.0.1
        client.executeNonQuery("delete from t");
        client.executeNonQuery("drop table t");

        // SqlClient client67 = new SqlClient("10.77.70.67"); //10.77.70.68 127.0.0.1
        // client67.executeNonQuery("delete from t");
        // client67.executeNonQuery("drop table t");

        // SqlClient client68 = new SqlClient("10.77.70.68"); //10.77.70.68 127.0.0.1
        // client68.executeNonQuery("delete from t");
        // client68.executeNonQuery("drop table t");

        // SqlClient client69 = new SqlClient("10.77.70.69"); //10.77.70.68 127.0.0.1
        // client69.executeNonQuery("delete from t");
        // client69.executeNonQuery("drop table t");

        scanner.close();
    }
}