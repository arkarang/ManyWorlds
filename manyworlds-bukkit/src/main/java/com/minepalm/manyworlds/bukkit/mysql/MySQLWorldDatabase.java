package com.minepalm.manyworlds.bukkit.mysql;

import com.minepalm.manyworlds.api.WorldProperties;
import com.minepalm.manyworlds.api.entity.PreparedWorld;
import com.minepalm.manyworlds.api.bukkit.WorldCategory;
import com.minepalm.manyworlds.api.bukkit.WorldDatabase;
import com.minepalm.manyworlds.api.entity.WorldInform;
import com.minepalm.manyworlds.bukkit.errors.WorldNotExistsException;
import com.minepalm.manyworlds.core.ManyProperties;
import com.minepalm.manyworlds.core.database.MySQLDatabase;
import lombok.Getter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.CompletableFuture;

public class MySQLWorldDatabase implements WorldDatabase {


    final MySQLDatabase database;
    @Getter
    final WorldCategory category;
    final String table;

    public MySQLWorldDatabase(WorldCategory category, String table, MySQLDatabase mysql){
        this.category = category;
        this.table = table;
        this.database = mysql;
        create();
    }

    protected void create() {
        database.execute(connection -> {
            PreparedStatement ps = connection.prepareStatement("CREATE TABLE IF NOT EXISTS "+table+" (" +
                    "`id` INT NOT NULL AUTO_INCREMENT, " +
                    "`name` VARCHAR(64) UNIQUE, " +
                    "`data` MEDIUMBLOB, " +
                    "`metadata` TEXT, " +
                    "`last_used` LONG, " +
                    "PRIMARY KEY (`id`, `name`)) charset=utf8mb4");
            ps.execute();
        });
    }

    @Override
    public CompletableFuture<PreparedWorld> prepareWorld(WorldInform info){
        return database.executeAsync(con->{
            PreparedStatement ps = con.prepareStatement("SELECT `data`, `metadata` FROM " + table + " WHERE `name`=?");
            ps.setString(1, info.getName());
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
    public CompletableFuture<PreparedWorld> prepareWorld(String get, WorldInform override) {
        return database.executeAsync(con->{
            PreparedStatement ps = con.prepareStatement("SELECT `data`, `metadata` FROM " + table + " WHERE `name`=?");
            ps.setString(1, get);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                WorldProperties properties = ManyProperties.fromJson(rs.getString(2));
                return new PreparedWorld(override, rs.getBytes(1), properties);
            } else {
                throw new WorldNotExistsException("world does not exists: " + get);
            }
        });
    }


    @Override
    public CompletableFuture<Void> saveWorld(PreparedWorld world){
        return database.executeAsync(con -> {
            PreparedStatement ps = con.prepareStatement("INSERT INTO " + table + " (`name`, `data`, `metadata`) VALUES(?, ?, ?) ON DUPLICATE KEY UPDATE `data`=?, `metadata`=?");
            ps.setString(1, world.getWorldInform().getName());
            for (int i = 0; i < 2; i++) {
                ps.setBytes(2 + i * 2, world.getWorldBytes());
                ps.setString(3 + i * 2, world.getProperties().toString());
            }
            ps.execute();
            return null;
        });
    }

    @Override
    public CompletableFuture<Boolean> exists(WorldInform inform) {
        return database.executeAsync(con->{
            PreparedStatement ps = con.prepareStatement("SELECT 1 FROM " + table + " WHERE `name`=?");
            ResultSet rs = ps.executeQuery();
            return rs.next();
        });
    }

    @Override
    public CompletableFuture<Void> deleteWorld(WorldInform info) {
        return deleteWorld(info.getName());
    }

    @Override
    public CompletableFuture<Void> deleteWorld(String fullName) {
        return database.executeAsync(con -> {
            PreparedStatement ps = con.prepareStatement("DELETE FROM "+table+" WHERE `name`=?");
            ps.setString(1, fullName);
            ps.execute();
            return null;
        });
    }

    private CompletableFuture<Void> updateLoaded(String worldName, Long time){
        return database.executeAsync(con -> {
            PreparedStatement ps = con.prepareStatement("UPDATE "+table+" SET `last_updated`=? WHERE `name`=? ");
            ps.setString(2, worldName);
            ps.setLong(1, time);
            ps.execute();
            return null;
        });
    }

    private CompletableFuture<Long> getLatestTime(String worldName){
        return database.executeAsync(con -> {
            PreparedStatement ps = con.prepareStatement("SELECT `last_updated` FROM "+table+" WHERE `name`=?");
            ps.setString(1, worldName);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return rs.getLong(1);
            }
            return null;
        });
    }
}
