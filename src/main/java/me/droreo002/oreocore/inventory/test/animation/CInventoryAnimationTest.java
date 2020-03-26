package me.droreo002.oreocore.inventory.test.animation;

import me.droreo002.oreocore.inventory.OreoInventory;
import me.droreo002.oreocore.inventory.animation.InventoryAnimationManager;
import me.droreo002.oreocore.inventory.animation.button.ButtonAnimationManager;
import me.droreo002.oreocore.inventory.animation.button.IButtonFrame;
import me.droreo002.oreocore.inventory.button.GUIButton;
import me.droreo002.oreocore.utils.item.ItemStackBuilder;
import me.droreo002.oreocore.utils.item.complex.UMaterial;
import org.bukkit.entity.Player;

import java.util.Objects;

/**
 * Animation test for CustomInventory
 *
 * Status [Success]
 */
public class CInventoryAnimationTest extends OreoInventory {

    public CInventoryAnimationTest() {
        super(9, "Animation ODebug");
        setInventoryAnimationManager(InventoryAnimationManager.getDefault());

        GUIButton button = new GUIButton(
                ItemStackBuilder.of(Objects.requireNonNull(UMaterial.DIAMOND.getItemStack()))
                        .setDisplayName("&aHello World").build());
        ButtonAnimationManager buttonAnimationManager = new ButtonAnimationManager(button);
        buttonAnimationManager.addFirstState();
        buttonAnimationManager.getButtonAnimation()
                .addFrame(new IButtonFrame() {
                    @Override
                    public String nextDisplayName(String previousDisplayName) {
                        return "&cHello World";
                    }
                })
                .addFrame(new IButtonFrame() {
                    @Override
                    public long getNextFrameUpdateSpeed() {
                        return 60L;
                    }
                })
                .setRepeating(true);
        button.setButtonAnimationManager(buttonAnimationManager);
        button.setInventorySlot(0);
        addButton(button, true);
    }
}
