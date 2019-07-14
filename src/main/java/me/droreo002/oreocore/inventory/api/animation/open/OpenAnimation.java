package me.droreo002.oreocore.inventory.api.animation.open;

import lombok.Getter;
import lombok.Setter;
import me.droreo002.oreocore.OreoCore;
import me.droreo002.oreocore.enums.Sounds;
import me.droreo002.oreocore.utils.inventory.InventoryUtils;
import me.droreo002.oreocore.utils.misc.SimpleCallback;
import me.droreo002.oreocore.utils.misc.SoundObject;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

@Getter @Setter
public abstract class OpenAnimation {

    private boolean clearOnStart;
    private long animationSpeed;
    private long startAfter;
    private int runnableId;
    private String animationName;
    private SimpleCallback<Void> whenDone;
    private Inventory inventory;
    private Map<Integer, ItemStack> inventoryItems;
    private SoundObject loopingSound;
    private SoundObject endSound;

    public OpenAnimation(String animationName, Inventory inventory) {
        this.animationSpeed = 2L;
        this.startAfter = 0L;
        this.animationName = animationName;
        this.inventory = inventory;
        this.clearOnStart = false;
        this.inventoryItems = InventoryUtils.getItemAsHashMap(inventory);
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

    public void updateInventory() {
        InventoryUtils.updateInventoryViewer(getInventory());
        if (loopingSound != null) {
            InventoryUtils.playSoundToViewer(getInventory(), loopingSound);
        }
    }

    /**
     * Run the animation
     */
    public abstract void run();
}
