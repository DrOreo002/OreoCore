package me.droreo002.oreocore.inventory.animation.open;

import me.droreo002.oreocore.utils.item.complex.XMaterial;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiagonalFill extends OpenAnimation {

    private int slot;
    private Map<Integer, List<Integer>> fillSection;

    public DiagonalFill() {
        super(OpenAnimationType.DIAGONAL_FILL_ANIMATION.name());
        this.slot = 1;
        this.fillSection = new HashMap<>();
    }

    @Override
    public void run() {
        int increment;
        for (int i = 0; i < getInventory().getSize() / 9; i++) {
            increment = slot;
            getInventory().setItem(increment, XMaterial.PURPLE_STAINED_GLASS_PANE.getItemStack());
            increment += 8;
            getInventory().setItem(increment, XMaterial.PURPLE_STAINED_GLASS_PANE.getItemStack());
        }
        slot++;
    }

    @Override
    public void onInit() {
        if (getInventory().getSize() <= 9) throw new IllegalStateException("Diagonal fill does not support inventory with the size of 9!");
    }
}
