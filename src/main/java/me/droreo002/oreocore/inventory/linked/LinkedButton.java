package me.droreo002.oreocore.inventory.linked;

import lombok.Getter;
import lombok.Setter;
import me.droreo002.oreocore.inventory.button.GUIButton;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class LinkedButton extends GUIButton {

    @Getter @Setter
    private List<ButtonListener> extraListeners;
    @Getter @Setter
    private String belongsTo;
    @Getter @Setter
    private String targetInventory;

    public LinkedButton(ItemStack item, String targetInventory, Linkable parentInventory) {
        super(item);
        this.extraListeners = new ArrayList<>();
        this.belongsTo = parentInventory.getInventoryName();
        this.targetInventory = targetInventory;
    }

    public LinkedButton(ItemStack item, int inventorySlot, String targetInventory, Linkable parentInventory) {
        super(item, inventorySlot);
        this.extraListeners = new ArrayList<>();
        this.belongsTo = parentInventory.getInventoryName();
        this.targetInventory = targetInventory;
    }
}
