package com.minepalm.manyworlds.core.database.global;

import com.minepalm.manyworlds.api.BukkitView;
import com.minepalm.manyworlds.api.BungeeView;
import com.minepalm.manyworlds.api.GlobalDatabase;
import com.minepalm.manyworlds.api.ServerView;
import com.minepalm.manyworlds.api.bukkit.WorldInfo;
import com.minepalm.manyworlds.core.database.AbstractMySQL;
import com.minepalm.manyworlds.core.server.BukkitServerView;
import com.minepalm.manyworlds.core.server.BungeeServerView;
import lombok.Getter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.logging.Logger;

public class MySQLGlobalDatabase extends AbstractMySQL implements GlobalDatabase {

    private final ExecutorService service;

    final Logger logger;
    final String serverList;
    final String worldList;
    final String proxyName;

    @Getter
    final ServerView currentServer;

    public MySQLGlobalDatabase(String proxy, ServerView self, String servers_table, String worlds_table, Properties properties, ExecutorService service, Logger logger) {
        super(properties);
        this.service = service;
        this.proxyName = proxy;
        this.currentServer = self;
        this.serverList = servers_table;
        this.worldList = worlds_table;
        this.logger = logger;
        create();
    }

    @Override
    protected void create() {
        try(Connection con = hikari.getConnection()){
            PreparedStatement ps1 = con.prepareStatement("CREATE TABLE IF NOT EXISTS `"+serverList+"` (`id` INT NOT NULL AUTO_INCREMENT, `type` VARCHAR(16), `server` VARCHAR (32) UNIQUE, PRIMARY KEY(`id`, `server`)) charset=utf8mb4");
            PreparedStatement ps2 = con.prepareStatement("CREATE TABLE IF NOT EXISTS `"+worldList+"` (`id` INT NOT NULL AUTO_INCREMENT, `proxy` VARCHAR(16), `server` VARCHAR(16), `world_name` VARCHAR (64) UNIQUE, PRIMARY KEY(`id`,`world_name`)) charset=utf8mb4");
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
    public Future<ServerView> getServer(String name) {
        return service.submit(()-> {
            try (Connection con = hikari.getConnection()) {
                PreparedStatement ps = con.prepareStatement("SELECT `server` FROM " + serverList + " WHERE `server`=?");
                ps.setString(1, name);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    return () -> name;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    @Override
    public Future<BukkitView> getBukkitServer(String name) {
        return service.submit(()-> {
            try (Connection con = hikari.getConnection()) {
                PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) as `cnt` FROM " + worldList + " NATURAL JOIN " + serverList + " WHERE `server`=?");
                ps.setString(1, name);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return new BukkitServerView(name, count);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    @Override
    public Future<List<BukkitView>> getServers() {
        return service.submit(()-> {
            try (Connection con = hikari.getConnection()) {
                List<BukkitView> list = new ArrayList<>();
                PreparedStatement ps = con.prepareStatement("SELECT `server` FROM " + serverList + " WHERE `type`=?");
                ps.setString(1, "BUKKIT");
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    list.add(getBukkitServer(rs.getString(1)).get());
                }
                return list;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    @Override
    public Future<List<String>> getLoadedWorlds(String serverName) {
        return service.submit(()-> {
            try (Connection con = hikari.getConnection()) {
                List<String> list = new ArrayList<>();
                PreparedStatement ps = con.prepareStatement("SELECT `world_name` FROM " + worldList + " WHERE `proxy`=?, `server`=?");

                ps.setString(1, proxyName);
                ps.setString(2, serverName);

                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    list.add(rs.getString(1));
                }

                return list;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    @Override
    public void register() {
        try(Connection con = hikari.getConnection()){
            PreparedStatement ps = con.prepareStatement("INSERT INTO "+serverList+" (`type`, `server`) VALUES (?, ?) ON DUPLICATE KEY UPDATE `type`=?, `server`=?");
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
            ps2.setString(1, currentServer.getServerName());
            ps.execute();
            ps2.execute();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public Future<Void> registerWorld(BukkitView serverSnapshot, WorldInfo info) {
        return service.submit(()-> {
            try (Connection con = hikari.getConnection()) {
                PreparedStatement ps = con.prepareStatement("INSERT INTO " + worldList + " (`proxy`, `server`, `world_name`) VALUES(?, ?, ?) ON DUPLICATE KEY UPDATE `proxy`=?, `server`=?, `world_name`=?");
                String server, name;
                server = serverSnapshot.getServerName();
                name = info.getWorldName();
                ps.setString(1, proxyName);
                ps.setString(2, server);
                ps.setString(3, name);
                ps.setString(4, proxyName);
                ps.setString(5, server);
                ps.setString(6, name);
                ps.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    @Override
    public Future<Void> unregisterWorld(String fullName) {
        return service.submit(()-> {
            try (Connection con = hikari.getConnection()) {
                PreparedStatement ps = con.prepareStatement("DELETE FROM " + worldList + " WHERE `world_name`=?");
                ps.setString(1, fullName);
                ps.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    @Override
    public Future<Void> resetWorlds(ServerView view){
        return service.submit(()-> {
            try (Connection con = hikari.getConnection()) {
                PreparedStatement ps = con.prepareStatement("DELETE FROM " + worldList + " WHERE `server`=?");
                ps.setString(1, view.getServerName());
                ps.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    @Override
    public Future<Void> unregisterWorld(String serverName, String sampleName, UUID uuid) {
        return service.submit(()-> {
            try (Connection con = hikari.getConnection()) {
                PreparedStatement ps = con.prepareStatement("DELETE FROM " + worldList + " WHERE `server`=?, `world_name`=?");
                ps.setString(1, serverName);
                ps.setString(2, sampleName + "_" + uuid);
                ps.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    @Override
    public Future<Boolean> isWorldLoaded(WorldInfo info) {
        return isWorldLoaded(info.getWorldName());
    }

    @Override
    public Future<Boolean> isWorldLoaded(String fullName) {
        logger.info("isWorldLoaded start1");
        return service.submit(()-> {
            logger.info("isWorldLoaded start2");
            try (Connection con = hikari.getConnection()) {
                PreparedStatement ps = con.prepareStatement("SELECT `world_name` FROM " + worldList + " WHERE `world_name`=?");
                ps.setString(1, fullName);
                ResultSet rs = ps.executeQuery();
                logger.info("executed query");
                return rs.next();
            } catch (SQLException e) {
                logger.info(e.getMessage());
                //e.printStackTrace();
            }
            return false;
        });
    }

    @Override
    public Future<Optional<BukkitView>> getLoadedServer(WorldInfo info) {
        return service.submit(()-> {
            BukkitView view = null;
            String serverName = null;

            try (Connection con = hikari.getConnection()) {
                PreparedStatement ps = con.prepareStatement("SELECT `server` FROM " + worldList + " WHERE `world_name`=?");
                ps.setString(1, info.getWorldName());
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    serverName = rs.getString(1);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            if (serverName != null)
                view = getBukkitServer(serverName).get();

            return Optional.ofNullable(view);
        });
    }

    @Override
    public Future<Boolean> isWorldLoaded(String serverName, String sampleName, UUID uuid) {
        return service.submit(()-> {
            try (Connection con = hikari.getConnection()) {
                PreparedStatement ps = con.prepareStatement("SELECT `world_name` FROM " + worldList + " WHERE `server`=?, ``world_name`=?");
                ps.setString(1, serverName);
                ps.setString(2, sampleName + "_" + uuid);
                ResultSet rs = ps.executeQuery();
                return rs.next();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        });
    }
}
