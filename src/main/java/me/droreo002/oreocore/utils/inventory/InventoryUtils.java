package me.droreo002.oreocore.utils.inventory;

import me.droreo002.oreocore.utils.item.CustomItem;
import me.droreo002.oreocore.utils.item.complex.UMaterial;
import me.droreo002.oreocore.utils.misc.SoundObject;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class InventoryUtils {

    /**
     * Check if player's inventory is full
     *
     * @param inventory The inventory to check
     * @return true if full, false otherwise
     */
    public static boolean isInventoryFull(Inventory inventory) {
        return inventory.firstEmpty() == -1;
    }

    /**
     * Check if player's inventory is empty
     *
     * @param inventory The inventory to check
     * @return true if empty, false otherwise
     */
    public static boolean isInventoryEmpty(Inventory inventory) {
        return inventory.firstEmpty() == 0;
    }

    /**
     * Get the content of the inventory
     *
     * @param inventory Inventory to check
     * @return the content
     */
    public static List<ItemStack> getContent(Inventory inventory) {
        final List<ItemStack> items = new ArrayList<>();
        for (ItemStack i : inventory.getContents()) {
            if (CustomItem.isEmpty(i) || i.getType().equals(UMaterial.AIR.getMaterial())) continue;
            items.add(i);
        }
        return items;
    }

    /**
     * Returns the amount of the item inside the inventory
     *
     * @param item      Item to check
     * @param inventory inventory
     * @return amount of the item
     */
    public static int getAmount(ItemStack item, Inventory inventory) {
        if (!inventory.contains(item.getType())) {
            return 0;
        }

        if (inventory.getType() == null) {
            return Integer.MAX_VALUE;
        }

        HashMap<Integer, ? extends ItemStack> items = inventory.all(item.getType());
        int itemAmount = 0;

        for (ItemStack iStack : items.values()) {
            if (!CustomItem.isSimilar(iStack, item)) {
                continue;
            }

            itemAmount += iStack.getAmount();
        }

        return itemAmount;
    }

    /**
     * Tells if the inventory is empty
     *
     * @param inventory inventory
     * @return Is the inventory empty?
     */
    public static boolean isEmpty(Inventory inventory) {
        for (ItemStack stack : getContent(inventory)) {
            if (stack != null) {
                return false;
            }
        }

        return true;
    }

    /**
     * Count amount of empty slots in an inventory
     *
     * @param inventory the inventory
     * @return The amount of empty slots
     */
    public static int countEmpty(Inventory inventory) {
        int emptyAmount = 0;
        for (ItemStack stack : getContent(inventory)) {
            if (CustomItem.isEmpty(stack)) {
                emptyAmount++;
            }
        }

        return emptyAmount;
    }

    /**
     * Checks if the inventory has stock of this type
     *
     * @param items     items
     * @param inventory inventory
     * @return Does the inventory contain stock of this type?
     */
    public static boolean hasItems(ItemStack[] items, Inventory inventory) {
        ItemStack[] mergedItems = mergeSimilarStacks(items);
        for (ItemStack item : mergedItems) {
            if (getAmount(item, inventory) < item.getAmount()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks if items fit in the inventory
     *
     * @param items     Items to check
     * @param inventory inventory
     * @return Do the items fit inside the inventory?
     */
    public static boolean fits(ItemStack[] items, Inventory inventory) {
        ItemStack[] mergedItems = mergeSimilarStacks(items);
        for (ItemStack item : mergedItems) {
            if (!fits(item, inventory)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks if the item fits the inventory
     *
     * @param item      Item to check
     * @param inventory inventory
     * @return Does item fit inside inventory?
     */
    public static boolean fits(ItemStack item, Inventory inventory) {
        int left = item.getAmount();

        if (inventory.getSize() == Integer.MAX_VALUE) {
            return true;
        }

        if (!getContent(inventory).isEmpty()) {
            for (ItemStack iStack : getContent(inventory)) {
                if (left <= 0) {
                    return true;
                }

                if (CustomItem.isEmpty(iStack)) {
                    left -= item.getMaxStackSize();
                    continue;
                }

                if (!CustomItem.isSimilar(iStack, item)) {
                    continue;
                }

                left -= (iStack.getMaxStackSize() - iStack.getAmount());
            }
        } else {
            return true;
        }

        return left <= 0;
    }

    /**
     * Transfers an item from one inventory to another one
     *
     * @param item              Item to transfer
     * @param sourceInventory   Inventory to transfer the item from
     * @param targetInventory   Inventory to transfer the item to
     * @return Number of leftover items
     */
    public static int transfer(ItemStack item, Inventory sourceInventory, Inventory targetInventory) {
        return transfer(item, sourceInventory, targetInventory, item.getMaxStackSize());
    }

    /**
     * Transfers an item from one inventory to another one
     *
     * @param item              Item to transfer
     * @param sourceInventory   Inventory to transfer the item from
     * @param targetInventory   Inventory to transfer the item to
     * @param maxStackSize      Maximum item's stack size
     * @return Number of leftover items
     */
    public static int transfer(ItemStack item, Inventory sourceInventory, Inventory targetInventory, int maxStackSize) {
        if (item.getAmount() < 1) {
            return 0;
        }

        int amount = item.getAmount();
        for (ItemStack currentItem : sourceInventory) {
            if (CustomItem.isSimilar(currentItem, item)) {
                ItemStack clone = currentItem.clone();
                if (currentItem.getAmount() >= amount) {
                    clone.setAmount(amount);
                    amount = 0;
                } else {
                    clone.setAmount(currentItem.getAmount());
                    amount -= clone.getAmount();
                }
                int leftOver = add(clone, targetInventory, maxStackSize);
                if (leftOver > 0) {
                    currentItem.setAmount(currentItem.getAmount() - clone.getAmount() + leftOver);
                    if (amount > 0) {
                        amount += leftOver;
                    } else {
                        return leftOver;
                    }
                } else {
                    currentItem.setAmount(currentItem.getAmount() - clone.getAmount());
                }
            }
            if (amount <= 0) {
                break;
            }
        }
        return amount;
    }

    /**
     * Adds an item to the inventory with given maximum stack size
     *
     * @param item         Item to add
     * @param inventory    Inventory
     * @param maxStackSize Maximum item's stack size
     * @return Number of leftover items
     */
    public static int add(ItemStack item, Inventory inventory, int maxStackSize) {
        if (item.getAmount() < 1) {
            return 0;
        }

        if (maxStackSize == item.getMaxStackSize()) {
            return add(item, inventory);
        }

        return addManually(item, inventory, maxStackSize);
    }

    private static int addManually(ItemStack item, Inventory inventory, int maxStackSize) {
        int amountLeft = item.getAmount();

        for (int currentSlot = 0; currentSlot < effectiveSize(inventory) && amountLeft > 0; currentSlot++) {
            ItemStack currentItem = inventory.getItem(currentSlot);

            if (CustomItem.isEmpty(currentItem)) {
                currentItem = new ItemStack(item);
                currentItem.setAmount(Math.min(amountLeft, maxStackSize));
                inventory.setItem(currentSlot, currentItem);

                amountLeft -= currentItem.getAmount();
            } else if (currentItem.getAmount() < maxStackSize && CustomItem.isSimilar(currentItem, item)) {
                int neededToAdd = Math.min(maxStackSize - currentItem.getAmount(), amountLeft);

                currentItem.setAmount(currentItem.getAmount() + neededToAdd);

                amountLeft -= neededToAdd;
            }
        }
        return amountLeft;
    }

    // Don't use the armor slots or extra slots
    private static int effectiveSize(Inventory inventory) {
        return getContent(inventory).size();
    }

    /**
     * Adds an item to the inventor
     *
     * @param item      Item to add
     * @param inventory Inventory
     * @return Number of leftover items
     */
    public static int add(ItemStack item, Inventory inventory) {
        Map<Integer, ItemStack> leftovers = inventory.addItem(item.clone()); // item needs to be cloned as cb changes the amount of the stack size

        if (!leftovers.isEmpty()) {
            for (Iterator<ItemStack> iterator = leftovers.values().iterator(); iterator.hasNext(); ) {
                ItemStack left = iterator.next();
                int amountLeft = addManually(left, inventory, left.getMaxStackSize());
                if (amountLeft == 0) {
                    iterator.remove();
                } else {
                    left.setAmount(amountLeft);
                }
            }
        }

        return countItems(leftovers);
    }

    /**
     * Removes an item from the inventory
     *
     * @param item      Item to remove
     * @param inventory Inventory
     * @return Number of items that couldn't be removed
     */
    public static int remove(ItemStack item, Inventory inventory) {
        Map<Integer, ItemStack> leftovers = inventory.removeItem(item);

        if (!leftovers.isEmpty()) {
            leftovers.values().removeIf(left -> removeManually(left, inventory) == 0);
        }

        return countItems(leftovers);
    }

    private static int removeManually(ItemStack item, Inventory inventory) {
        int amountLeft = item.getAmount();

        for (int currentSlot = 0; currentSlot < effectiveSize(inventory) && amountLeft > 0; currentSlot++) {
            ItemStack currentItem = inventory.getItem(currentSlot);

            if (currentItem != null && CustomItem.isSimilar(currentItem, item)) {
                int neededToRemove = Math.min(currentItem.getAmount(), amountLeft);

                currentItem.setAmount(currentItem.getAmount() - neededToRemove);
                inventory.setItem(currentSlot, currentItem);

                amountLeft -= neededToRemove;
            }
        }
        return amountLeft;
    }

    /**
     * If items in arguments are similar, this function merges them into stacks of the same type
     *
     * @param items Items to merge
     * @return Merged stack array
     */
    public static ItemStack[] mergeSimilarStacks(ItemStack... items) {
        if (items.length <= 1) {
            return items;
        }

        List<ItemStack> itemList = new LinkedList<>();

        Iterating:
        for (ItemStack item : items) {
            for (ItemStack iStack : itemList) {
                if (CustomItem.isSimilar(item, iStack)) {
                    iStack.setAmount(iStack.getAmount() + item.getAmount());
                    continue Iterating;
                }
            }

            itemList.add(item.clone());
        }

        return itemList.toArray(new ItemStack[itemList.size()]);
    }

    /**
     * Counts the amount of items in ItemStacks
     *
     * @param items ItemStacks of items to count
     * @return How many items are there?
     */
    public static int countItems(ItemStack... items) {
        int count = 0;

        for (ItemStack item : items) {
            count += item.getAmount();
        }

        return count;
    }

    /**
     * Counts leftovers from a map
     *
     * @param items Leftovers
     * @return Number of leftovers
     */
    public static int countItems(Map<Integer, ItemStack> items) {
        int totalLeft = 0;

        for (ItemStack left : items.values()) {
            totalLeft += left.getAmount();
        }

        return totalLeft;
    }

    /**
     * Get an array of different item stacks that are properly stacked to their max stack size
     *
     * @param items The items to stack
     * @return An array of item stacks which's amount is a maximum of the allowed stack size
     */
    public static ItemStack[] getItemsStacked(ItemStack... items) {
        List<ItemStack> stackedItems = new LinkedList<>();
        for (ItemStack item : items) {
            int maxStackSize = item.getMaxStackSize();
            if (maxStackSize == 0) {
                continue;
            }
            if (item.getAmount() <= maxStackSize) {
                stackedItems.add(item.clone());
                continue;
            }
            for (int i = 0; i < Math.floor(item.getAmount() / maxStackSize); i++) {
                ItemStack itemClone = item.clone();
                itemClone.setAmount(maxStackSize);
                stackedItems.add(itemClone);
            }
            if (item.getAmount() % maxStackSize != 0) {
                ItemStack rest = item.clone();
                rest.setAmount(item.getAmount() % maxStackSize);
                stackedItems.add(rest);
            }
        }
        return stackedItems.toArray(new ItemStack[stackedItems.size()]);
    }

    /**
     * Update the inventory viewer
     *
     * @param inventory The inventory to update
     */
    public static void updateInventoryViewer(Inventory inventory) {
        for (HumanEntity entity : inventory.getViewers()) {
            if (entity instanceof Player) {
                ((Player) entity).updateInventory();
            }
        }
    }

    /**
     * Play sound to inventory viewer
     *
     * @param inventory The inventory
     * @param soundObject The sound to play
     */
    public static void playSoundToViewer(Inventory inventory, SoundObject soundObject) {
        for (HumanEntity entity : inventory.getViewers()) {
            if (entity instanceof Player) {
                soundObject.send((Player) entity);
            }
        }
    }

    /**
     * Get the item as a HashMap
     *
     * @return The item as a HashMap, where key is the slot and value is the item
     */
    public static Map<Integer, ItemStack> getItemAsHashMap(Inventory inventory) {
        final Map<Integer, ItemStack> items = new HashMap<>();
        for (int i = 0; i < inventory.getSize(); i++) {
            items.put(i, inventory.getItem(i));
        }
        return items;
    }
}
