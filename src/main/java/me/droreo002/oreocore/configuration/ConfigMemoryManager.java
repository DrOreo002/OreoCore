package me.droreo002.oreocore.configuration;

import me.droreo002.oreocore.utils.logging.Debug;
import org.apache.commons.lang.reflect.FieldUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.*;

public final class ConfigMemoryManager {

    private static final Map<JavaPlugin, List<ConfigMemory>> CONFIG_MEMORY = new HashMap<>();

    public static void registerMemory(JavaPlugin plugin, ConfigMemory memory) {
        if (CONFIG_MEMORY.containsKey(plugin)) {
            List<ConfigMemory> mem = CONFIG_MEMORY.get(plugin);
            if (mem.contains(memory)) return;
            process(memory);
            mem.add(memory);
            CONFIG_MEMORY.put(plugin, mem);
            Debug.log("&eConfigMemory &ffor yaml file with the name of &7(&c" + memory.getParent().getFileName() + "&7) &ffrom plugin &b" + plugin.getName() + "&f has been registered!", true);
        } else {
            process(memory);
            CONFIG_MEMORY.put(plugin, new ArrayList<>(Collections.singletonList(memory)));
            Debug.log("&eConfigMemory &ffor yaml file with the name of &7(&c" + memory.getParent().getFileName() + "&7) &ffrom plugin &b" + plugin.getName() + "&f has been registered!", true);
        }
    }

    public static void reloadMemory(JavaPlugin plugin, ConfigMemory memory) {
        if (CONFIG_MEMORY.containsKey(plugin)) {
            List<ConfigMemory> mem = CONFIG_MEMORY.get(plugin);
            if (mem.contains(memory)) {
                mem.remove(memory);
                process(memory);
                mem.add(memory);
                CONFIG_MEMORY.remove(plugin);
                CONFIG_MEMORY.put(plugin, mem);
                Debug.log("&eConfigMemory &ffor yaml file with the name of &7(&c" + memory.getParent().getFileName() + "&7) &ffrom plugin &b" + plugin.getName() + "&f has been registered!", true);
            }
        }
    }

    private static void process(ConfigMemory memory) {
        Class<? extends ConfigMemory> obj = memory.getClass();
        FileConfiguration config = memory.getParent().getConfig();
        for (Field f : obj.getDeclaredFields()) {
            if (f.isAnnotationPresent(ConfigVariable.class)) {
                final ConfigVariable configVariable = f.getAnnotation(ConfigVariable.class);
                final Object configValue = config.get(configVariable.path());
                if (configValue == null && configVariable.errorWhenNull()) throw new NullPointerException("Failed to get config value on path " + configVariable.path());
                if (!f.isAccessible()) f.setAccessible(true);
                try {
                    f.set(memory, configValue);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
