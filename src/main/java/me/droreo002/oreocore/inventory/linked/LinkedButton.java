package me.droreo002.oreocore.inventory.linked;

import lombok.Getter;
import lombok.Setter;
import me.droreo002.oreocore.inventory.button.GUIButton;
import me.droreo002.oreocore.utils.item.helper.TextPlaceholder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LinkedButton extends GUIButton {

    @Getter
    private final Map<ClickType, List<ButtonListener>> buttonListeners;
    @Getter @Setter
    private String belongsTo;
    @Getter @Setter
    private String targetInventory;

    /**
     * Construct a new LickedButton (without slot)
     *
     * @param item The item
     */
    public LinkedButton(ItemStack item, String targetInventory, Linkable owner) {
        super(item);
        this.buttonListeners = new HashMap<>();
        this.belongsTo = owner.getInventoryName();
        this.targetInventory = targetInventory;
    }

    /**
     * Construct new LinkedButton from configuration section, this will also accept button slot
     * the data key will be 'slot' and with the placeholder specified for item
     *
     * @param section The section
     * @param textPlaceholder The TextPlaceholder to replace placeholder on item data
     */
    public LinkedButton(ConfigurationSection section, TextPlaceholder textPlaceholder, String targetInventory, Linkable owner) {
        super(section, textPlaceholder);
        this.buttonListeners = new HashMap<>();
        this.belongsTo = owner.getInventoryName();
        this.targetInventory = targetInventory;
    }

    /**
     * Construct a new LinkedButton
     *
     * @param item The item
     * @param inventorySlot The slot of this button
     */
    public LinkedButton(ItemStack item, int inventorySlot, String targetInventory, Linkable owner) {
        super(item, inventorySlot);
        this.buttonListeners = new HashMap<>();
        this.belongsTo = owner.getInventoryName();
        this.targetInventory = targetInventory;
    }

    /**
     * Construct new LinkedButton from GUIButton
     *
     * @param button The button
     * @param targetInventory The target inventory
     * @param owner The owner
     */
    public LinkedButton(GUIButton button, String targetInventory, Linkable owner) {
        super(button.getItem(), button.getInventorySlot());
        this.buttonListeners = new HashMap<>();
        this.targetInventory = targetInventory;
        this.belongsTo = owner.getInventoryName();

        setAnimated(button.isAnimated());
        setButtonAnimation(button.getButtonAnimation());
    }

    /**
     * Add a new extra listener
     *
     * @param buttonListener The extra listener
     */
    public LinkedButton addListener(ClickType clickType, ButtonListener buttonListener) {
        if (buttonListeners.containsKey(clickType)) {
            List<ButtonListener> val = buttonListeners.get(clickType);
            val.add(buttonListener);
            buttonListeners.put(clickType, val);
        } else {
            buttonListeners.put(clickType, new ArrayList<>(Collections.singletonList(buttonListener)));
        }
        return this;
    }
}
