package me.droreo002.oreocore.utils.modules;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import lombok.Getter;
import me.droreo002.oreocore.OreoCore;
import me.droreo002.oreocore.hook.PluginHooker;
import me.droreo002.oreocore.debugging.Debug;
import me.droreo002.oreocore.utils.bridge.ServerUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

public class EssentialsUtils implements PluginHooker {

    @Getter
    private final Essentials essentials;

    EssentialsUtils() {
        this.essentials = (Essentials) ServerUtils.getPlugin("Essentials");
    }

    /**
     * Check if player have played before or not
     * useful for cracked servers
     *
     * @param playerName The player to check
     * @return true if has played before, false otherwise
     */
    public boolean hasPlayerBefore(String playerName) {
        User user = essentials.getUser(playerName);
        if (user == null) return false;
        OfflinePlayer off = Bukkit.getOfflinePlayer(user.getConfigUUID());
        if (off == null) return false;
        return off.hasPlayedBefore();
    }

    @Override
    public String getRequiredPlugin() {
        return "Essentials";
    }

    @Override
    public void hookSuccess() {
        Debug.log("Successfully hooked to plugin &e" + getRequiredPlugin(), true);
    }

    @Override
    public void hookFailed() {
        Debug.log("&cCannot hook to plugin &e" + getRequiredPlugin() + "&c because its not installed on the server!", true);
    }

    @Override
    public boolean disablePluginIfNotFound() {
        return false;
    }

    @Override
    public JavaPlugin getPlugin() {
        return OreoCore.getInstance();
    }
}
