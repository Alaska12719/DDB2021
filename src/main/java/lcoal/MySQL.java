package lcoal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import network.proto.ExecuteNonQueryResponse;
import network.proto.ExecuteQueryResponse;

public class MySQL {
    private static final String DATABASE_NAME = "ddb";
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";  
    private static final String DATABASE_URL = "jdbc:mysql://127.0.0.1:3306/";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    public static void executeQuery(String query, ExecuteQueryResponse.Builder builder) {
        Connection connection = null;
        Statement statement = null;
        try {
            Class.forName(JDBC_DRIVER);
            connection = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
            statement = connection.createStatement();
            statement.execute(String.format("create database if not exists `%s`", DATABASE_NAME));
            statement.execute(String.format("use `%s`", DATABASE_NAME));
            ResultSet resultSet = statement.executeQuery(query);
            ResultSetMetaData metaData = resultSet.getMetaData();
            List<String> columnNames = new ArrayList<>();
            List<String> columnTypes = new ArrayList<>();
            StringBuffer metaBuffer = new StringBuffer();
            for (int i = 0; i < metaData.getColumnCount(); i++) {
                if (i != 0) {
                    metaBuffer.append(',');
                }
                metaBuffer.append(metaData.getColumnName(i + 1))
                          .append(' ')
                          .append(metaData.getColumnTypeName(i + 1));
                columnNames.add(metaData.getColumnName(i + 1));
                columnTypes.add(metaData.getColumnTypeName(i + 1));
            }
            builder.setAttributeMeta(metaBuffer.toString());
            while (resultSet.next()) {
                StringBuffer rowBuffer = new StringBuffer();
                for (String columnName : columnNames) {
                    rowBuffer.append(resultSet.getString(columnName))
                             .append(',');
                }
                rowBuffer.deleteCharAt(rowBuffer.length() - 1);
                builder.addAttributeValues(rowBuffer.toString());
            }
            resultSet.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (Exception ignoreException){}
            try {
                if (statement != null) {
                    connection.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void executeNonQuery(String nonQuery, ExecuteNonQueryResponse.Builder builder) {
        Connection connection = null;
        Statement statement = null;
        try {
            Class.forName(JDBC_DRIVER);
            connection = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
            statement = connection.createStatement();
            statement.execute(String.format("create database if not exists `%s`", DATABASE_NAME));
            statement.execute(String.format("use `%s`", DATABASE_NAME));
            statement.execute(nonQuery);
            if (builder != null) {
                builder.setSuccess(true);
            }
            statement.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
            } catch (Exception ignoreException){}
            try {
                if (statement != null) {
                    connection.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
