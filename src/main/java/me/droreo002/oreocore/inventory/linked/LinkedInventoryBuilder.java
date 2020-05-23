package me.droreo002.oreocore.inventory.linked;

import lombok.Getter;
import lombok.Setter;
import me.droreo002.oreocore.utils.entity.PlayerUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class LinkedInventoryBuilder {

    @Getter
    private final LinkedList<Linkable> inventories;
    @Getter
    private final LinkedDataList linkedDataList;
    @Getter
    private final List<UUID> modifiedButton;
    @Getter
    private LinkedInventoryListener linkedInventoryListener;
    @Getter @Setter
    private String currentInventory;
    @Getter @Setter
    private int currentInventorySlot;

    public LinkedInventoryBuilder() {
        this.inventories = new LinkedList<>();
        this.currentInventorySlot = 0;
        this.currentInventory = "";
        this.linkedDataList = new LinkedDataList();
        this.modifiedButton = new ArrayList<>();
    }

    /**
     * Build this linked inventory
     */
    public void build() {
        this.inventories.forEach(l -> setupLinkedButtons(l, false));
    }

    /**
     * Build this linked inventory, will auto open
     *
     * @param player Target player
     * @param extraData The extra data
     */
    public void build(Player player, LinkedDataList extraData) {
        build(player, null, extraData);
    }

    /**
     * Build this linked inventory, will auto open
     *
     * @param player Target player
     * @param extraData The extra data
     * @param firstInventory The first inventory to open
     */
    public void build(Player player, String firstInventory, LinkedDataList extraData) {
        if (inventories.isEmpty()) throw new NullPointerException("Inventories cannot be empty!");
        Linkable linkable = (firstInventory == null) ? inventories.get(0) : getLinkedInventory(firstInventory);
        setCurrentInventory(linkable.getInventoryName());
        if (extraData != null) {
            this.linkedDataList.addAll(extraData.getData());
            linkable.acceptData(this.linkedDataList, null);
        }
        linkable.getInventoryOwner().open(player);
        build();
    }

    /**
     * Setup the buttons
     *
     * @param linkable The linkable inventory
     */
    public void setupLinkedButtons(Linkable linkable, boolean reSetup) {
        if (reSetup) modifiedButton.clear();

        linkable.getLinkedButtons().forEach(button -> {
            if (modifiedButton.contains(button.getUniqueId())) return;
            if (button.getTargetInventory() == null) return;

            button.addListener(e -> { // Default ClickType would be left
                final Player player = (Player) e.getWhoClicked();
                final Linkable targetInventory = getLinkedInventory(button.getTargetInventory());
                final Linkable prevInventory = getLinkedInventory(getCurrentInventory());
                if (targetInventory == null) return;
                if (prevInventory == null) return;
                PlayerUtils.closeInventory(player);

                linkedDataList.addAll(prevInventory.getInventoryData());

                // Pre
                if (this.linkedInventoryListener != null) {
                    this.linkedInventoryListener.onInventoryPreTransfer(prevInventory, targetInventory);
                }

                targetInventory.acceptData(linkedDataList, prevInventory);
                targetInventory.onLinkOpen(prevInventory);
                targetInventory.getInventoryOwner().open(player);
                setupLinkedButtons(targetInventory, false); // Because sometimes buttons need to get initialized after acceptData

                // Post
                if (this.linkedInventoryListener != null) {
                    this.linkedInventoryListener.onInventoryTransfer(prevInventory, targetInventory);
                }

                setCurrentInventory(targetInventory.getInventoryName());
                setCurrentInventorySlot(currentInventorySlot + 1);
            });
            modifiedButton.add(button.getUniqueId());
        });
    }

    /**
     * Get the next inventory
     *
     * @return The next inventory
     */
    @Nullable
    public Linkable getNextInventory() {
        if (currentInventorySlot + 1 >= inventories.size()) return null;
        setCurrentInventorySlot(currentInventorySlot + 1);
        Linkable nextInventory = getInventories().get(currentInventorySlot);
        setCurrentInventory(nextInventory.getInventoryName());
        return nextInventory;
    }

    /**
     * Get the previous inventory
     *
     * @return The previous inventory
     */
    @Nullable
    public Linkable getPreviousInventory() {
        if (currentInventorySlot - 1 < 0) return null;
        setCurrentInventorySlot(currentInventorySlot - 1);
        Linkable prevInventory = getInventories().get(currentInventorySlot);
        setCurrentInventory(prevInventory.getInventoryName());
        return prevInventory;
    }

    /**
     * Add the inventories
     *
     * @param linkable The linkable inventories
     */
    public LinkedInventoryBuilder addAll(Linkable... linkable) {
        for (Linkable l : linkable) add(l);
        return this;
    }

    /**
     * Add a inventory
     *
     * @param linkable The linkable inventory
     */
    public LinkedInventoryBuilder add(Linkable linkable) {
        if (linkable == null) throw new NullPointerException("Linkable cannot be null!");
        if (linkable.getInventoryOwner() == null) throw new NullPointerException("InventoryOwner of Linkable cannot be null!");
        inventories.add(linkable);
        return this;
    }

    /**
     * Set the linked inventory listener
     *
     * @param linkedInventoryListener The listener
     */
    public LinkedInventoryBuilder setLinkedInventoryListener(LinkedInventoryListener linkedInventoryListener) {
        this.linkedInventoryListener = linkedInventoryListener;
        return this;
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

    /**
     * Interface for listener method
     * on LinkedInventoryBuilder
     */
    public interface LinkedInventoryListener {

        /**
         * Called when there's a inventory transfer occurred
         *
         * @param before Linkable
         * @param after Linkable
         */
        default void onInventoryTransfer(Linkable before, Linkable after) {

        }

        /**
         * Called when inventory pre transferred
         *
         * @param before Linkable
         * @param after Linkable
         */
        default void onInventoryPreTransfer(Linkable before, Linkable after) {

        }
    }
}
