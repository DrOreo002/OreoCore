package me.droreo002.oreocore.configuration;

import com.google.common.base.Charsets;
import com.google.common.primitives.Chars;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A class to update/add new sections/keys to your config
 * while keeping your current values
 *
 * from https://github.com/tchristofferson/Config-Updater/blob/master/src/main/java/com/thedasmc/configupdater/ConfigUpdater.java (Some changes included)
 */
public class ConfigUpdater {


    /**
     * Update config comments, currently private usage only
     *vp
     * @param toUpdate Config to update
     * @param plugin The JavaPlugin
     * @param fileSourceName The source file name
     * @throws IOException If there's something bad happens
     */
    public static void update(File toUpdate, JavaPlugin plugin, String fileSourceName) throws IOException {
        if (!toUpdate.exists()) throw new NullPointerException("File cannot be null!");
        FileConfiguration config = YamlConfiguration.loadConfiguration(toUpdate);
        FileConfiguration updateSource = YamlConfiguration.loadConfiguration(new InputStreamReader(plugin.getResource(fileSourceName), Charsets.UTF_8));

        final BufferedReader reader = new BufferedReader(new InputStreamReader(plugin.getResource(fileSourceName), Charsets.UTF_8));
        final Writer writer = new OutputStreamWriter(new FileOutputStream(toUpdate), Charsets.UTF_8);

        final List<String> checked = new ArrayList<>();
        final List<String> updateSourceKeys = new ArrayList<>(updateSource.getKeys(true));

        String line;
        outer: while ((line = reader.readLine()) != null) {

            if (line.startsWith("#")) {
                write(writer, line);
                continue;
            }

            for (String key : updateSourceKeys) {
                if (checked.contains(key)) continue;
                String[] keyArray = key.split("\\.");
                String keyString = keyArray[keyArray.length - 1];

                if (line.trim().startsWith(keyString + ":")) {
                    checked.add(key);
                    if (config.isConfigurationSection(key)) {
                        write(writer, line);
                        continue outer;
                    }

                    String[] array = line.split(": ");

                    if (array.length > 1) {
                        if (array[1].startsWith("\"") || array[1].startsWith("'")) {
                            char c = array[1].charAt(0);
                            String s = config.getString(key, (String) updateSource.get(key));
                            if (s.contains("'")) {
                                s = s.replace("'", "''");
                            }
                            line = array[0] + ": " + c + s + c;
                        } else {
                            line = array[0] + ": " + config.get(key, updateSource.get(key));
                        }
                    }
                    write(writer, line);
                    continue outer;
                }
            }
            write(writer, line);
        }
        writer.flush();
        writer.close();
        reader.close();
    }

    /**
     * Write to file
     *
     * @param writer The writer
     * @param line Line to write
     * @throws IOException If there's something bad happens
     */
    private static void write(Writer writer, String line) throws IOException {
        writer.write(line + System.lineSeparator());
    }
}
