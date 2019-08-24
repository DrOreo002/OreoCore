package me.droreo002.oreocore.inventory.linked;

import lombok.Getter;
import lombok.Setter;
import me.droreo002.oreocore.inventory.OreoInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;

public class LinkedInventoryManager {

    @Getter
    private final List<Linkable> inventories;
    @Getter @Setter
    private String currentInventory;
    @Getter @Setter
    private String firstInventory;

    public LinkedInventoryManager(Linkable... linkables) {
        this.inventories = new ArrayList<>();
        this.currentInventory = "";
        this.firstInventory = "";
        for (Linkable l : linkables) addLinkedInventory(l);
    }

    /**
     * Open the inventory
     *
     * @param player Target player
     */
    public void openInventory(Player player) {
        if (inventories.isEmpty()) throw new NullPointerException("Inventories cannot be empty!");
        if (!firstInventory.isEmpty()) {
            Linkable linkable = getLinkedInventory(firstInventory);
            setCurrentInventory(linkable.getInventoryName());
            linkable.getInventoryOwner().open(player);
        } else {
            Linkable linkable = inventories.get(0);
            setCurrentInventory(linkable.getInventoryName());
            linkable.getInventoryOwner().open(player); // Force open on index 0
        }
    }

    /**
     * Add a inventory
     *
     * @param linkable The inventory linkable inventory
     */
    public void addLinkedInventory(Linkable linkable) {
        if (linkable == null) throw new NullPointerException("Linkable cannot be null!");
        if (linkable.getInventoryOwner() == null) throw new NullPointerException("InventoryOwner of Linkable cannot be null!");

        OreoInventory inventory = linkable.getInventoryOwner();
        for (LinkedButton linkedButton : linkable.getLinkedButtons()) {
            setupNavigationButton(linkedButton, linkable.getDefaultListenerClickType());
            inventory.addButton(linkedButton, true);
        }
        inventories.add(linkable);
    }

    /**
     * Setup the button
     *
     * @param button The linked button
     */
    private void setupNavigationButton(LinkedButton button, ClickType clickType) {
        button.addListener(clickType, e -> {
            final Player player = (Player) e.getWhoClicked();
            final Linkable targetInventory = getLinkedInventory(button.getTargetInventory());
            final Linkable prevInventory = getLinkedInventory(getCurrentInventory());

            prevInventory.onPreOpenOtherInventory(e, targetInventory);
            targetInventory.onLinkAcceptData(prevInventory.onLinkRequestData(), prevInventory);

            targetInventory.getInventoryOwner().open(player);
            setCurrentInventory(targetInventory.getInventoryName());
        });
    }

    /**
     * Get linked inventory by name
     *
     * @param name The inventory name
     * @return The linked inventory
     */
    public Linkable getLinkedInventory(String name) {
        return inventories.stream().filter(inv -> inv.getInventoryName().equalsIgnoreCase(name)).findAny().orElse(null);
    }
}
