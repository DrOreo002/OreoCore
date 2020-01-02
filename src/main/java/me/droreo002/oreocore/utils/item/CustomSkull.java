package me.droreo002.oreocore.utils.item;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import me.droreo002.oreocore.OreoCore;
import me.droreo002.oreocore.utils.bridge.ServerUtils;
import me.droreo002.oreocore.utils.item.complex.UMaterial;
import me.droreo002.oreocore.utils.multisupport.BukkitReflectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

public final class CustomSkull {

    private static final String TEXTURE_URL = "http://textures.minecraft.net/texture/";
    private static final Map<String, ItemStack> CACHE = new HashMap<>();

    static {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(OreoCore.getInstance(), CACHE::clear, 0L, 20L * 1800); // Will clear the cache every 30 minute
    }

    /**
     * Create a new GameProfile
     *
     * @param texture The head texture that will get applied into the Profile
     * @param id : The ID
     * @return a new GameProfile with its texture edited
     */
    private static GameProfile createGameProfile(final String texture, final UUID id) {
        final GameProfile profile = new GameProfile(id, null);
        final PropertyMap propertyMap = profile.getProperties();
        propertyMap.put("textures", new Property("textures", texture));
        return profile;
    }

    /**
     * Get the head texture
     *
     * @param item : The head item
     * @return the texture if available, empty string otherwise
     */
    public static String getTexture(ItemStack item) {
        final SkullMeta headMeta = (SkullMeta) item.getItemMeta();
        GameProfile gameProfile;
        try {
            gameProfile = (GameProfile) BukkitReflectionUtils.getValue(headMeta, true, "profile");
        } catch (IllegalAccessException | NoSuchFieldException e) {
            return "";
        }
        Property property = gameProfile.getProperties().get("textures").iterator().next();
        return new String(Base64.getDecoder().decode(property.getValue().getBytes()));
    }

    /**
     * Get the player head
     *
     * @param uuid : The owner of the head A.K.A the texture
     * @return The player's head as a ItemStack
     */
    public static ItemStack fromUniqueId(UUID uuid) {
        if (CACHE.containsKey(uuid.toString())) return CACHE.get(uuid.toString());
        ItemStack item = UMaterial.PLAYER_HEAD_ITEM.getItemStack();
        SkullMeta skull = (SkullMeta) item.getItemMeta();
        setOwningPlayer(skull, uuid);
        item.setItemMeta(skull);
        addToCache(item, uuid.toString());
        return item;
    }

    /**
     * Get the skull, can freeze the server
     *
     * @param url The texture 'url' example: bd69e06e5dadfd84e5f3d1c21063f2553b2fa945ee1d4d7152fdc5425bc12a9
     * @return an player head with that texture applied
     */
    public static ItemStack fromUrl(String url) {
        if (!url.contains(TEXTURE_URL)) url = TEXTURE_URL + url;
        if (CACHE.containsKey(url)) return CACHE.get(url);
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);

        PropertyMap propertyMap = profile.getProperties();

        if (propertyMap == null) throw new IllegalStateException("Profile doesn't contain a property map");

        byte[] encodedData = Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());

        propertyMap.put("textures", new Property("textures", new String(encodedData)));

        ItemStack head = UMaterial.PLAYER_HEAD_ITEM.getItemStack();

        SkullMeta headMeta = (SkullMeta) head.getItemMeta();

        try {
            BukkitReflectionUtils.setValue(headMeta, true, "profile", profile);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
            return null;
        }

        head.setItemMeta(headMeta);
        addToCache(head, url);
        return head;
    }

    /**
     * If its an actual player head then, set that ItemStack head into {@param player}'s head!
     *
     * @param item : The ItemStack that will get edited
     * @param uuid : The owner of the head A.K.A the texture
     * @return the result ItemStack if successful, null otherwise
     */
    public static ItemStack toHeadUuid(ItemStack item, UUID uuid) {
        tryFix(item);

        if (CACHE.containsKey(uuid.toString())) {
            if (ItemUtils.isSimilar(item, CACHE.get(uuid.toString()))) {
                return CACHE.get(uuid.toString());
            }
        }
        SkullMeta skull = (SkullMeta) item.getItemMeta();
        ItemMeta itemMeta = item.getItemMeta();

        setOwningPlayer(skull, uuid);
        if (itemMeta.hasDisplayName()) skull.setDisplayName(itemMeta.getDisplayName());
        if (itemMeta.hasLore()) skull.setLore(itemMeta.getLore());
        item.setItemMeta(skull);
        addToCache(item, uuid.toString());
        return item;
    }

    /**
     * Convert the item into head while keeping its meta data
     *
     * @param item The ItemStack that will get edited
     * @param url The texture url
     * @return the result ItemStack if successful, null otherwise
     */
    public static ItemStack toHeadUrl(ItemStack item, String url) {
        if (!url.contains(TEXTURE_URL)) url = TEXTURE_URL + url;
        item.setType(UMaterial.PLAYER_HEAD_ITEM.getMaterial());

        GameProfile profile = new GameProfile(UUID.randomUUID(), null);

        PropertyMap propertyMap = profile.getProperties();

        if (propertyMap == null) throw new IllegalStateException("Profile doesn't contain a property map");

        byte[] encodedData = Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());

        propertyMap.put("textures", new Property("textures", new String(encodedData)));

        SkullMeta headMeta = (SkullMeta) item.getItemMeta();

        try {
            BukkitReflectionUtils.setValue(headMeta, true, "profile", profile);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
            return null;
        }

        item.setItemMeta(headMeta);
        return item;
    }

    /**
     * If its an actual player head then, set that ItemStack head texture into {@param texture}
     *
     * @param item : The ItemStack that will get edited
     * @param texture : The texture
     * @return the result ItemStack if successful, null otherwise
     */
    public static ItemStack toHeadTexture(ItemStack item, String texture) {
        tryFix(item);

        if (CACHE.containsKey(texture)) {
            if (ItemUtils.isSimilar(item, CACHE.get(texture))) {
                return CACHE.get(texture);
            }
        }
        final ItemMeta meta = item.getItemMeta();
        final Object skin = createGameProfile(texture, UUID.randomUUID());
        try {
            BukkitReflectionUtils.setValue(meta, true, "profile", skin);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        item.setItemMeta(meta);
        addToCache(item, texture);
        return item;
    }

    /**
     * Add into the cache, this will also clear the item's ItemMeta before adding
     *
     * @param item : The head
     * @param texture : The head texture
     */
    private static void addToCache(ItemStack item, String texture) {
        tryFix(item);

        if (CACHE.containsKey(texture)) return;
        if (item.hasItemMeta()) {
            item.getItemMeta().setLore(new ArrayList<>());
            item.getItemMeta().setDisplayName("Head");
        }
        CACHE.put(texture, item);
    }

    /**
     * Validate the item
     *
     * @param item : The item to validate
     */
    private static void tryFix(ItemStack item) {
        if (item.getType() != UMaterial.PLAYER_HEAD_ITEM.getMaterial())
            item.setType(UMaterial.PLAYER_HEAD_ITEM.getMaterial());
    }

    /**
     * Set the owning player of Skull, with backward version compatible
     *
     * @param skull The SkullMeta
     * @param uuid the player's UUID
     */
    private static void setOwningPlayer(SkullMeta skull, UUID uuid) {
        boolean oldLegacy = false;
        if (ServerUtils.getServerVersion().getBaseVersion().equals("V1_8")) {
            oldLegacy = true;
        }
        if (oldLegacy) {
            skull.setOwner(Bukkit.getOfflinePlayer(uuid).getName());
        } else {
            skull.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
        }
    }
}
