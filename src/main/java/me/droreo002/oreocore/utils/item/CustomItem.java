package me.droreo002.oreocore.utils.item;

import me.droreo002.oreocore.enums.XMaterial;
import me.droreo002.oreocore.utils.item.helper.TextPlaceholder;
import me.droreo002.oreocore.utils.list.ListUtils;
import me.droreo002.oreocore.utils.strings.StringUtils;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static me.droreo002.oreocore.utils.strings.StringUtils.*;

@SerializableAs("CustomItem")
public class CustomItem extends ItemStack {

    public static final ItemStack LBLUE_GLASSPANE = new CustomItem(XMaterial.BLUE_STAINED_GLASS_PANE.parseItem(false), ".");
    public static final ItemStack PURPLE_GLASSPANE = new CustomItem(XMaterial.PURPLE_STAINED_GLASS_PANE.parseItem(false), ".");
    public static final ItemStack GRAY_GLASSPANE = new CustomItem(XMaterial.GRAY_STAINED_GLASS_PANE.parseItem(false), ".");
    public static final ItemStack BLUE_GLASSPANE = new CustomItem(XMaterial.BLUE_STAINED_GLASS_PANE.parseItem(false), ".");

    /**
     * Create new custom item
     *
     * @param item : The ItemStack
     */
    public CustomItem(ItemStack item) {
        super(item);
    }

    /**
     * Create new custom item
     *
     * @param item : The ItemStack
     * @param name : The display name for the item
     */
    public CustomItem(ItemStack item, String name) {
        super(item);
        ItemMeta meta = getItemMeta();
        meta.setDisplayName(color(name));
        setItemMeta(meta);
    }

    /**
     * Create new custom item
     *
     * @param item : The ItemStack
     * @param name : The display name for the item
     * @param lores : The lore for the item
     */
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

    /**
     * Create new custom item
     *
     * @param item : The ItemStack
     * @param name : The display name for the item
     * @param lores : The lore for the item
     */
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

    /**
     * Create new custom item
     *
     * @param item : The ItemStack
     * @param lores : The lore for the item
     */
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

    /**
     * Create new custom item
     *
     * @param item : The ItemStack
     * @param lores : The lore for the item
     */
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

    /**
     * Create new custom item
     *
     * @param item : The ItemStack
     * @param lores : The lore for the item
     * @param isExtra : Should we keep the original ore and add the new one?
     */
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

    /**
     * Create new custom item
     *
     * @param item : The ItemStack
     * @param lores : The lore for the item
     * @param isExtra : Should we keep the original ore and add the new one?
     */
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

    /**
     * Add flag to the item
     *
     * @param flags : The flags to add
     * @return the modified CustomItem
     */
    public CustomItem addFlags(ItemFlag... flags) {
        ItemMeta meta = getItemMeta();
        meta.addItemFlags(flags);
        setItemMeta(meta);
        return this;
    }

    /**
     * Hide all attributes for the item, it should just preview
     * the item name and lore when hovering
     *
     * @return the modified CustomItem
     */
    public CustomItem hideAllAttributes() {
        ItemMeta meta = getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
        setItemMeta(meta);
        return this;
    }

    /**
     * Check if the item is similar, the isSimilar method is buggy so we use this
     *
     * @param first: The item 1
     * @param second : The item 2
     * @return true if both item is the same, false otherwise
     */
    public static boolean isSimilar(ItemStack first,ItemStack second) {

        boolean similar = false;

        if (first == null || second == null) return false;
        boolean sameType = (first.getType() == second.getType());
        boolean sameDurability = (first.getDurability() == second.getDurability());
        boolean sameHasItemMeta = (first.hasItemMeta() == second.hasItemMeta());
        boolean sameEnchantments = (first.getEnchantments().equals(second.getEnchantments()));
        boolean sameItemMeta = true;

        if (sameHasItemMeta) {
            sameItemMeta = Bukkit.getItemFactory().equals(first.getItemMeta(), second.getItemMeta());
        }

        if (sameType && sameDurability && sameHasItemMeta && sameEnchantments && sameItemMeta){
            similar = true;
        }

        return similar;
    }

