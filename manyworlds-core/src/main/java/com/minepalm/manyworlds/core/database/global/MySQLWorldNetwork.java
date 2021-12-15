package com.minepalm.manyworlds.core.database.global;

import com.minepalm.manyworlds.api.WorldNetwork;
import com.minepalm.manyworlds.api.entity.BukkitView;
import com.minepalm.manyworlds.api.entity.BungeeView;
import com.minepalm.manyworlds.api.entity.ServerView;
import com.minepalm.manyworlds.api.entity.WorldInform;
import com.minepalm.manyworlds.core.database.MySQLDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

public class MySQLWorldNetwork implements WorldNetwork {

    final Logger logger;
    final String serverList;
    final String worldList;
    final String proxyName;

    final MySQLDatabase database;
    final ServerView currentServer;

    public MySQLWorldNetwork(String proxy, ServerView self, String servers_table, String worlds_table, MySQLDatabase database, Logger logger) {
        this.database = database;
        this.proxyName = proxy;
        this.currentServer = self;
        this.serverList = servers_table;
        this.worldList = worlds_table;
        this.logger = logger;
        create();
    }

    protected void create() {
        database.execute(connection ->{
            PreparedStatement ps1 = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `"+serverList+"` (`id` INT NOT NULL AUTO_INCREMENT, `type` VARCHAR(16), `server` VARCHAR (32) UNIQUE, PRIMARY KEY(`id`, `server`)) charset=utf8mb4");
            PreparedStatement ps2 = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `"+worldList+"` (`id` INT NOT NULL AUTO_INCREMENT, `proxy` VARCHAR(16), `server` VARCHAR(16), `world_name` VARCHAR (64) UNIQUE, PRIMARY KEY(`id`,`world_name`)) charset=utf8mb4");
            ps1.execute();
            ps2.execute();
        });
    }

