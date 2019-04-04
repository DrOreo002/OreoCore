package me.droreo002.oreocore.utils.world;

import jdk.nashorn.internal.ir.Block;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import javax.swing.text.html.parser.Entity;
import java.util.ArrayList;
import java.util.List;

public final class LocationUtils {

    public static String convertToString(Location location) {
        Validate.notNull(location, "Location cannot be null!");
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

    public static List<Location> getCircle(Location center, double radius, int amount) {
        List<Location> locations = new ArrayList<>();
        World world = center.getWorld();
        double increment = (2 * Math.PI) / amount;
        for(int i = 0; i < amount; i++) {
            double angle = i * increment;
            double x = center.getX() + (radius * Math.cos(angle));
            double z = center.getZ() + (radius * Math.sin(angle));
            locations.add(new Location(world, x, center.getY(), z));
        }
        return locations;
    }

    public static List<Location> getUpCircle(Location center, double radius, int amount, double upIncrement) {
        List<Location> locations = new ArrayList<>();
        World world = center.getWorld();
        double increment = (2 * Math.PI) / amount;
        double currIncrement = upIncrement;

        for(int i = 0; i < amount; i++) {
            double angle = i * increment;
            double x = center.getX() + (radius * Math.cos(angle));
            double z = center.getZ() + (radius * Math.sin(angle));
            locations.add(new Location(world, x, center.getY() + currIncrement, z));
            currIncrement += upIncrement;
        }
        return locations;
    }

    public static List<Location> getCircleReverse(Location center, double radius, int amount) {
        World world = center.getWorld();
        double increment = (2 * Math.PI) / amount;
        List<Location> locations = new ArrayList<>();
        for(int i = 0; i < amount; i++) {
            double angle = i * increment;
            double x = center.getX() - (radius * Math.cos(angle));
            double z = center.getZ() - (radius * Math.sin(angle));
            locations.add(new Location(world, x, center.getY(), z));
        }
        return locations;
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
