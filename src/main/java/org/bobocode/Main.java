package org.bobocode;

import org.bobocode.datasource.PooledDataSource;
import org.postgresql.ds.PGSimpleDataSource;
import javax.sql.DataSource;
import java.sql.SQLException;

public class Main {
    private static final String url = "jdbc:postgresql://localhost:5432/postgres";
    private static final String username = "postgres";
    private static final String password = "Abcd1234";

    public static void main(String[] args) {

        var dataSource= getPooledDataSource();
//        var dataSource= getDataSource();
        try {
            int total = 0;
            var start = System.nanoTime();
            for (int i=0; i<500; i++) {
                var connection = dataSource.getConnection();
                connection.setAutoCommit(false);
                var preparedStatement = connection.prepareStatement("SELECT * FROM products ORDER BY RANDOM()");
                var resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("name");
                    int price = resultSet.getInt("price");

                    total += price;
//                    System.out.printf("Product with: id=%d, name=%s, price=%d\r\n", id, name, price);
                }
                connection.close();
            }
            System.out.println("Total: " + total);
            System.out.println("Execution time: " + (System.nanoTime() - start)/1_000_000 + "ms");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static DataSource getDataSource () {
        var datasource = new PGSimpleDataSource();
        datasource.setURL(url);
        datasource.setUser(username);
        datasource.setPassword(password);
        return datasource;
    }

    private static DataSource getPooledDataSource() {
        var dataSource = new PooledDataSource(url, username, password);
        return dataSource;
    }
}