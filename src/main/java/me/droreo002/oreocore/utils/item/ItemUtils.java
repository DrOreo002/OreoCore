package me.droreo002.oreocore.utils.item;

import me.droreo002.oreocore.utils.bridge.ServerUtils;
import me.droreo002.oreocore.utils.item.complex.XMaterial;
import me.droreo002.oreocore.utils.list.ListUtils;
import me.droreo002.oreocore.utils.multisupport.SimpleReflectionUtils;
import me.droreo002.oreocore.utils.strings.StringUtils;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class ItemUtils {

    /**
     * Get item name
     *
     * @param item The target item
     * @param xMat Should we use XMaterial as item name alternative?
     * @return the item name if there's any, empty string otherwise
     */
    public static String getName(ItemStack item, boolean xMat) {
        String[] nameData;
        StringBuilder nameBuilder = new StringBuilder();

        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            if (meta.hasDisplayName()) return meta.getDisplayName();
        }

        if (xMat) {
            String name = XMaterial.match(item.getType().name()).name();
            if (!name.contains("_")) {
                nameData = new String[]{StringUtils.upperCaseFirstLetter(name.toLowerCase())};
            } else {
                nameData = name.split("_");
            }
        } else {
            nameData = item.getType().name().split("_");
        }

        for (int i = 0; i < nameData.length; i++) {
            nameBuilder.append(StringUtils.upperCaseFirstLetter(nameData[i].toLowerCase()));
            if (i != (nameData.length - 1)) nameBuilder.append(" ");
        }
        return nameBuilder.toString();
    }

    /**
     * Get the item lore
     *
     * @param item : The item
     * @param strip: Should we strip color codes?
     * @return the lore if there's any, empty list otherwise
     */
    public static List<String> getLore(ItemStack item, boolean strip) {
        if (!item.hasItemMeta()) return new ArrayList<>();
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasLore()) return new ArrayList<>();
        if (strip) {
            return ListUtils.strip(meta.getLore());
        } else {
            return meta.getLore();
        }
    }

    /**
     * Check if the item is similar or not
     *
     * @param one First item
     * @param two Second item
     * @return true if similar, false otherwise
     */
    public static boolean isSimilar(ItemStack one, ItemStack two) {
        return ItemStackBuilder.isSimilar(one, two);
    }

    /**
     * Check if the item is empty or air
     *
     * @param itemStack The item to check
     * @return True if empty, false otherwise
     */
    public static boolean isEmpty(ItemStack itemStack) {
        if (itemStack == null) return true;
        return itemStack.getType() == XMaterial.AIR.getMaterial();
    }

    /**
     * Get the enchantment as a list of string
     *
     * @param item The item
     * @param withLevel Should we also return the level?
     * @return The enchantment name
     */
    public static List<String> getEnchantAsString(ItemStack item, boolean withLevel) {
        final List<String> ec = new ArrayList<>();

        Map<Enchantment, Integer> enchants = item.getEnchantments();
        if (item.getType() == XMaterial.ENCHANTED_BOOK.getMaterial()) {
            EnchantmentStorageMeta storageMeta = (EnchantmentStorageMeta) item.getItemMeta();
            enchants = storageMeta.getStoredEnchants();
        }
        for (Map.Entry<Enchantment, Integer> ent : enchants.entrySet()) {
            String enchant = null;
            Enchantment enchantment = ent.getKey();
            // Apparently #getKey only available at 1.13+
            if (ServerUtils.isLegacyVersion()) {
                try {
                    enchant = (String) SimpleReflectionUtils.getMethod(enchantment.getClass(), "getName").invoke(enchantment);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            } else {
                enchant = StringUtils.upperCaseFirstLetter(enchantment.getKey().getKey());
            }
            if (withLevel) enchant += "|" + (int) ent.getValue();
            ec.add(enchant);
        }

        return ec;
    }

    /**
     * Get the item flags
     *
     * @param item The item
     * @return the flags as string
     */
    public static List<String> getItemFlagAsString(ItemStack item) {
        final List<String> flags = new ArrayList<>();
        if (!item.hasItemMeta()) return flags;
        for (ItemFlag f : item.getItemMeta().getItemFlags()) {
            flags.add(f.toString());
        }
        return flags;
    }

    /**
     * Copy the item meta (most of them)
     *
     * @param item1 Item source
     * @param item2 Item target
     */
    public static ItemStack copyMeta(ItemStack item1, ItemStack item2) {
        ItemMeta targetMeta = item2.getItemMeta();
        ItemMeta sourceMeta = item1.getItemMeta();

        targetMeta.setLore(sourceMeta.getLore());
        targetMeta.setDisplayName(sourceMeta.getDisplayName());
        targetMeta.setLocalizedName(sourceMeta.getLocalizedName());

        sourceMeta.getItemFlags().forEach(targetMeta::addItemFlags);
        for (Map.Entry ent : sourceMeta.getEnchants().entrySet()) {
            Enchantment enchantment = (Enchantment) ent.getKey();
            int level = (int) ent.getValue();
            targetMeta.addEnchant(enchantment, level, false);
        }

        item2.setItemMeta(targetMeta);
        return item2.clone();
    }
}
