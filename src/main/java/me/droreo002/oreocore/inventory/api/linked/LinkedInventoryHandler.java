package me.droreo002.oreocore.inventory.api.linked;

import lombok.Getter;
import lombok.Setter;
import me.droreo002.oreocore.inventory.api.CustomInventory;
import me.droreo002.oreocore.inventory.api.GUIButton;
import me.droreo002.oreocore.utils.entity.PlayerUtils;
import me.droreo002.oreocore.utils.misc.SoundObject;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LinkedInventoryHandler {

    @Getter @Setter
    private List<LinkedInventory> inventories;
    @Getter @Setter
    private int currentInventorySlot;
    @Getter @Setter
    private SoundObject onOpenOtherInventory;

    public LinkedInventoryHandler() {
        this.currentInventorySlot = 0;
        this.inventories = new ArrayList<>();
    }

    /**
     * Open the first inventory
     */
    public void open(Player player) {
        if (inventories.isEmpty()) throw new NullPointerException("No inventory available!");
        if (onOpenOtherInventory != null) onOpenOtherInventory.send(player);
        LinkedInventory target = inventories.get(0);
        target.onOpen(player, new HashMap<>());
        target.open(player);
    }

    /**
     * Add a inventory
     *
     * @param backButton The back button
     * @param inventory The inventory to add
     */
    public void addInventory(GUIButton backButton, GUIButton nextButton, LinkedInventory inventory) {
        setupButton(backButton, nextButton);

        if (backButton != null) inventory.addButton(backButton, true);
        if (nextButton != null) inventory.addButton(nextButton, true);
        inventories.add(inventory);
    }

    /**
     * Setup the button
     *
     * @param backButton The back button
     * @param nextButton The next button
     */
    private void setupButton(GUIButton backButton, GUIButton nextButton) {
        if (backButton != null) {
            backButton.setListener(e -> {
                int page = (currentInventorySlot - 1);
                if (page < 0) return;

                final Player player = (Player) e.getWhoClicked();
                final LinkedInventory targetInventory = inventories.get(page);
                final LinkedInventory prevInventory = inventories.get(currentInventorySlot);
                PlayerUtils.closeInventory(player);
                if (onOpenOtherInventory != null) onOpenOtherInventory.send(player);
                inventories.get(page).open(player);

                targetInventory.onOpen(player, prevInventory.requestData());
                this.currentInventorySlot = page;
            });
        }
        if (nextButton != null) {
            nextButton.setListener(e -> {
                int page = (currentInventorySlot + 1);
                if (page >= inventories.size()) return;

                final Player player = (Player) e.getWhoClicked();
                final LinkedInventory targetInventory = inventories.get(page);
                final LinkedInventory prevInventory = inventories.get(currentInventorySlot);
                PlayerUtils.closeInventory(player);
                if (onOpenOtherInventory != null) onOpenOtherInventory.send(player);
                inventories.get(page).open(player);

                targetInventory.onOpen(player, prevInventory.requestData());
                this.currentInventorySlot = page;
            });
        }
    }
}
