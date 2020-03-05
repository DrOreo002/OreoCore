package me.droreo002.oreocore.inventory.button;

import lombok.Getter;
import lombok.Setter;
import me.droreo002.oreocore.configuration.SerializableConfigVariable;
import me.droreo002.oreocore.inventory.animation.button.ButtonAnimation;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GUIButton implements SerializableConfigVariable, Cloneable {

    public static final ButtonListener CLOSE_LISTENER = e -> PlayerUtils.closeInventory((Player) e.getWhoClicked());

    @Getter
    private final UUID uniqueId;
    @Getter
    private boolean animated;
    @Getter
    private TextPlaceholder textPlaceholder;
    @Getter
    private ConfigurationSection itemDataSection;
    @Getter
    private ItemStack item;
    @Getter
    private SoundObject soundOnClick;
    @Setter
    private Map<ClickType, List<ButtonListener>> buttonListeners;
    @Getter @Setter
    private int inventorySlot;
    @Getter @Setter
    private ButtonAnimation buttonAnimation;

    /*
    For config getting
     */
    public GUIButton() {
        this.uniqueId = UUID.randomUUID();
        this.buttonListeners = new HashMap<>();
    }

    /**
     * Construct a new gui button (without slot)
     *
     * @param item The item
     */
    public GUIButton(ItemStack item) {
        this.uniqueId = UUID.randomUUID();
        this.item = item;
        this.buttonListeners = new HashMap<>();
    }

    /**
     * Get gui button from configuration section, this will also accept button slot
     * the data key will be 'slot'
     *
     * @param section The section
     * @param textPlaceholder The TextPlaceholder to replace placeholder on item data
     */
    public GUIButton(ConfigurationSection section, TextPlaceholder textPlaceholder) {
        this.uniqueId = UUID.randomUUID();
        this.item = ItemStackBuilder.deserialize(section).getItemStack();
        this.inventorySlot = section.getInt("slot", 0);
        this.textPlaceholder = textPlaceholder;
        this.itemDataSection = section;
        this.buttonListeners = new HashMap<>();
        applyTextPlaceholder(textPlaceholder);
        setAnimated(section.getBoolean("animated", false));

        if (section.getConfigurationSection("soundOnClick") != null)
            this.soundOnClick = SoundObject.deserialize(section.getConfigurationSection("soundOnClick"));
    }

    /**
     * Construct a new gui button
     *
     * @param item The item
     * @param inventorySlot The slot of this button
     */
    public GUIButton(ItemStack item, int inventorySlot) {
        this.uniqueId = UUID.randomUUID();
        this.item = item;
        this.inventorySlot = inventorySlot;
        this.buttonListeners = new HashMap<>();
    }

    /**
     * Set the item for this button
     *
     * @param item The new item
     * @param updateMetaData Should we update the meta data?, meta data will be changed
     *                       and will use the new item's meta data
     * @param updateAnimationFrames Should we update the animation frames?
     */
    public void setItem(ItemStack item, boolean updateMetaData, boolean updateAnimationFrames) {
        ItemMeta lastMeta = this.item.getItemMeta().clone();
        if (animated) {
            buttonAnimation.updateButtonMetaData(item);
            if (updateAnimationFrames) {
                buttonAnimation.setupAnimation(this, true);
            }
        }
        if (!updateMetaData) {
            item.setItemMeta(lastMeta);
        }

        this.item = item;
    }

    /**
     * Set the text placeholder for this button
     *
     * @param textPlaceholder The text placeholder
     */
    public void applyTextPlaceholder(TextPlaceholder textPlaceholder) {
        if (textPlaceholder == null) return;
        this.textPlaceholder = textPlaceholder;
        if (item == null) {
            setItem(textPlaceholder.format(ItemStackBuilder.deserialize(itemDataSection).getItemStack()), true, true);
        } else {
            setItem(textPlaceholder.format(item), true, true);
        }
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
     * Set button as animated or not
     *
     * @param animated Boolean
     */
    public void setAnimated(boolean animated) {
        this.animated = animated;
        if (animated) {
            if (itemDataSection != null && itemDataSection.contains("animationData")) {
                this.buttonAnimation = new ButtonAnimation(itemDataSection, item);
            } else {
                this.buttonAnimation = new ButtonAnimation(item);
            }
            this.buttonAnimation.setupAnimation(this, true);
        } else {
            this.buttonAnimation = null;
        }
    }

    /**
     * Add a listener
     *
     * @param buttonListener The ButtonListener to add
     * @return GUIButton, modified.
     */
    public GUIButton addListener(ButtonListener buttonListener) {
        final ClickType clickType = buttonListener.getClickType();
        if (buttonListeners.containsKey(clickType)) {
            List<ButtonListener> val = buttonListeners.get(clickType);
            val.add(buttonListener);
            buttonListeners.put(clickType, val);
        } else {
            buttonListeners.put(clickType, new ArrayList<>(Collections.singletonList(buttonListener)));
        }
        return this;
    }

    /**
     * Clear the entire button listener
     */
    public void clearListener() {
        this.buttonListeners.clear();
    }

    /**
     * Get the button listener (sorted)
     *
     * @return The button listener
     */
    @NotNull
    public Map<ClickType, List<ButtonListener>> getButtonListeners() {
        Map<ClickType, List<ButtonListener>> newMap = new HashMap<>();
        for (Map.Entry<ClickType, List<ButtonListener>> entry : this.buttonListeners.entrySet()) {
            List<ButtonListener> listeners = entry.getValue();
            // We do not want to change anything if no priority is set to other than default
            if (listeners.stream().anyMatch(bListener -> bListener.getListenerPriority() != ButtonListener.Priority.DEFAULT)) {
                listeners.sort(Comparator.comparingInt(button -> button.getListenerPriority().getLevel()));
                Collections.reverse(listeners);
            }
            newMap.put(entry.getKey(), listeners);
        }
        return newMap;
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
            Apparently HashMap also need tobe cloned.
             */
            b.setButtonListeners(new HashMap<>(this.buttonListeners));
            b.setItem(this.item.clone(), true, true);

            if (this.buttonAnimation != null) {
                b.setButtonAnimation(this.buttonAnimation.clone());
            }
            return b;
        } catch (CloneNotSupportedException e) {
            throw new Error(e);
        }
    }

    public static GUIButton deserialize(ConfigurationSection section) {
        return new GUIButton(section, null);
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        return new HashMap<>(); // TODO: 25/02/2020 Actually make this?
    }
}
