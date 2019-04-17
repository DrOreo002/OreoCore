package me.droreo002.oreocore.inventory.api.paginated;

import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainTasks;
import lombok.Getter;
import lombok.Setter;
import me.droreo002.oreocore.OreoCore;
import me.droreo002.oreocore.enums.Sounds;
import me.droreo002.oreocore.enums.XMaterial;
import me.droreo002.oreocore.inventory.api.GUIButton;
import me.droreo002.oreocore.utils.item.CustomItem;
import me.droreo002.oreocore.utils.inventory.GUIPattern;
import me.droreo002.oreocore.utils.inventory.Paginator;
import me.droreo002.oreocore.utils.item.CustomSkull;
import me.droreo002.oreocore.utils.misc.SoundObject;
import me.droreo002.oreocore.utils.misc.ThreadingUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

public abstract class PaginatedInventory implements InventoryHolder {

    /*
    Variables
     */
    private static final String NEXT_ARROW = "http://textures.minecraft.net/texture/19bf3292e126a105b54eba713aa1b152d541a1d8938829c56364d178ed22bf";
    private static final String PREV_ARROW = "http://textures.minecraft.net/texture/bd69e06e5dadfd84e5f3d1c21063f2553b2fa945ee1d4d7152fdc5425bc12a9";

    private final OreoCore main = OreoCore.getInstance();
    private Inventory inventory;

    @Getter
    @Setter
    private GUIButton informationButton, nextButton, backButton;
    @Getter
    private String title;
    @Getter
    private int size, totalPage, currentPage, informationButtonSlot;
    @Getter
    @Setter
    private SoundObject openSound, closeSound, clickSound;

    /*
    Lists
     */
    @Getter
    private Paginator<GUIButton> paginator;
    @Getter
    private List<Integer> itemSlot;
    @Getter
    private List<List<GUIButton>> items;
    @Getter
    private List<GUIButton> paginatedButton;
    @Getter
    private Map<Integer, GUIButton> inventoryButton;

    public PaginatedInventory(int size, String title) {
        this.title = title;
        this.size = size;
        this.inventory = Bukkit.createInventory(this, size, title);

        /*
        Default values
         */
        openSound = new SoundObject(Sounds.BAT_TAKEOFF);
        closeSound = new SoundObject(Sounds.BAT_TAKEOFF);
        clickSound = new SoundObject(Sounds.CLICK);
        itemSlot = new ArrayList<>();
        paginatedButton = new ArrayList<>();
        inventoryButton = new HashMap<>();

        informationButton = new GUIButton(new CustomItem(XMaterial.PAPER.parseItem(false), "&7[ &bInformation &7]", new String[] {
                "&8&m------------------",
                "&r",
                "&fYou're currently on page &c%currPage",
                "&fThere's a total of &c%totalPage &fpages!"
        }));
        try {
            this.backButton = new GUIButton(new CustomItem(CustomSkull.getSkullUrl(PREV_ARROW), "&7[ &bPrevious Page &7]")).setListener(event -> {
                Player player = (Player) event.getWhoClicked();
                prevPage(player);
            });
            this.nextButton = new GUIButton(new CustomItem(CustomSkull.getSkullUrl(NEXT_ARROW), "&7[ &bNext Page &7]")).setListener(event -> {
                Player player = (Player) event.getWhoClicked();
                nextPage(player);
            });
        } catch (Exception e) {
            this.backButton = new GUIButton(new CustomItem(XMaterial.ARROW.parseItem(false), "&7[ &bPrevious Page &7]")).setListener(event -> {
                Player player = (Player) event.getWhoClicked();
                prevPage(player);
            });
            this.nextButton = new GUIButton(new CustomItem(XMaterial.ARROW.parseItem(false), "&7[ &bNext Page &7]")).setListener(event -> {
                Player player = (Player) event.getWhoClicked();
                nextPage(player);
            });
        }
    }

