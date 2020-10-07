package me.droreo002.oreocore.inventory.animation.open;

import lombok.Getter;
import me.droreo002.oreocore.utils.bridge.XSound;
import me.droreo002.oreocore.utils.inventory.InventoryUtils;
import me.droreo002.oreocore.utils.item.complex.XMaterial;
import me.droreo002.oreocore.utils.misc.MathUtils;
import me.droreo002.oreocore.utils.misc.SoundObject;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemWave extends OpenAnimation {

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
     * Get the default ItemWave
     *
     * @return the ItemWave
     */
    public static ItemWave getDefault() {
        final ItemWave waveAnimation = new ItemWave(XMaterial.DIAMOND_PICKAXE.getItemStack(), XMaterial.STONE.getItemStack(), new SoundObject(XSound.BLOCK_STONE_HIT));
        waveAnimation.setEndSound(new SoundObject(XSound.BLOCK_ANVIL_FALL));
        return waveAnimation;
    }

    /**
     * Construct new ItemWave
     *
     * @param waveHeader The header of the wave
     * @param fillEmpty Item to fill empty gaps
     * @param waveSound The wave sound
     */
    public ItemWave(ItemStack waveHeader, ItemStack fillEmpty, SoundObject waveSound) {
        super(OpenAnimationType.ITEM_WAVE_ANIMATION.name());
        this.waveHeader = waveHeader;
        this.startingPoint = new ArrayList<>();
        this.endSlot = new ArrayList<>();
        this.reached = new ArrayList<>();
        this.fillEmpty = fillEmpty;

        for (int i : InventoryUtils.getEndSlot()) {
            endSlot.add(i + 1);
        }

        setLoopingSound(waveSound);
        setClearOnStart(true);
    }

    @Override
    public void onInit() {
        Inventory inventory = getInventory();
        this.rows = InventoryUtils.getInventoryRows(inventory);
        for (int i = 0; i < inventory.getSize(); i += 9) {
            int increment = MathUtils.random(0, 3, false);
            startingPoint.add(i + increment);
        }
    }

    @Override
    public void run() {
        for (int i : reached) {
            getInventory().setItem(i, getInventoryItems().get(i));
        }

        if (reached.size() >= rows.size()) {
            stop(true);
            return;
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
