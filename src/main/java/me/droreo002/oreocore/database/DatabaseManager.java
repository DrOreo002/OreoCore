package me.droreo002.oreocore.database;

import me.droreo002.oreocore.utils.logging.Debug;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class DatabaseManager {

    private static final Map<JavaPlugin, List<Database>> DATABASES = new HashMap<>();

    public static void registerDatabase(JavaPlugin plugin, Database database) {
        if (DATABASES.containsKey(plugin)) {
            List<Database> databases = DATABASES.get(plugin);
            databases.add(database);
            DATABASES.put(plugin, databases);
        } else {
            DATABASES.put(plugin, new ArrayList<>(Collections.singletonList(database)));
        }
        Debug.log("Database with the type of &c" + database.getDatabaseType() + "&f. From plugin &e" + plugin.getName() + "&f, has been registered!", true);
    }

    public static boolean isDatabaseRegistered(JavaPlugin plugin, DatabaseType type) {
        if (!DATABASES.containsKey(plugin)) return false;
        for (Database data : DATABASES.get(plugin)) {
            if (data.getDatabaseType().equals(type)) return true;
        }
        return false;
    }

    public static Database getDatabase(JavaPlugin plugin, DatabaseType type) {
        if (!isDatabaseRegistered(plugin, type)) return null;
        List<Database> databases = DATABASES.get(plugin);
        for (Database data : databases) {
            if (data.getDatabaseType().equals(type)) return data;
        }
        return null;
    }
}
