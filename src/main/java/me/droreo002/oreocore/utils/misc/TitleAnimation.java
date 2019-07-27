package me.droreo002.oreocore.utils.misc;

import lombok.Getter;
import lombok.Setter;
import me.droreo002.oreocore.OreoCore;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

public class TitleAnimation {

    @Getter @Setter
    private long animationSpeed;
    @Getter @Setter
    private boolean repeatingAnimation;
    @Getter
    private int runnableTaskId;
    @Getter
    private List<TitleObject> titleFrame;
    @Getter
    private int currentFrame;
    @Getter
    private SimpleCallback<Void> onDone;

    public TitleAnimation(TitleObject firstFrame, SimpleCallback<Void> onDone, long animationSpeed) {
        this.animationSpeed = animationSpeed;
        this.runnableTaskId = 0;
        this.currentFrame = 0;
        this.titleFrame = new ArrayList<>();
        this.onDone = onDone;
        this.repeatingAnimation = false;

        titleFrame.add(firstFrame);
    }

    /**
     * Add a new frame
     *
     * @param frame The frame
     */
    public void addFrame(TitleObject frame) {
        titleFrame.add(frame);
    }

    /**
     * Start the animation
     */
    public void start() {
        this.runnableTaskId = Bukkit.getScheduler().runTaskTimer(OreoCore.getInstance(), new AnimationHandler(), 0L, animationSpeed).getTaskId();
    }

    /**
     * Stop the animation
     *
     * @param callBack Should we call the callback?
     */
    public void stop(boolean callBack) {
        if (runnableTaskId == 0) return;
        Bukkit.getScheduler().cancelTask(runnableTaskId);
        onDone.success(null);
    }

    /**
     * Check if the animation is running or not
     *
     * @return True if running, false otherwise
     */
    public boolean isRunning() {
        return this.runnableTaskId != 0;
    }

    public class AnimationHandler implements Runnable {

        @Override
        public void run() {
            if (currentFrame >= titleFrame.size()) {
                if (!repeatingAnimation) {
                    stop(true);
                    return;
                }
            }
            currentFrame++;
        }
    }
}
