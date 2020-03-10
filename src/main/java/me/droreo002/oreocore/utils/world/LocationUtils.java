package me.droreo002.oreocore.utils.world;

import jdk.nashorn.internal.ir.Block;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class LocationUtils {

    /**
     * Convert the list of strings into a list of locations
     *
     * @param strings The list of strings
     * @return List of locations
     */
    public static List<Location> toLocations(List<String> strings) {
        return strings.stream().map(LocationUtils::toLocation).collect(Collectors.toList());
    }

    /**
     * Convert the list of locations into a list of strings
     *
     * @param locations The list of locations
     * @return List of strings
     */
    public static List<String> toStringList(List<Location> locations) {
        return locations.stream().map(LocationUtils::toString).collect(Collectors.toList());
    }

    /**
     * Convert the Location into a readable string
     *
     * @param location The location to convert
     * @return The converted location as string
     */
    public static String toString(Location location) {
        Validate.notNull(location, "Location cannot be null!");
        return "Location;" + location.getWorld().getName() + ";" + location.getX() + ";" + location.getY() + ";" + location.getZ() + ";" + location.getYaw() + ";" + location.getPitch();
    }

    /**
     * Convert the string into a location
     *
     * @param format The string
     * @return the Location object if successful, null otherwise
     */
    public static Location toLocation(String format) {
        if (format.isEmpty()) return null;
        String[] sp = format.split(";");
        if (!sp[0].equalsIgnoreCase("Location")) return null;
        String world = sp[1];
        double x = Double.parseDouble(sp[2]);
        double y = Double.parseDouble(sp[3]);
        double z = Double.parseDouble(sp[4]);
        float yaw = Float.parseFloat(sp[5]);
        float pitch = Float.parseFloat(sp[6]);
        return new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
    }

    /**
     * Make a circle, will return a list of locations
     *
     * @param center The center location of the circle
     * @param radius The radius of the circle
     * @param amount The amount of point to create
     * @return a list of locations
     */
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

    /**
     * Make a spiral circle (Up)
     *
     * @param center The center location of the circle
     * @param radius The radius of the circle
     * @param amount The amount of point to create
     * @param upIncrement The up increment of the circle a.k.a up speed
     * @return a list of locations
     */
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

    /**
     * Make a reverse circle
     *
     * @param center The center location of the circle
     * @param radius The radius of the circle
     * @param amount The amount of point to create
     * @return a list of locations
     */
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

    /**
     * Make the Entity facing the locations's direction
     *
     * @param armorStand : The target ArmorStand
     * @param target : The target location
     * @param center : Should we center the location?
     */
    public static void faceDirection(ArmorStand armorStand, Location target, boolean center) {
        Vector dir = target.clone().subtract(armorStand.getEyeLocation()).toVector();
        if (center) dir.add(new Vector(.5D, 2D, .5D));
        Location loc = armorStand.getEyeLocation().setDirection(dir);
        armorStand.teleport(loc);
    }
}
