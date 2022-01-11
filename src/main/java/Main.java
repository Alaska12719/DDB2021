import java.util.ArrayList;
import java.util.Scanner;

import network.Constants;
import network.EtcdClient;
import network.Logger;
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
            Scanner cin = new Scanner(System.in);
            String input = cin.nextLine();
            while(!input.equals("exit")) {
                parser.parseInput(input);
                Logger.show();
                input = cin.nextLine();
            }
		    cin.close();
            // parser.deleteAllEtcd();
            // parser.parseInput("drop table Publisher");
            // parser.parseInput("drop table Customer");
            // parser.parseInput("drop table Orders");
            // parser.parseInput("drop table Book");
            // parser.parseInput("create table Publisher(id int key, name text, nation text)");
            // parser.parseInput("create table Book(id int key, title text, authors text, publisher_id int, copies int)");
            // parser.parseInput("create table Orders(customer_id int, book_id int, quantity int)");
            // parser.parseInput("create table Customer(id int key, name text, rank int)");
            // parser.parseInput("fragment Publisher horizontally into id < 104000 and nation='PRC', id < 104000 and nation = 'USA', id >= 104000 and nation ='PRC', id >= 104000 and nation ='USA'");
            // parser.parseInput("allocate Publisher-0 to 192.168.31.101:31100");
            // parser.parseInput("allocate Publisher-1 to 192.168.31.102:31100");
            // parser.parseInput("allocate Publisher-2 to 192.168.31.103:31100");
            // parser.parseInput("allocate Publisher-3 to 192.168.31.101:31101");
            // parser.parseInput("fragment Book horizontally into id < 205000, id >= 205000 and  id < 210000, id >= 210000");
            // parser.parseInput("allocate Book-0 to 192.168.31.101:31100");
            // parser.parseInput("allocate Book-1 to 192.168.31.102:31100");
            // parser.parseInput("allocate Book-2 to 192.168.31.103:31100");
            // parser.parseInput("fragment Customer vertically into (id,name),(id, rank)");
            // parser.parseInput("allocate Customer-0 to 192.168.31.101:31100");
            // parser.parseInput("allocate Customer-1 to 192.168.31.102:31100");
            // parser.parseInput("fragment Orders horizontally into customer_id < 307000 and book_id < 215000, customer_id < 307000 and book_id >= 215000, customer_id >= 307000 and book_id < 215000, customer_id >= 307000 and book_id >= 215000");
            // parser.parseInput("allocate Orders-0 to 192.168.31.101:31100");
            // parser.parseInput("allocate Orders-1 to 192.168.31.102:31100");
            // parser.parseInput("allocate Orders-2 to 192.168.31.103:31100");
            // parser.parseInput("allocate Orders-3 to 192.168.31.101:31101");
            // parser.parseInput("LOAD DATA LOCAL INFILE '/var/lib/mysql-files/data/data/book.tsv' INTO TABLE Book FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n'");
            // parser.parseInput("LOAD DATA LOCAL INFILE '/var/lib/mysql-files/data/data/customer.tsv' INTO TABLE Customer FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n'");
            // parser.parseInput("LOAD DATA LOCAL INFILE '/var/lib/mysql-files/data/data/orders.tsv' INTO TABLE Orders FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n'");
            // parser.parseInput("LOAD DATA LOCAL INFILE '/var/lib/mysql-files/data/data/publisher.tsv' INTO TABLE Publisher FIELDS TERMINATED BY '\t' LINES TERMINATED BY '\n'");
            
            
            // parser.parseInput("select * from Customer");
            //  parser.parseInput("select Publisher.name from Publisher");
            //  Logger.show();
             // parser.parseInput("select Book.title from Book where copies>5000");
            // parser.parseInput("select customer_id, quantity from Orders where quantity < 8");
            // parser.parseInput("select Book.title,Book.copies,Publisher.name,Publisher.nation from Book,Publisher where Book.publisher_id=Publisher.id and Publisher.nation='USA' and Book.copies > 1000");
            // parser.parseInput("select Customer.name,Orders.quantity from Customer,Orders where Customer.id=Orders.customer_id");
            // parser.parseInput("select Customer.name,Customer.rank,Orders.quantity from Customer,Orders where Customer.id=Orders.customer_id and Customer.rank=1");
            // parser.parseInput("select Customer.name ,Orders.quantity,Book.title from Customer,Orders,Book where Customer.id=Orders.customer_id and Book.id=Orders.book_id and Customer.rank=1 and Book.copies>5000");
            // parser.parseInput("select Customer.name, Book.title, Publisher.name, Orders.quantity from Customer, Book, Publisher, Orders where Customer.id=Orders.customer_id and Book.id=Orders.book_id and Book.publisher_id=Publisher.id and Book.id>220000 and Publisher.nation='USA' and Orders.quantity>1");
            // parser.parseInput("select Customer.name, Book.title,Publisher.name, Orders.quantity from Customer, Book, Publisher, Orders where Customer.id=Orders.customer_id and Book.id=Orders.book_id and Book.publisher_id=Publisher.id and Customer.id>308000 and Book.copies>100 and Orders.quantity>1 and Publisher.nation='PRC'");
            
            
            
            // parser.parseInput("insert into Publisher values(1,'abc','USA')");
            // parser.parseInput("insert into Publisher values(2,'BCD','PRC')");
            // parser.parseInput("select Customer.name, Orders.quantity from Customer,Orders where Customer.id=Orders.customer_id and Customer.id<3000 and Orders.book_id < 2000 and Orders.customer_id < 2000");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}