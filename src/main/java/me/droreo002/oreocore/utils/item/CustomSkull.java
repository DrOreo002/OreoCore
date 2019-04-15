package me.droreo002.oreocore.utils.item;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import me.droreo002.oreocore.enums.XMaterial;
import me.droreo002.oreocore.utils.item.helper.ItemMetaType;
import me.droreo002.oreocore.utils.item.helper.TextPlaceholder;
import me.droreo002.oreocore.utils.misc.ThreadingUtils;
import me.droreo002.oreocore.utils.multisupport.BukkitReflectionUtils;
import me.droreo002.oreocore.utils.strings.StringUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static me.droreo002.oreocore.utils.strings.StringUtils.color;

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
            final ItemStack item = XMaterial.PLAYER_HEAD.parseItem(false);
            final SkullMeta meta = (SkullMeta) item.getItemMeta();
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
     * @param url :  The texture URL
     * @return an player head with that texture applied
     */
    public static ItemStack getSkullUrl(final String url) {
        if (CACHE.containsKey(url)) return CACHE.get(url);
        final Base64 base64 = new Base64();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);

        PropertyMap propertyMap = profile.getProperties();

        if (propertyMap == null) throw new IllegalStateException("Profile doesn't contain a property map");

        byte[] encodedData = base64.encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());

        propertyMap.put("textures", new Property("textures", new String(encodedData)));

        ItemStack head = XMaterial.PLAYER_HEAD.parseItem(false);

        SkullMeta headMeta = (SkullMeta) head.getItemMeta();

        try {
            BukkitReflectionUtils.setValue(headMeta, true, "profile", profile);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
            return null;
        }

        head.setItemMeta(headMeta);
        CACHE.put(url, head);
        return head;
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
     * @param uuid : The owner of the head A.K.A the texture
     * @return The player's head as a ItemStack
     */
    public static ItemStack getHead(UUID uuid) {
        if (CACHE.containsKey(uuid.toString())) return CACHE.get(uuid.toString());
        ItemStack item = XMaterial.PLAYER_HEAD.parseItem(false);
        SkullMeta skull = (SkullMeta) item.getItemMeta();
        skull.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
        item.setItemMeta(skull);
        CACHE.put(uuid.toString(), item);
        return item;
    }

    /**
     * If its an actual player head then, set that ItemStack head into {@param player}'s head!
     *
     * @param item : The ItemStack that will get edited
     * @param uuid : The owner of the head A.K.A the texture
     * @return the result ItemStack if successful, null otherwise
     */
    public static ItemStack toHead(ItemStack item, UUID uuid) {
        if (item.getType() != XMaterial.PLAYER_HEAD.parseMaterial()) return null;
        if (CACHE.containsKey(uuid.toString())) return CACHE.get(uuid.toString());
        SkullMeta skull = (SkullMeta) item.getItemMeta();
        skull.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
        item.setItemMeta(skull);
        CACHE.put(uuid.toString(), item);
        return item;
    }

    /**
     * If its an actual player head then, set that ItemStack head into {@param player}'s head!
     * will try to get the head using async way
     *
     * @param item : The ItemStack that will get edited
     * @param uuid : The owner of the head A.K.A the texture
     * @return the result ItemStack if successful, null otherwise
     */
    public static Future<ItemStack> toHeadAsync(ItemStack item, UUID uuid) {
        if (item.getType() != XMaterial.PLAYER_HEAD.parseMaterial()) return null;
        if (CACHE.containsKey(uuid.toString())) return ThreadingUtils.makeFuture(() -> CACHE.get(uuid.toString()));
        return ThreadingUtils.makeFuture(() -> {
            SkullMeta skull = (SkullMeta) item.getItemMeta();
            skull.setOwningPlayer(Bukkit.getOfflinePlayer(uuid));
            item.setItemMeta(skull);
            CACHE.put(uuid.toString(), item);
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

    /**
     * Get from section, the section must have material key name in order to work
     * Available key :
     *  material > Material as string (String)
     *  materialDurr > The durability or item ID (int)
     *  amount > The item amount (int)
     *  name > The item displayName (String)
     *  lore > The item lore (List String)
     *  glow > Set the item glow or not (bool)
     *  texture > The head texture, will only work if the material is player skull or head (String)
     *
     *
     * @param placeholder : The placeholder, leave null for no placeholder. This will try to replace the specified editable enum
     *                    into the specified string from the TextPlaceholder class
     * @param section : The section
     * @param headOwner : The owner of the head, use null to use texture instead. Texture will be taken from config section
     * @return a new ItemStack if its valid section, null otherwise
     */
    @SuppressWarnings("deprecation")
    public static ItemStack fromSection(ConfigurationSection section, Map<ItemMetaType, TextPlaceholder> placeholder, UUID headOwner) {
        Validate.notNull(section, "Section cannot be null!");
        if (!section.contains("material")) throw new NullPointerException("Section must have material key!");
        String material = section.getString("material", "DIRT");
        int materialDurr = section.getInt("itemID", 0);
        int amount = section.getInt("amount", 1);
        boolean glow = section.getBoolean("glow", false);
        String texture = section.getString("texture");
        String displayName = section.getString("name");
        List<String> lore = (section.getStringList("lore") == null) ? new ArrayList<>() : section.getStringList("lore");

        if (placeholder != null) {
            for (Map.Entry ent : placeholder.entrySet()) {
                final ItemMetaType editable = (ItemMetaType) ent.getKey();
                final TextPlaceholder place = (TextPlaceholder) ent.getValue();

                switch (editable) {
                    case DISPLAY_NAME:
                        for (TextPlaceholder t : place.getPlaceholders()) {
                            if (displayName.contains(t.getFrom())) {
                                displayName = displayName.replace(t.getFrom(), t.getTo());
                            }
                        }
                        break;
                    case LORE:
                        if (!lore.isEmpty()) {
                            for (TextPlaceholder t : place.getPlaceholders()) {
                                lore = lore.stream().map(s -> {
                                    if (s.contains(t.getFrom())) return s.replace(t.getFrom(), t.getTo());
                                    return s;
                                }).collect(Collectors.toList());
                            }
                        }
                        break;
                    default: break;
                }
            }
        }

        ItemStack res = new ItemStack(XMaterial.fromString(material).parseMaterial(), amount, (short) materialDurr);
        if (texture != null) {
            res = CustomSkull.setTexture(res, texture);
        } else {
            res = CustomSkull.getHead(headOwner);
        }

        SkullMeta meta = (SkullMeta) res.getItemMeta();
        if (displayName != null) meta.setDisplayName(color(displayName));
        meta.setLore(lore.stream().map(StringUtils::color).collect(Collectors.toList()));
        if (glow) meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);

        if (res == null) return null;
        res.setItemMeta(meta);
        return res;
    }
}
