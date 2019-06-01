package me.droreo002.oreocore.configuration;

import me.droreo002.oreocore.configuration.annotations.ConfigVariable;
import me.droreo002.oreocore.debugging.Debug;
import me.droreo002.oreocore.utils.item.CustomItem;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
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
            boolean match = mem.stream().anyMatch(configMemory -> configMemory.getParent().getFileName().equals(memory.getParent().getFileName()));
            if (match) {
                mem.remove(memory);
                process(memory);
                mem.add(memory);
                CONFIG_MEMORY.put(plugin, mem);
            }
        }
    }

    public static void updateMemory(JavaPlugin plugin, ConfigMemory memory) {
        if (CONFIG_MEMORY.containsKey(plugin)) {
            List<ConfigMemory> mem = CONFIG_MEMORY.get(plugin);
            boolean match = mem.stream().anyMatch(configMemory -> configMemory.getParent().getFileName().equals(memory.getParent().getFileName()));
            if (match) {
                // Update
                update(memory);
                // Reload
                reloadMemory(plugin, memory);
            }
        }
    }

    private static void update(ConfigMemory memory) {
        final FileConfiguration config = memory.getParent().getConfig();
        Class<? extends ConfigMemory> obj = memory.getClass();
        for (Field f : obj.getDeclaredFields()) {
            if (f.isAnnotationPresent(ConfigVariable.class)) {
                if (!f.isAccessible()) f.setAccessible(true);
                final ConfigVariable configVariable = f.getAnnotation(ConfigVariable.class);
                if (configVariable.isUpdateAbleObject()) {
                    try {
                        config.set(configVariable.path(), f.get(memory));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private static void process(ConfigMemory memory) {
        Class<? extends ConfigMemory> obj = memory.getClass();
        FileConfiguration config = memory.getParent().getConfig();
        for (Field f : obj.getDeclaredFields()) {
            if (f.isAnnotationPresent(ConfigVariable.class)) {
                final ConfigVariable configVariable = f.getAnnotation(ConfigVariable.class);
                String path = configVariable.path();
                Object configValue = config.get(path);
                if (configVariable.isSerializableObject()) {
                    ConfigurationSection cs = config.getConfigurationSection(path);
                    if (cs == null && configVariable.errorWhenNull()) throw new NullPointerException("Failed to get ConfigurationSection on path " + path);
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
                if (configValue == null && configVariable.errorWhenNull()) throw new NullPointerException("Failed to get config value on path " + path);
                if (!f.isAccessible()) f.setAccessible(true);
                if (f.getType().isEnum()) {
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
                
                switch (configVariable.valueType()) {
                    case FLOAT:
                        configValue = (float) config.getDouble(path);
                        set(f, memory, configValue);
                        return;
                    case BOOLEAN:
                        configValue = config.getBoolean(path);
                        set(f, memory, configValue);
                        return;
                    case BOOLEAN_LIST:
                        configValue = config.getBooleanList(path);
                        set(f, memory, configValue);
                        return;
                    case BYTE_LIST:
                        configValue = config.getByteList(path);
                        set(f, memory, configValue);
                        return;
                    case CHARACTER_LIST:
                        configValue = config.getCharacterList(path);
                        set(f, memory, configValue);
                        return;
                    case COLOR:
                        configValue = config.getColor(path);
                        set(f, memory, configValue);
                        return;
                    case CONFIGURATION_SECTION:
                        configValue = config.getConfigurationSection(path);
                        set(f, memory, configValue);
                        return;
                    case DOUBLE:
                        configValue = config.getDouble(path);
                        set(f, memory, configValue);
                        return;
                    case DOUBLE_LIST:
                        configValue = config.getDoubleList(path);
                        set(f, memory, configValue);
                        return;
                    case FLOAT_LIST:
                        configValue = config.getFloatList(path);
                        set(f, memory, configValue);
                        return;
                    case INT:
                        configValue = config.getInt(path);
                        set(f, memory, configValue);
                        return;
                    case INTEGER_LIST:
                        configValue = config.getIntegerList(path);
                        set(f, memory, configValue);
                        return;
                    case ITEM_STACK:
                        configValue = config.getItemStack(path);
                        set(f, memory, configValue);
                        return;
                    case LIST:
                        configValue = config.getList(path);
                        set(f, memory, configValue);
                        return;
                    case LONG:
                        configValue = config.getLong(path);
                        set(f, memory, configValue);
                        return;
                    case LONG_LIST:
                        configValue = config.getLongList(path);
                        set(f, memory, configValue);
                        return;
                    case MAP_LIST:
                        configValue = config.getMapList(path);
                        set(f, memory, configValue);
                        return;
                    case STRING:
                        configValue = config.getString(path);
                        set(f, memory, configValue);
                        return;
                    case STRING_LIST:
                        configValue = config.getStringList(path);
                        set(f, memory, configValue);
                        return;
                    case VECTOR:
                        configValue = config.getVector(path);
                        set(f, memory, configValue);
                        return;
                }

                /*
                Nothing found try to set it instead
                 */
                set(f, memory, configValue);
            }
        }
    }
    
    private static void set(Field f, ConfigMemory memory, Object v) {
        try {
            f.set(memory, v);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
