package me.droreo002.oreocore.inventory.api.animation;

import me.droreo002.oreocore.OreoCore;
import me.droreo002.oreocore.inventory.api.CustomInventory;
import org.bukkit.Bukkit;
import org.bukkit.inventory.InventoryHolder;

import java.util.HashMap;
import java.util.Map;

public final class ItemAnimationManager {

    // Where UUID is the player UUID, Integer is the animation ID. and CustomInventory is the object
    private static final Map<InventoryHolder, Map<Integer, CustomInventory>> ANIMATIONS = new HashMap<>();

    public static void registerRunnable(CustomInventory inventory) {
        if (ANIMATIONS.containsKey(inventory.getInventory().getHolder())) {
            stopRunnable(inventory.getInventory().getHolder());
        }
        HashMap<Integer, CustomInventory> hash = new HashMap<>();
        int task = Bukkit.getScheduler().runTaskTimer(OreoCore.getInstance(), new AnimationRunnable(inventory), 0L, 5L).getTaskId();
        hash.put(task, inventory);
        ANIMATIONS.put(inventory.getInventory().getHolder(), hash);
    }

    public static void stopRunnable(InventoryHolder holder) {
        if (!ANIMATIONS.containsKey(holder)) return;
        Map<Integer, CustomInventory> hash = ANIMATIONS.get(holder);
        for (int i : hash.keySet()) {
            Bukkit.getScheduler().cancelTask(i);
        }
        ANIMATIONS.remove(holder);
    }

    public static Map<InventoryHolder, Map<Integer, CustomInventory>> getAnimations() {
        return ANIMATIONS;
    }
}
