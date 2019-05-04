package me.droreo002.oreocore.utils.world;

import me.droreo002.oreocore.OreoCore;
import org.apache.commons.lang.Validate;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.ArrayList;
import java.util.List;

public final class WorldUtils {

    public static void createExplosion(Location location) {
        Validate.notNull(location, "Location cannot be null!");
        location.getWorld().createExplosion(location.getX(), location.getY(), location.getZ(), 5f, false, false);
    }

    public static void spawnFireWork(Location location, int detonateDelaySecond, int amount, FireworkEffect effect) {
        final World w = location.getWorld();
        for (int i = 0; i < amount; i++) {
            Firework fw = (Firework) w.spawnEntity(location, EntityType.FIREWORK);
            FireworkMeta fwm = fw.getFireworkMeta();
            fwm.setDisplayName("OreoCore - Firework");

            fwm.setPower(1);
            fwm.addEffect(effect);

            fw.setFireworkMeta(fwm);
            Bukkit.getScheduler().scheduleSyncDelayedTask(OreoCore.getInstance(), fw::detonate, 20L * detonateDelaySecond);
        }
    }

    public static List<Entity> getEntitiesOnChuck(Chunk chunk, EntityType type) {
        List<Entity> ent = new ArrayList<>();
        for (Entity e : chunk.getEntities()) {
            if (e.getType().equals(type)) ent.add(e);
        }
        return ent;
    }
}
