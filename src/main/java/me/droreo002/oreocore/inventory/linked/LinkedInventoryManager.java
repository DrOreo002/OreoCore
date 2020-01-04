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

public class LinkedInventoryManager {

    @Getter
    private final LinkedList<Linkable> inventories;
    @Getter
    private final LinkedDatas linkedDatas;
    @Getter
    private final List<UUID> modifiedButton;
    @Getter @Setter
    private String currentInventory;
    @Getter @Setter
    private int currentInventorySlot;
    @Getter @Setter
    private LinkedListener linkedListener;

    public LinkedInventoryManager(Linkable... linkables) {
        this.inventories = new LinkedList<>();
        this.currentInventorySlot = 0;
        this.currentInventory = "";
        this.linkedDatas = new LinkedDatas();
        this.modifiedButton = new ArrayList<>();

        addLinkedInventory(linkables);
    }

    /**
     * Open inventory without specifying inventory
     * to open
     *
     * @param player Target player
     * @param extraData The extra data
     */
    public void openInventory(Player player, LinkedDatas extraData) {
        openInventory(player, null, extraData);
    }

    /**
     * Open the inventory
     *
     * @param player Target player
     * @param extraData The extra data
     * @param firstInventory The first inventory to open
     */
    public void openInventory(Player player, String firstInventory, LinkedDatas extraData) {
        if (inventories.isEmpty()) throw new NullPointerException("Inventories cannot be empty!");
        Linkable linkable = (firstInventory == null) ? inventories.get(0) : getLinkedInventory(firstInventory);
        setCurrentInventory(linkable.getInventoryName());
        if (extraData != null) {
            this.linkedDatas.addAll(extraData.getData());
            linkable.acceptData(this.linkedDatas, null);
            setupButtons(linkable, false); // Just in case if there's a new one added when accepting data
        }
        linkable.getInventoryOwner().open(player);
    }

    /**
     * Setup the buttons
     *
     * @param linkable The linkable inventory
     */
    public void setupButtons(Linkable linkable, boolean reSetup) {
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

                linkedDatas.addAll(prevInventory.getInventoryData());
                if (button.getExtraDataProvider() != null) {
                    try {
                        linkedDatas.addAll(button.getExtraDataProvider().get().getData());
                    } catch (InterruptedException | ExecutionException ex) {
                        ex.printStackTrace();
                    }
                }

                // Pre
                if (this.linkedListener != null) {
                    this.linkedListener.onInventoryPreTransfer(prevInventory, targetInventory);
                }

                targetInventory.acceptData(linkedDatas, prevInventory);
                targetInventory.onLinkOpen(prevInventory);
                targetInventory.getInventoryOwner().open(player);

                // Post
                if (this.linkedListener != null) {
                    this.linkedListener.onInventoryTransfer(prevInventory, targetInventory);
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
     * Add a linkable inventory, with some extra wow
     *
     * @param linkable The linkable inventory
     * @return Current object
     */
    public LinkedInventoryManager then(Linkable linkable) {
        addLinkedInventory(linkable);
        return this;
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
        setupButtons(linkable, false);
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

    /**
     * Interface for listener method
     * on LinkedInventoryManager
     */
    public interface LinkedListener {

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
