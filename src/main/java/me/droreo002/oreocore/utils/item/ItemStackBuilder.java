package me.droreo002.oreocore.utils.item;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import me.droreo002.oreocore.configuration.SerializableConfigVariable;
import me.droreo002.oreocore.utils.bridge.NBTEditor;
import me.droreo002.oreocore.utils.bridge.ServerUtils;
import me.droreo002.oreocore.utils.item.complex.UMaterial;
import me.droreo002.oreocore.utils.item.helper.TextPlaceholder;
import me.droreo002.oreocore.utils.list.ListUtils;
import me.droreo002.oreocore.utils.misc.SimpleCallback;
import me.droreo002.oreocore.utils.strings.StringUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static me.droreo002.oreocore.utils.strings.StringUtils.*;

/**
 * Define custom item,
 * custom item is useful for building simple item
 * without the hassle of modifying ItemMeta
 * ItemStackBuilder also accept from config with read-able keys
 */
public class ItemStackBuilder implements SerializableConfigVariable, Cloneable {

    public static final ItemStack GRAY_GLASS_PANE = ItemStackBuilder.of(UMaterial.GRAY_STAINED_GLASS_PANE.getItemStack()).getItemStack();

    @Getter @Setter
    @NotNull
    private ItemStack itemStack;
    @Getter
    @Nullable
    private String headTexture, headTextureUrl;
    @Getter @Setter
    @NotNull
    private List<ItemStackBuilderCondition> builderConditions;

    /**
     * Create new custom item
     *
     * @param item The ItemStack
     */
    private ItemStackBuilder(@NotNull ItemStack item) {
        this.itemStack = item;
        this.builderConditions = new ArrayList<>();
    }

    /**
     * Make a new item stack builder
     *
     * @param item The starting item
     * @return ItemStackBuilder
     */
    public static ItemStackBuilder of(@NotNull ItemStack item) {
        return new ItemStackBuilder(item);
    }

    /**
     * Make a new item stack builder
     *
     * @param material The material
     * @return ItemStackBuilder
     */
    public static ItemStackBuilder of(@NotNull Material material) {
        return new ItemStackBuilder(new ItemStack(material));
    }

    /**
     * Add flag to the builder
     *
     * @param flags The flags to add
     * @return the modified ItemStackBuilder
     */
    public ItemStackBuilder addFlags(@NotNull ItemFlag... flags) {
        ItemMeta meta = getItemMeta();
        meta.addItemFlags(flags);
        return setItemMeta(meta);
    }

