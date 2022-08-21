package org.bobocode.datasource;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class PooledDataSource implements DataSource {
    Queue<Connection> connectionsQueue = new LinkedList<>();
    DataSource dataSource;
    private final int MAX_CONNECTIONS = 8;
    public PooledDataSource(Supplier<DataSource> supplier){
        super();
        dataSource = supplier.get();
        for (int i=0; i < MAX_CONNECTIONS; i++){
            try {
                var connection = dataSource.getConnection();
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

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return dataSource.getConnection(username, password);
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return dataSource.getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        dataSource.setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        dataSource.setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return dataSource.getLoginTimeout();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return dataSource.getParentLogger();
    }


    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return dataSource.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return dataSource.isWrapperFor(iface);
    }
}