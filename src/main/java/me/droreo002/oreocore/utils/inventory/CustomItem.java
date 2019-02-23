package me.droreo002.oreocore.utils.inventory;

import me.droreo002.oreocore.enums.XMaterial;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("deprecation")
public class CustomItem extends ItemStack {

    /*
        We'll use the Deprecated method for now.
        I don't even think it would get removed.
     */

    public static final ItemStack LBLUE_GLASSPANE = XMaterial.BLUE_STAINED_GLASS_PANE.parseItem();
    public static final ItemStack PURPLE_GLASSPANE = XMaterial.PURPLE_STAINED_GLASS_PANE.parseItem();
    public static final ItemStack GRAY_GLASSPANE = XMaterial.GRAY_STAINED_GLASS_PANE.parseItem();
    public static final ItemStack BLUE_GLASSPANE = XMaterial.BLUE_STAINED_GLASS_PANE.parseItem();

    public static CustomItem deserialize(Map<String,Object> des) {
        return new CustomItem(ItemStack.deserialize(des));
    }

    public CustomItem(XMaterial mat) {
        super(mat.parseMaterial());
    }

    public CustomItem(ItemStack item) {
        super(item);
    }

    public CustomItem(XMaterial mat, String name) {
        super(mat.parseMaterial());
        ItemMeta meta = getItemMeta();
        meta.setDisplayName(color(name));
        setItemMeta(meta);
    }

    public CustomItem(ItemStack item, String name) {
        super(item);
        ItemMeta meta = getItemMeta();
        meta.setDisplayName(color(name));
        setItemMeta(meta);
    }

    public CustomItem(XMaterial mat, String name, int id) {
        super(mat.parseMaterial(), 1, (short) id);
        ItemMeta meta = getItemMeta();
        meta.setDisplayName(color(name));
        setItemMeta(meta);
    }

    public CustomItem(XMaterial mat, String name, List<String> lores) {
        super(mat.parseMaterial(), 1);
        ItemMeta meta = getItemMeta();
        meta.setDisplayName(color(name));
        List<String> all = new ArrayList<>();
        for (String s : lores) {
            all.add(color(s));
        }
        meta.setLore(all);
        setItemMeta(meta);
    }


    public CustomItem(XMaterial mat, String name, int id, String[] lores) {
        super(mat.parseMaterial(), 1, (short) id);
        ItemMeta meta = getItemMeta();
        meta.setDisplayName(color(name));
        List<String> all = new ArrayList<>();
        for (String s : lores) {
            all.add(color(s));
        }
        meta.setLore(all);
        setItemMeta(meta);
    }

    public CustomItem(XMaterial mat, String name, String[] lores, int id) {
        super(mat.parseMaterial(), 1, (short) id);
        ItemMeta meta = getItemMeta();
        meta.setDisplayName(color(name));
        List<String> all = new ArrayList<>();
        for (String s : lores) {
            all.add(color(s));
        }
        meta.setLore(all);
        setItemMeta(meta);
    }

    public CustomItem(String name, String[] lores, String texture) throws Exception {
        super(CustomSkull.getSkull(texture));
        ItemMeta meta = getItemMeta();
        meta.setDisplayName(color(name));
        List<String> add = new ArrayList<>();
        for (String s : lores) {
            add.add(color(s));
        }
        meta.setLore(add);
        setItemMeta(meta);
    }

    public CustomItem(ItemStack item, String name, String[] lores) {
        super(item);
        ItemMeta meta = getItemMeta();
        meta.setDisplayName(color(name));
        List<String> add = new ArrayList<>();
        for (String s : lores) {
            add.add(color(s));
        }
        meta.setLore(add);
        setItemMeta(meta);
    }

    public CustomItem(ItemStack item, String name, List<String> lores) {
        super(item);
        ItemMeta meta = getItemMeta();
        meta.setDisplayName(color(name));
        List<String> add = new ArrayList<>();
        for (String s : lores) {
            add.add(color(s));
        }
        meta.setLore(add);
        setItemMeta(meta);
    }

