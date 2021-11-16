package com.minepalm.manyworlds.core.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class MySQLDatabase {

    protected final HikariDataSource dataSource;
    protected final ExecutorService service;

    public MySQLDatabase(Properties properties, ExecutorService service){
        HikariConfig hikariConfig = new HikariConfig();
        String address = properties.getProperty("address");
        String port = properties.getProperty("port");
        String database = properties.getProperty("database");
        String username = properties.getProperty("username");
        String password = properties.getProperty("password");

        hikariConfig.setJdbcUrl("jdbc:mysql://" + address + ":" + port + "/" + database + "?autoReconnect=true&allowMultiQueries=true");
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);

        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        hikariConfig.addDataSourceProperty("useServerPrepStmts", "true");
        hikariConfig.addDataSourceProperty("useLocalSessionState", "true");
        hikariConfig.addDataSourceProperty("rewriteBatchedStatements", "true");
        hikariConfig.addDataSourceProperty("cacheResultSetMetadata", "true");
        hikariConfig.addDataSourceProperty("cacheServerConfiguration", "true");
        hikariConfig.addDataSourceProperty("elideSetAutoCommits", "true");
        hikariConfig.addDataSourceProperty("maintainTimeStats", "false");

        this.dataSource = new HikariDataSource(hikariConfig);
        this.service = service;
    }
    
    public <R> CompletableFuture<R> executeAsync(ThrowingFunction<Connection, R> function) {
        CompletableFuture<R> future = new CompletableFuture<>();
        service.submit(() -> {
            try (Connection con = dataSource.getConnection()) {
                future.complete(function.acceptThrowing(con));
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        });
        return future;
    }

    public void executeAsync(ThrowingConsumer<Connection> consumer) {
        service.submit(() -> {
            try (Connection connection = dataSource.getConnection()) {
                consumer.acceptThrowing(connection);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public <R> R execute(ThrowingFunction<Connection, R> function) {
        try (Connection con = dataSource.getConnection()) {
            return function.acceptThrowing(con);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void execute(ThrowingConsumer<Connection> consumer) {
        try (Connection con = dataSource.getConnection()) {
            consumer.acceptThrowing(con);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
