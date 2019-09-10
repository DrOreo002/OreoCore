package me.droreo002.oreocore.inventory.linked;

import lombok.Getter;
import lombok.Setter;
import me.droreo002.oreocore.inventory.OreoInventory;
import me.droreo002.oreocore.inventory.paginated.PaginatedInventory;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class LinkedInventoryManager {

    @Getter
    private final List<Linkable> inventories;
    @Getter @Setter
    private String currentInventory;

    public LinkedInventoryManager(Linkable... linkables) {
        this.inventories = new ArrayList<>();
        this.currentInventory = "";
        for (Linkable l : linkables) addLinkedInventory(l);
    }

    /**
     * Open inventory without specifying inventory
     * to open
     *
     * @param player Target player
     */
    public void openInventory(Player player) {
        openInventory(player, null);
    }

    /**
     * Open the inventory
     *
     * @param player Target player
     */
    public void openInventory(Player player, String firstInventory) {
        if (inventories.isEmpty()) throw new NullPointerException("Inventories cannot be empty!");
        Linkable linkable = (firstInventory == null) ? inventories.get(0) : getLinkedInventory(firstInventory);
        setCurrentInventory(linkable.getInventoryName());
        linkable.getInventoryOwner().open(player);
    }

    /**
     * Setup the buttons
     *
     * @param linkable The linkable inventory
     */
    private void setupButtons(Linkable linkable) {
        OreoInventory inventory = linkable.getInventoryOwner();
        for (LinkedButton linkedButton : linkable.getLinkedButtons()) {
            setupNavigationButton(linkedButton);
            if (inventory instanceof PaginatedInventory) {
                ((PaginatedInventory) inventory).addPaginatedButton(linkedButton);
            } else {
                inventory.addButton(linkedButton, true);
            }
        }
    }

    /**
     * Add the inventories
     *
     * @param linkable The linkable inventories
     */
    public void addLinkedInventory(Linkable... linkable) {
        for (Linkable l : linkable) addLinkedInventory(l);
    }

    /**
     * Add a inventory
     *
     * @param linkable The linkable inventory
     */
    public void addLinkedInventory(Linkable linkable) {
        if (linkable == null) throw new NullPointerException("Linkable cannot be null!");
        if (linkable.getInventoryOwner() == null) throw new NullPointerException("InventoryOwner of Linkable cannot be null!");
        setupButtons(linkable);
        inventories.add(linkable);
    }

    /**
     * Setup the button
     *
     * @param button The linked button
     */
    private void setupNavigationButton(LinkedButton button) {
        if (button.getTargetInventory() == null) return; // Don't setup
        button.addListener(e -> { // Default ClickType would be left
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
