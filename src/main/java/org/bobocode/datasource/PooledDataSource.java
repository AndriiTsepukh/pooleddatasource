package org.bobocode.datasource;

import org.postgresql.ds.PGSimpleDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Queue;

public class PooledDataSource extends PGSimpleDataSource {
    Queue<Connection> connectionsQueue = new LinkedList<>();
    private final int MAX_CONNECTIONS = 8;
    public PooledDataSource(String url, String userName, String password){
        super();
        for (int i=0; i < MAX_CONNECTIONS; i++){
            Connection connection = null;
            try {
                super.setURL(url);
                super.setUser(userName);
                super.setPassword(password);
                connection = super.getConnection();

                ConnectionProxy connectionProxy = new ConnectionProxy(connection, connectionsQueue);

                connectionsQueue.add(connectionProxy);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        return connectionsQueue.peek();
    }
}