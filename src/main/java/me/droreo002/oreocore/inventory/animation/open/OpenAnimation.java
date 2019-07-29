package me.droreo002.oreocore.inventory.animation.open;

import lombok.Getter;
import lombok.Setter;
import me.droreo002.oreocore.OreoCore;
import me.droreo002.oreocore.utils.inventory.InventoryUtils;
import me.droreo002.oreocore.utils.misc.SimpleCallback;
import me.droreo002.oreocore.utils.misc.SoundObject;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public abstract class OpenAnimation {

    @Getter
    private final String animationName;
    @Getter
    private Inventory inventory;
    @Getter @Setter
    private boolean clearOnStart;
    @Getter @Setter
    private long animationSpeed;
    @Getter @Setter
    private long startAfter;
    @Getter @Setter
    private int runnableId;
    @Getter @Setter
    private SimpleCallback<Void> whenDone;
    @Getter @Setter
    private Map<Integer, ItemStack> inventoryItems;
    @Getter @Setter
    private SoundObject loopingSound;
    @Getter @Setter
    private SoundObject endSound;

    public OpenAnimation(String animationName) {
        this.animationSpeed = 2L;
        this.startAfter = 0L;
        this.animationName = animationName;
        this.clearOnStart = false;
    }

    /**
     * Start the animation
     */
    public void start(SimpleCallback<Void> whenDone) {
        if (clearOnStart) inventory.clear();
        this.runnableId = Bukkit.getScheduler().runTaskTimer(OreoCore.getInstance(), OpenAnimation.this::run, startAfter, animationSpeed).getTaskId();
        this.whenDone = whenDone;
    }

    /**
     * Stop the animation
     */
    public void stop(boolean callCallback) {
        if (runnableId == 0) return;
        Bukkit.getScheduler().cancelTask(runnableId);
        runnableId = 0;
        if (callCallback) whenDone.success(null);
        if (endSound != null) InventoryUtils.playSoundToViewer(getInventory(), endSound);
    }

    /**
     * Update the inventory
     */
    public void updateInventory() {
        InventoryUtils.updateInventoryViewer(getInventory());
        if (loopingSound != null) {
            InventoryUtils.playSoundToViewer(getInventory(), loopingSound);
        }
    }

    /**
     * Set the inventory (Will automatically called after setup)
     *
     * @param inventory The inventory
     */
    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
        this.inventoryItems = InventoryUtils.getItemAsHashMap(inventory);
        this.onInit();
    }

    /**
     * Run the animation
     */
    public abstract void run();

    /**
     * Called when inventory is successfully initialized
     */
    public abstract void onInit();
}
