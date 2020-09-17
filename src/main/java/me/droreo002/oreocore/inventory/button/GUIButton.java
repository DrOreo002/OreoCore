package me.droreo002.oreocore.inventory.button;

import lombok.Getter;
import lombok.Setter;
import me.droreo002.oreocore.configuration.SerializableConfigVariable;
import me.droreo002.oreocore.inventory.animation.button.ButtonAnimationManager;
import me.droreo002.oreocore.utils.entity.PlayerUtils;
import me.droreo002.oreocore.utils.item.ItemStackBuilder;
import me.droreo002.oreocore.utils.item.helper.TextPlaceholder;
import me.droreo002.oreocore.utils.misc.SoundObject;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class GUIButton implements SerializableConfigVariable, Cloneable {

    public static final ButtonListener CLOSE_LISTENER = e -> PlayerUtils.closeInventory((Player) e.getWhoClicked());

    @Getter
    private final UUID uniqueId;
    @Getter
    private TextPlaceholder textPlaceholder;
    @Getter
    private ConfigurationSection itemDataSection;
    @Getter
    private SoundObject soundOnClick;
    @Setter
    @Getter
    private List<ButtonListener> buttonListeners;
    @Getter
    @Setter
    private boolean animated;
    @Getter
    @Setter
    private ItemStackBuilder buttonItemStackBuilder;
    @Getter
    @Setter
    private int inventorySlot;
    @Getter
    @Nullable
    private ButtonAnimationManager buttonAnimationManager;
    @Getter
    @Setter
    private int maxListener;

    /**
     * Construct a new gui button
     *
     * @param item The item
     */
    public GUIButton(ItemStack item) {
        this.uniqueId = UUID.randomUUID();
        this.buttonItemStackBuilder = ItemStackBuilder.of(item);
        this.buttonListeners = new ArrayList<>();
    }

    /**
     * Construct a new gui button
     *
     * @param buttonItemStackBuilder The item stack builder
     */
    public GUIButton(ItemStackBuilder buttonItemStackBuilder) {
        this(buttonItemStackBuilder.build());
        this.buttonItemStackBuilder = buttonItemStackBuilder;
    }

    /**
     * Get gui button from configuration section, this will also accept button slot
     * the data key will be 'slot'
     *
     * @param itemDataSection The section
     * @param textPlaceholder The TextPlaceholder to replace placeholder on item data
     */
    public GUIButton(ConfigurationSection itemDataSection, TextPlaceholder textPlaceholder) {
        this(ItemStackBuilder.deserialize(itemDataSection));
        this.inventorySlot = itemDataSection.getInt("slot", 0);
        this.textPlaceholder = textPlaceholder;
        this.itemDataSection = itemDataSection;
        applyTextPlaceholder(textPlaceholder);
        setAnimated(itemDataSection.getBoolean("animated", false));

        if (itemDataSection.getConfigurationSection("soundOnClick") != null)
            this.soundOnClick = SoundObject.deserialize(itemDataSection.getConfigurationSection("soundOnClick"));
        if (itemDataSection.contains("animationData"))
            this.buttonAnimationManager = new ButtonAnimationManager(itemDataSection, getItem());
    }

    /**
     * Construct a new gui button
     *
     * @param item          The item
     * @param inventorySlot The slot of this button
     */
    public GUIButton(ItemStack item, int inventorySlot) {
        this.uniqueId = UUID.randomUUID();
        this.buttonItemStackBuilder = ItemStackBuilder.of(item);
        this.inventorySlot = inventorySlot;
        this.buttonListeners = new ArrayList<>();
    }

    public static GUIButton deserialize(ConfigurationSection section) {
        return new GUIButton(section, null);
    }

    /**
     * Set the item for this button
     *
     * @param item                  The new item
     * @param updateMetaData        Should we update the meta data?, meta data will be changed
     *                              and will use the new item's meta data
     * @param updateAnimationFrames Should we update the animation frames?
     */
    public void setItem(ItemStack item, boolean updateMetaData, boolean updateAnimationFrames) {
        ItemMeta lastMeta = this.getItem().getItemMeta().clone();
        if (animated) {
            if (buttonAnimationManager != null) {
                buttonAnimationManager.updateButtonMetaData(item);
                if (updateAnimationFrames) {
                    buttonAnimationManager.updateAnimationFrames(item);
                }
            }
        }
        if (!updateMetaData) {
            item.setItemMeta(lastMeta);
        }

        if (this.buttonItemStackBuilder == null) {
            this.buttonItemStackBuilder = ItemStackBuilder.of(item);
        } else {
            this.buttonItemStackBuilder.setItemStack(item);
        }
    }

    /**
     * Set the text placeholder for this button
     *
     * @param textPlaceholder The text placeholder
     */
    public void applyTextPlaceholder(TextPlaceholder textPlaceholder) {
        if (textPlaceholder == null) return;
        this.textPlaceholder = textPlaceholder;
        setItem(textPlaceholder.format(getItem()));
    }

    /**
     * Set the sound on click
     *
     * @param soundOnClick The sound
     * @return The GUIButton
     */
    public GUIButton setSoundOnClick(SoundObject soundOnClick) {
        this.soundOnClick = soundOnClick;
        return this;
    }

    /**
     * Add a listener
     *
     * @param buttonListener The ButtonListener to add
     * @return GUIButton, modified.
     */
    public GUIButton addListener(ButtonListener buttonListener) {
        if (this.maxListener != 0 && this.buttonListeners.size() >= this.maxListener)
            throw new IllegalStateException("Max listener has been reached!");
        this.buttonListeners.add(buttonListener);
        this.buttonListeners.sort(Comparator.comparingInt(listener -> listener.getListenerPriority().getLevel()));
        Collections.reverse(this.buttonListeners);
        return this;
    }

    /**
     * Clear the entire button listener
     */
    public void clearListener() {
        this.buttonListeners.clear();
    }

    /**
     * Clone the GUIButton
     *
     * @return The GUIButton
     */
    @Override
    public GUIButton clone() {
        try {
            GUIButton b = (GUIButton) super.clone();

            /*
            Hell yeah hardcode because why not
             */
            b.setButtonListeners(new ArrayList<>(this.buttonListeners));
            ItemStackBuilder clonedBuilder = ItemStackBuilder.of(this.getItem().clone());
            try {
                clonedBuilder.setHeadTexture(this.buttonItemStackBuilder.getHeadTexture());
                clonedBuilder.setHeadTextureUrl(this.buttonItemStackBuilder.getHeadTextureUrl());
            } catch (Exception ignored) {
            }
            clonedBuilder.setBuilderConditions(new ArrayList<>(this.buttonItemStackBuilder.getBuilderConditions()));
            b.setButtonItemStackBuilder(clonedBuilder);

            if (this.buttonAnimationManager != null) {
                b.setButtonAnimationManager(this.buttonAnimationManager.clone());
            }
            return b;
        } catch (CloneNotSupportedException e) {
            throw new Error(e);
        }
    }

    /**
     * Set the button animation
     * this will also automatically set the button as animated
     *
     * @param buttonAnimationManager The button animation to se
     */
    public void setButtonAnimationManager(@Nullable ButtonAnimationManager buttonAnimationManager) {
        if (buttonAnimationManager == null) return;
        this.animated = true;
        this.buttonAnimationManager = buttonAnimationManager;
    }

    /**
     * Get the button item
     *
     * @return The button item
     */
    @NotNull
    public ItemStack getItem() {
        return this.buttonItemStackBuilder.getItemStack();
    }

    /**
     * Set the item for this button
     * will use the default boolean values of {@link GUIButton#setItem(ItemStack, boolean, boolean)}
     *
     * @param item The new item
     */
    public void setItem(ItemStack item) {
        setItem(item, true, true);
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        return new HashMap<>(); // TODO: 25/02/2020 Actually make this?
    }
}