    /**
     * Add flags to builder
     *
     * @param flagStringList The flag string list
     * @return ItemStackBuilder
     */
    public ItemStackBuilder addFlags(List<String> flagStringList) {
        for (String s : flagStringList) {
            try {
                addFlags(ItemFlag.valueOf(s));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return this;
    }

    /**
     * Hide all attributes for the item, it should just preview
     * the item name and lore when hovering
     *
     * @return the modified ItemStackBuilder
     */
    public ItemStackBuilder hideAllAttributes() {
        return addFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
    }

    /**
     * Add a display name
     *
     * @param display : The display name to add
     *
     * @return the modified ItemStackBuilder
     */
    public ItemStackBuilder setDisplayName(@NotNull String display) {
        ItemMeta meta = getItemMeta();
        meta.setDisplayName(color(display));
        return setItemMeta(meta);
    }

    /**
     * Set the lore for this builder
     *
     * @param lore The item lore
     * @return ItemStackBuilder
     */
    public ItemStackBuilder setLore(@NotNull String... lore) {
        return setLore(ListUtils.convertArrayToList(lore));
    }

    /**
     * Set the lore for this builder
     *
     * @param lore The item lore
     * @return ItemStackBuilder
     */
    public ItemStackBuilder setLore(@NotNull List<String> lore) {
        ItemMeta meta = getItemMeta();
        lore = ListUtils.color(lore);
        meta.setLore(lore);
        return setItemMeta(meta);
    }

    /**
     * Add another lore entry
     *
     * @param lore The lore to add
     * @return ItemStackBuilder
     */
    public ItemStackBuilder addLore(@NotNull String... lore) {
        ItemMeta meta = getItemMeta();
        if (meta.getLore() == null) {
            return setLore(lore);
        } else {
            // INFO: DOESN'T MAKE SENSE IK. JUST DO NOT CHANGE OR 1.8 WOULDN'T LIKE IT
            List<String> add = new ArrayList<>();
            List<String> actual = meta.getLore();
            Collections.addAll(add, lore);
            actual.addAll(add);

            return setLore(actual);
        }
    }

    /**
     * Set the material for this builder
     *
     * @param material The material
     * @return ItemStackBuilder
     */
    public ItemStackBuilder setMaterial(@NotNull Material material) {
        this.itemStack.setType(material);
        return this;
    }

    /**
     * Check if the item is similar
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
     * Set the head texture of this builder
     *
     * @param headTexture The head texture
     */
    public void setHeadTexture(@Nullable String headTexture) {
        if (!isPlayerHead()) throw new IllegalStateException("Not a player head!");
        this.headTexture = headTexture;
    }

    /**
     * Set the head texture url of this builder
     *
     * @param headTextureUrl The head texture url
     */
    public void setHeadTextureUrl(@Nullable String headTextureUrl) {
        if (!isPlayerHead()) throw new IllegalStateException("Not a player head!");
        this.headTextureUrl = headTextureUrl;
    }

    /**
     * Add a new builder condition to this builder
     *
     * @param builderCondition The builder condition
     * @return ItemStackBuilder
     */
    public ItemStackBuilder addBuilderCondition(ItemStackBuilderCondition builderCondition) {
        this.builderConditions.add(builderCondition);
        return this;
    }

    /**
     * Apply a text placeholder to this builder
     *
     * @param textPlaceholder The text placeholder
     * @return ItemStackBuilder
     */
    public ItemStackBuilder applyTextPlaceholder(TextPlaceholder textPlaceholder) {
        this.itemStack = textPlaceholder.format(this.itemStack);
        return this;
    }

    /**
     * Apply the condition. Will be retrieved via name
     *
     * @param conditionName The condition name
     * @param conditionValue The condition value
     * @param onConditionApplied Called when the condition is successfully applied
     * @return ItemStackBuilder
     */
    public ItemStackBuilder applyBuilderCondition(String conditionName, boolean conditionValue, @Nullable SimpleCallback<Void> onConditionApplied) {
        ItemStackBuilderCondition builderCondition = this.builderConditions.stream().filter(c -> c.getConditionName().equalsIgnoreCase(conditionName)).findAny().orElse(null);
        if (builderCondition == null) throw new NullPointerException("Cannot find builder condition with the name of " + conditionName);
        return builderCondition.applyCondition(this, conditionValue, onConditionApplied);
    }

    /**
     * Set unbreakable or not
     *
     * @param unbreakable Condition
     * @return ItemStackBuilder
     */
    public ItemStackBuilder setUnbreakable(boolean unbreakable) {
        ItemMeta meta = getItemMeta();
        if (ServerUtils.isOldAsFuckVersion()) {
            try {
                this.itemStack = NBTEditor.set(this.itemStack, (byte) 1, "Unbreakable");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return this;
        } else {
            meta.setUnbreakable(unbreakable);
            return setItemMeta(meta);
        }
    }

    /**
     * Finally build. Or you can use {@link ItemStackBuilder#getItemStack()}
     *
     * @return Result item stack
     */
    public ItemStack build() {
        return this.itemStack;
    }

    /**
     * Check if this builder's item is a
     * player head or not
     *
     * @return true if player head, false otherwise
     */
    public boolean isPlayerHead() {
        return this.itemStack.getType().name().contains("PLAYER_HEAD");
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        ItemMeta meta = getItemMeta();
        map.put("material", itemStack.getType().name());

        if (meta.hasDisplayName()) map.put("name", getItemMeta().getDisplayName());
        if (meta.hasLore()) map.put("lore", getItemMeta().getLore());
        if (meta.isUnbreakable()) map.put("unbreakable", true);
        if (!meta.getItemFlags().isEmpty()) {
            List<String> flagAsList = meta.getItemFlags().stream().map(Enum::name).collect(Collectors.toList());
            map.put("itemFlags", flagAsList);
        }
        if (itemStack.getAmount() > 1) map.put("amount", itemStack.getAmount());
        if (headTexture != null) map.put("texture", headTexture);
        if (headTextureUrl != null) map.put("texture-url", headTextureUrl);
        builderConditions.forEach(c -> map.putAll(c.serialize()));
        return map;
    }

    /**
     * Get the item meta
     *
     * @return ItemMeta
     */
    @NotNull
    public ItemMeta getItemMeta() {
        if (this.getItemStack().getItemMeta() == null) throw new NullPointerException("ItemMeta is null!");
        return this.itemStack.getItemMeta();
    }

    /**
     * Set this item's item meta
     *
     * @param meta ItemMeta
     */
    public ItemStackBuilder setItemMeta(ItemMeta meta) {
        this.itemStack.setItemMeta(meta);
        return this;
    }

    /**
     * Clone this item stack builder
     *
     * @return The cloned ItemStackBuilder
     */
    @Override
    public ItemStackBuilder clone() {
        try {
            // Not sure if this will also clone the item. But we want to be safe here
            ItemStackBuilder builder = (ItemStackBuilder) super.clone();
            builder.setItemStack(getItemStack().clone());
            return builder;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Deserialize data from section
     *
     * @param section The section
     * @return ItemStackBuilder
     */
    public static ItemStackBuilder deserialize(ConfigurationSection section) {
        String material = section.getString("material", "DIRT");
        int amount = section.getInt("amount", 1);
        boolean unbreakAble = section.getBoolean("unbreakable", false);
        String texture = section.getString("texture");
        String texture_url = section.getString("texture-url");
        List<String> lore = section.getStringList("lore");
        List<String> itemFlags = section.getStringList("itemFlags");
        List<ConfigurationSection> builderConditions = new ArrayList<>();
        if (itemFlags.isEmpty()) {
            // Add default
            itemFlags.add(ItemFlag.HIDE_ENCHANTS.name());
            itemFlags.add(ItemFlag.HIDE_ATTRIBUTES.name());
            itemFlags.add(ItemFlag.HIDE_UNBREAKABLE.name());
        }
        if (section.isSet("conditions")) {
            for (String s : section.getConfigurationSection("conditions").getKeys(false)) {
                builderConditions.add(section.getConfigurationSection("conditions." + s));
            }
        }

        if (material.equals(UMaterial.AIR.getMaterial().toString())) return new ItemStackBuilder(UMaterial.AIR.getItemStack());

        UMaterial uMaterial = UMaterial.match(material);
        if (uMaterial == null) throw new NullPointerException("Cannot find material with the ID of " + material);
        ItemStack itemStack = uMaterial.getItemStack();
        itemStack.setAmount(amount);

        if (uMaterial.name().contains("PLAYER_HEAD")) {
            if (texture != null) {
                itemStack = CustomSkull.fromHeadTexture(texture);
            }
            if (texture_url != null) {
                itemStack = CustomSkull.fromUrl(texture_url);
            }
        }

        ItemStackBuilder itemStackBuilder = ItemStackBuilder.of(itemStack);
        itemStackBuilder.setUnbreakable(unbreakAble);
        itemStackBuilder.addFlags(itemFlags);
        itemStackBuilder.setDisplayName(section.getString("name", " "));
        itemStackBuilder.setLore(lore);
        for (ConfigurationSection condition : builderConditions) {
            itemStackBuilder.addBuilderCondition(new ItemStackBuilderCondition(condition.getName(), condition));
        }
        
        return itemStackBuilder;
    }

    /**
     * Get from section, the section must have material key name in order to work
     *
     * @param section The section
     * @return ItemStack
     *
     * @deprecated Please use deserialize. Placeholder is also not needed, since you can format placeholder from
     * item already using {@link TextPlaceholder#format(ItemStack)}
     */
    @SuppressWarnings("deprecation")
    @Deprecated
    public static ItemStack fromSection(ConfigurationSection section) {
        return deserialize(section).getItemStack();
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
