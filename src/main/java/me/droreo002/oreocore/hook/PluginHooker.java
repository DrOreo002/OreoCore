package me.droreo002.oreocore.hook;

import org.bukkit.plugin.java.JavaPlugin;

public interface PluginHooker {

    String getRequiredPlugin();
    void hookSuccess();
    void hookFailed();
    boolean disablePluginIfNotFound();
    JavaPlugin getPlugin();
}
