package com.minepalm.manyworlds.bukkit.mysql;

import com.minepalm.manyworlds.api.bukkit.PreparedWorld;
import com.minepalm.manyworlds.api.bukkit.WorldDatabase;
import com.minepalm.manyworlds.api.bukkit.WorldInfo;
import com.minepalm.manyworlds.api.bukkit.WorldType;
import com.minepalm.manyworlds.bukkit.JsonSlimeProperties;
import com.minepalm.manyworlds.core.JsonWorldMetadata;
import com.minepalm.manyworlds.core.database.AbstractMySQL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public abstract class MySQLWorldDatabase extends AbstractMySQL implements WorldDatabase {

    final WorldType type;
    final String table;


    MySQLWorldDatabase(WorldType type, String table, Properties props){
        super(props);
        this.type = type;
        this.table = table;
    }

    @Override
    protected void create() {
        try(Connection con = hikari.getConnection()){
            PreparedStatement ps = con.prepareStatement("CREATE TABLE IF NOT EXISTS "+table+" (`name` VARCHAR(64), `data` MEDIUMBLOB,  `properties` TEXT, `metadata` TEXT, `last_used` LONG, PRIMARY KEY (`name`)) charset=utf8mb4");
            ps.execute();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public PreparedWorld prepareWorld(WorldInfo info){
        if(info.getWorldType().equals(WorldType.USER)) {
            try (Connection con = hikari.getConnection()) {
                PreparedStatement ps = con.prepareStatement("SELECT `data`, `properties`, `metadata` FROM " + table + " WHERE `name`=?");
                ps.setString(1, info.getWorldName());
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    return new MySQLWorld(info.clone(), rs.getBytes(1), JsonWorldMetadata.fromJson(rs.getString(2)), JsonSlimeProperties.fromString(rs.getString(3)));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }else
            throw new IllegalStateException("Cannot load this world type. Required: "+WorldType.USER+", Input: "+info.getWorldType());
    }

    @Override
    public void saveWorld(PreparedWorld world){
        if(world.getWorldInfo().getWorldType().equals(this.type)) {
            try(Connection con = hikari.getConnection()){
                PreparedStatement ps = con.prepareStatement("INSERT INTO "+table+" (`name`, `bytes`, `properties`, `metadata`) VALUES(?, ?, ?, ?) ON DUPLICATE KEY UPDATE `bytes`=?, `properties`=?, `metadata`=?");
                ps.setString(1, world.getWorldInfo().getWorldName());
                for(int i = 0 ; i < 2; i++) {
                    ps.setBytes(2+i*3, world.getWorldBytes());
                    ps.setString(3+i*3, world.getProperties().toString());
                    ps.setString(4+i*3, world.getMetadata().toString());
                }
                ps.execute();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }else
            throw new IllegalStateException("Cannot save this world type. Required: "+this.type+", Input: "+world.getWorldInfo().getWorldType());
    }

    private void updateLoaded(String worldName, Long time){
        try(Connection con = hikari.getConnection()){
            PreparedStatement ps = con.prepareStatement("UPDATE "+table+" SET `last_updated`=? WHERE `name`=? ");
            ps.setString(2, worldName);
            ps.setLong(1, time);
            ps.execute();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    private long getLatestTime(String worldName){
        try(Connection con = hikari.getConnection()){
            PreparedStatement ps = con.prepareStatement("SELECT `last_updated` FROM "+table+" WHERE `name`=?");
            ps.setString(1, worldName);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return rs.getLong(1);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return 0L;
    }
}
