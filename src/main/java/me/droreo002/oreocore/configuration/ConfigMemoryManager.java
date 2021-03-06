package me.droreo002.oreocore.configuration;

import me.droreo002.oreocore.configuration.annotations.ConfigVariable;
import me.droreo002.oreocore.debugging.ODebug;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public final class ConfigMemoryManager {

    /**
     * Update the config's memory
     *
     * @param memory The memory
     */
    public static void updateMemory(ConfigurationMemory memory) {
        writeChanges(memory);
        memory.getParent().saveConfig(false);
        processMemory(memory);
    }

    /**
     * Save memory to file
     *
     * @param memory The memory
     */
    private static void writeChanges(ConfigurationMemory memory) {
        FileConfiguration config = memory.getParent().getConfig();
        for (Field f : getDeclaredFields(memory)) {
            if (f.isAnnotationPresent(ConfigVariable.class)) {
                if (!f.isAccessible()) f.setAccessible(true);
                final ConfigVariable configVariable = f.getAnnotation(ConfigVariable.class);
                if (memory.isUpdatable() || configVariable.isUpdateAbleObject()) {
                    if (SerializableConfigVariable.class.isAssignableFrom(f.getType())) {
                        // Use different saving
                        try {
                            SerializableConfigVariable configurationSerializable = (SerializableConfigVariable) f.get(memory);
                            Map<String, Object> serialized = configurationSerializable.serialize();
                            for (Map.Entry<String, Object> data : serialized.entrySet()) {
                                config.set(configVariable.path() + "." + data.getKey(), data.getValue());
                            }
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
    public static void processMemory(ConfigurationMemory memory) {
        final Map<ConfigVariable, Field> variables = new LinkedHashMap<>();
        for (Field f : getDeclaredFields(memory)) {
            if (f.isAnnotationPresent(ConfigVariable.class)) {
                ConfigVariable variable = f.getAnnotation(ConfigVariable.class);
                if (variable.loadPriority() < 0) throw new IllegalStateException("Load priority cannot be less than 0!");
                variables.put(variable, f);
            }
        }

        List<ConfigVariable> sorted = new ArrayList<>(variables.keySet());
        sorted.sort(Comparator.comparingInt(ConfigVariable::loadPriority));
        Collections.reverse(sorted);

        for (ConfigVariable configVariable : sorted) {
            FileConfiguration config = (memory.getParent() instanceof MultiFileConfiguration) ? memory.getParent().getConfig(configVariable.yamlFileName()) : memory.getParent().getConfig();
            Field f = variables.get(configVariable);

            String path = configVariable.path();
            Object configValue = config.get(path);
            if (configVariable.isSerializableObject()) {
                ConfigurationSection configurationSection = config.getConfigurationSection(path);
                if (configurationSection == null) {
                    if (configVariable.errorWhenNull()) {
                        throw new NullPointerException("Failed to get config value on path " + path);
                    } else {
                        continue;
                    }
                }

                if (!f.isAccessible()) f.setAccessible(true);
                if (!SerializableConfigVariable.class.isAssignableFrom(f.getType())) continue;
                try {
                    configValue = f.getType().getMethod("deserialize", ConfigurationSection.class).invoke(null, configurationSection);
                    f.set(memory, configValue);
                } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                    e.printStackTrace();
                }
                continue;
            }
            if (configValue == null) {
                if (configVariable.errorWhenNull()) {
                    throw new NullPointerException("Failed to get config value on path " + path);
                } else {
                    continue;
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
                    ODebug.log(memory.getParent().getPlugin(), "Failed to serialize config variable!. Variable name " + configValue + ". Enum class " + f.getType().getName(), true);
                    continue;
                }
                continue;
            }

            switch (configVariable.valueType()) {
                case FLOAT:
                    configValue = (float) config.getDouble(path);
                    set(f, memory, configValue);
                    break;
                case BOOLEAN:
                    configValue = config.getBoolean(path);
                    set(f, memory, configValue);
                    break;
                case BOOLEAN_LIST:
                    configValue = config.getBooleanList(path);
                    set(f, memory, configValue);
                    break;
                case BYTE_LIST:
                    configValue = config.getByteList(path);
                    set(f, memory, configValue);
                    break;
                case CHARACTER_LIST:
                    configValue = config.getCharacterList(path);
                    set(f, memory, configValue);
                    break;
                case COLOR:
                    configValue = config.getColor(path);
                    set(f, memory, configValue);
                    break;
                case CONFIGURATION_SECTION:
                    configValue = config.getConfigurationSection(path);
                    set(f, memory, configValue);
                    break;
                case DOUBLE:
                    configValue = config.getDouble(path);
                    set(f, memory, configValue);
                    break;
                case DOUBLE_LIST:
                    configValue = config.getDoubleList(path);
                    set(f, memory, configValue);
                    break;
                case FLOAT_LIST:
                    configValue = config.getFloatList(path);
                    set(f, memory, configValue);
                    break;
                case INT:
                    configValue = config.getInt(path);
                    set(f, memory, configValue);
                    break;
                case INTEGER_LIST:
                    configValue = config.getIntegerList(path);
                    set(f, memory, configValue);
                    break;
                case ITEM_STACK:
                    configValue = config.getItemStack(path);
                    set(f, memory, configValue);
                    break;
                case LIST:
                    configValue = config.getList(path);
                    set(f, memory, configValue);
                    break;
                case LONG:
                    configValue = config.getLong(path);
                    set(f, memory, configValue);
                    break;
                case LONG_LIST:
                    configValue = config.getLongList(path);
                    set(f, memory, configValue);
                    break;
                case MAP_LIST:
                    configValue = config.getMapList(path);
                    set(f, memory, configValue);
                    break;
                case STRING:
                    configValue = config.getString(path);
                    set(f, memory, configValue);
                    break;
                case STRING_LIST:
                    configValue = config.getStringList(path);
                    set(f, memory, configValue);
                    break;
                case VECTOR:
                    configValue = config.getVector(path);
                    set(f, memory, configValue);
                    break;
                default:
                    set(f, memory, configValue);
                    break;
            }
        }
    }

    /**
     * Set field value
     *
     * @param f The field
     * @param memory The memory
     * @param v The object to set
     */
    private static void set(Field f, ConfigurationMemory memory, Object v) {
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
    private static Field[] getDeclaredFields(ConfigurationMemory memory) {
        Class<? extends ConfigurationMemory> obj = memory.getClass();
        return obj.getDeclaredFields();
    }
}
