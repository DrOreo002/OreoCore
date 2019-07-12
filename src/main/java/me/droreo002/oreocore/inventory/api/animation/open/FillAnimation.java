package me.droreo002.oreocore.inventory.api.animation.open;

import lombok.Getter;
import lombok.Setter;
import me.droreo002.oreocore.enums.Sounds;
import me.droreo002.oreocore.utils.inventory.InventoryUtils;
import me.droreo002.oreocore.utils.misc.MathUtils;
import me.droreo002.oreocore.utils.misc.SoundObject;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FillAnimation extends OpenAnimation {

    @Getter
    private boolean firstRun;
    @Getter
    private Map<Integer, ItemStack> inventoryItems;
    @Getter
    private List<Integer> slotAdded;
    @Getter @Setter
    private int addPerRun;

    public FillAnimation(Inventory inventory) {
        super( "FillAnimation", inventory);
        this.slotAdded = new ArrayList<>();
        this.inventoryItems = InventoryUtils.getItemAsHashMap(inventory);
        this.firstRun = true;

        switch (inventory.getSize()) {
            case 9:
            case 18:
                this.addPerRun = 1;
                break;
            case 27:
            case 36:
            case 45:
                this.addPerRun = 3;
                break;
            case 54:
                this.addPerRun = 4;
                break;
            default:
                this.addPerRun = 2;
                break;
        }
    }

    @Override
    public void run() {
        if (firstRun) {
            getInventory().clear();
            firstRun = false;

            if (addPerRun > 2) { // Ultra speed
                for (int i : inventoryItems.keySet()) {
                    int random = MathUtils.random(0, 1, false);
                    boolean shouldAdd = random == 0;
                    if (shouldAdd) {
                        getInventory().setItem(i, inventoryItems.get(i));
                        slotAdded.add(i);
                    }
                }
            }
            updateInventory();
            return;
        }
        for (int i = 0; i < addPerRun; i++) {
            // All item has been added
            if (slotAdded.size() >= inventoryItems.size()) {
                stop(true);
                return;
            }

            int randomSlot = randomize();
            getInventory().setItem(randomSlot, inventoryItems.get(randomSlot));
            slotAdded.add(randomSlot);
        }
        updateInventory();
    }

    private int randomize() {
        int max = getInventory().getSize() - 1;
        int randomSlot = MathUtils.random(0, max, false);

        if (slotAdded.contains(randomSlot)) return randomize(); // Randomize again
        return randomSlot;
    }

    private void updateInventory() {
        InventoryUtils.updateInventoryViewer(getInventory());
        InventoryUtils.playSoundToViewer(getInventory(), new SoundObject(Sounds.STEP_WOOD));
    }
}
