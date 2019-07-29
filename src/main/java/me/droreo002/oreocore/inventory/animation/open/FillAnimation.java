package me.droreo002.oreocore.inventory.animation.open;

import lombok.Getter;
import lombok.Setter;
import me.droreo002.oreocore.utils.misc.MathUtils;
import me.droreo002.oreocore.utils.misc.SoundObject;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

public class FillAnimation extends OpenAnimation {

    @Getter
    private boolean firstRun;
    @Getter
    private List<Integer> slotAdded;
    @Getter @Setter
    private int addPerRun;

    public FillAnimation(SoundObject fillSound, SoundObject endSound) {
        super( "FillAnimation");
        this.slotAdded = new ArrayList<>();
        this.firstRun = true;

        setClearOnStart(true);
        setEndSound(endSound);
        setLoopingSound(fillSound);
    }

    @Override
    public void onInit() {
        Inventory inventory = getInventory();
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
            firstRun = false;
            if (addPerRun > 2) { // Ultra speed
                for (int i : getInventoryItems().keySet()) {
                    int random = MathUtils.random(0, 1, false);
                    boolean shouldAdd = random == 0;
                    if (shouldAdd) {
                        getInventory().setItem(i, getInventoryItems().get(i));
                        slotAdded.add(i);
                    }
                }
            }
            updateInventory();
            return;
        }
        for (int i = 0; i < addPerRun; i++) {
            // All item has been added
            if (slotAdded.size() >= getInventoryItems().size()) {
                stop(true);
                return;
            }

            int randomSlot = randomize();
            getInventory().setItem(randomSlot, getInventoryItems().get(randomSlot));
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
}
