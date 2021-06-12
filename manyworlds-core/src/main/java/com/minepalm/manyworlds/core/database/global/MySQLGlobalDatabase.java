package com.minepalm.manyworlds.core.database.global;

import com.minepalm.manyworlds.api.*;
import com.minepalm.manyworlds.api.bukkit.WorldInfo;
import com.minepalm.manyworlds.core.database.AbstractMySQL;
import com.minepalm.manyworlds.core.server.BukkitServerView;
import com.minepalm.manyworlds.core.server.BungeeServerView;
import lombok.Getter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

public class MySQLGlobalDatabase extends AbstractMySQL implements GlobalDatabase {

    final String serverList;
    final String worldList;
    final String proxyName;

    @Getter
    final ServerView currentServer;


    public MySQLGlobalDatabase(String proxy, ServerView self, String servers_table, String worlds_table, Properties properties) {
        super(properties);
        this.serverList = servers_table;
        this.worldList = worlds_table;
        this.proxyName = proxy;
        this.currentServer = self;
        create();
    }

    @Override
    protected void create() {
        try(Connection con = hikari.getConnection()){
            PreparedStatement ps1 = con.prepareStatement("CREATE IF NOT EXISTS "+serverList+"(`type` VARCHAR(16), `server` VARCHAR (32))");
            PreparedStatement ps2 = con.prepareStatement("CREATE IF NOT EXISTS "+worldList+"(`proxy` VARCHAR(16), `server` VARCHAR(16), `world_name` VARCHAR (64))");
            ps1.execute();
            ps2.execute();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public BungeeView getProxy() {
        try(Connection con = hikari.getConnection()){
            PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) as `cnt` FROM "+worldList+" NATURAL JOIN "+serverList+" WHERE `proxy`=?");
            ps.setString(1, proxyName);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                int count = rs.getInt(1);
                return new BungeeServerView(proxyName, count);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ServerView getServer(String name) {
        try(Connection con = hikari.getConnection()){
            PreparedStatement ps = con.prepareStatement("SELECT `name` FROM "+serverList+" WHERE `name`=?");
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                return () -> name;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public BukkitView getBukkitServer(String name) {
        try(Connection con = hikari.getConnection()){
            PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) as `cnt` FROM "+worldList+" NATURAL JOIN "+serverList+" WHERE `server`=?");
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                int count = rs.getInt(1);
                return new BukkitServerView(name, count);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<BukkitView> getServers() {
        try(Connection con = hikari.getConnection()){
            List<BukkitView> list = new ArrayList<>();
            PreparedStatement ps = con.prepareStatement("SELECT `name` FROM "+serverList+" WHERE `type`=?");
            ps.setString(1, "BUKKIT");
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                list.add(getBukkitServer(rs.getString(1)));
            }
            return list;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<String> getLoadedWorlds(String serverName) {
        try(Connection con = hikari.getConnection()){
            List<String> list = new ArrayList<>();
            PreparedStatement ps = con.prepareStatement("SELECT `world_name` FROM "+worldList+" WHERE `proxy`=?, `server`=?");

            ps.setString(1, proxyName);
            ps.setString(2, serverName);

            ResultSet rs = ps.executeQuery();

            while (rs.next()){
                list.add(rs.getString(1));
            }

            return list;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void register() {
        try(Connection con = hikari.getConnection()){
            PreparedStatement ps = con.prepareStatement("INSERT INTO "+serverList+" VALUES (?, ?) ON DUPLICATE KEY UPDATE `type`=?, `server`=?");
            String type = currentServer instanceof BungeeView ? "BUNGEE" : "BUKKIT";
            ps.setString(1, type);
            ps.setString(2, currentServer.getServerName());
            ps.setString(3, type);
            ps.setString(4, currentServer.getServerName());
            ps.execute();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void unregister() {
        try (Connection con = hikari.getConnection()){
            PreparedStatement ps = con.prepareStatement("DELETE FROM "+serverList+" WHERE `server`=?");
            PreparedStatement ps2 = con.prepareStatement("DELETE FROM "+worldList+" WHERE `server`=?");
            ps.setString(1, currentServer.getServerName());
            ps2.setString(2, currentServer.getServerName());
            ps.execute();
            ps2.execute();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void registerWorld(BukkitView serverSnapshot, WorldInfo info) {
        try(Connection con = hikari.getConnection()){
            PreparedStatement ps = con.prepareStatement("INSERT INTO "+worldList+" VALUES(?, ?) ON DUPLICATE KEY UPDATE `SERVER`=?, `world_name`=?");
            String server, name;
            server = serverSnapshot.getServerName();
            name = info.getWorldName();
            ps.setString(1, server);
            ps.setString(2, name);
            ps.setString(3, server);
            ps.setString(4, name);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void unregisterWorld(String fullName) {
        try (Connection con = hikari.getConnection()){
            PreparedStatement ps = con.prepareStatement("DELETE FROM "+worldList+" WHERE `world_name`=?");
            ps.setString(1, fullName);
            ps.execute();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void unregisterWorld(String serverName, String sampleName, UUID uuid) {
        try (Connection con = hikari.getConnection()){
            PreparedStatement ps = con.prepareStatement("DELETE FROM "+worldList+" WHERE `server`=?, `world_name`=?");
            ps.setString(1, serverName);
            ps.setString(2, sampleName+"_"+uuid);
            ps.execute();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean isWorldLoaded(WorldInfo info) {
        return isWorldLoaded(info.getWorldName());
    }

    @Override
    public boolean isWorldLoaded(String fullName) {
        try(Connection con = hikari.getConnection()){
            PreparedStatement ps = con.prepareStatement("SELECT `world_name` FROM "+worldList+" WHERE `world_name`=?");
            ps.setString(1, fullName);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean isWorldLoaded(String serverName, String sampleName, UUID uuid) {
        try(Connection con = hikari.getConnection()){
            PreparedStatement ps = con.prepareStatement("SELECT `world_name` FROM "+worldList+" WHERE `server`=?, ``world_name`=?");
            ps.setString(1, serverName);
            ps.setString(2, sampleName+"_"+uuid);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }
}
