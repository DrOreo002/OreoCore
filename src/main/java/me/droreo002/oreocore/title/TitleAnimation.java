package me.droreo002.oreocore.title;

import lombok.Getter;
import lombok.Setter;
import me.droreo002.oreocore.OreoCore;
import me.droreo002.oreocore.utils.misc.SimpleCallback;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TitleAnimation {

    @Getter @Setter
    private long animationSpeed;
    @Getter @Setter
    private boolean repeatingAnimation;
    @Getter @Setter
    private Player target;
    @Getter
    private int runnableTaskId;
    @Getter
    private List<TitleFrame> titleFrame;
    @Getter
    private int frameNumber;
    @Getter
    private SimpleCallback<Player> onDone;
    @Getter
    private OreoTitle baseTitle;

    public TitleAnimation(OreoTitle baseTitle, long animationSpeed) {
        this.animationSpeed = animationSpeed;
        this.runnableTaskId = 0;
        this.frameNumber = 0;
        this.titleFrame = new ArrayList<>();
        this.repeatingAnimation = true;
        this.baseTitle = baseTitle;
    }

    public TitleAnimation setOnDone(SimpleCallback<Player> onDone) {
        this.onDone = onDone;
        return this;
    }

    /**
     * Add a new frame
     *
     * @param frame The frame
     */
    public TitleAnimation addFrame(TitleFrame frame) {
        titleFrame.add(frame);
        return this;
    }

    /**
     * Start the animation
     */
    public void start(Player target) {
        this.runnableTaskId = Bukkit.getScheduler().runTaskTimer(OreoCore.getInstance(), new AnimationHandler(), 0L, animationSpeed).getTaskId();
        this.target = target;
    }

    /**
     * Stop the animation
     *
     * @param callBack Should we call the callback?
     */
    public void stop(boolean callBack) {
        if (runnableTaskId == 0) return;
        Bukkit.getScheduler().cancelTask(runnableTaskId);
        if (callBack) {
            if (onDone != null) onDone.success(target);
        }
    }

    /**
     * Check if the animation is running or not
     *
     * @return True if running, false otherwise
     */
    public boolean isRunning() {
        return this.runnableTaskId != 0;
    }

    public TitleFrame getCurrentFrame() {
        return this.titleFrame.get(frameNumber);
    }

    public class AnimationHandler implements Runnable {

        @Override
        public void run() {
            if (frameNumber >= titleFrame.size()) {
                if (!repeatingAnimation) {
                    stop(true);
                    return;
                }
                frameNumber = 0;
            }
            if (target.isOnline()) {
                TitleFrame frame = getCurrentFrame();
                baseTitle.setSubTitle(frame.getNextSubTitle(baseTitle.getSubTitle()));
                baseTitle.setTitle(frame.getNextTitle(baseTitle.getTitle()));
                baseTitle.setSoundOnSend(frame.getSound());

                baseTitle.send(target);
            }
            frameNumber++;
        }
    }
}
