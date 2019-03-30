package me.droreo002.oreocore.utils.entity;

import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.*;

public final class EntityUtils {

    public static Entity getEntityFromLocation(Location location) {
        Validate.notNull(location, "Location cannot be null!");

        return Arrays.stream(location.getChunk().getEntities()).min(Comparator.comparingDouble(o -> o.getLocation().distanceSquared(location)))
                .orElse(null);
    }

    public static Entity getNearestEntityInSight(Player player, int range) {
        List<Entity> entities = player.getNearbyEntities(range, range, range);
        List<Block> sightBlock = player.getLineOfSight(null, range);
        List<Location> sight = new ArrayList<>();
        for (Block block : sightBlock) sight.add(block.getLocation());
        for (Location location : sight) {
            for (Entity entity : entities) {
                if (Math.abs(entity.getLocation().getX() - location.getX()) < 1.3) {
                    if (Math.abs(entity.getLocation().getY() - location.getY()) < 1.5) {
                        if (Math.abs(entity.getLocation().getZ() - location.getZ()) < 1.3) {
                            return entity;
                        }
                    }
                }
            }
        }
        return null; //Return null/nothing if no entity was found
    }

    public static Entity tryGetEntityInSight(Player player, EntityType entityType, int range) {
        List<Entity> entities = player.getNearbyEntities(range, range, range);
        List<Block> sightBlock = player.getLineOfSight(null, range);
        List<Location> sight = new ArrayList<>();
        for (Block block : sightBlock) sight.add(block.getLocation());
        for (Location location : sight) {
            for (Entity entity : entities) {
                if (Math.abs(entity.getLocation().getX() - location.getX()) < 1.3) {
                    if (Math.abs(entity.getLocation().getY() - location.getY()) < 1.5) {
                        if (Math.abs(entity.getLocation().getZ() - location.getZ()) < 1.3) {
                            if (entity.getType() == entityType) return entity;
                        }
                    }
                }
            }
        }
        return null; //Return null/nothing if no entity was found
    }
}
