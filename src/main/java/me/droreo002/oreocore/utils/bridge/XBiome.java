/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 Crypto Morin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package me.droreo002.oreocore.utils.bridge;

import com.google.common.base.Enums;
import com.google.common.base.Optional;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.apache.commons.lang.Validate;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

/**
 * <b>XBiome</b> - Cross-version support for biome names.<br>
 * Biomes: https://minecraft.gamepedia.com/Biome
 * Biome: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/block/Biome.html
 *
 * @author Crypto Morin
 * @version 1.0.1
 * @see Biome
 */
public enum XBiome {
    BADLANDS("MESA"),
    BADLANDS_PLATEAU("MESA_CLEAR_ROCK", "MESA_PLATEAU"),
    BEACH("BEACHES"),
    BIRCH_FOREST("BIRCH_FOREST"),
    BIRCH_FOREST_HILLS("BIRCH_FOREST_HILLS"),
    COLD_OCEAN("COLD_OCEAN"),
    DARK_FOREST("ROOFED_FOREST"),
    DARK_FOREST_HILLS("MUTATED_ROOFED_FOREST", "ROOFED_FOREST_MOUNTAINS"),
    DEEP_COLD_OCEAN("COLD_DEEP_OCEAN"),
    DEEP_FROZEN_OCEAN("FROZEN_DEEP_OCEAN"),
    DEEP_LUKEWARM_OCEAN("LUKEWARM_DEEP_OCEAN"),
    DEEP_OCEAN("DEEP_OCEAN"),
    DEEP_WARM_OCEAN("WARM_DEEP_OCEAN"),
    DESERT("DESERT"),
    DESERT_HILLS("DESERT_HILLS"),
    DESERT_LAKES("MUTATED_DESERT", "DESERT_MOUNTAINS"),
    END_BARRENS("SKY_ISLAND_BARREN"),
    END_HIGHLANDS("SKY_ISLAND_HIGH"),
    END_MIDLANDS("SKY_ISLAND_MEDIUM"),
    ERODED_BADLANDS("MUTATED_MESA", "MESA_BRYCE"),
    FLOWER_FOREST("MUTATED_FOREST"),
    FOREST("FOREST"),
    FROZEN_OCEAN("FROZEN_OCEAN"),
    FROZEN_RIVER("FROZEN_RIVER"),
    GIANT_SPRUCE_TAIGA("MUTATED_REDWOOD_TAIGA", "MEGA_SPRUCE_TAIGA"),
    GIANT_SPRUCE_TAIGA_HILLS("MUTATED_REDWOOD_TAIGA_HILLS", "MEGA_SPRUCE_TAIGA_HILLS"),
    GIANT_TREE_TAIGA("REDWOOD_TAIGA", "MEGA_TAIGA"),
    GIANT_TREE_TAIGA_HILLS("REDWOOD_TAIGA_HILLS", "MEGA_TAIGA_HILLS"),
    GRAVELLY_MOUNTAINS("MUTATED_EXTREME_HILLS", "EXTREME_HILLS_MOUNTAINS"),
    ICE_SPIKES("MUTATED_ICE_FLATS", "ICE_PLAINS_SPIKES"),
    JUNGLE("JUNGLE"),
    JUNGLE_EDGE("JUNGLE_EDGE"),
    JUNGLE_HILLS("JUNGLE_HILLS"),
    LUKEWARM_OCEAN("LUKEWARM_OCEAN"),
    MODIFIED_BADLANDS_PLATEAU("MUTATED_MESA_CLEAR_ROCK", "MESA_PLATEAU"),
    MODIFIED_GRAVELLY_MOUNTAINS("MUTATED_EXTREME_HILLS_WITH_TREES", "EXTREME_HILLS_MOUNTAINS"),
    MODIFIED_JUNGLE("MUTATED_JUNGLE", "JUNGLE_MOUNTAINS"),
    MODIFIED_JUNGLE_EDGE("MUTATED_JUNGLE_EDGE", "JUNGLE_EDGE_MOUNTAINS"),
    MODIFIED_WOODED_BADLANDS_PLATEAU("MUTATED_MESA_ROCK", "MESA_PLATEAU_FOREST_MOUNTAINS"),
    MOUNTAINS("EXTREME_HILLS"),
    MOUNTAIN_EDGE("SMALLER_EXTREME_HILLS"),
    MUSHROOM_FIELDS("MUSHROOM_ISLAND"),
    MUSHROOM_FIELD_SHORE("MUSHROOM_ISLAND_SHORE", "MUSHROOM_SHORE"),
    NETHER("HELL"),
    OCEAN("OCEAN"),
    PLAINS("PLAINS"),
    RIVER("RIVER"),
    SAVANNA("SAVANNA"),
    SAVANNA_PLATEAU("SAVANNA_ROCK", "SAVANNA_PLATEAU"),
    SHATTERED_SAVANNA("MUTATED_SAVANNA", "SAVANNA_MOUNTAINS"),
    SHATTERED_SAVANNA_PLATEAU("MUTATED_SAVANNA_ROCK", "SAVANNA_PLATEAU_MOUNTAINS"),
    SMALL_END_ISLANDS("SKY_ISLAND_LOW"),
    SNOWY_BEACH("COLD_BEACH"),
    SNOWY_MOUNTAINS("ICE_MOUNTAINS"),
    SNOWY_TAIGA("TAIGA_COLD", "COLD_TAIGA"),
    SNOWY_TAIGA_HILLS("TAIGA_COLD_HILLS", "COLD_TAIGA_HILLS"),
    SNOWY_TAIGA_MOUNTAINS("MUTATED_TAIGA_COLD", "COLD_TAIGA_MOUNTAINS"),
    SNOWY_TUNDRA("ICE_FLATS", "ICE_PLAINS"),
    STONE_SHORE("STONE_BEACH"),
    SUNFLOWER_PLAINS("MUTATED_PLAINS"),
    SWAMP("SWAMPLAND"),
    SWAMP_HILLS("MUTATED_SWAMPLAND", "SWAMPLAND_MOUNTAINS"),
    TAIGA("TAIGA"),
    TAIGA_HILLS("TAIGA_HILLS"),
    TAIGA_MOUNTAINS("MUTATED_TAIGA"),
    TALL_BIRCH_FOREST("MUTATED_BIRCH_FOREST", "BIRCH_FOREST_MOUNTAINS"),
    TALL_BIRCH_HILLS("MUTATED_BIRCH_FOREST_HILLS", "MESA_PLATEAU_FOREST_MOUNTAINS"),
    THE_END("SKY"),
    THE_VOID("VOID"),
    WARM_OCEAN("WARM_OCEAN"),
    WOODED_BADLANDS_PLATEAU("MESA_ROCK", "MESA_PLATEAU_FOREST"),
    WOODED_HILLS("FOREST_HILLS"),
    WOODED_MOUNTAINS("EXTREME_HILLS_WITH_TREES", "EXTREME_HILLS_PLUS"),
    BAMBOO_JUNGLE,
    BAMBOO_JUNGLE_HILLS;

