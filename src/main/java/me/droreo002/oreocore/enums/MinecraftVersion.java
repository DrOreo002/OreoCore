package me.droreo002.oreocore.enums;

import lombok.Getter;

/**
 * All version can be seen here
 * https://www.spigotmc.org/wiki/spigot-nms-and-minecraft-versions/
 */
public enum MinecraftVersion {

    V1_8_R1("V1_8"),
    V1_8_R2("V1_8"),
    V1_8_R3("V1_8"),

    V1_9_R1("V1_9"),
    V1_9_R2("V1_9"),

    V1_10_R1("V1_10"),

    V1_11_R1("V1_11"),

    V1_12_R1("V1_12"),

    V1_13_R1("V1_13"),
    V1_13_R2("V1_13"),

    V1_14_R1("V1_14"),

    V1_15_R1("V1_15"),

    UNKNOWN("UNKNOWN");

    @Getter
    private String baseVersion;

    MinecraftVersion(String baseVersion) {
        this.baseVersion = baseVersion;
    }

    /**
     * Get a mnecraft version from base version
     *
     * @param base The base version
     * @return The minecraft version
     */
    public static MinecraftVersion getFromBase(String base) {
        for (MinecraftVersion ver : values()) {
            if (ver.getBaseVersion().equalsIgnoreCase(base)) return ver;
        }
        return UNKNOWN;
    }
}
