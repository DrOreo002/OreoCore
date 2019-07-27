package me.droreo002.oreocore.configuration;

import me.droreo002.oreocore.configuration.annotations.ConfigVariable;
import me.droreo002.oreocore.debugging.Debug;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public final class ConfigMemoryManager {

    private static final Map<JavaPlugin, List<ConfigMemory>> CONFIG_MEMORY = new HashMap<>();

    /**
     * Register the memory
     *
     * @param plugin Owner of this memory
     * @param memory The memory
     */
    public static void registerMemory(JavaPlugin plugin, ConfigMemory memory) {
        if (isLoaded(plugin, memory)) return;
        List<ConfigMemory> arr = new ArrayList<>();
        if (CONFIG_MEMORY.containsKey(plugin)) arr = CONFIG_MEMORY.get(plugin);
        process(memory);

        arr.add(memory);
        CONFIG_MEMORY.put(plugin, arr);
        Debug.log("&eConfigMemory &ffor yaml file with the name of &7(&c" + memory.getParent().getFileName() + "&7) &ffrom plugin &b" + plugin.getName() + "&f has been registered!", true);
    }

    /**
     * Update the memory (Will remove and replace)
     *
     * @param plugin Owner of this memory
     * @param memory The memory
     */
    public static void updateMemory(JavaPlugin plugin, ConfigMemory memory) {
        if (CONFIG_MEMORY.containsKey(plugin)) {
            if (isLoaded(plugin, memory)) {
                CONFIG_MEMORY.get(plugin).remove(memory);
                saveToFile(memory);
                memory.getParent().saveConfig(false);
                process(memory);
                CONFIG_MEMORY.get(plugin).add(memory);
            }
        }
    }

    /**
     * Save memory to file
     *
     * @param memory The memory
     */
    private static void saveToFile(ConfigMemory memory) {
        final FileConfiguration config = memory.getParent().getConfig();
        for (Field f : getDeclaredFields(memory)) {
            if (f.isAnnotationPresent(ConfigVariable.class)) {
                if (!f.isAccessible()) f.setAccessible(true);
                final ConfigVariable configVariable = f.getAnnotation(ConfigVariable.class);
                if (memory.isUpdatable() || configVariable.isUpdateAbleObject()) {
                    if (SerializableConfigVariable.class.isAssignableFrom(f.getType())) {
                        // Use different saving
                        try {
                            SerializableConfigVariable seri = (SerializableConfigVariable) f.get(memory);
                            seri.saveToConfig(configVariable.path(), config);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            config.set(configVariable.path(), f.get(memory));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    /**
     * Process the memory
     *
     * @param memory The config memory
     */
    private static void process(ConfigMemory memory) {
        final FileConfiguration config = memory.getParent().getConfig();
        for (Field f : getDeclaredFields(memory)) {
            if (f.isAnnotationPresent(ConfigVariable.class)) {
                final ConfigVariable configVariable = f.getAnnotation(ConfigVariable.class);
                String path = configVariable.path();
                Object configValue = config.get(path);
                if (configVariable.isSerializableObject()) {
                    ConfigurationSection cs = config.getConfigurationSection(path);
                    if (cs == null) {
                        if (configVariable.errorWhenNull()) {
                            throw new NullPointerException("Failed to get config value on path " + path);
                        } else {
                            Debug.log("&cFailed to get config value on path &e" + configVariable.path() + " &cplease update your config!", true);
                            continue; // We ignore the null value
                        }
                    }

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
                if (configValue == null) {
                    if (configVariable.errorWhenNull()) {
                        throw new NullPointerException("Failed to get config value on path " + path);
                    } else {
                        Debug.log("&cFailed to get config value on path &e" + configVariable.path() + " &cplease update your config!", true);
                        continue; // We ignore the null value
                    }
                }

                if (!f.isAccessible()) f.setAccessible(true);
                if (f.getType().isEnum()) {
                    try {
                        Method valueOf = f.getType().getMethod("valueOf", String.class);
                        Object value = valueOf.invoke(null, String.valueOf(configValue));
                        f.set(memory, value);
                    } catch (Exception e) {
                        // handle error here
                        e.printStackTrace();
                        Debug.log("Failed to serialize config variable!. Variable name " + configValue + ". Enum class " + f.getType().getName(), true);
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

    /**
     * Check if memory is loaded or not
     *
     * @param plugin The owner of the memory
     * @param memory The memory
     * @return true if loaded, false otherwise
     */
    private static boolean isLoaded(JavaPlugin plugin, ConfigMemory memory) {
        if (CONFIG_MEMORY.get(plugin) == null) return false;
       return CONFIG_MEMORY.get(plugin).stream().anyMatch(configMemory -> configMemory.getParent().getFileName().equals(memory.getParent().getFileName()));
    }

    /**
     * Set field value
     *
     * @param f The field
     * @param memory The memory
     * @param v The object to set
     */
    private static void set(Field f, ConfigMemory memory, Object v) {
        try {
            f.set(memory, v);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the declared fields of the memory
     *
     * @param memory The memory
     * @return the declared fields as array
     */
    private static Field[] getDeclaredFields(ConfigMemory memory) {
        Class<? extends ConfigMemory> obj = memory.getClass();
        return obj.getDeclaredFields();
    }
}
