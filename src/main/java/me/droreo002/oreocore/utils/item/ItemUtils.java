package me.droreo002.oreocore.utils.item;

import me.droreo002.oreocore.utils.item.namingSupport.UMaterial;
import me.droreo002.oreocore.utils.list.ListUtils;
import me.droreo002.oreocore.utils.strings.StringUtils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

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
                return "";
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
                return "";
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
}
