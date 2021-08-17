package com.minepalm.manyworlds.core.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.util.Properties;

public abstract class AbstractMySQL{

    protected static HikariDataSource hikari = null;

    protected AbstractMySQL(Properties properties){
        if(hikari == null) {
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

            hikari = new HikariDataSource(hikariConfig);
        }
    }

    protected abstract void create();
}
