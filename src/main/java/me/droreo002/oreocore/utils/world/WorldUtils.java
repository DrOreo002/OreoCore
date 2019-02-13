package me.droreo002.oreocore.utils.world;

import org.bukkit.Location;
import org.bukkit.World;

public final class WorldUtils {

    public static void createExplosion(Location location) {
        location.getWorld().createExplosion(location.getX(), location.getY(), location.getZ(), 5f, false, false);
    }
}
