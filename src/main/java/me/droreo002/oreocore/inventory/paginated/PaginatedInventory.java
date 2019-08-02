package me.droreo002.oreocore.inventory.paginated;

import lombok.Getter;
import lombok.Setter;
import me.droreo002.oreocore.inventory.InventoryTemplate;
import me.droreo002.oreocore.inventory.button.GUIButton;
import me.droreo002.oreocore.inventory.OreoInventory;
import me.droreo002.oreocore.inventory.linked.Linkable;
import me.droreo002.oreocore.utils.inventory.GUIPattern;
import me.droreo002.oreocore.utils.inventory.Paginator;
import me.droreo002.oreocore.utils.item.CustomItem;
import me.droreo002.oreocore.utils.item.CustomSkull;
import me.droreo002.oreocore.utils.item.complex.UMaterial;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

import static me.droreo002.oreocore.utils.item.CustomItem.fromSection;
import static me.droreo002.oreocore.inventory.InventoryTemplate.*;

public abstract class PaginatedInventory extends OreoInventory implements Linkable {

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
        setupDefaultButton();
    }

    public PaginatedInventory(InventoryTemplate template) {
        super(template);
        this.paginatedButtons = new ArrayList<>();
        this.paginatedButtonSlots = new ArrayList<>();
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
                paginatedButtonSlots.add(i);
            }
        } else {
            int start = row * 9;
            int stop = start + 9;

            for (int i = start; i < stop; i++) {
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
            GUIButton informationButton = inventoryTemplate.getGUIButtons(PAGINATED_IB_KEY).get(0);

            setNextPageButton(nextPageButton);
            setPreviousPageButton(previousPageButton);
            setInformationButton(informationButton);

            setupDefaultButton(); // Setup the listener

            addButton(this.nextPageButton, true);
            addButton(this.previousPageButton, true);
            addButton(this.informationButton, true);

            setInformationButtonSlot(informationButton.getInventorySlot());
        }

        super.setup();
        if (paginatedButtons.isEmpty()) {
            this.currentPage = 0;
            this.totalPage = 1;
        } else {
            // Paginate
            this.buttonPaginator = new Paginator<>(new ArrayList<>(paginatedButtons));
            this.paginatedButtonResult = buttonPaginator.paginates(paginatedButtonSlots.size());

            // Specify more variables
            this.currentPage = 0;
            this.totalPage = buttonPaginator.totalPage(paginatedButtonSlots.size()) - 1; // Somehow returned <original + 1> not sure why.

            setupPaginatedButtons();
        }

        getButtons().forEach(but -> getInventory().setItem(but.getInventorySlot(), but.getItem()));
        updateInformationButton();
    }

    /**
     * Setup the paginated buttons
     */
    private void setupPaginatedButtons() {
        // Add the buttons
        int toGet = 0;
        for (int i : paginatedButtonSlots) {
            List<GUIButton> but = getCurrentPageButtons();
            ItemStack item;
            try {
                item = but.get(toGet).getItem();
            } catch (IndexOutOfBoundsException e) {
                toGet++;
                continue;
            }
            toGet++;
            if (item == null) continue;
            but.get(toGet - 1).setInventorySlot(i);
            getInventory().setItem(i, item);
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

        // Paginated button listener
        if (!getPaginatedButtons().isEmpty()) {
            // Convert to a HashMap where key is the slot
            Map<Integer, GUIButton> buttons = new HashMap<>();
            List<GUIButton> list = getCurrentPageButtons();
            int currSlot = 0;
            for (int i : getPaginatedButtonSlots()) {
                GUIButton b;
                try {
                    b = list.get(currSlot);
                } catch (IndexOutOfBoundsException e1) {
                    currSlot++;
                    continue;
                }
                buttons.put(i, b);
                currSlot++;
            }

            if (buttons.containsKey(slot)) {
                GUIButton.ButtonListener lis = buttons.get(slot).getListener();
                if (lis != null) lis.onClick(e);
            }
        }
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
        ItemStack infoButtonClone = informationButton.getItem().clone();
        if (!infoButtonClone.hasItemMeta()) return;
        ItemMeta meta = infoButtonClone.getItemMeta();
        if (meta.getLore() == null) return;

        List<String> temp = new ArrayList<>();
        for (String s : meta.getLore()) {
            temp.add(s.replaceAll("%currPage", String.valueOf(currentPage + 1)).replaceAll("%totalPage", String.valueOf(totalPage)));
        }
        meta.setLore(temp);
        infoButtonClone.setItemMeta(meta);
        getInventory().setItem(informationButtonSlot, infoButtonClone);
    }

    /**
     * Setup the button listener as default
     */
    private void setupDefaultButton() {
        // Default buttons
        if (this.informationButton == null) {
            this.informationButton = new GUIButton(new CustomItem(UMaterial.PAPER.getItemStack(), "&aInformation", new String[]{
                    "&7You're currently on page &a%currPage",
                    "&7there's in total of &a%totalPage &7pages!"
            }));
        }

        if (this.previousPageButton == null) {
            this.previousPageButton = new GUIButton(new CustomItem(CustomSkull.getSkullUrl(PREV_ARROW), "&aPrevious Page", new String[]{"&7Click me!"}));
        }

        if (this.nextPageButton == null) {
            this.nextPageButton = new GUIButton(new CustomItem(CustomSkull.getSkullUrl(NEXT_ARROW), "&aNext Page", new String[]{"&7Click me!"}));
        }

        this.previousPageButton.setListener(event -> {
            Player player = (Player) event.getWhoClicked();
            prevPage(player);
        });
        this.nextPageButton.setListener(event -> {
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