    public CustomItem(ItemStack item, List<String> lores) {
        super(item);
        ItemMeta meta = getItemMeta();
        List<String> add = new ArrayList<>();
        for (String s : lores) {
            add.add(color(s));
        }
        meta.setLore(add);
        setItemMeta(meta);
    }

    public CustomItem(ItemStack item, String[] lores) {
        super(item);
        ItemMeta meta = getItemMeta();
        List<String> add = new ArrayList<>();
        for (String s : lores) {
            add.add(color(s));
        }
        meta.setLore(add);
        setItemMeta(meta);
    }

    public CustomItem(ItemStack item, String[] lores, boolean isExtra) {
        super(item);
        ItemMeta meta = getItemMeta();
        List<String> add = new ArrayList<>();
        if (!isExtra) {
            for (String s : lores) {
                add.add(color(s));
            }
        } else {
            add = meta.getLore();
            for (String s : lores) {
                add.add(color(s));
            }
        }
        meta.setLore(add);
        setItemMeta(meta);
    }

    public CustomItem(ItemStack item, List<String> lores, boolean isExtra) {
        super(item);
        ItemMeta meta = getItemMeta();
        List<String> add = new ArrayList<>();
        if (!isExtra) {
            for (String s : lores) {
                add.add(color(s));
            }
        } else {
            add = meta.getLore();
            for (String s : lores) {
                add.add(color(s));
            }
        }
        meta.setLore(add);
        setItemMeta(meta);
    }
    
    public CustomItem(String texture, String name) throws Exception {
        super(CustomSkull.getSkull(texture));
        ItemMeta meta = getItemMeta();
        meta.setDisplayName(color(name));
        setItemMeta(meta);
    }

    public CustomItem(String texture) throws Exception {
        super(CustomSkull.getSkull(texture));
    }

    public CustomItem(String texture, String name, String[] lores) throws Exception {
        super(CustomSkull.getSkull(texture));
        ItemMeta meta = getItemMeta();
        meta.setDisplayName(color(name));
        List<String> all = new ArrayList<>();
        for (String s : lores) {
            all.add(color(s));
        }
        meta.setLore(all);
        meta.setDisplayName(color(name));
        setItemMeta(meta);
    }

    public CustomItem(String texture, String name, List<String> lores) throws Exception {
        super(CustomSkull.getSkull(texture));
        ItemMeta meta = getItemMeta();
        meta.setDisplayName(color(name));
        List<String> all = new ArrayList<>();
        for (String s : lores) {
            all.add(color(s));
        }
        meta.setLore(all);
        meta.setDisplayName(color(name));
        setItemMeta(meta);
    }


    public CustomItem(XMaterial mat, String name, String[] lores) {
        super(mat.parseMaterial());
        ItemMeta meta = getItemMeta();
        List<String> all = new ArrayList<>();
        for (String s : lores) {
            all.add(color(s));
        }
        meta.setLore(all);
        meta.setDisplayName(color(name));
        setItemMeta(meta);
    }

    public CustomItem addFlags(ItemFlag... flags) {
        ItemMeta meta = getItemMeta();
        meta.addItemFlags(flags);
        setItemMeta(meta);
        return this;
    }

    public CustomItem hideAllAttributes() {
        ItemMeta meta = getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
        setItemMeta(meta);
        return this;
    }

    public static boolean isSimilar(ItemStack item1, ItemStack item2) {
        int count = 0; // Has to be 4
        ItemMeta meta1 = item1.getItemMeta();
        ItemMeta meta2 = item2.getItemMeta();

        if (meta1.hasDisplayName() && meta2.hasDisplayName()) {
            if (meta1.getDisplayName().equals(meta2.getDisplayName())) {
                count++;
            }
        }

        if (meta1.hasLore() && meta2.hasLore()) {
            if (meta1.getLore().equals(meta2.getLore())) {
                count++;
            }
        }

        if (item1.getAmount() == item2.getAmount()) {
            count++;
        }

        if (item1.getEnchantments().equals(item2.getEnchantments())) {
            count++;
        }

        return count == 4;
    }

    private String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
