package com.minepalm.manyworlds.bukkit.prototype;

import com.grinderwolf.swm.api.exceptions.UnknownWorldException;
import com.grinderwolf.swm.api.exceptions.WorldAlreadyExistsException;
import com.grinderwolf.swm.api.exceptions.WorldInUseException;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.grinderwolf.swm.nms.CraftSlimeWorld;
import com.grinderwolf.swm.plugin.SWMPlugin;
import com.grinderwolf.swm.plugin.loaders.mysql.MysqlLoader;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Properties;

@RequiredArgsConstructor
public class SWMUtils {
    @Getter
    final SWMPlugin swm;

    public void copyWorld(String worldName, SlimeLoader currentLoader, SlimeLoader newLoader) throws IOException, WorldInUseException, WorldAlreadyExistsException, UnknownWorldException {
        Objects.requireNonNull(worldName, "World name cannot be null");
        Objects.requireNonNull(currentLoader, "Current loader cannot be null");
        Objects.requireNonNull(newLoader, "New loader cannot be null");
        if (newLoader.worldExists(worldName)) {
            throw new WorldAlreadyExistsException(worldName);
        } else {
            World bukkitWorld = Bukkit.getWorld(worldName);
            boolean leaveLock = false;
            if (bukkitWorld != null) {
                CraftSlimeWorld slimeWorld = (CraftSlimeWorld)swm.getNms().getSlimeWorld(bukkitWorld);
                if (slimeWorld != null && currentLoader.equals(slimeWorld.getLoader())) {
                    slimeWorld.setLoader(newLoader);
                    if (!slimeWorld.isReadOnly()) {
                        currentLoader.unlockWorld(worldName);
                        leaveLock = true;
                    }
                }
            }

            byte[] serializedWorld = currentLoader.loadWorld(worldName, false);
            newLoader.saveWorld(worldName, serializedWorld, leaveLock);
        }
    }

    public MysqlLoader getLoader(Properties props, String tableName) throws SQLException {
        props.setProperty("table", tableName);
        return new MysqlLoader(props);
    }
}