    @Deprecated
    public CompletableFuture<BungeeView> getProxy() {
        return database.executeAsync(con->{
            PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) as `cnt` FROM "+ worldList + " AS `w` INNER JOIN " + serverList + " AS `s` ON `w`.`proxy` = `s`.`server` WHERE `s`.`server`=?");
            ps.setString(1, proxyName);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                int count = rs.getInt(1);
                return new BungeeView(proxyName, count);
            }
            return null;
        });
    }

    @Override
    public ServerView getCurrentServer() {
        return this.currentServer;
    }

    @Override
    public CompletableFuture<ServerView> getServer(String name) {
        return database.executeAsync(con->{
            PreparedStatement ps = con.prepareStatement("SELECT `server` FROM " + serverList + " WHERE `server`=?");
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new ServerView(name);
            }
            return null;
        });
    }

    @Override
    public CompletableFuture<BukkitView> getBukkitServer(String name) {
        return database.executeAsync(con-> {
            PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) as `cnt` FROM " + worldList + " AS `w` INNER JOIN " + serverList + " AS `s` ON `w`.`server` = `s`.`server` WHERE `s`.`server`=?");
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                return new BukkitView(name, count);
            }
            return null;
        });
    }

    @Override
    public CompletableFuture<List<BukkitView>> getServers() {
        return database.executeAsync(con->{
            List<CompletableFuture<BukkitView>> list = new ArrayList<>();
            PreparedStatement ps = con.prepareStatement("SELECT `server` FROM " + serverList + " WHERE `type`=?");
            ps.setString(1, "BUKKIT");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(getBukkitServer(rs.getString(1)));
            }
            return list;
        }).thenCompose(futureList-> CompletableFuture.allOf(futureList.toArray(new CompletableFuture[0])).thenApply(ignore->{
            List<BukkitView> views = new ArrayList<>();
            for (CompletableFuture<BukkitView> future : futureList) {
                try {
                    views.add(future.get());
                } catch (InterruptedException | ExecutionException ignored) {

                }
            }
            return views;
        }));
    }

    @Override
    public CompletableFuture<List<String>> getLoadedWorlds(String serverName) {
        return database.executeAsync(con->{
            List<String> list = new ArrayList<>();
            PreparedStatement ps = con.prepareStatement("SELECT `world_name` FROM " + worldList + " WHERE `proxy`=? AND `server`=?");

            ps.setString(1, proxyName);
            ps.setString(2, serverName);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(rs.getString(1));
            }

            return list;
        });
    }

    @Override
    public void registerServer() {
        database.execute(con->{
            registerServer0(con, this.currentServer.getServerName());
        });
    }

    @Override
    public CompletableFuture<Void> registerServer(String name) {
        return database.executeAsync(con->{
            registerServer0(con, name);
            return null;
        });
    }

    private void registerServer0(Connection con, String name) throws SQLException{
        PreparedStatement ps = con.prepareStatement("INSERT INTO "+serverList+" (`type`, `server`) VALUES (?, ?) ON DUPLICATE KEY UPDATE `type`=?, `server`=?");
        String type = currentServer instanceof BungeeView ? "BUNGEE" : "BUKKIT";
        ps.setString(1, type);
        ps.setString(2, name);
        ps.setString(3, type);
        ps.setString(4, name);
        ps.execute();
    }

    @Override
    public void unregister() {
        database.execute(con->{
            unregisterServer0(con, currentServer.getServerName());
        });
    }

    @Override
    public CompletableFuture<Void> unregisterServer(String name) {
        return database.executeAsync(con->{
            unregisterServer0(con, name);
            return null;
        });
    }

    private void unregisterServer0(Connection con, String name) throws SQLException{
        PreparedStatement ps = con.prepareStatement("DELETE FROM "+serverList+" WHERE `server`=?");
        PreparedStatement ps2 = con.prepareStatement("DELETE FROM "+worldList+" WHERE `server`=?");
        ps.setString(1, name);
        ps2.setString(1, name);
        ps.execute();
        ps2.execute();
    }

    @Override
    public CompletableFuture<Void> registerWorld(BukkitView serverSnapshot, WorldInform info) {
        return database.executeAsync(con->{
            PreparedStatement ps = con.prepareStatement("INSERT INTO " + worldList + " (`proxy`, `server`, `world_name`) VALUES(?, ?, ?) ON DUPLICATE KEY UPDATE `proxy`=?, `server`=?, `world_name`=?");
            String server, name;
            server = serverSnapshot.getServerName();
            name = info.getName();
            ps.setString(1, proxyName);
            ps.setString(2, server);
            ps.setString(3, name);
            ps.setString(4, proxyName);
            ps.setString(5, server);
            ps.setString(6, name);
            ps.execute();
            return null;
        });
    }

    @Override
    public CompletableFuture<Void> unregisterWorld(String fullName) {
        return database.executeAsync(con->{
            PreparedStatement ps = con.prepareStatement("DELETE FROM " + worldList + " WHERE `world_name`=?");
            ps.setString(1, fullName);
            ps.execute();
            return null;
        });
    }

    @Override
    public CompletableFuture<Void> resetWorlds(ServerView view){
        return database.executeAsync(con->{
            PreparedStatement ps = con.prepareStatement("DELETE FROM " + worldList + " WHERE `server`=?");
            ps.setString(1, view.getServerName());
            ps.execute();
            return null;
        });
    }

    @Override
    public CompletableFuture<Void> unregisterWorld(String serverName, String sampleName, UUID uuid) {
        return database.executeAsync(con -> {
            PreparedStatement ps = con.prepareStatement("DELETE FROM " + worldList + " WHERE `server`=? AND `world_name`=?");
            ps.setString(1, serverName);
            ps.setString(2, sampleName + "_" + uuid);
            ps.execute();
            return null;
        });
    }

    @Override
    public CompletableFuture<Boolean> isWorldLoaded(WorldInform info) {
        return isWorldLoaded(info.getName());
    }

    @Override
    public CompletableFuture<Boolean> isWorldLoaded(String fullName) {
        return database.executeAsync(con->{
            PreparedStatement ps = con.prepareStatement("SELECT `world_name` FROM " + worldList + " WHERE `world_name`=?");
            ps.setString(1, fullName);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        });
    }

    @Override
    public CompletableFuture<Optional<BukkitView>> getLoadedServer(WorldInform info) {
        return database.executeAsync(con -> {
            BukkitView view = null;
            String serverName = null;
            PreparedStatement ps = con.prepareStatement("SELECT `server` FROM " + worldList + " WHERE `world_name`=?");
            ps.setString(1, info.getName());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                serverName = rs.getString(1);
            }
            if (serverName != null)
                view = getBukkitServer(serverName).get();
            return Optional.ofNullable(view);
        });
    }

    @Override
    public CompletableFuture<Boolean> isWorldLoaded(String serverName, String sampleName, UUID uuid) {
        return database.executeAsync(con -> {
            PreparedStatement ps = con.prepareStatement("SELECT `world_name` FROM " + worldList + " WHERE `server`=? AND `world_name`=?");
            ps.setString(1, serverName);
            ps.setString(2, sampleName + "_" + uuid);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        });
    }
}
