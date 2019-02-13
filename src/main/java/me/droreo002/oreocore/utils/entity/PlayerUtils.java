package me.droreo002.oreocore.utils.entity;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import me.droreo002.oreocore.OreoCore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Set;

public final class PlayerUtils {

    public static boolean hasPlayerBefore(String playerName) {
        Essentials ess = OreoCore.getInstance().getEssentials();
        User user = ess.getUser(playerName);
        if (user == null) return false;
        OfflinePlayer off = Bukkit.getOfflinePlayer(user.getConfigUUID());
        if (off == null) return false;
        return off.hasPlayedBefore();
    }

    public static Location getPlayerLooking(Player player, int distance) {
        ArrayList<Block> sightBlock = (ArrayList<Block>) player.getLineOfSight( (Set<Material>) null, distance);
        ArrayList<Location> sight = new ArrayList<Location>();
        for (int i = 0; i < sightBlock.size(); i++) {
            sight.add(sightBlock.get(i).getLocation());
        }
        // Get the last
        return sight.get(sight.size() - 1);
    }
}
