package me.droreo002.oreocore.utils.item;

import jdk.nashorn.internal.objects.annotations.Setter;
import me.droreo002.oreocore.utils.bridge.ServerUtils;
import me.droreo002.oreocore.utils.item.complex.UMaterial;
import me.droreo002.oreocore.utils.list.ListUtils;
import me.droreo002.oreocore.utils.multisupport.SimpleReflectionUtils;
import me.droreo002.oreocore.utils.strings.StringUtils;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class ItemUtils {

    /**
     * Get item name
     *
     * @param item : The item
     * @param uMaterial : Should we use uMaterial if item doesn't have any name?
     * @return the item name if there's any, empty string otherwise
     */
    public static String getName(ItemStack item, boolean uMaterial) {
        if (!item.hasItemMeta()) {
            if (uMaterial) {
                String name = UMaterial.match(item).name();
                if (!name.contains("_")) return StringUtils.upperCaseFirstLetter(name.toLowerCase());
                StringBuilder builder = new StringBuilder();
                String[] arr = name.split("_");
                for (int i = 0; i <= (arr.length - 1); i++) {
                    if (i != arr.length) { // Last one
                        builder.append(StringUtils.upperCaseFirstLetter(arr[i].toLowerCase())).append(" ");
                    }
                }
                return builder.toString();
            } else {
                StringBuilder builder = new StringBuilder();
                String[] arr = item.getType().toString().split("_");
                for (int i = 0; i <= (arr.length - 1); i++) {
                    if (i != arr.length) { // Last one
                        builder.append(StringUtils.upperCaseFirstLetter(arr[i].toLowerCase())).append(" ");
                    }
                }
                return builder.toString();
            }
        }
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasDisplayName()) {
            if (uMaterial) {
                String name = UMaterial.match(item).name();
                if (!name.contains("_")) return StringUtils.upperCaseFirstLetter(name.toLowerCase());
                StringBuilder builder = new StringBuilder();
                String[] arr = name.split("_");
                for (int i = 0; i <= (arr.length - 1); i++) {
                    if (i != arr.length) { // Last one
                        builder.append(StringUtils.upperCaseFirstLetter(arr[i].toLowerCase())).append(" ");
                    }
                }
                return builder.toString();
            } else {
                StringBuilder builder = new StringBuilder();
                String[] arr = item.getType().toString().split("_");
                for (int i = 0; i <= (arr.length - 1); i++) {
                    if (i != arr.length) { // Last one
                        builder.append(StringUtils.upperCaseFirstLetter(arr[i].toLowerCase())).append(" ");
                    }
                }
                return builder.toString();
            }
        }
        return meta.getDisplayName();
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
        return CustomItem.isSimilar(one, two);
    }

    /**
     * Check if the item is empty or air
     *
     * @param itemStack The item to check
     * @return True if empty, false otherwise
     */
    public static boolean isEmpty(ItemStack itemStack) {
        if (itemStack == null) return true;
        return itemStack.getType() == UMaterial.AIR.getMaterial();
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

        for (Map.Entry ent : item.getEnchantments().entrySet()) {
            String enchant = null;
            if (ServerUtils.isOldAsFuckVersion()) {
                Enchantment enchantment = (Enchantment) ent.getKey();
                try {
                    enchant = (String) SimpleReflectionUtils.getMethod(enchantment.getClass(), "getName").invoke(enchantment);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            } else {
                enchant = StringUtils.upperCaseFirstLetter(((Enchantment) ent.getKey()).getKey().getKey());
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
