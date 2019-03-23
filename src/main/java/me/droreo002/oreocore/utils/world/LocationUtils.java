package me.droreo002.oreocore.utils.world;

import jdk.nashorn.internal.ir.Block;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import javax.swing.text.html.parser.Entity;

public final class LocationUtils {

    public static String convertToString(Location location) {
        return "Location;" + location.getWorld().getName() + ";" + Math.round(location.getX()) + ";" + Math.round(location.getY()) + ";" + Math.round(location.getZ());
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

    /**
     * Make the Player facing the locations's direction
     *
     * @param player : The target player
     * @param target : The target location
     * @param center : Should we center the location?
     */
    public static void faceDirection(Player player, Location target, boolean center) {
        Vector dir = target.clone().subtract(player.getEyeLocation()).toVector();
        if (center) dir.add(new Vector(.5D, 2D, .5D));
        Location loc = player.getLocation().setDirection(dir);
        player.teleport(loc);
    }
}