    /**
     * A cached list of {@link XBiome#values()} to avoid allocating memory for
     * calling the method every time.
     *
     * @since 1.0.0
     */
    public static final EnumSet<XBiome> VALUES = EnumSet.allOf(XBiome.class);
    /**
     * Guava (Google Core Libraries for Java)'s cache for performance and timed caches.
     * Caches the parsed {@link Biome} objects instead of string. Because it has to go through catching exceptions again
     * since {@link Biome} class doesn't have a method like {@link org.bukkit.Material#getMaterial(String)}.
     * So caching these would be more efficient.
     * This cache will not expire since there are only a few biome names.
     *
     * @since 1.0.0
     */
    private static final Cache<XBiome, Optional<Biome>> CACHE = CacheBuilder.newBuilder()
            .softValues().build();
    /**
     * Pre-compiled RegEx pattern.
     * Include both replacements to avoid creating string multiple times and multiple RegEx checks.
     *
     * @since 1.0.0
     */
    private static final Pattern FORMAT_PATTERN = Pattern.compile("\\d+|\\W+");
    private String[] legacy;

    XBiome(String... legacy) {
        this.legacy = legacy;
    }

    /**
     * Attempts to build the string like an enum name.<br>
     * Removes all the spaces, numbers and extra non-English characters. Also removes some config/in-game based strings.
     *
     * @param name the biome name to modify.
     * @return a Biome enum name.
     * @since 1.0.0
     */
    @Nonnull
    private static String format(@Nonnull String name) {
        return FORMAT_PATTERN.matcher(
                name.trim().replace('-', '_').replace(' ', '_')).replaceAll("").toUpperCase(Locale.ENGLISH);
    }

    /**
     * Checks if XBiome enum and the legacy names contains a biome with this name.
     *
     * @param biome name of the biome
     * @return true if XBiome enum has this biome.
     * @since 1.0.0
     */
    public static boolean contains(@Nonnull String biome) {
        Validate.notEmpty(biome, "Cannot check for null or empty biome name");
        biome = format(biome);

        for (XBiome biomes : VALUES)
            if (biomes.name().equals(biome) || biomes.anyMatchLegacy(biome)) return true;
        return false;
    }

