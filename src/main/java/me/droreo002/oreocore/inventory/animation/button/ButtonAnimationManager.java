package me.droreo002.oreocore.inventory.animation.button;

import lombok.Getter;
import lombok.Setter;
import me.droreo002.oreocore.inventory.button.GUIButton;
import me.droreo002.oreocore.utils.item.complex.XMaterial;
import me.droreo002.oreocore.utils.item.helper.ItemMetaType;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ButtonAnimationManager implements Cloneable {

    @Getter
    private ConfigurationSection buttonAnimationData;
    @Getter
    private int currentFrameSlot;
    @Getter
    private boolean useFirstState;
    @Getter @NotNull
    private ButtonAnimation buttonAnimation;
    @Getter @Setter
    private Map<String, Object> buttonMetaData;

    public ButtonAnimationManager(GUIButton button) {
        this(button.getItem());
    }

    public ButtonAnimationManager(ItemStack buttonItem) {
        this.buttonAnimationData = null;
        this.buttonMetaData = new HashMap<>();
        this.buttonAnimation = new UnspecificAnimation();
        updateButtonMetaData(buttonItem);
    }

    public ButtonAnimationManager(ConfigurationSection buttonDataSection, ItemStack buttonItem) {
        this(buttonItem);
        if (!buttonDataSection.isSet("animationData")) throw new NullPointerException("Button animation data cannot be null!");
        this.buttonAnimationData = buttonDataSection.getConfigurationSection("animationData");
        this.buttonMetaData = new HashMap<>();
        String animationName = buttonAnimationData.getString("animationName");
        this.buttonAnimation = (animationName != null) ? Objects.requireNonNull(ButtonAnimationRegistry.getAnimation(animationName)) : new UnspecificAnimation();
        this.buttonAnimation.initializeFrame(buttonItem);
    }

    /**
     * Update this animation's frames
     */
    public void updateAnimationFrames(ItemStack buttonItem) {
        this.buttonAnimation.getAnimationFrames().clear();
        addFirstState(this.buttonAnimation);
        this.buttonAnimation.initializeFrame(buttonItem);
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
     * Set the button animation
     *
     * @param buttonAnimation The button animation to set
     * @param useFirstState Should use the first state of this button?
     * @return ButtonAnimationManager
     */
    public ButtonAnimationManager setButtonAnimation(ButtonAnimation buttonAnimation, boolean useFirstState) {
        if (useFirstState) {
            this.useFirstState = true;
            addFirstState(buttonAnimation);
        }
        this.buttonAnimation = buttonAnimation;
        return this;
    }

    /**
     * Copy this button's first state
     * this is useful for repeating animation
     */
    public void addFirstState() {
        this.useFirstState = true;
        this.addFirstState(null);
    }

    /**
     * Copy this button's first state
     * this is useful for repeating animation
     *
     * @param buttonAnimation The button animation
     */
    public void addFirstState(@Nullable ButtonAnimation buttonAnimation) {
        if (buttonAnimation == null) buttonAnimation = this.buttonAnimation;
        if (!useFirstState) return;
        if (!buttonMetaData.isEmpty()) {
            buttonAnimation.addFrame(new IButtonFrame() {
                @Override
                public String nextDisplayName(String previousDisplayName) {
                    String next = (String) buttonMetaData.get(ItemMetaType.DISPLAY_NAME.name());
                    return (next == null || next.isEmpty()) ? null : next;
                }

                @Override
                public List<String> nextLore(List<String> previousLore) {
                    List<String> next = (List<String>) buttonMetaData.get(ItemMetaType.LORE.name());
                    return (next == null || next.isEmpty()) ? null : next;
                }

                @Override
                public Material nextMaterial(Material previousMaterial) {
                    Material material = (Material) buttonMetaData.get("MATERIAL");
                    return (material == XMaterial.PLAYER_HEAD.getMaterial()) ? null : material;
                }
            });
        }
    }

    /**
     * Get the next frame, will return null if nextFrame is last one already
     *
     * @return the next frame. Will clear the nextFrame back to 0 if already reached max and this is a repeating animation
     */
    public IButtonFrame getNextFrame() {
        if (this.currentFrameSlot >= this.buttonAnimation.getFrameSize()) {
            if (this.buttonAnimation.isRepeating()) {
                this.currentFrameSlot = 0;
                return getNextFrame();
            } else {
                return null;
            }
        }
        IButtonFrame frm = this.buttonAnimation.getFrame(this.currentFrameSlot);
        this.currentFrameSlot++;
        return frm;
    }

    /**
     * Get the current frame
     *
     * @return the frame if available, null otherwise
     */
    public IButtonFrame getCurrentFrame() {
        return this.buttonAnimation.getFrame(this.currentFrameSlot);
    }

    @Override
    public ButtonAnimationManager clone() {
        try {
            ButtonAnimationManager b = (ButtonAnimationManager) super.clone();

            /*
            Apparently HashMap also need tobe cloned.
             */
            ButtonAnimation newAnimation = ButtonAnimationRegistry.getAnimation(this.buttonAnimation.getButtonAnimationName());
            if (newAnimation == null) newAnimation = new UnspecificAnimation();
            newAnimation.getAnimationFrames().addAll(this.buttonAnimation.getAnimationFrames());
            b.setButtonAnimation(newAnimation, this.useFirstState);
            b.setButtonMetaData(new HashMap<>(this.buttonMetaData));
            return b;
        } catch (CloneNotSupportedException e) {
            throw new Error(e);
        }
    }
}
