package me.droreo002.oreocore.hook;

import me.droreo002.oreocore.utils.bridge.ServerUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class HookManager {

    private static final Map<JavaPlugin, List<PluginHooker>> PLUGIN_HOOK = new HashMap<>();

    /**
     * Register the hook, this will also process the hook. Not just saving it
     *
     * @param plugin : The java plugin
     * @param hooker : The hook class
     * @return true if succeeded, false otherwise
     */
    public static boolean registerHook(JavaPlugin plugin, PluginHooker hooker) {
        if (PLUGIN_HOOK.containsKey(plugin)) {
            if (!ServerUtils.isPluginInstalled(hooker.getRequiredPlugin())) {
                if (hooker.disablePluginIfNotFound()) {
                    ServerUtils.disablePlugin(plugin);
                    return false;
                }
                hooker.hookFailed();
                return false;
            }
            List<PluginHooker> hooks = PLUGIN_HOOK.get(plugin);
            hooks.add(hooker);
            PLUGIN_HOOK.put(plugin, hooks);
            hooker.hookSuccess();
        } else {
            if (!ServerUtils.isPluginInstalled(hooker.getRequiredPlugin())) {
                if (hooker.disablePluginIfNotFound()) {
                    ServerUtils.disablePlugin(plugin);
                    return false;
                }
                hooker.hookFailed();
                return false;
            }
            PLUGIN_HOOK.put(plugin, new ArrayList<>(Collections.singletonList(hooker)));
            hooker.hookSuccess();
        }
        return true;
    }

    /**
     * Unregister the hook.
     *
     * @param plugin : The plugin
     * @param hooker : The hook class
     * @return true if succeeded, false otherwise
     */
    public static boolean unregisterHook(JavaPlugin plugin, PluginHooker hooker) {
        List<PluginHooker> hooks = getHooks(plugin);
        if (hooks == null) {
            throw new IllegalStateException("The plugin " + plugin.getName() + " didn't have any hook!. I can't unregister it!");
        }
        if (!hooks.contains(hooker)) return false;
        hooks.remove(hooker);
        return true;
    }

    /**
     * Get the list of hooks that is registered on that plugin
     *
     * @param plugin : The java plugin
     * @return The list of hooks if there's any, null otherwise
     */
    public static List<PluginHooker> getHooks(JavaPlugin plugin) {
        return PLUGIN_HOOK.get(plugin);
    }

    /**
     * Get the hook class from that plugin by using what plugin that the class is hooking
     *
     * @param plugin : The owning plugin
     * @param hookedPlugin : The plugin that the class is hooking on
     * @return the PluginHooker object if there's any, null otherwise
     */
    public static PluginHooker getHook(JavaPlugin plugin, String hookedPlugin) {
        List<PluginHooker> hooks = getHooks(plugin);
        if (hooks == null) return null;
        for (PluginHooker h : hooks) {
            if (h.getRequiredPlugin().equalsIgnoreCase(hookedPlugin)) return h;
        }
        return null;
    }
}
