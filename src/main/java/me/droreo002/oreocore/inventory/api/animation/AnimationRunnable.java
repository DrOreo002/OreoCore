package me.droreo002.oreocore.inventory.api.animation;

import me.droreo002.oreocore.inventory.api.CustomInventory;

public class AnimationRunnable implements Runnable {

    private CustomInventory inventory;

    public AnimationRunnable(CustomInventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public void run() {
        for (ItemAnimation anim : inventory.getAnimationButtonMap().values()) {
            anim.update();
            inventory.getInventory().setItem(anim.getSlot(), anim);
        }
    }
}

