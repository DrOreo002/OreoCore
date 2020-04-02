package me.droreo002.oreocore.inventory.linked;

import lombok.Getter;
import lombok.Setter;
import me.droreo002.oreocore.inventory.button.ButtonListener;
import me.droreo002.oreocore.inventory.button.GUIButton;
import me.droreo002.oreocore.utils.item.helper.TextPlaceholder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.Future;

public class LinkedButton extends GUIButton {

    @Getter @Setter
    private String belongsTo;
    @Getter @Setter
    private String targetInventory;
    @Getter @Setter
    private Future<LinkedDataList> extraDataProvider;

    /**
     * Construct a new LickedButton (without slot)
     *
     * @param item The item
     */
    public LinkedButton(ItemStack item, String targetInventory, Linkable owner) {
        super(item);
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
        this.targetInventory = targetInventory;
        this.belongsTo = owner.getInventoryName();

        setAnimated(button.isAnimated());
        setButtonAnimationManager(button.getButtonAnimationManager());
    }

    @Override
    public LinkedButton addListener(ButtonListener buttonListener) {
        return (LinkedButton) super.addListener(buttonListener);
    }
}
