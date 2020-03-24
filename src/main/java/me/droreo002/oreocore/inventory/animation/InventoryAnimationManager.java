package me.droreo002.oreocore.inventory.animation;

import lombok.Getter;
import lombok.Setter;
import me.droreo002.oreocore.OreoCore;
import me.droreo002.oreocore.inventory.OreoInventory;
import me.droreo002.oreocore.inventory.animation.open.OpenAnimation;
import me.droreo002.oreocore.inventory.button.GUIButton;
import me.droreo002.oreocore.inventory.paginated.PaginatedInventory;
import me.droreo002.oreocore.utils.inventory.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class InventoryAnimationManager {

    @Getter
    private int inventoryUpdaterTaskId, animationTaskId;
    @Getter
    private long animationUpdateSpeed, inventoryUpdaterSpeed;
    @Getter
    private OpenAnimation openAnimation;
    @Getter
    private IAnimationRunnable animationHandler;

    public InventoryAnimationManager(long animationUpdateSpeed, long inventoryUpdaterSpeed, OpenAnimation openAnimation) {
        this.animationUpdateSpeed = (animationUpdateSpeed == 0L) ? 2L : animationUpdateSpeed;
        this.inventoryUpdaterSpeed = (inventoryUpdaterSpeed == 0L) ? 1L : inventoryUpdaterSpeed;
        this.openAnimation = openAnimation;
    }

    /**
     * Get default inventory animation
     *
     * @return The default inventory animation
     */
    public static InventoryAnimationManager getDefault() {
        return new InventoryAnimationManager(0L, 0L, null);
    }

    /**
     * Start the animation
     *
     * @param oreoInventory The source inventory of this animation
     */
    public void startAnimation(OreoInventory oreoInventory) {
        if (oreoInventory.getButtons().stream().anyMatch(GUIButton::isAnimated)) {
            animationHandler = new IAnimationRunnable(oreoInventory.getButtons(), oreoInventory.getInventory(), oreoInventory);
        }
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
        InventoryUtils.updateInventoryViewer(oreoInventory.getInventory());

        this.animationTaskId = Bukkit.getScheduler().runTaskTimer(OreoCore.getInstance(), animationHandler, 5L, animationUpdateSpeed).getTaskId();
        this.inventoryUpdaterTaskId = new BukkitRunnable() {
            @Override
            public void run() {
                InventoryUtils.updateInventoryViewer(oreoInventory.getInventory());
            }
        }.runTaskTimer(OreoCore.getInstance(), 5L, inventoryUpdaterSpeed).getTaskId();
    }

    /**
     * Stop the animation
     */
    public void stopAnimation() {
        if (animationHandler == null) return;
        Bukkit.getScheduler().cancelTask(animationTaskId);
        Bukkit.getScheduler().cancelTask(inventoryUpdaterTaskId);
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

    public InventoryAnimationManager setAnimationUpdateSpeed(long animationUpdateSpeed) {
        this.animationUpdateSpeed = animationUpdateSpeed;
        return this;
    }

    public InventoryAnimationManager setInventoryUpdaterSpeed(long inventoryUpdaterSpeed) {
        this.inventoryUpdaterSpeed = inventoryUpdaterSpeed;
        return this;
    }

    public InventoryAnimationManager setOpenAnimation(OpenAnimation openAnimation) {
        this.openAnimation = openAnimation;
        return this;
    }
}