    /**
     * Parses the XBiome with the given name.
     *
     * @param biome the name of the biome.
     * @return a matched XBiome.
     * @since 1.0.0
     */
    @Nonnull
    public static java.util.Optional<XBiome> matchXBiome(@Nonnull String biome) {
        Validate.notEmpty(biome, "Cannot match XBiome of a null or empty biome name");
        biome = format(biome);

        for (XBiome biomes : VALUES)
            if (biomes.name().equals(biome) || biomes.anyMatchLegacy(biome)) return java.util.Optional.of(biomes);
        return java.util.Optional.empty();
    }

    /**
     * Parses the XBiome with the given bukkit biome.
     *
     * @param biome the Bukkit biome.
     * @return a matched biome.
     * @throws IllegalArgumentException may be thrown as an unexpected exception.
     * @since 1.0.0
     */
    @Nonnull
    public static XBiome matchXBiome(@Nonnull Biome biome) {
        Objects.requireNonNull(biome, "Cannot match XBiome of a null biome");
        return matchXBiome(biome.name())
                .orElseThrow(() -> new IllegalArgumentException("Unsupported Biome: " + biome.name()));
    }

    /**
     * Checks if the given string matches any of this biome's legacy biome names.
     *
     * @param biome the biome name to check
     * @return true if it's one of the legacy names.
     * @since 1.0.0
     */
    public boolean anyMatchLegacy(@Nonnull String biome) {
        Validate.notEmpty(biome, "Cannot check for legacy name for null or empty biome name");
        return Arrays.asList(this.legacy).contains(format(biome));
    }

    /**
     * Parses the XBiome as a {@link Biome} based on the server version.
     *
     * @return the vanilla biome.
     * @since 1.0.0
     */
    @Nullable
    @SuppressWarnings({"Guava", "OptionalAssignedToNull"})
    public Biome parseBiome() {
        com.google.common.base.Optional<Biome> cached = CACHE.getIfPresent(this);
        if (cached != null) return cached.orNull();
        com.google.common.base.Optional<Biome> biome;

        biome = Enums.getIfPresent(Biome.class, this.name());

        if (!biome.isPresent()) {
            for (String legacy : this.legacy) {
                biome = Enums.getIfPresent(Biome.class, legacy);
                if (biome.isPresent()) break;
            }
        }

        CACHE.put(this, biome);
        return biome.orNull();
    }

    /**
     * Sets the biome of the chunk.
     * If the chunk is not generated/loaded already, it'll be generated and loaded.
     * Note that this doesn't send any update packets to the nearby clients.
     *
     * @param chunk the chunk to change the biome.
     * @since 1.0.0
     */
    @Nonnull
    public CompletableFuture<Void> setBiome(@Nonnull Chunk chunk) {
        Objects.requireNonNull(chunk, "Cannot set biome of null chunk");
        if (!chunk.isLoaded()) {
            if (!chunk.load(true)) throw new IllegalArgumentException("Could not load chunk at " + chunk.getX() + ", " + chunk.getZ());
        }

        Biome biome = this.parseBiome();
        if (biome == null) throw new IllegalArgumentException("Unsupported Biome: " + this.name());

        // Apparently setBiome is thread-safe.
        return CompletableFuture.runAsync(() -> {
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    Block block = chunk.getBlock(x, 0, z);
                    if (block.getBiome() != biome) block.setBiome(biome);
                }
            }
        });
    }

    /**
     * Change the biome in the selected region.
     * Unloaded chunks will be ignored.
     * Note that this doesn't send any update packets to the nearby clients.
     *
     * @param start the start position.
     * @param end   the end position.
     * @since 1.0.0
     */
    @Nonnull
    public CompletableFuture<Void> setBiome(@Nonnull Location start, @Nonnull Location end) {
        Objects.requireNonNull(start, "Start location cannot be null");
        Objects.requireNonNull(end, "End location cannot be null");
        if (!start.getWorld().getName().equals(end.getWorld().getName()))
            throw new IllegalArgumentException("Location worlds mismatch");

        Biome biome = this.parseBiome();
        if (biome == null) throw new IllegalArgumentException("Unsupported Biome: " + this.name());

        // Apparently setBiome is thread-safe.
        return CompletableFuture.runAsync(() -> {
            for (int x = start.getBlockX(); x < end.getBlockX(); x++) {
                for (int z = start.getBlockZ(); z < end.getBlockZ(); z++) {
                    Block block = new Location(start.getWorld(), x, 0, z).getBlock();
                    if (block.getBiome() != biome) block.setBiome(biome);
                }
            }
        });
    }
}
