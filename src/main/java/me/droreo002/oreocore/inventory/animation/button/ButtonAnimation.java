package me.droreo002.oreocore.inventory.animation.button;

import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.List;

/**
 * A representation of ButtonAnimation
 * every button animation also must have static
 * method of build() that returns itself
 */
public abstract class ButtonAnimation {

    public static final String UNSPECIFIC = "UnspecificAnimation";

    @Getter
    private String buttonAnimationName;
    @Getter
    private LinkedList<IButtonFrame> animationFrames;
    @Getter
    private int animationSpeed;
    @Getter
    private boolean repeating;

    public ButtonAnimation(String buttonAnimationName) {
        this.buttonAnimationName = buttonAnimationName;
        this.animationSpeed = 1;
        this.animationFrames = new LinkedList<>();
    }

    /**
     * Initialize this frame
     *
     * @param buttonItem The item that will get affected
     */
    public abstract void initializeFrame(ItemStack buttonItem);

    /**
     * Get frame on that slot
     *
     * @param slot The frame slot
     * @return IButtonFrame
     */
    public IButtonFrame getFrame(int slot) {
        return this.animationFrames.get(slot);
    }

    /**
     * Get the frame size
     *
     * @return Frame size
     */
    public int getFrameSize() {
        return this.animationFrames.size();
    }

    /**
     * Add a frame into the Button with default param
     * value
     *
     * @param buttonFrame The button frame
     * @return ButtonAnimation
     */
    public ButtonAnimation addFrame(IButtonFrame buttonFrame) {
        animationFrames.add(buttonFrame);
        return this;
    }

    /**
     * Set the animation speed or frame duplicates
     * will do the frame duplication automatically
     *
     * @param animationSpeed The animation speed, the greater the longer animation you get
     * @return ButtonAnimation
     */
    public ButtonAnimation setAnimationSpeed(int animationSpeed) {
        if (animationSpeed == 0) throw new IllegalStateException("Animation speed cannot be null!");
        this.animationSpeed = animationSpeed;
        List<IButtonFrame> newFrame = new LinkedList<>();
        for (IButtonFrame frame : this.animationFrames) {
            for (int i = 0; i < animationSpeed; i++) {
                newFrame.add(frame);
            }
        }
        this.animationFrames.clear();
        this.animationFrames.addAll(newFrame);
        return this;
    }

    /**
     * Set this as a repeating animation
     *
     * @param repeating Value
     * @return ButtonAnimation
     */
    public ButtonAnimation setRepeating(boolean repeating) {
        this.repeating = repeating;
        return this;
    }

    /**
     * Build the frames
     *
     * @return A LinkedList of IButtonFrame
     */
    public LinkedList<IButtonFrame> buildFrames() {
        LinkedList<IButtonFrame> result = new LinkedList<>();
        // Duplicate the frames
        for (IButtonFrame frame : this.animationFrames) {
            for (int i = 0; i < this.animationSpeed; i++) {
                result.add(frame);
            }
        }
        return result;
    }
}
