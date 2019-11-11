package me.droreo002.oreocore.inventory.paginated;

import lombok.Getter;
import lombok.Setter;
import me.droreo002.oreocore.inventory.InventoryTemplate;
import me.droreo002.oreocore.inventory.button.ButtonListener;
import me.droreo002.oreocore.inventory.button.GUIButton;
import me.droreo002.oreocore.inventory.OreoInventory;
import me.droreo002.oreocore.inventory.linked.LinkedButton;
import me.droreo002.oreocore.utils.inventory.GUIPattern;
import me.droreo002.oreocore.utils.inventory.Paginator;
import me.droreo002.oreocore.utils.item.CustomItem;
import me.droreo002.oreocore.utils.item.CustomSkull;
import me.droreo002.oreocore.utils.item.complex.UMaterial;
import me.droreo002.oreocore.utils.list.Iterators;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

import static me.droreo002.oreocore.inventory.InventoryTemplate.*;

public abstract class PaginatedInventory extends OreoInventory {

    /*
    Variables
     */
    private static final String NEXT_ARROW = "http://textures.minecraft.net/texture/19bf3292e126a105b54eba713aa1b152d541a1d8938829c56364d178ed22bf";
    private static final String PREV_ARROW = "http://textures.minecraft.net/texture/bd69e06e5dadfd84e5f3d1c21063f2553b2fa945ee1d4d7152fdc5425bc12a9";

    @Getter @Setter
    private GUIButton informationButton, nextPageButton, previousPageButton;
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
        informationButton.setInventorySlot(informationSlot);

        addButton(previousPageButton, true);
        addButton(nextPageButton, true);
        addButton(informationButton, true);

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
        if (paginatedButtons.isEmpty()) {
            this.currentPage = 0;
            this.totalPage = 1;
        } else {
            // Paginate the buttons
            this.buttonPaginator = new Paginator<>(new ArrayList<>(paginatedButtons));

            this.paginatedButtonResult = Iterators.divideIterable(paginatedButtons, paginatedButtonSlots.size());
            this.currentPage = 0;
            this.totalPage = paginatedButtonResult.size();

            setupPaginatedButtons();
        }
        updateInformationButton();
    }

    /**
     * Setup the paginated buttons
     */
    private void setupPaginatedButtons() {
        // Add the buttons
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
     * The click handler, we add more thing into it, while keeping
     * the default one
     *
     * @param e : The click event object
     */
    @Override
    public void onClickHandler(InventoryClickEvent e) {
        super.onClickHandler(e); // This will handle the normal button click
        int slot = e.getSlot();

        GUIButton button = getPaginatedButton(slot);
        if (button != null) {
            List<ButtonListener> loadedListeners = button.getButtonListeners().get(e.getClick());
            if (loadedListeners != null) loadedListeners.forEach(buttonListener -> buttonListener.onClick(e));
        }
    }

    /**
     * Check if the item inside the slot is a paginated button
     *
     * @param slot The slot to check
     * @return true if paginated button, false otherwise
     */
    public boolean isPaginatedButton(int slot) {
        return getPaginatedButton(slot) != null;
    }

    /**
     * Get paginated button by slot
     *
     * @param slot The slot fo get
     * @return the paginated button as GUIButton if found, null otherwise
     */
    public GUIButton getPaginatedButton(int slot) {
        List<GUIButton> buttons = getCurrentPageButtons();

        for (GUIButton button : buttons) {
            if (button.getInventorySlot() == slot) {
                return button;
            }
        }
        return null;
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

        updateAttributes(player);
    }

    /**
     * Update the inventory attributes
     *
     * @param player The target player
     */
    private void updateAttributes(Player player) {
        if (getInventoryAnimation() != null) getInventoryAnimation().stopAnimation();
        setupPaginatedButtons();
        updateInformationButton();
        updateInventory(player);
        if (getInventoryAnimation() != null) getInventoryAnimation().startAnimation(this);
    }

    /**
     * Update the information button
     */
    private void updateInformationButton() {
        if (informationButton == null) return;
        ItemStack infoButtonClone = informationButton.getItem().clone();
        if (!infoButtonClone.hasItemMeta()) return;
        ItemMeta meta = infoButtonClone.getItemMeta();
        if (meta.getLore() == null) return;

        List<String> temp = new ArrayList<>();
        for (String s : meta.getLore()) {
            temp.add(s.replaceAll("%currPage%", String.valueOf(currentPage + 1)).replaceAll("%totalPage%", String.valueOf(totalPage)));
        }
        meta.setLore(temp);
        infoButtonClone.setItemMeta(meta);
        getInventory().setItem(informationButtonSlot, infoButtonClone);
    }

    /**
     * Setup the button listener as default
     */
    private void setupDefaultButton() {
        if (this.previousPageButton == null) {
            this.previousPageButton = new GUIButton(new CustomItem(CustomSkull.getSkullUrl(PREV_ARROW), "&aPrevious Page", new String[]{"&7Click me!"}));
        }

        if (this.nextPageButton == null) {
            this.nextPageButton = new GUIButton(new CustomItem(CustomSkull.getSkullUrl(NEXT_ARROW), "&aNext Page", new String[]{"&7Click me!"}));
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
}
