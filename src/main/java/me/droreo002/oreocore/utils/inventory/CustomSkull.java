package me.droreo002.oreocore.utils.inventory;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import me.droreo002.oreocore.enums.XMaterial;
import me.droreo002.oreocore.utils.multisupport.BukkitReflectionUtils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public final class CustomSkull {

    public static ItemStack getSkull(final String texture) throws Exception {
        final ItemStack item = XMaterial.PLAYER_HEAD.parseItem();
        final ItemMeta meta = item.getItemMeta();
        final Object skin = createGameProfile(texture, UUID.randomUUID());
        BukkitReflectionUtils.setValue(meta, true, "profile", skin);
        item.setItemMeta(meta);
        return item;
    }

    private static GameProfile createGameProfile(final String texture, final UUID id) {
        final GameProfile profile = new GameProfile(id, (String)null);
        final PropertyMap propertyMap = profile.getProperties();
        propertyMap.put("textures", new Property("textures", texture));
        return profile;
    }
}
