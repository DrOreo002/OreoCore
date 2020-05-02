package me.droreo002.oreocore.inventory.paginated;

import lombok.Getter;
import lombok.Setter;
import me.droreo002.oreocore.OreoCore;
import me.droreo002.oreocore.inventory.InventoryTemplate;
import me.droreo002.oreocore.inventory.button.GUIButton;
import me.droreo002.oreocore.inventory.OreoInventory;
import me.droreo002.oreocore.inventory.linked.Linkable;
import me.droreo002.oreocore.utils.inventory.GUIPattern;
import me.droreo002.oreocore.utils.inventory.InventoryTitleHelper;
import me.droreo002.oreocore.utils.inventory.InventoryUtils;
import me.droreo002.oreocore.utils.inventory.Paginator;
import me.droreo002.oreocore.utils.item.ItemStackBuilder;
import me.droreo002.oreocore.utils.item.CustomSkull;
import me.droreo002.oreocore.utils.item.complex.UMaterial;
import me.droreo002.oreocore.utils.item.helper.TextPlaceholder;
import me.droreo002.oreocore.utils.list.Iterators;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nullable;
import java.util.*;

import static me.droreo002.oreocore.inventory.InventoryTemplate.*;

public abstract class PaginatedInventory extends OreoInventory {

    /*
    Variables
     */
    private static final String NEXT_ARROW = "19bf3292e126a105b54eba713aa1b152d541a1d8938829c56364d178ed22bf";
    private static final String PREV_ARROW = "bd69e06e5dadfd84e5f3d1c21063f2553b2fa945ee1d4d7152fdc5425bc12a9";

    @Getter @Setter
    private GUIButton informationButton, nextPageButton, previousPageButton;
    @Getter @Setter
    private ItemStack noNextPageItem, noPreviousPageItem;
    @Getter @Setter
    private int totalPage, currentPage, informationButtonSlot;
    @Getter
    private Paginator<GUIButton> buttonPaginator;
    @Getter
    private List<List<GUIButton>> paginatedButtonResult;
    @Getter
    private List<Integer> paginatedButtonSlots;
    @Getter
    private List<GUIButton> paginatedButtons;

    public PaginatedInventory(int size, String title) {
        super(size, title);
        this.paginatedButtons = new ArrayList<>();
        this.paginatedButtonSlots = new ArrayList<>();
        this.paginatedButtonResult = new ArrayList<>();
        setupDefaultButton();
    }

    public PaginatedInventory(InventoryTemplate template) {
        super(template);
        this.paginatedButtons = new ArrayList<>();
        this.paginatedButtonSlots = new ArrayList<>();
        this.paginatedButtonResult = new ArrayList<>();
        setupDefaultButton();
    }

    /**
     * Set the item slot. This is good if you want to make a spaced or maybe
     * other design inventory
     *
     * @param rows : The rows
     */
    public void setItemRow(int... rows) {
        for (int i : rows) {
            setItemRow(i);
        }
    }

    @Override
    public void clear() {
        if (this instanceof Linkable) {
            Linkable linkable = (Linkable) this;
            if (linkable.getLinkedButtons() != null) linkable.getLinkedButtons().clear();
        }
        this.paginatedButtonResult.clear();
        this.paginatedButtons.clear();

        this.currentPage = 0;
        this.totalPage = 0;

        for (int i : this.paginatedButtonSlots) {
            getInventory().setItem(i, UMaterial.AIR.getItemStack());
        }
    }

    /**
     * Reload the paginated buttons
     */
    private void reloadPaginatedButtons() {
        int toGet = 0;
        for (int i : paginatedButtonSlots) {
            List<GUIButton> buttons = getCurrentPageButtons();
            ItemStack item;
            try {
                item = buttons.get(toGet).getItem();
            } catch (IndexOutOfBoundsException e) {
                break;
            }
            buttons.get(toGet).setInventorySlot(i);
            if (item != null) getInventory().setItem(i, item);
            toGet++;
        }
    }

    /**
     * Set the item slot to that inventory row
     *
     * @param row : The inventory row
     */
    private void setItemRow(int row) {
        if (row == 0) {
            for (int i = 0; i < 9; i++) {
                if (paginatedButtonSlots.contains(i)) continue;
                paginatedButtonSlots.add(i);
            }
        } else {
            int start = row * 9;
            int stop = start + 9;

            for (int i = start; i < stop; i++) {
                if (paginatedButtonSlots.contains(i)) continue;
                paginatedButtonSlots.add(i);
            }
        }
    }

