package me.droreo002.oreocore.utils.bridge;

import me.droreo002.oreocore.enums.Sounds;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public final class BridgeUtils {

    public static void playSound(Player player, Sounds sounds) {
        player.playSound(player.getLocation(), sounds.bukkitSound(), 1.0f, 1.0f);
    }

    public static void playParticle(Location location) {

    }
}
