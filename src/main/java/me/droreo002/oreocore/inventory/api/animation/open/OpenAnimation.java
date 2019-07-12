package me.droreo002.oreocore.inventory.api.animation.open;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.droreo002.oreocore.OreoCore;
import me.droreo002.oreocore.utils.misc.SimpleCallback;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

@Getter @Setter
public abstract class OpenAnimation {

    private long animationSpeed;
    private long startAfter;
    private int runnableId;
    private String animationName;
    private SimpleCallback<Void> whenDone;
    private Inventory inventory;

    public OpenAnimation(String animationName, Inventory inventory) {
        this.animationSpeed = 2L;
        this.startAfter = 0L;
        this.animationName = animationName;
        this.inventory = inventory;
    }

    /**
     * Start the animation
     */
    public void start(SimpleCallback<Void> whenDone) {
        this.runnableId = Bukkit.getScheduler().runTaskTimer(OreoCore.getInstance(), OpenAnimation.this::run, startAfter, animationSpeed).getTaskId();
        this.whenDone = whenDone;
    }

    /**
     * Stop the animation
     */
    public void stop(boolean callCallback) {
        if (runnableId == 0) return;
        Bukkit.getScheduler().cancelTask(runnableId);
        if (callCallback) whenDone.success(null);
    }

    /**
     * Run the animation
     */
    public abstract void run();
}