    /**
     * Add a paginated or a list button into the inventory
     *
     * @param button : The button object
     */
    public void addPaginatedButton(GUIButton button) {
        paginatedButtons.add(button);
    }

    /**
     * Set that row into the search row of the inventory
     * where there will be buttons and an information button
     *
     * @param row : The row on the inventory
     * @param addBorder : Add border into the empty slot on that row or not
     */
    public void setSearchRow(int row, boolean addBorder, ItemStack border) {
        if (row < 0) throw new IllegalStateException("Row cannot be less than 0!");
        int backSlot = row * 9;
        int nextSlot = (row * 9) + 8;
        int informationSlot = (nextSlot + backSlot) / 2;

        informationButtonSlot = informationSlot;

        previousPageButton.setInventorySlot(backSlot);
        nextPageButton.setInventorySlot(nextSlot);

        addButton(previousPageButton, true);
        addButton(nextPageButton, true);

        if (informationButton != null) {
            informationButton.setInventorySlot(informationSlot);
            addButton(informationButton, true);
        }

        if (addBorder) {
            for (int i = backSlot; i < nextSlot; i++) {
                if (getInventory().getItem(i) == null) getInventory().setItem(i, border);
            }
        }
    }

    /**
     * Add an patterned button. Useful if you want to make it 1 liner or something
     *
     * @param pat : The pattern object
     * @param replaceIfExists : Replace the item on the inventory if it exists already on that slot
     */
    public void addPatternButton(boolean replaceIfExists, GUIPattern... pat) {
        for (GUIPattern pt : pat) {
            addButton(pt.getButton(), replaceIfExists);
        }
    }

    /**
     * Setup the inventory
     */
    @Override
    public void setup() {
        final InventoryTemplate inventoryTemplate = getInventoryTemplate();
        if (inventoryTemplate != null) {
            if (!inventoryTemplate.isPaginatedInventory()) throw new IllegalStateException("Inventory data is not for PaginatedInventory!");
            inventoryTemplate.getPaginatedItemRow().forEach(this::setItemRow);

            GUIButton nextPageButton = inventoryTemplate.getGUIButtons(PAGINATED_NB_KEY).get(0);
            GUIButton previousPageButton = inventoryTemplate.getGUIButtons(PAGINATED_PB_KEY).get(0);
            List<GUIButton> info = inventoryTemplate.getGUIButtons(PAGINATED_IB_KEY);

            setNextPageButton(nextPageButton);
            setPreviousPageButton(previousPageButton);

            setupDefaultButton(); // Setup the listener

            if (!info.isEmpty()) {
                GUIButton informationButton = info.get(0);
                setInformationButton(informationButton);
                setInformationButtonSlot(informationButton.getInventorySlot());
                addButton(this.informationButton, true);
            }

            addButton(this.nextPageButton, true);
            addButton(this.previousPageButton, true);
        }

        super.setup();
        setupPaginatedButtons();
        updatePageInformation();
        if (totalPage > 1) {
            getInventory().setItem(nextPageButton.getInventorySlot(), nextPageButton.getItem());
        } else {
            getInventory().setItem(nextPageButton.getInventorySlot(), noNextPageItem);
        }
        getInventory().setItem(previousPageButton.getInventorySlot(), noPreviousPageItem); // Always null because inventory will open on first slot
    }

    /**
     * Setup the paginated buttons
     */
    private void setupPaginatedButtons() {
        if (paginatedButtons.isEmpty()) {
            this.currentPage = 0;
            this.totalPage = 1;
        } else {
            // Paginate the buttons
            this.buttonPaginator = new Paginator<>(new ArrayList<>(paginatedButtons));

            this.paginatedButtonResult = Iterators.divideIterable(paginatedButtons, paginatedButtonSlots.size());
            this.currentPage = 0;
            this.totalPage = paginatedButtonResult.size();
            reloadPaginatedButtons();
        }
    }

    /**
     * Refresh this inventory's paginated button and other things
     */
    @Override
    public void refresh() {
        setup();
        InventoryUtils.updateInventoryViewer(getInventory());
    }

    /**
     * Check if the item inside the slot is a paginated button
     *
     * @param slot The slot to check
     * @return true if paginated button, false otherwise
     */
    public boolean isPaginatedButton(int slot) {
        return getButton(slot) != null;
    }

