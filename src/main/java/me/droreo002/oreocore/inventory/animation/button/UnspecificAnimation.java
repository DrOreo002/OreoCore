package me.droreo002.oreocore.inventory.animation.button;

import org.bukkit.inventory.ItemStack;

public class UnspecificAnimation extends ButtonAnimation {

    public UnspecificAnimation() {
        super(ButtonAnimation.UNSPECIFIC);
    }

    @Override
    public void initializeFrame(ItemStack buttonItem) {
        // Do nothing
    }
}
