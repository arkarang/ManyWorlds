package com.minepalm.manyworlds.bukkit.mysql;

import com.minepalm.manyworlds.api.WorldProperties;
import com.minepalm.manyworlds.api.bukkit.WorldCategory;
import com.minepalm.manyworlds.api.bukkit.WorldDatabase;
import com.minepalm.manyworlds.api.entity.PreparedWorld;
import com.minepalm.manyworlds.api.entity.WorldInform;
import com.minepalm.manyworlds.bukkit.errors.WorldNotExistsException;
import com.minepalm.manyworlds.core.ManyProperties;
import com.minepalm.manyworlds.core.WorldTokens;
import com.minepalm.manyworlds.core.database.MySQLDatabase;
import lombok.Getter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.CompletableFuture;

public class MySQLWorldTypeDatabase implements WorldDatabase {

    final MySQLDatabase database;
    @Getter
    final WorldCategory category;
    final String table;


    public MySQLWorldTypeDatabase(String table, MySQLDatabase mysql) {
        category = WorldTokens.TYPE;
        this.table = table;
        this.database = mysql;
    }

    public void create(){
        database.execute(connection -> {
            PreparedStatement ps = connection.prepareStatement("CREATE TABLE IF NOT EXISTS "+table+" (" +
                    "`id` INT UNIQUE NOT NULL AUTO_INCREMENT, " +
                    "`type` VARCHAR(64) UNIQUE, " +
                    "`data` MEDIUMBLOB, " +
                    "`metadata` TEXT, " +
                    "PRIMARY KEY (`type`)) charset=utf8mb4");
            ps.execute();
        });
    }

    @Override
    public CompletableFuture<PreparedWorld> prepareWorld(WorldInform info) {
        return database.executeAsync(con->{
            PreparedStatement ps = con.prepareStatement("SELECT `data`, `metadata` FROM " + table + " WHERE `type`=?");
            ps.setString(1, info.getType());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                WorldProperties properties = ManyProperties.fromJson(rs.getString(2));
                return new PreparedWorld(info, rs.getBytes(1), properties);
            } else {
                throw new WorldNotExistsException("world does not exists: " + info.getName());
            }
        });
    }

    @Override
    public CompletableFuture<Void> saveWorld(PreparedWorld world) {
        return database.executeAsync(con -> {
            PreparedStatement ps = con.prepareStatement("INSERT INTO " + table + " (`type`, `data`, `metadata`) VALUES(?, ?, ?) ON DUPLICATE KEY UPDATE `data`=?, `metadata`=?");
            ps.setString(1, world.getWorldInform().getType());
            for (int i = 0; i < 2; i++) {
                ps.setBytes(2 + i * 2, world.getWorldBytes());
                ps.setString(3 + i * 2, world.getProperties().toString());
            }
            ps.execute();
            return null;
        });
    }

    @Override
    public CompletableFuture<Void> deleteWorld(WorldInform info) {
        return deleteWorld(info.getType());
    }

    @Override
    public CompletableFuture<Void> deleteWorld(String fullName) {
        return database.executeAsync(con -> {
            PreparedStatement ps = con.prepareStatement("DELETE FROM "+table+" WHERE `type`=?");
            ps.setString(1, fullName);
            ps.execute();
            return null;
        });
    }
}
