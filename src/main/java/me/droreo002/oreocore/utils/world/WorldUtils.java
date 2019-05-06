package me.droreo002.oreocore.utils.world;

import com.comphenix.packetwrapper.WrapperPlayServerWorldParticles;
import com.comphenix.protocol.wrappers.EnumWrappers;
import me.droreo002.oreocore.OreoCore;
import me.droreo002.oreocore.enums.Sounds;
import me.droreo002.oreocore.utils.bridge.BridgeUtils;
import me.droreo002.oreocore.utils.misc.SoundObject;
import org.apache.commons.lang.Validate;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public final class WorldUtils {

    /**
     * Create a fake explosion on that location for that player
     *
     * @param location : The location
     * @param player : The player
     */
    public static void createFakeExplosion(Location location, Player player) {
        Validate.notNull(location, "Location cannot be null!");
        BridgeUtils.playParticles(player, EnumWrappers.Particle.EXPLOSION_HUGE, 5, location, new Vector(0D,0D, 0D));
        new SoundObject(Sounds.EXPLODE).send(player);
    }

    /**
     * Create an explosion on that location
     *
     * @param location : The location
     * @param power : The explosion power
     */
    public static void createExplosion(Location location, float power) {
        Validate.notNull(location, "Location cannot be null!");
        location.getWorld().createExplosion(location.getX(), location.getY(), location.getZ(), power, false, false);
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
