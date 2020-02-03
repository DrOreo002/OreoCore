package me.droreo002.oreocore.utils.world;

import me.droreo002.oreocore.OreoCore;
import me.droreo002.oreocore.enums.ParticleEffect;
import me.droreo002.oreocore.utils.bridge.OSound;
import me.droreo002.oreocore.utils.bridge.ServerUtils;
import me.droreo002.oreocore.utils.misc.SoundObject;
import org.apache.commons.lang.Validate;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.ArrayList;
import java.util.List;

public final class WorldUtils {

    /**
     * Create a fake explosion on that location for that player
     *
     * @param location The target location
     * @param player The target player
     */
    public static void createFakeExplosion(Location location, Player player) {
        Validate.notNull(location, "Location cannot be null!");
        playParticles(ParticleEffect.EXPLOSION_HUGE, 0, 0, 0, 1, 5, location, 4);
        new SoundObject(OSound.ENTITY_GENERIC_EXPLODE).send(player);
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

    /**
     * Play particles on the specified location
     *
     * @param effect The particle effect
     * @param offsetX The offset on X axis
     * @param offsetY The offset on Y axis
     * @param offsetZ The offset on Z axis
     * @param speed The particle speed
     * @param amount The particle amount
     * @param center Center location of particle
     * @param range The particle range
     */
    public static void playParticles(ParticleEffect effect, float offsetX, float offsetY, float offsetZ, float speed, int amount, Location center, double range) {
        if (ServerUtils.isLegacyVersion()) {
            effect.display(offsetX, offsetY, offsetZ, speed, amount, center, range);
        } else {
            if (center.getWorld() != null) {
                try {
                    Particle particle = Particle.valueOf(effect.name());
                    center.getWorld().spawnParticle(particle, center, amount, offsetX, offsetY, offsetZ, speed);
                } catch (Exception ignored) {

                }
            }
        }
    }

    /**
     * Spawn a firework at the location
     *
     * @param location The location
     * @param detonateDelaySecond Detonate delay in second
     * @param amount The firework amount
     * @param effect The firework effect
     */
    public static void spawnFirework(Location location, int detonateDelaySecond, int amount, FireworkEffect effect) {
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

    /**
     * Get entities on that chuck
     *
     * @param chunk The chuck
     * @param type The entity type to get
     * @return List of entity
     */
    public static List<Entity> getEntitiesOnChuck(Chunk chunk, EntityType type) {
        List<Entity> ent = new ArrayList<>();
        for (Entity e : chunk.getEntities()) {
            if (e.getType().equals(type)) ent.add(e);
        }
        return ent;
    }

    /**
     * Get nearby players from that location
     *
     * @param location The location
     * @param distance The allowed distance
     * @return List of nearby player
     */
    public static List<Player> getNearbyPlayer(Location location, double distance) {
        List<Player> players = new ArrayList<>();
        double distanceSquared = distance * distance;

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getLocation().distanceSquared(location) < distanceSquared) {
                players.add(p);
            }
        }

        return players;
    }

}
