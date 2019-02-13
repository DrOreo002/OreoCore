package me.droreo002.oreocore.utils.world;

import jdk.nashorn.internal.ir.Block;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public final class LocationUtils {

    public static String convertToString(Location location) {
        return "Location;" + location.getWorld().getName() + ";" + location.getX() + ";" + location.getY() + ";" + location.getZ();
    }

    public static Location toLocation(String format) {
        String[] sp = format.split(";");
        if (!sp[0].equalsIgnoreCase("Location")) return null;
        String world = sp[1];
        double x = Double.valueOf(sp[2]);
        double y = Double.valueOf(sp[3]);
        double z = Double.valueOf(sp[4]);
        return new Location(Bukkit.getWorld(world), x, y, z);
    }
}
