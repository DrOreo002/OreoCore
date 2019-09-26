package me.droreo002.oreocore.inventory.linked;

import lombok.Getter;
import lombok.Setter;
import me.droreo002.oreocore.inventory.paginated.PaginatedInventory;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LinkedInventoryManager {

    @Getter
    private final List<Linkable> inventories;
    @Getter
    private final LinkedDatas linkedDatas;
    @Getter @Setter
    private String currentInventory;

    public LinkedInventoryManager(Linkable... linkables) {
        this.inventories = new ArrayList<>();
        this.currentInventory = "";
        this.linkedDatas = new LinkedDatas();
        addLinkedInventory(linkables);
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
        linkable.getLinkedButtons().forEach(button -> {
            if (button.getTargetInventory() == null) return; // Don't setup
            button.addListener(e -> { // Default ClickType would be left
                final Player player = (Player) e.getWhoClicked();
                final Linkable targetInventory = getLinkedInventory(button.getTargetInventory());
                final Linkable prevInventory = getLinkedInventory(getCurrentInventory());

                linkedDatas.addAll(prevInventory.getInventoryData());
                targetInventory.acceptData(linkedDatas, prevInventory);
                targetInventory.onLinkOpen(prevInventory);
                targetInventory.getInventoryOwner().open(player);
                setCurrentInventory(targetInventory.getInventoryName());
            });
        });
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
     * Get linked inventory by name
     *
     * @param name The inventory name
     * @return The linked inventory
     */
    public Linkable getLinkedInventory(String name) {
        return inventories.stream().filter(inv -> inv.getInventoryName().equalsIgnoreCase(name)).findAny().orElse(null);
    }
}
