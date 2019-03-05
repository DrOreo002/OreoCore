package me.droreo002.oreocore.utils.item;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import me.droreo002.oreocore.enums.XMaterial;
import me.droreo002.oreocore.utils.misc.ThreadingUtils;
import me.droreo002.oreocore.utils.multisupport.BukkitReflectionUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public final class CustomSkull {

    private static final Map<String, ItemStack> CACHE = new HashMap<>();

    /**
     * Get the Skull via async way
     *
     * @param texture : The texture string
     * @return an player head with that texture applied
     */
    public static Future<ItemStack> getSkullAsync(final String texture) {
        if (CACHE.containsKey(texture)) return ThreadingUtils.makeFuture(() -> CACHE.get(texture));
        return ThreadingUtils.makeFuture(() -> {
            final ItemStack item = XMaterial.PLAYER_HEAD.parseItem();
            final ItemMeta meta = item.getItemMeta();
            final Object skin = createGameProfile(texture, UUID.randomUUID());
            BukkitReflectionUtils.setValue(meta, true, "profile", skin);
            item.setItemMeta(meta);
            CACHE.put(texture, item);
            return item;
        });
    }

    /**
     * Get the skull (Warning, will freeze the server if its loaded more than 10 at one time)
     *
     * @param texture : The texture string
     * @return an player head with that texture applied
     */
    public static ItemStack getSkull(final String texture) {
        if (CACHE.containsKey(texture)) return CACHE.get(texture);
        final ItemStack item = XMaterial.PLAYER_HEAD.parseItem();
        final ItemMeta meta = item.getItemMeta();
        final Object skin = createGameProfile(texture, UUID.randomUUID());
        try {
            BukkitReflectionUtils.setValue(meta, true, "profile", skin);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        item.setItemMeta(meta);
        CACHE.put(texture, item);
        return item;
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
     * Get the player head
     *
     * @param player : The target player
     * @return The player's head as a ItemStack
     */
    public static ItemStack getHead(Player player) {
        if (CACHE.containsKey(player.getUniqueId().toString())) return CACHE.get(player.getUniqueId().toString());
        ItemStack item = XMaterial.PLAYER_HEAD.parseItem();
        SkullMeta skull = (SkullMeta) item.getItemMeta();
        skull.setOwningPlayer(player);
        item.setItemMeta(skull);
        CACHE.put(player.getUniqueId().toString(), item);
        return item;
    }

    /**
     * If its an actual player head then, set that ItemStack head into {@param player}'s head!
     *
     * @param item : The ItemStack that will get edited
     * @param player : The owner of the head A.K.A the texture
     * @return the result ItemStack if successful, null otherwise
     */
    public static ItemStack toHead(ItemStack item, Player player) {
        if (item.getType() != XMaterial.PLAYER_HEAD.parseMaterial()) return null;
        if (CACHE.containsKey(player.getUniqueId().toString())) return CACHE.get(player.getUniqueId().toString());
        SkullMeta skull = (SkullMeta) item.getItemMeta();
        skull.setOwningPlayer(player);
        item.setItemMeta(skull);
        CACHE.put(player.getUniqueId().toString(), item);
        return item;
    }

    /**
     * If its an actual player head then, set that ItemStack head into {@param player}'s head!
     * will try to get the head using async way
     *
     * @param item : The ItemStack that will get edited
     * @param player : The owner of the head A.K.A the texture
     * @return the result ItemStack if successful, null otherwise
     */
    public static Future<ItemStack> toHeadAsync(ItemStack item, Player player) {
        if (item.getType() != XMaterial.PLAYER_HEAD.parseMaterial()) return null;
        if (CACHE.containsKey(player.getUniqueId().toString())) return ThreadingUtils.makeFuture(() -> CACHE.get(player.getUniqueId().toString()));
        return ThreadingUtils.makeFuture(() -> {
            SkullMeta skull = (SkullMeta) item.getItemMeta();
            skull.setOwningPlayer(player);
            item.setItemMeta(skull);
            CACHE.put(player.getUniqueId().toString(), item);
            return item;
        });
    }

    /**
     * If its an actual player head then, set that ItemStack head texture into {@param texture}
     *
     * @param item : The ItemStack that will get edited
     * @param texture : The texture
     * @return the result ItemStack if successful, null otherwise
     */
    public static ItemStack setTexture(ItemStack item, String texture) {
        if (item.getType() != XMaterial.PLAYER_HEAD.parseMaterial()) return null;
        if (CACHE.containsKey(texture)) return CACHE.get(texture);
        final ItemMeta meta = item.getItemMeta();
        final Object skin = createGameProfile(texture, UUID.randomUUID());
        try {
            BukkitReflectionUtils.setValue(meta, true, "profile", skin);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
        item.setItemMeta(meta);
        CACHE.put(texture, item);
        return item;
    }
}
