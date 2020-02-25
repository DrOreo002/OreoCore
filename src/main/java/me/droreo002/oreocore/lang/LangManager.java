package me.droreo002.oreocore.lang;

import lombok.Getter;
import lombok.Setter;
import me.droreo002.oreocore.configuration.CustomConfiguration;
import me.droreo002.oreocore.utils.item.helper.TextPlaceholder;
import me.droreo002.oreocore.utils.strings.StringUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class LangManager extends CustomConfiguration {

    @Getter
    private final Map<String, Object> values = new HashMap<>();
    @Getter
    private final Set<String> paths = new HashSet<>();
    @Getter
    private boolean loaded;
    @Getter @Setter
    private String pluginPrefix;

    public LangManager(JavaPlugin plugin, String langFileName, String pluginPrefix) {
        super(plugin, new File(plugin.getDataFolder(), (langFileName.contains(".yml")) ? langFileName.replace(".yml", "") + ".yml" : langFileName + ".yml"));
        this.pluginPrefix = pluginPrefix;
    }

    /**
     * Add a path to lang message
     *
     * @param s : The path
     */
    public void addPath(String s) {
        if (paths.contains(s)) return;
        paths.add(s);
    }

    /**
     * Load the lang datas
     */
    public void loadData() {
        if (!values.isEmpty()) values.clear();
        for (String exactPath : paths) {
            if (getConfig().contains(exactPath)) values.put(exactPath, getConfig().get(exactPath));
        }
        loaded = true;
    }

    /**
     * Get the lang string
     *
     * @param path The lang path
     * @return the String if there's any, null otherwise
     */
    public String getLang(String path) {
        return getLang(path, false);
    }

    /**
     * Get the lang string
     *
     * @param path The lang path
     * @param addPrefix Should we add prefix?
     * @return the String if there's any null otherwise
     */
    public String getLang(String path, boolean addPrefix) {
        return getLang(path, null, addPrefix);
    }

    /**
     * Get the lang string
     *
     * @param path The lang path
     * @param placeholder The placeholder (null if there's none)
     * @param addPrefix Should we add prefix?
     * @return the String if there's any, null otherwise
     */
    public String getLang(String path, TextPlaceholder placeholder, boolean addPrefix) {
        if (values.get(path) == null) throw new NullPointerException("Error. Lang message on path " + path + " cannot be found!, did you forget to load the data?");
        if (placeholder != null) {
            // Process placeholder
            String result = (String) values.get(path);
            for (TextPlaceholder place : placeholder.getPlaceholders()) {
                String from = place.getFrom();
                String to = place.getTo();
                result = result.replace(from, to);
            }
            return (addPrefix) ? StringUtils.color(pluginPrefix + result) : StringUtils.color(result);
        }
        return (addPrefix) ? StringUtils.color(pluginPrefix + values.get(path)) : StringUtils.color((String) values.get(path));
    }

    /**
     * Get the lang as list
     *
     * @param path The lang path
     * @return List containing the string if there's any, null otherwise
     */
    public List<String> getLangList(String path) {
        return getLangList(path, null);
    }

    /**
     * Get the lang as a list
     *
     * @param path The lang path
     * @param placeholder The placeholder, null if there's none
     * @return List containing the string if there's any, null otherwise
     */
    public List<String> getLangList(String path, TextPlaceholder placeholder) {
        if (values.get(path) == null) throw new NullPointerException("Error. Lang message on path " + path + " cannot be found!, did you forget to load the data?");
        if (placeholder != null) {
            List<String> result = new ArrayList<>();
            for (String s : (List<String>) values.get(path)) {
                for (TextPlaceholder place : placeholder.getPlaceholders()) {
                    String from = place.getFrom();
                    String to = place.getTo();
                    if (s.contains(from)) {
                        s = s.replace(from, to);
                    }
                }
                result.add(s);
            }
            return result.stream().map(StringUtils::color).collect(Collectors.toList());
        }
        return ((List<String>) values.get(path)).stream().map(StringUtils::color).collect(Collectors.toList());
    }

    /**
     * Get the path as a ConfigurationSection object
     *
     * @param path The lang path
     * @return a ConfigurationSection if there's any, null otherwise
     */
    public ConfigurationSection asSection(String path) {
        return getConfig().getConfigurationSection(path);
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        loadData();
    }
}
