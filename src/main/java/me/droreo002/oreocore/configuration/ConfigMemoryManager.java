package me.droreo002.oreocore.configuration;

import me.droreo002.oreocore.configuration.annotations.ConfigVariable;
import me.droreo002.oreocore.configuration.annotations.SerializableEnum;
import me.droreo002.oreocore.utils.logging.Debug;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
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
                Object configValue = config.get(configVariable.path());
                if (configVariable.isSerializableObject()) {
                    ConfigurationSection cs = config.getConfigurationSection(configVariable.path());
                    if (cs == null && configVariable.errorWhenNull()) throw new NullPointerException("Failed to get ConfigurationSection on path " + configVariable.path());
                    if (!f.isAccessible()) f.setAccessible(true);
                    if (!SerializableConfigVariable.class.isAssignableFrom(f.getType())) continue;
                    try {
                        SerializableConfigVariable seri = (SerializableConfigVariable) f.get(memory);
                        Validate.notNull(seri, "Please always initialize the variable first!. Variable name " + f.getName());
                        configValue = seri.getFromConfig(cs);
                        f.set(memory, configValue);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    continue;
                }
                if (configValue == null && configVariable.errorWhenNull()) throw new NullPointerException("Failed to get config value on path " + configVariable.path());
                if (!f.isAccessible()) f.setAccessible(true);
                if (f.getType().isEnum()) {
                    System.out.println("Is enum!");
                    try {
                        Method valueOf = f.getType().getMethod("valueOf", String.class);
                        Object value = valueOf.invoke(null, String.valueOf(configValue));
                        f.set(memory, value);
                    } catch (Exception e) {
                        // handle error here
                        e.printStackTrace();
                        Debug.log("Failed to serialize config variable!. Variable name " + configValue + ". Enum class " + f.getType().getName());
                        continue;
                    }
                    continue;
                }
                try {
                    f.set(memory, configValue);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
