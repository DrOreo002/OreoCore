package me.droreo002.oreocore.utils.bridge;

import com.comphenix.packetwrapper.WrapperPlayServerWorldParticles;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.wrappers.EnumWrappers;
import me.droreo002.oreocore.enums.Sounds;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;

public final class BridgeUtils {

    public static void playSound(Player player, Sounds sounds) {
        player.playSound(player.getLocation(), sounds.bukkitSound(), 1.0f, 1.0f);
    }

    // TODO : Place in ParticleFactory
    public static void playParticles(Player player, EnumWrappers.Particle particle, int particleNum, Location location, Vector direction) {
        WrapperPlayServerWorldParticles particles = new WrapperPlayServerWorldParticles();
        particles.setParticleType(particle);
        particles.setNumberOfParticles(particleNum);
        particles.setLocation(location);
        particles.setDirection(direction);

        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player, particles.getHandle());
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("Unable to send packet", e);
        }
    }
}
