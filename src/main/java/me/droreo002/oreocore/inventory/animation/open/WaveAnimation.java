package me.droreo002.oreocore.inventory.animation.open;

import lombok.Getter;
import lombok.Setter;
import me.droreo002.oreocore.enums.Sounds;
import me.droreo002.oreocore.utils.bridge.OSound;
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
     * @return the WaveAnimation
     */
    public static WaveAnimation getDefault() {
        final WaveAnimation waveAnimation = new WaveAnimation(UMaterial.DIAMOND_PICKAXE.getItemStack(), UMaterial.STONE.getItemStack(), new SoundObject(OSound.BLOCK_STONE_HIT));
        waveAnimation.setEndSound(new SoundObject(OSound.BLOCK_ANVIL_FALL));
        return waveAnimation;
    }

    /**
     * Construct new WaveAnimation
     *
     * @param waveHeader The header of the wave
     * @param fillEmpty Item to fill empty gaps
     * @param waveSound The wave sound
     */
    public WaveAnimation(ItemStack waveHeader, ItemStack fillEmpty, SoundObject waveSound) {
        super("WaveAnimation");
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
