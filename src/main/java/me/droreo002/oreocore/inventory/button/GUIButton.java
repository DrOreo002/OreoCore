package me.droreo002.oreocore.inventory.button;

import com.comphenix.protocol.wrappers.EnumWrappers;
import lombok.Getter;
import lombok.Setter;
import me.droreo002.oreocore.configuration.SerializableConfigVariable;
import me.droreo002.oreocore.inventory.animation.ButtonAnimation;
import me.droreo002.oreocore.utils.entity.PlayerUtils;
import me.droreo002.oreocore.utils.item.CustomItem;
import me.droreo002.oreocore.utils.item.helper.TextPlaceholder;
import me.droreo002.oreocore.utils.misc.SoundObject;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GUIButton implements SerializableConfigVariable<GUIButton>, Cloneable {

    public static final ButtonListener CLOSE_LISTENER = e -> PlayerUtils.closeInventory((Player) e.getWhoClicked());

    @Getter @Setter
    private int inventorySlot;
    @Getter @Setter
    private ButtonAnimation buttonAnimation;
    @Getter
    private boolean animated;
    @Getter
    private TextPlaceholder textPlaceholder;
    @Getter
    private ConfigurationSection itemDataSection;
    @Getter
    private ItemStack item;
    @Getter
    private ButtonListener listener;
    @Getter
    private SoundObject soundOnClick;

    /*
    For config getting
     */
    public GUIButton() { }

    /**
     * Construct a new gui button (without slot)
     *
     * @param item The item
     */
    public GUIButton(ItemStack item) {
        this.item = item;
    }

    /**
     * Get gui button from configuration section, this will also accept button slot
     * the data key will be 'slot'
     *
     * @param section The section
     * @param textPlaceholder The TextPlaceholder to replace placeholder on item data
     */
    public GUIButton(ConfigurationSection section, TextPlaceholder textPlaceholder) {
        this.item = CustomItem.fromSection(section, textPlaceholder);
        this.inventorySlot = section.getInt("slot", 0);
        this.textPlaceholder = textPlaceholder;
        this.itemDataSection = section;
        setAnimated(section.getBoolean("animated", false));

        if (section.getConfigurationSection("soundOnClick") != null)
            this.soundOnClick = new SoundObject().getFromConfig(section.getConfigurationSection("soundOnClick"));
    }

    /**
     * Construct a new gui button
     *
     * @param item The item
     * @param inventorySlot The slot of this button
     */
    public GUIButton(ItemStack item, int inventorySlot) {
        this.item = item;
        this.inventorySlot = inventorySlot;
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
        ItemMeta lastMeta = this.item.getItemMeta();
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
        this.textPlaceholder = textPlaceholder;
        if (item == null) {
            setItem(CustomItem.fromSection(itemDataSection, textPlaceholder), true, true);
        } else {
            setItem(textPlaceholder.format(item), true, true);
        }
    }

    /**
     * Set the listener
     *
     * @param listener The on click listener
     * @return The GUIButton
     */
    public GUIButton setListener(ButtonListener listener) {
        this.listener = listener;
        return this;
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
            if (itemDataSection != null) {
                this.buttonAnimation = new ButtonAnimation(itemDataSection, item);
            } else {
                this.buttonAnimation = new ButtonAnimation(item);
            }
            this.buttonAnimation.setupAnimation(this, true);
        } else {
            this.buttonAnimation = null;
        }
    }

    @Override
    public GUIButton getFromConfig(ConfigurationSection section) {
        return new GUIButton(section, null);
    }

    @Override
    public void saveToConfig(String path, FileConfiguration config) {
        // TODO: 08/08/2019 Save to config
    }

    @Override
    public GUIButton clone() {
        try {
            GUIButton b = (GUIButton) super.clone();
            b.setItem(b.getItem().clone(), true, true);
            return b;
        } catch (CloneNotSupportedException e) {
            throw new Error(e);
        }
    }

    public interface ButtonListener {

        /**
         * Called when button is clicked
         *
         * @param e The inventory click event
         */
        void onClick(InventoryClickEvent e);
    }
}