    /**
     * Set the item slot. This is good if you want to make a spaced or maybe
     * other design inventory
     *
     * @param rows : The rows
     */
    public void setItemRow(Integer... rows) {
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
                itemSlot.add(i);
            }
        } else {
            int start = row * 9;
            int stop = start + 9;

            for (int i = start; i < stop; i++) {
                itemSlot.add(i);
            }
        }
    }

    /**
     * Add a paginated or a list button into the inventory
     *
     * @param button : The button object
     */
    public void addPaginatedButton(GUIButton button) {
        paginatedButton.add(button);
    }

    /**
     * Add a normal button into the inventory with specified slot
     *
     * @param button : The button object
     * @param slot : The slot
     * @param replaceIfExist : Replace the item on the inventory if it exist already
     */
    public void addNormalButton(GUIButton button, int slot, boolean replaceIfExist) {
        if (replaceIfExist) {
            if (inventoryButton.containsKey(slot)) {
                inventoryButton.remove(slot);
                inventory.setItem(slot, new ItemStack(Material.AIR));
                inventoryButton.put(slot, button);
            } else {
                inventory.setItem(slot, new ItemStack(Material.AIR));
                inventoryButton.put(slot, button);
            }
        } else {
            // We don't want to add duplicated slot. Because its not replace if exist, so it also not going to replace
            // on the hashmap
            if (!inventoryButton.containsKey(slot)) {
                inventoryButton.put(slot, button);
            }
        }
    }

    /**
     * Set that row into the search row of the inventory
     * where there will be buttons and an information button
     *
     * @param row : The row on the inventory
     * @param addBorder : Add border into the empty slot on that row or not
     */
    public void setSearchRow(int row, boolean addBorder, ItemStack border) {
        int backSlot = row * 9;
        int nextSlot = (row * 9) + 8;
        int informationSlot = (nextSlot + backSlot) / 2;

        this.informationButtonSlot = informationSlot;
        addNormalButton(backButton, backSlot, true);
        addNormalButton(nextButton, nextSlot, true);
        addNormalButton(informationButton, informationSlot, true);

        if (addBorder) {
            for (int i = backSlot; i < nextSlot; i++) {
                if (inventory.getItem(i) == null) {
                    inventory.setItem(i, border);
                }
            }
        }
    }

    /**
     * Fill that row with the specified item. Will replace any item if there's any on that slot
     *
     * @param row : The inventory row
     * @param item : The item
     * @param replaceIfExist : Replace the item on that slot if there's any?
     */
    public void addBorder(int row, GUIButton item, boolean replaceIfExist) {
        for (int i = 9 * row; i < row + 9; i++) {
            if (replaceIfExist) {
                addNormalButton(item, i, true);
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
            addNormalButton(pt.getButton(), pt.getSlot(), replaceIfExists);
        }
    }

    /**
     * Open the inventory for the specified player
     *
     * @param player : The targeted player
     */
    public void open(Player player) {
        if (paginatedButton.isEmpty()) {
            this.currentPage = 0;
            this.totalPage = 1;

            for (Map.Entry ent : inventoryButton.entrySet()) {
                int slot = (int) ent.getKey();
                GUIButton button = (GUIButton) ent.getValue();
                inventory.setItem(slot, button.getItem());
            }

            updateInformationButton();
            main.getOpening().remove(player.getUniqueId());
            main.getOpening().put(player.getUniqueId(), this);
            Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> player.openInventory(inventory), 1L);
            return;
        }
        // Paginate
        this.paginator = new Paginator<>(paginatedButton);
        this.items = paginator.paginates(itemSlot.size());
        // Specify more variables
        this.currentPage = 0;
        this.totalPage = paginator.totalPage(itemSlot.size()) - 1; // Somehow returned <original + 1> not sure why.

        // Add the items
        int toGet = 0;
        for (int i : itemSlot) {
            List<GUIButton> but = items.get(currentPage);
            ItemStack item;
            try {
                item = but.get(toGet).getItem();
            } catch (IndexOutOfBoundsException e) {
                toGet++;
                continue;
            }
            toGet++;
            if (item == null) continue;
            inventory.setItem(i, item);
        }

        for (Map.Entry ent : inventoryButton.entrySet()) {
            int slot = (int) ent.getKey();
            GUIButton button = (GUIButton) ent.getValue();
            inventory.setItem(slot, button.getItem());
        }

        updateInformationButton();
        main.getOpening().remove(player.getUniqueId());
        main.getOpening().put(player.getUniqueId(), this);
        Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> player.openInventory(inventory), 1L);
    }

    /**
     * Open the inventory for the specified player
     *
     * @param player : The targeted player
     */
    public void openAsync(Player player, int delayInSecond) {
        Bukkit.getScheduler().runTaskLater(OreoCore.getInstance(), () -> {
            TaskChain<Inventory> chain = ThreadingUtils.makeChain();
            chain.delay(delayInSecond, TimeUnit.SECONDS).asyncFirst(() -> {
                if (paginatedButton.isEmpty()) {
                    PaginatedInventory.this.currentPage = 0;
                    PaginatedInventory.this.totalPage = 1;

                    for (Map.Entry ent : inventoryButton.entrySet()) {
                        int slot = (int) ent.getKey();
                        GUIButton button = (GUIButton) ent.getValue();
                        inventory.setItem(slot, button.getItem());
                    }

                    updateInformationButton();
                    main.getOpening().remove(player.getUniqueId());
                    main.getOpening().put(player.getUniqueId(), PaginatedInventory.this);
                    return inventory;
                }
                // Paginate
                PaginatedInventory.this.paginator = new Paginator<>(paginatedButton);
                PaginatedInventory.this.items = paginator.paginates(itemSlot.size());
                // Specify more variables
                PaginatedInventory.this.currentPage = 0;
                PaginatedInventory.this.totalPage = paginator.totalPage(itemSlot.size()) - 1; // Somehow returned <original + 1> not sure why.

                // Add the items
                int toGet = 0;
                for (int i : itemSlot) {
                    List<GUIButton> but = items.get(currentPage);
                    ItemStack item;
                    try {
                        item = but.get(toGet).getItem();
                    } catch (IndexOutOfBoundsException e) {
                        toGet++;
                        continue;
                    }
                    toGet++;
                    if (item == null) continue;
                    inventory.setItem(i, item);
                }

                for (Map.Entry ent : inventoryButton.entrySet()) {
                    int slot = (int) ent.getKey();
                    GUIButton button = (GUIButton) ent.getValue();
                    inventory.setItem(slot, button.getItem());
                }

                updateInformationButton();
                main.getOpening().remove(player.getUniqueId());
                main.getOpening().put(player.getUniqueId(), PaginatedInventory.this);

                return inventory;
            }).asyncLast(player::openInventory).execute((e, task) -> {
                System.out.println("Got an error! " + e.getMessage());
                e.printStackTrace();
            });
        }, 1L);
    }

    /**
     * Go into the next page
     *
     * @param player : The targeted player
     */
    public void nextPage(Player player) {
        if (totalPage == 0 || (currentPage + 1) >= totalPage) {
            // No next page
            return;
        }
        this.currentPage += 1;
        for (int i : itemSlot) {
            if (inventory.getItem(i) != null) {
                inventory.setItem(i, new ItemStack(Material.AIR));
            }
        }

        // Add the items
        int toGet = 0;
        for (int i : itemSlot) {
            List<GUIButton> but = items.get(currentPage);
            ItemStack item;
            try {
                item = but.get(toGet).getItem();
            } catch (IndexOutOfBoundsException e) {
                toGet++;
                continue;
            }
            toGet++;
            if (item == null) continue;
            inventory.setItem(i, item);
        }

        updateInformationButton();
        updateInventory(player);
    }

    /**
     * Go into the prev page
     *
     * @param player : The targeted player
     */
    public void prevPage(Player player) {
        if (totalPage == 0 || (currentPage - 1) < 0) {
            // No prev page
            return;
        }
        this.currentPage -= 1;
        for (int i : itemSlot) {
            if (inventory.getItem(i) != null) {
                inventory.setItem(i, new ItemStack(Material.AIR));
            }
        }

        // Add the items
        int toGet = 0;
        for (int i : itemSlot) {
            int fixedCurrentPage = (currentPage == 0) ? currentPage : (currentPage - 1);
            List<GUIButton> but = items.get(fixedCurrentPage);
            ItemStack item;
            try {
                item = but.get(toGet).getItem();
            } catch (IndexOutOfBoundsException e) {
                toGet++;
                continue;
            }
            toGet++;
            if (item == null) continue;
            inventory.setItem(i, item);
        }

        updateInformationButton();
        updateInventory(player);
    }

    /**
     * Update the inventory, scheduled 1 tick to prevent duplication glitch
     *
     * @param player : The target player
     */
    private void updateInventory(Player player) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(main, player::updateInventory, 1L);
    }

    /**
     * Update the information button
     */
    private void updateInformationButton() {
        ItemStack infoButtonClone = informationButton.getItem().clone();
        ItemMeta meta = infoButtonClone.getItemMeta();
        List<String> temp = new ArrayList<>();
        for (String s : meta.getLore()) {
            temp.add(s.replaceAll("%currPage", String.valueOf(currentPage + 1)).replaceAll("%totalPage", String.valueOf(totalPage)));
        }
        meta.setLore(temp);
        infoButtonClone.setItemMeta(meta);
        inventory.setItem(informationButtonSlot, infoButtonClone);
    }

    /**
     * Close the player's inventory, scheduled 1 tick to prevent duplication glitch
     *
     * @param player : Target player
     */
    public void close(Player player) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(OreoCore.getInstance(), player::closeInventory, 1L);
    }

    /**
     * Open an inventory for the player, scheduled 1 tick to prevent duplication glitch
     *
     * @param player : Target player
     * @param inventory : Inventory to open
     */
    public void open(Player player, Inventory inventory) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(OreoCore.getInstance(), () -> player.openInventory(inventory), 1L);
    }

    /**
     * Close the player's inventory, scheduled 1 tick to prevent duplication glitch. This will also play sounds
     *
     * @param player : Target player
     * @param soundWhenClose : The sound that will get played when the inventory closes
     */
    public void close(Player player, SoundObject soundWhenClose) {
        if (soundWhenClose != null) {
            soundWhenClose.send(player);
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(OreoCore.getInstance(), player::closeInventory, 1L);
    }

    /**
     * Open an inventory for the player, scheduled 1 tick to prevent duplication glitch. This will also play sounds
     *
     * @param player : Target player
     * @param inventory : The inventory
     * @param soundWhenOpen : The shounds that will get played when the inventory opens
     */
    public void open(Player player, Inventory inventory, SoundObject soundWhenOpen) {
        if (soundWhenOpen != null) {
            soundWhenOpen.send(player);
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(OreoCore.getInstance(), () -> player.openInventory(inventory), 1L);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
