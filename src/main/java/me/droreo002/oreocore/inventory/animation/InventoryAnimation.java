package me.droreo002.oreocore.inventory.animation;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import me.droreo002.oreocore.OreoCore;
import me.droreo002.oreocore.inventory.OreoInventory;
import me.droreo002.oreocore.inventory.animation.open.OpenAnimation;
import me.droreo002.oreocore.inventory.button.GUIButton;
import me.droreo002.oreocore.inventory.paginated.PaginatedInventory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class InventoryAnimation {

    @Getter @Setter
    private int animationUpdaterTaskId, animationTaskId;
    @Getter @Setter
    private long animationUpdateSpeed, animationUpdaterSpeed;
    @Getter @Setter
    private OpenAnimation openAnimation;
    @Getter @Setter
    private IAnimationRunnable animationHandler;

    @Builder
    public InventoryAnimation(long animationUpdateSpeed, long animationUpdaterSpeed, OpenAnimation openAnimation) {
        this.animationUpdateSpeed = (animationUpdateSpeed == 0L) ? 2L : animationUpdateSpeed;
        this.animationUpdaterSpeed = (animationUpdaterSpeed == 0L) ? 1L : animationUpdaterSpeed;
        this.openAnimation = openAnimation;
    }

    /**
     * Get default inventory animation
     *
     * @return The default inventory animation
     */
    public static InventoryAnimation getDefault() {
        return InventoryAnimation.builder().build();
    }

    /**
     * Start the animation
     *
     * @param oreoInventory The source inventory of this animation
     */
    public void startAnimation(OreoInventory oreoInventory) {
        System.out.println("Starting animation");
        // Setup the animation handler, last spaghetti code here xD
        // Here we handle as normal inventory
        if (oreoInventory.getButtons().stream().anyMatch(GUIButton::isAnimated)) {
            animationHandler = new IAnimationRunnable(oreoInventory.getButtons(), oreoInventory.getInventory(), oreoInventory);
        }
        // If it's a paginated inventory. We handle it with extra care
        if (oreoInventory instanceof PaginatedInventory) {
            PaginatedInventory paginatedInventory = (PaginatedInventory) oreoInventory;
            if (paginatedInventory.getCurrentPageButtons().stream().anyMatch(GUIButton::isAnimated)) {
                List<GUIButton> toHandle = new ArrayList<>(paginatedInventory.getCurrentPageButtons());
                toHandle.addAll(paginatedInventory.getButtons()); // Also handle normal buttons
                animationHandler = new IAnimationRunnable(toHandle, oreoInventory.getInventory(), oreoInventory);
            }
        }

        if (animationHandler == null) return;
        if (animationTaskId != 0) Bukkit.getScheduler().cancelTask(animationTaskId);
        animationTaskId = Bukkit.getScheduler().runTaskTimer(OreoCore.getInstance(), animationHandler, 0L, animationUpdateSpeed).getTaskId();
        animationUpdaterTaskId = new BukkitRunnable() {
            @Override
            public void run() {
                oreoInventory.getInventory().getViewers().forEach(humanEntity -> ((Player) humanEntity).updateInventory());
            }
        }.runTaskTimer(OreoCore.getInstance(), 0L, animationUpdaterSpeed).getTaskId();
    }

    /**
     * Stop the animation
     */
    public void stopAnimation() {
        if (animationHandler == null) return;
        Bukkit.getScheduler().cancelTask(animationTaskId);
        Bukkit.getScheduler().cancelTask(animationUpdaterTaskId);
        animationHandler.getSingleButtonRunnable().forEach(Bukkit.getScheduler()::cancelTask);
    }

    /**
     * Check if the button animation is running or not
     *
     * @return True if running, false otherwise
     */
    public boolean isButtonAnimationRunning() {
        return animationTaskId != 0;
    }

    /**
     * Check if the open animation is running or not
     *
     * @return True if running, false otherwise
     */
    public boolean isOpenAnimationRunning() {
        if (openAnimation == null) return false;
        return openAnimation.getRunnableId() != 0;
    }
}
