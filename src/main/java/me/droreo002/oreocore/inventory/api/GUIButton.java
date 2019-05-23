package me.droreo002.oreocore.inventory.api;

import lombok.Getter;
import lombok.Setter;
import me.droreo002.oreocore.inventory.api.animation.IButtonFrame;
import me.droreo002.oreocore.utils.entity.PlayerUtils;
import me.droreo002.oreocore.utils.item.helper.ItemMetaType;
import me.droreo002.oreocore.utils.misc.SoundObject;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GUIButton {

    public static final ButtonListener CLOSE_LISTENER = e -> PlayerUtils.closeInventory((Player) e.getWhoClicked());

    @Getter @Setter
    private ItemStack item;
    @Getter @Setter
    private boolean animated;
    @Getter @Setter
    private boolean repeatingAnimation;
    @Getter @Setter
    private int nextFrame;
    @Getter @Setter
    private int inventorySlot;
    @Getter
    private ButtonListener listener;
    @Getter
    private SoundObject soundOnClick;
    @Getter
    private final List<IButtonFrame> frames;
    @Getter
    private final Map<ItemMetaType, Object> firstState;

    public GUIButton(ItemStack item) {
        this.item = item;
        this.inventorySlot = 0;
        this.frames = new ArrayList<>();
        this.animated = false;
        this.nextFrame = 0;
        this.firstState = new HashMap<>();

        if (item.hasItemMeta()) {
            if (item.getItemMeta().hasDisplayName()) firstState.put(ItemMetaType.DISPLAY_NAME, item.getItemMeta().getDisplayName());
            if (item.getItemMeta().hasLore()) firstState.put(ItemMetaType.LORE, item.getItemMeta().getLore());
        }
    }

    public GUIButton(ItemStack item, int inventorySlot) {
        this.item = item;
        this.inventorySlot = inventorySlot;
        this.frames = new ArrayList<>();
        this.animated = false;
        this.nextFrame = 0;
        this.firstState = new HashMap<>();

        if (item.hasItemMeta()) {
            if (item.getItemMeta().hasDisplayName()) firstState.put(ItemMetaType.DISPLAY_NAME, item.getItemMeta().getDisplayName());
            if (item.getItemMeta().hasLore()) firstState.put(ItemMetaType.LORE, item.getItemMeta().getLore());
        }
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

    public void addFrame(IButtonFrame buttonFrame) {
        if (!animated) this.animated = true;
        if (frames.isEmpty()) { // If first add, append this one first
            if (!firstState.isEmpty()) { // If not empty, we proceed
                frames.add(new IButtonFrame() {
                    @Override
                    public String nextDisplayName(String prev) {
                        return (String) firstState.get(ItemMetaType.DISPLAY_NAME);
                    }

                    @Override
                    public List<String> nextLore(List<String> prev) {
                        return (List<String>) firstState.get(ItemMetaType.LORE);
                    }

                    @Override
                    public ItemMetaType toUpdate() {
                        if (firstState.containsKey(ItemMetaType.LORE) && firstState.containsKey(ItemMetaType.DISPLAY_NAME)) return ItemMetaType.DISPLAY_AND_LORE;
                        if (firstState.containsKey(ItemMetaType.LORE)) return ItemMetaType.LORE;
                        if (firstState.containsKey(ItemMetaType.DISPLAY_NAME)) return ItemMetaType.DISPLAY_NAME;
                        return ItemMetaType.NONE;
                    }
                });
            }
        }
        frames.add(buttonFrame);
    }

    public GUIButton setListener(ButtonListener listener) {
        this.listener = listener;
        return this;
    }

    public GUIButton setSoundOnClick(SoundObject soundOnClick) {
        this.soundOnClick = soundOnClick;
        return this;
    }

    public interface ButtonListener {
        void onClick(InventoryClickEvent e);
    }
}
