package me.droreo002.oreocore.inventory.button;

import lombok.Getter;
import lombok.Setter;
import me.droreo002.oreocore.configuration.SerializableConfigVariable;
import me.droreo002.oreocore.inventory.animation.IButtonFrame;
import me.droreo002.oreocore.utils.bridge.ServerUtils;
import me.droreo002.oreocore.utils.entity.PlayerUtils;
import me.droreo002.oreocore.utils.item.CustomItem;
import me.droreo002.oreocore.utils.item.complex.UMaterial;
import me.droreo002.oreocore.utils.item.helper.ItemMetaType;
import me.droreo002.oreocore.utils.item.helper.TextPlaceholder;
import me.droreo002.oreocore.utils.misc.SoundObject;
import me.droreo002.oreocore.utils.strings.StringUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GUIButton implements SerializableConfigVariable<GUIButton> {

    public static final ButtonListener CLOSE_LISTENER = e -> PlayerUtils.closeInventory((Player) e.getWhoClicked());
    public static final GUIButton DEFAULT_BACK_BUTTON = new GUIButton(new CustomItem(UMaterial.ARROW.getItemStack(), "&aBack"));
    public static final GUIButton DEFAULT_NEXT_BUTTON = new GUIButton(new CustomItem(UMaterial.ARROW.getItemStack(), "&aNext"));

    @Getter @Setter
    private boolean animated;
    @Getter @Setter
    private boolean repeatingAnimation;
    @Getter @Setter
    private int inventorySlot;
    @Getter
    private TextPlaceholder textPlaceholder;
    @Getter
    private ConfigurationSection itemDataSection;
    @Getter
    private ItemStack item;
    @Setter
    private int nextFrame;
    @Getter
    private ButtonListener listener;
    @Getter
    private SoundObject soundOnClick;
    @Getter
    private final List<IButtonFrame> frames;
    @Getter
    private final Map<String, Object> firstState;

    /*
    For config getting
     */
    public GUIButton() {
        this.frames = new ArrayList<>();
        this.firstState = new HashMap<>();
    }

    /**
     * Construct a new gui button (without slot)
     *
     * @param item The item
     */
    public GUIButton(ItemStack item) {
        this.item = item;
        this.inventorySlot = 0;
        this.frames = new ArrayList<>();
        this.animated = false;
        this.nextFrame = 0;
        this.firstState = new HashMap<>();

        updateFirstState();
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
        this.inventorySlot = section.getInt("slot");
        this.textPlaceholder = textPlaceholder;
        this.itemDataSection = section;
        this.frames = new ArrayList<>();
        this.nextFrame = 0;
        this.firstState = new HashMap<>();
        this.animated = section.getBoolean("animated", false);
        if (section.getConfigurationSection("soundOnClick") != null)
            this.soundOnClick = new SoundObject().getFromConfig(section.getConfigurationSection("soundOnClick"));

        updateFirstState();
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
        this.frames = new ArrayList<>();
        this.animated = false;
        this.nextFrame = 0;
        this.firstState = new HashMap<>();

        updateFirstState();
    }

    /**
     * Get the next frame, will return null if nextFrame is last one already
     *
     * @return the next frame. Will clear the nextFrame back to 0 if already reached max and this is a repeating animation
     */
    public IButtonFrame getNextFrame() {
        if (nextFrame >= frames.size()) {
            if (repeatingAnimation) {
                this.nextFrame = 0;
                return getNextFrame();
            } else {
                return null;
            }
        }
        IButtonFrame frm = frames.get(nextFrame);
        nextFrame++;
        return frm;
    }

    /**
     * Get the current frame
     *
     * @return the frame if available, null otherwise
     */
    public IButtonFrame getCurrentFrame() {
        return frames.get(nextFrame);
    }

    /**
     * Add a frame into the Button
     *
     * @param buttonFrame the Button frame to add
     */
    public void addFrame(IButtonFrame buttonFrame, boolean addFirstState) {
        if (!animated) this.animated = true;
        if (addFirstState) {
            if (frames.isEmpty()) { // If first add, append this one first
                if (!firstState.isEmpty()) { // If not empty, we proceed adding default value first
                    frames.add(new IButtonFrame() {
                        @Override
                        public String nextDisplayName(String prev) {
                            String next = (String) firstState.get(ItemMetaType.DISPLAY_NAME.name());
                            ServerUtils.broadcast(StringUtils.color(next), null);
                            return (next == null || next.isEmpty()) ? null : next;
                        }

                        @Override
                        public List<String> nextLore(List<String> prev) {
                            List<String> next = (List<String>) firstState.get(ItemMetaType.LORE.name());
                            return (next == null || next.isEmpty()) ? null : next;
                        }

                        @Override
                        public Material nextMaterial() {
                            return (Material) firstState.get("MATERIAL");
                        }
                    });
                }
            }
        }
        // And then add the next one
        frames.add(buttonFrame);
    }

    /**
     * Set the item for this button
     *
     * @param item The new item
     * @param updateFirstState Should we update the first state?
     */
    public void setItem(ItemStack item, boolean updateFirstState) {
        this.item = item;
        if (updateFirstState) updateFirstState();
    }

    /**
     * Set the text placeholder for this button
     *
     * @param textPlaceholder The text placeholder
     */
    public void applyTextPlaceholder(TextPlaceholder textPlaceholder) {
        this.textPlaceholder = textPlaceholder;
        if (item == null) {
            setItem(CustomItem.fromSection(itemDataSection, textPlaceholder), true);
        } else {
            setItem(CustomItem.applyPlaceholder(item, textPlaceholder), true);
        }
    }

    /**
     * Update the first state of the item
     */
    private void updateFirstState() {
        firstState.clear();
        if (item.hasItemMeta()) {
            if (item.getItemMeta().hasDisplayName()) firstState.put(ItemMetaType.DISPLAY_NAME.name(), item.getItemMeta().getDisplayName());
            if (item.getItemMeta().hasLore()) firstState.put(ItemMetaType.LORE.name(), item.getItemMeta().getLore());
        }
        firstState.put("MATERIAL", item.getType());
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

    @Override
    public GUIButton getFromConfig(ConfigurationSection section) {
        return new GUIButton(section, null);
    }

    @Override
    public void saveToConfig(String path, FileConfiguration config) {
        config.set(path, itemDataSection);
        config.set(path + ".animated", animated);
        config.set(path + ".repeatingAnimation", repeatingAnimation);
        if (soundOnClick != null) soundOnClick.saveToConfig(path + ".soundOnClick", config);
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