    /**
     * Go into the next page
     *
     * @param player : The targeted player
     */
    private void nextPage(Player player) {
        if (totalPage == 0 || (currentPage + 1) >= totalPage) {
            // No next page
            return;
        }
        this.currentPage += 1;
        for (int i : paginatedButtonSlots) {
            if (getInventory().getItem(i) != null) getInventory().setItem(i, UMaterial.AIR.getItemStack());
        }

        if ((currentPage + 1) >= totalPage) {
            // This current page is the last page
            getInventory().setItem(nextPageButton.getInventorySlot(), noNextPageItem);
        }
        getInventory().setItem(previousPageButton.getInventorySlot(), previousPageButton.getItem());
        updateAttributes(player);
    }

    /**
     * Go into the prev page
     *
     * @param player : The targeted player
     */
    private void prevPage(Player player) {
        if (totalPage == 0 || (currentPage - 1) < 0) {
            // No prev page
            return;
        }
        this.currentPage -= 1;
        for (int i : paginatedButtonSlots) {
            if (getInventory().getItem(i) != null) getInventory().setItem(i, UMaterial.AIR.getItemStack());
        }

        if (currentPage <= 0) {
            // This current page is the first page
            getInventory().setItem(previousPageButton.getInventorySlot(), noPreviousPageItem);
        }
        getInventory().setItem(nextPageButton.getInventorySlot(), nextPageButton.getItem());

        updateAttributes(player);
    }

    /**
     * Update the inventory attributes
     *
     * @param player The target player
     */
    private void updateAttributes(Player player) {
        if (getInventoryAnimationManager() != null) getInventoryAnimationManager().stopAnimation();
        reloadPaginatedButtons();
        updatePageInformation();
        updateInventory(player);
        if (getInventoryAnimationManager() != null) getInventoryAnimationManager().startAnimation(this);
    }

    @Override
    public void onOpenHandler(InventoryOpenEvent e) {
        super.onOpenHandler(e);
        updatePageInformation();
    }

    /**
     * Update the information button
     */
    public void updatePageInformation() {
        TextPlaceholder placeholder = TextPlaceholder.of("%currPage%", currentPage + 1).add("%currentPage%", currentPage + 1).add("%totalPage%", totalPage).add("%maxPage%", totalPage);
        getInventory().getViewers().forEach(view -> {
            Bukkit.getScheduler().scheduleSyncDelayedTask(OreoCore.getInstance(), () -> InventoryTitleHelper.updateTitle((Player) view, placeholder.format(getTitle())), 1L);
        });
        if (informationButton == null) return;
        ItemStack infoButtonClone = placeholder.format(informationButton.getItem());
        if (!(this.totalPage > 64)) {
            infoButtonClone.setAmount(currentPage + 1);
        }
        getInventory().setItem(informationButtonSlot, infoButtonClone);
    }

    /**
     * Setup the button listener as default
     */
    private void setupDefaultButton() {
        if (this.previousPageButton == null) {
            this.previousPageButton = new GUIButton(ItemStackBuilder.of(CustomSkull.fromUrl(PREV_ARROW))
                    .setDisplayName("&aPrevious Page")
                    .setLore("&7Click me!")
                    .getItemStack());
        }

        if (this.nextPageButton == null) {
            this.nextPageButton = new GUIButton(ItemStackBuilder.of(CustomSkull.fromUrl(NEXT_ARROW))
                    .setDisplayName("&aNext Page")
                    .setLore("&7Click me!")
                    .getItemStack());
        }

        this.previousPageButton.addListener(event -> {
            Player player = (Player) event.getWhoClicked();
            prevPage(player);
        });
        this.nextPageButton.addListener(event -> {
            Player player = (Player) event.getWhoClicked();
            nextPage(player);
        });
    }

    /**
     * Get the current page buttons
     *
     * @return The current page buttons
     */
    public List<GUIButton> getCurrentPageButtons() {
        if (paginatedButtonResult.isEmpty()) return new ArrayList<>();
        return paginatedButtonResult.get(currentPage);
    }

    @Nullable
    @Override
    public GUIButton getButton(int slot) {
        GUIButton button = super.getButton(slot);
        return (button == null) ? getCurrentPageButtons().stream().filter(paginatedButton -> paginatedButton.getInventorySlot() == slot).findAny().orElse(null) : button;
    }
}
