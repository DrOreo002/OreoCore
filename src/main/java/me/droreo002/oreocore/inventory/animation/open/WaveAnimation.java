package me.droreo002.oreocore.inventory.animation.open;

import lombok.Getter;
import me.droreo002.oreocore.enums.Sounds;
import me.droreo002.oreocore.utils.inventory.InventoryUtils;
import me.droreo002.oreocore.utils.item.complex.UMaterial;
import me.droreo002.oreocore.utils.misc.MathUtils;
import me.droreo002.oreocore.utils.misc.SoundObject;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WaveAnimation extends OpenAnimation {

    @Getter
    private ItemStack waveHeader;
    @Getter
    private ItemStack fillEmpty;
    @Getter
    private List<Integer> startingPoint;
    @Getter
    private List<Integer> endSlot;
    @Getter
    private List<Integer> reached;
    @Getter
    private Map<Integer, List<Integer>> rows;

    /**
     * Get the default WaveAnimation
     *
     * @param inventory The inventory
     * @return the WaveAnimation
     */
    public static WaveAnimation getDefault(Inventory inventory) {
        final WaveAnimation waveAnimation = new WaveAnimation(inventory,
                UMaterial.DIAMOND_PICKAXE.getItemStack(), UMaterial.STONE.getItemStack(), new SoundObject(Sounds.DIG_STONE));
        waveAnimation.setEndSound(new SoundObject(Sounds.ANVIL_LAND));
        return waveAnimation;
    }

    /**
     * Construct new WaveAnimation
     *
     * @param inventory The inventory
     * @param waveHeader The header of the wave
     * @param fillEmpty Item to fill empty gaps
     * @param waveSound The wave sound
     */
    public WaveAnimation(Inventory inventory, ItemStack waveHeader, ItemStack fillEmpty, SoundObject waveSound) {
        super("WaveAnimation", inventory);
        this.waveHeader = waveHeader;
        this.startingPoint = new ArrayList<>();
        this.endSlot = new ArrayList<>();
        this.rows = InventoryUtils.getInventoryRows(inventory);
        this.reached = new ArrayList<>();
        this.fillEmpty = fillEmpty;

        for (int i = 0; i < inventory.getSize(); i += 9) {
            int increment = MathUtils.random(0, 3, false);
            startingPoint.add(i + increment);
        }

        for (int i : InventoryUtils.getEndSlot()) {
            endSlot.add(i + 1);
        }

        setLoopingSound(waveSound);
        setClearOnStart(true);
    }

    @Override
    public void run() {
        if (reached.size() >= rows.size()) {
            stop(true);
            return;
        }
        for (int i : reached) {
            getInventory().setItem(i, getInventoryItems().get(i));
        }

        for (int i : startingPoint) {
            if (reached.contains(i)) continue;
            int currentRow = InventoryUtils.getRowBySlot(i, rows);
            try {
                for (int rSlot : rows.get(currentRow)) {
                    if (rSlot < i) getInventory().setItem(rSlot, getInventoryItems().get(rSlot));
                    if (rSlot > i) getInventory().setItem(rSlot, fillEmpty);
                }
                getInventory().setItem(i, waveHeader);
                if (InventoryUtils.getEndSlot().contains(i)) reached.add(i);
            } catch (ArrayIndexOutOfBoundsException | NullPointerException ignored) {

            }
        }
        List<Integer> toAdd = new ArrayList<>(startingPoint);
        startingPoint.clear();

        for (int i : toAdd) {
            i += 1;
            if (endSlot.contains(i)) {
                // Stop, don't add
                i -= 1;
                startingPoint.add(i);
            } else {
                startingPoint.add(i);
            }
        }

        updateInventory();
    }
}
