package me.droreo002.oreocore.utils.item;

import me.droreo002.oreocore.configuration.SerializableConfigVariable;
import me.droreo002.oreocore.enums.XMaterial;
import me.droreo002.oreocore.utils.item.complex.UMaterial;
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

    public static final ItemStack LBLUE_GLASSPANE = new CustomItem(UMaterial.BLUE_STAINED_GLASS_PANE.getItemStack(), ".");
    public static final ItemStack PURPLE_GLASSPANE = new CustomItem(UMaterial.PURPLE_STAINED_GLASS_PANE.getItemStack(), ".");
    public static final ItemStack GRAY_GLASSPANE = new CustomItem(UMaterial.GRAY_STAINED_GLASS_PANE.getItemStack(), ".");
    public static final ItemStack BLUE_GLASSPANE = new CustomItem(UMaterial.BLUE_STAINED_GLASS_PANE.getItemStack(), ".");

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
     * Add a display name
     *
     * @param display : The display name to add\
     *
     * @return the modified CustomItem
     */
    public CustomItem addDisplayName(String display) {
        ItemMeta meta = getItemMeta();
        if (meta.hasDisplayName()) {
            meta.setDisplayName(color(meta.getDisplayName() + display));
        } else {
            meta.setDisplayName(color(display));
        }
        setItemMeta(meta);
        return this;
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
        if (getType().equals(UMaterial.PLAYER_HEAD.getMaterial())) {
            String texture = CustomSkull.getTexture(this);
            if (!texture.equals("")) {
                config.set(path + ".texture", texture);
            }
        }
        config.set(path + ".glow", false);
    }

    /**
     * Check if the item is similar, the isSimilar method is buggy so we use this
     *
     * @param one: The item 1
     * @param two : The item 2
     * @return true if both item is the same, false otherwise
     */
    public static boolean isSimilar(ItemStack one, ItemStack two) {

        if (one == null || two == null) {
            return one == two;
        }
        if (one.isSimilar(two)) {
            return true;
        }

        // Additional checks as serialisation and de-serialisation might lead to different item meta
        // This would only be done if the items share the same item meta type so it shouldn't be too inefficient
        // Special check for books as their pages might change when serialising (See SPIGOT-3206)
        // Special check for explorer maps/every item with a localised name (See SPIGOT-4672)
        return one.getType() == two.getType()
                && one.getDurability() == two.getDurability()
                && one.getData().equals(two.getData())
                && one.hasItemMeta() && two.hasItemMeta()
                && one.getItemMeta().getClass() == two.getItemMeta().getClass()
                && one.getItemMeta().serialize().equals(two.getItemMeta().serialize());
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
     *  hide-att > Should we hide the item's attribute?. Default is true. This will ide enchant, attribute, and unbreakable tag
     *
     * @param placeholder : The placeholder, leave null for no placeholder. This will try to replace the specified editable enum
     *                    into the specified string from the TextPlaceholder class
     * @param section : The section
     * @return a new ItemStack if its valid section. This is a non nullable method
     */
    @SuppressWarnings("deprecation")
    public static ItemStack fromSection(ConfigurationSection section, TextPlaceholder placeholder) {
        if (section == null) throw new NullPointerException("Section cannot be null!");
        if (!section.contains("material")) throw new NullPointerException("Section must have material key!");

        String material = section.getString("material", "DIRT");
        int materialDurr = section.getInt("itemID", -1);
        int amount = section.getInt("amount", 1);
        boolean glow = section.getBoolean("glow", false);
        boolean hideAtt = section.getBoolean("hide-att", true);
        String texture = section.getString("texture");
        StringBuilder displayName = new StringBuilder(section.getString("name", " "));
        List<String> lore = section.getStringList("lore");

        if (material.equals(UMaterial.AIR.getMaterial().toString())) return new ItemStack(UMaterial.AIR.getMaterial());
        applyPlaceholder(placeholder, displayName, lore);

        UMaterial uMaterial = UMaterial.match(material);
        if (uMaterial == null) throw new NullPointerException("Cannot find material with the ID of " + material);
        ItemStack res;
        if (materialDurr != -1) {
            res = new ItemStack(uMaterial.getMaterial(), amount, (short) materialDurr);
        } else {
            res = new ItemStack(uMaterial.getMaterial(), amount);
        }

        if (material.equals(UMaterial.PLAYER_HEAD.getMaterial().toString())) {
            if (texture != null) {
                res = CustomSkull.setTexture(res, texture);
            }
        }

        ItemMeta meta = res.getItemMeta();
        if (meta == null) return res;
        if (displayName != null) meta.setDisplayName(color(displayName.toString()));
        meta.setLore(lore.stream().map(StringUtils::color).collect(Collectors.toList()));
        if (glow) meta.setUnbreakable(true);
        if (hideAtt) meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
        res.setItemMeta(meta);

        return res;
    }

    /**
     * Apply the sections thing into the specified ItemStack. Will only apply display name, and lore
     *
     * @param item : The item to change
     * @param section : The section of values needed
     * @param placeholder : The placeholder, leave null for no placeholder. This will try to replace the specified editable enum
     *                     into the specified string from the TextPlaceholder class
     * @return a new ItemStack if its valid section. This is a non nullable method
     */
    public static ItemStack applyFromSection(ItemStack item, ConfigurationSection section, TextPlaceholder placeholder) {
        if (section == null) throw new NullPointerException("Section cannot be null!");

        StringBuilder displayName = new StringBuilder(section.getString("name"));
        List<String> lore = (section.getStringList("lore") == null) ? new ArrayList<>() : section.getStringList("lore");
        applyPlaceholder(placeholder, displayName, lore);

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        if (displayName != null) meta.setDisplayName(color(displayName.toString()));
        meta.setLore(lore.stream().map(StringUtils::color).collect(Collectors.toList()));
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
        item.setItemMeta(meta);

        return item;
    }

    /**
     * Apply placeholder to item
     *
     * @param item The item
     * @param placeholder The placeholder
     * @return The modified item
     */
    public static ItemStack applyPlaceholder(ItemStack item, TextPlaceholder placeholder) {
        StringBuilder displayName = new StringBuilder(ItemUtils.getName(item, true));
        List<String> lore = ItemUtils.getLore(item, false);
        applyPlaceholder(placeholder, displayName, lore);

        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        if (displayName != null) meta.setDisplayName(color(displayName.toString()));
        meta.setLore(lore.stream().map(StringUtils::color).collect(Collectors.toList()));
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
        item.setItemMeta(meta);

        return item;
    }

    /**
     * Apply the placeholder
     *
     * @param placeholder The placeholder
     * @param displayName The item display name
     * @param lore The item lore
     */
    private static void applyPlaceholder(TextPlaceholder placeholder, StringBuilder displayName, List<String> lore) {
        if (placeholder != null) {
            for (TextPlaceholder place : placeholder.getPlaceholders()) {
                boolean doLore = false;
                boolean doDisplay = false;

                switch (place.getType()) {
                    case DISPLAY_AND_LORE:
                        doLore = true;
                        doDisplay = true;
                        break;
                    case DISPLAY_NAME:
                        doDisplay = true;
                        break;
                    case LORE:
                        doLore = true;
                        break;
                    default: break;
                }

                if (doLore) {
                    if (!lore.isEmpty()) {
                        for (TextPlaceholder t : place.getPlaceholders()) {
                            List<String> toAsLore = ListUtils.toList(t.getTo(), ListUtils.DEFAULT_SPLIT_MARK);
                            if (!toAsLore.isEmpty()) {
                                List<Integer> index = new ArrayList<>();
                                for (int i = 0; i < lore.size(); i++) {
                                    final String s = lore.get(i);
                                    if (s.contains(t.getFrom())) {
                                        lore.set(i, s.replace(t.getFrom(), ""));
                                        index.add(i);
                                    }
                                }

                                for (int i : index) {
                                    int start = i + 1;
                                    try {
                                        lore.addAll(start, toAsLore);
                                    } catch (IndexOutOfBoundsException e) {
                                        lore.add(" ");
                                        lore.addAll(start, toAsLore);
                                    }
                                }
                            } else {
                                List<String> def = new ArrayList<>(lore);
                                lore.clear();
                                lore.addAll(def.stream().map(s -> {
                                    if (s.contains(t.getFrom())) return s.replace(t.getFrom(), t.getTo());
                                    return s;
                                }).collect(Collectors.toList()));
                            }
                        }
                    }
                }
                if (doDisplay) {
                    if (displayName != null) {
                        String displayAsString = displayName.toString();
                        for (TextPlaceholder t : place.getPlaceholders()) {
                            displayAsString = displayAsString.replace(t.getFrom(), t.getTo());
                        }
                        displayName.delete(0, displayName.length()); // Clear the StringBuilder
                        displayName.append(displayAsString);
                    }
                }
            }
        }
    }

    /**
     * Check if item is empty or not
     *
     * @param item Item to check
     * @return true if empty, false otherwise
     */
    public static boolean isEmpty(ItemStack item) {
        return (item == null) || item.getType().equals(UMaterial.AIR.getMaterial());
    }
}
