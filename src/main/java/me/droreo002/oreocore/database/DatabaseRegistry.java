package me.droreo002.oreocore.database;

import java.util.HashSet;
import java.util.Set;

public class DatabaseRegistry {

    private static final Set<Database> REGISTRY = new HashSet<>();

    /**
     * Register the database
     *
     * @param database Database to register
     */
    public static void register(Database database) {
        REGISTRY.add(database);
    }

    /**
     * Get all databases
     *
     * @return Set of database
     */
    public static Set<Database> getDatabases() {
        return REGISTRY;
    }
}
