package me.droreo002.oreocore.inventory.linked;

import lombok.Getter;
import lombok.Setter;
import me.droreo002.oreocore.inventory.InventoryTemplate;
import me.droreo002.oreocore.inventory.OreoInventory;
import me.droreo002.oreocore.inventory.button.GUIButton;
import me.droreo002.oreocore.utils.entity.PlayerUtils;
import me.droreo002.oreocore.utils.misc.SoundObject;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class LinkedInventory extends OreoInventory implements Linkable {

    @Getter @Setter
    private List<Linkable> inventories;
    @Getter @Setter
    private int currentInventorySlot;
    @Getter @Setter
    private SoundObject onOpenOtherInventory;
    @Getter @Setter
    private GUIButton mainNextButton;

    public LinkedInventory(InventoryTemplate template) {
        super(template);
        this.currentInventorySlot = 0;
        this.inventories = new ArrayList<>();
    }

    public LinkedInventory(int size, String title) {
        super(size, title);
        this.currentInventorySlot = 0;
        this.inventories = new ArrayList<>();
    }

    @Override
    public void setup() {
        if (mainNextButton == null) throw new NullPointerException("Main next button cannot be null!");
        setupNavigationButton(null, mainNextButton);
        addButton(mainNextButton, true);
        inventories.add(0, this);

        super.setup();
    }

    /**
     * Open the first inventory
     */
    public void open(Player player) {
        if (inventories.isEmpty()) throw new NullPointerException("Inventories list cannot be empty!");
        super.open(player);
    }

    /**
     * Add a inventory
     *
     * @param backButton The back button
     * @param linkable The inventory linkable inventory
     */
    public void addLinkedInventory(GUIButton backButton, GUIButton nextButton, Linkable linkable) {
        if (linkable instanceof LinkedInventory) throw new IllegalStateException("Cannot add another LinkedInventory instance!");
        setupNavigationButton(backButton, nextButton);

        OreoInventory inventory = (OreoInventory) linkable;
        if (backButton != null) inventory.addButton(backButton, true);
        if (nextButton != null) inventory.addButton(nextButton, true);
        inventories.add(linkable);
    }

    /**
     * Setup the button
     *
     * @param backButton The back button
     * @param nextButton The next button
     */
    private void setupNavigationButton(GUIButton backButton, GUIButton nextButton) {
        if (backButton != null) {
            backButton.setListener(e -> {
                int page = (currentInventorySlot - 1);
                if (page < 0) return;

                final Player player = (Player) e.getWhoClicked();
                final Linkable targetInventory = inventories.get(page);
                final Linkable prevInventory = inventories.get(currentInventorySlot);
                PlayerUtils.closeInventory(player);
                if (onOpenOtherInventory != null) onOpenOtherInventory.send(player);
                ((OreoInventory) inventories.get(page)).open(player);

                targetInventory.onOpen(player, prevInventory.onLinkRequestData());
                this.currentInventorySlot = page;
            });
        }
        if (nextButton != null) {
            nextButton.setListener(e -> {
                int page = (currentInventorySlot + 1);
                if (page >= inventories.size()) return;

                final Player player = (Player) e.getWhoClicked();
                final Linkable targetInventory = inventories.get(page);
                final Linkable prevInventory = inventories.get(currentInventorySlot);
                PlayerUtils.closeInventory(player);
                if (onOpenOtherInventory != null) onOpenOtherInventory.send(player);
                ((OreoInventory) inventories.get(page)).open(player);

                targetInventory.onOpen(player, prevInventory.onLinkRequestData());
                this.currentInventorySlot = page;
            });
        }
    }
}
