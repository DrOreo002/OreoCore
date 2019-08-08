package me.droreo002.oreocore.inventory.animation;

import lombok.Getter;
import lombok.Setter;
import me.droreo002.oreocore.inventory.button.GUIButton;
import me.droreo002.oreocore.utils.bridge.ServerUtils;
import me.droreo002.oreocore.utils.item.helper.ItemMetaType;
import me.droreo002.oreocore.utils.strings.StringUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ButtonAnimation {

    @Getter
    private final ConfigurationSection buttonAnimationData;
    @Getter
    private final String buttonAnimationName;
    @Setter
    private int nextFrame;
    @Getter @Setter
    private Map<String, Object> buttonMetaData;
    @Getter @Setter
    private boolean repeatingAnimation;
    @Getter @Setter
    private List<IButtonFrame> frames;

    public ButtonAnimation(ConfigurationSection buttonDataSection, ItemStack buttonItem) {
        if (!buttonDataSection.isSet("animationData")) throw new NullPointerException("Button animation data cannot be null!");
        this.buttonAnimationData = buttonDataSection.getConfigurationSection("animationData");
        this.nextFrame = 0;
        this.repeatingAnimation = false;
        this.frames = new ArrayList<>();
        this.buttonAnimationName = buttonAnimationData.getString("animationName", "none");
        this.buttonMetaData = new HashMap<>();

        if (buttonAnimationName == null) {
            // TODO: 06/08/2019 Setup animation from raw frames
        }

        updateButtonMetaData(buttonItem);
    }

    public ButtonAnimation(ItemStack buttonItem) {
        this.buttonAnimationName = "none";
        this.buttonAnimationData = null;
        this.nextFrame = 0;
        this.repeatingAnimation = false;
        this.frames = new ArrayList<>();
        this.buttonMetaData = new HashMap<>();

        updateButtonMetaData(buttonItem);
    }

    /**
     * Update the first state of the item
     *
     * @param buttonItem The button item
     */
    public void updateButtonMetaData(ItemStack buttonItem) {
        buttonMetaData.clear();
        if (buttonItem.hasItemMeta()) {
            if (buttonItem.getItemMeta().hasDisplayName()) buttonMetaData.put(ItemMetaType.DISPLAY_NAME.name(), buttonItem.getItemMeta().getDisplayName());
            if (buttonItem.getItemMeta().hasLore()) buttonMetaData.put(ItemMetaType.LORE.name(), buttonItem.getItemMeta().getLore());
        }
        buttonMetaData.put("MATERIAL", buttonItem.getType());
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
        if (addFirstState) {
            if (frames.isEmpty()) { // If first add, append this one first
                if (!buttonMetaData.isEmpty()) { // If not empty, we proceed adding default value first
                    frames.add(new IButtonFrame() {
                        @Override
                        public String nextDisplayName(String prev) {
                            String next = (String) buttonMetaData.get(ItemMetaType.DISPLAY_NAME.name());
                            return (next == null || next.isEmpty()) ? null : next;
                        }

                        @Override
                        public List<String> nextLore(List<String> prev) {
                            List<String> next = (List<String>) buttonMetaData.get(ItemMetaType.LORE.name());
                            return (next == null || next.isEmpty()) ? null : next;
                        }

                        @Override
                        public Material nextMaterial() {
                            return (Material) buttonMetaData.get("MATERIAL");
                        }
                    });
                }
            }
        }
        // And then add the next one
        frames.add(buttonFrame);
    }

    /**
     * Setup animation with default values (will be taken from getButtonAnimationName)
     *
     * @param parent The button parent
     */
    public void setupDefault(GUIButton parent) {
        DefaultButtonAnimation animation = DefaultButtonAnimation.fromString(getButtonAnimationName());
        if (animation != null) {
            ButtonAnimationUtils.addAnimation(parent, animation);
        }
    }
}