    /**
     * Get from section, the section must have material key name in order to work
     * Available key :
     *  material > Material as string (String)
     *  itemID > The durability or item ID (int)
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
     * @return a new ItemStack if its valid section. This is a non nullable method
     */
    @SuppressWarnings("deprecation")
    public static ItemStack fromSection(ConfigurationSection section, TextPlaceholder placeholder) {
        Validate.notNull(section, "Section cannot be null!");
        if (!section.contains("material")) throw new NullPointerException("Section must have material key!");
        String material = section.getString("material", "DIRT");
        int materialDurr = section.getInt("itemID", -1);
        int amount = section.getInt("amount", 1);
        boolean glow = section.getBoolean("glow", false);
        String texture = section.getString("texture");
        String displayName = section.getString("name");
        List<String> lore = (section.getStringList("lore") == null) ? new ArrayList<>() : section.getStringList("lore");

        if (placeholder != null) {
            for (TextPlaceholder place : placeholder.getPlaceholders()) {
                switch (place.getType()) {
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
                                if (t.isLorePlaceholder()) {
                                    List<Integer> index = new ArrayList<>();
                                    for (int i = 0; i < lore.size(); i++) {
                                        final String s = lore.get(i);
                                        if (s.contains(t.getFrom())) {
                                            lore.set(i, s.replace(t.getFrom(), ""));
                                            index.add(i);
                                        }
                                    }

                                    List<String> lores = ListUtils.toList(t.getTo());
                                    for (int i : index) {
                                        int start = i+1;
                                        try {
                                            lore.addAll(start, lores);
                                        } catch (IndexOutOfBoundsException e) {
                                            lore.add(" ");
                                            lore.addAll(start, lores);
                                        }
                                    }
                                } else {
                                    lore = lore.stream().map(s -> {
                                        if (s.contains(t.getFrom())) return s.replace(t.getFrom(), t.getTo());
                                        return s;
                                    }).collect(Collectors.toList());
                                }
                            }
                        }
                        break;
                    default: break;
                }
            }
        }

        ItemStack res;
        if (materialDurr != -1) {
            res = new ItemStack(XMaterial.fromString(material).parseMaterial(), amount, (short) materialDurr);
        } else {
            res = new ItemStack(XMaterial.fromString(material).parseMaterial(), amount);
        }

        if (material.equalsIgnoreCase(XMaterial.PLAYER_HEAD.parseMaterial().toString())) {
            if (texture != null) {
                res = CustomSkull.setTexture(res, texture);
            }
        }

        ItemMeta meta = res.getItemMeta();
        if (meta == null) return res;
        if (displayName != null) meta.setDisplayName(color(displayName));
        meta.setLore(lore.stream().map(StringUtils::color).collect(Collectors.toList()));
        if (glow) meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
        res.setItemMeta(meta);

        return res;
    }

    /**
     * Convert this CustomItem to a section
     *
     * @param config : The config target
     * @param path : The path to save
     */
    public void toSection(FileConfiguration config, String path) {
        config.set(path + ".material", this.getType().toString());
        config.set(path + ".itemID", this.getDurability());
        config.set(path + ".amount", this.getAmount());
        if (getItemMeta() != null) {
            if (getItemMeta().hasDisplayName()) config.set(path + ".name", getItemMeta().getDisplayName());
            if (getItemMeta().hasLore()) config.set(path + ".lore", getItemMeta().getLore());
        }
        if (getType().equals(XMaterial.PLAYER_HEAD.parseMaterial())) {
            String texture = CustomSkull.getTexture(this);
            if (!texture.equals("")) {
                config.set(path + ".texture", texture);
            }
        }
        config.set(path + ".glow", false);
    }
}
