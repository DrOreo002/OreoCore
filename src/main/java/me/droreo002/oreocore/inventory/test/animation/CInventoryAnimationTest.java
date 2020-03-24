package me.droreo002.oreocore.inventory.test.animation;

import me.droreo002.oreocore.inventory.OreoInventory;
import me.droreo002.oreocore.inventory.animation.InventoryAnimationManager;
import me.droreo002.oreocore.inventory.animation.button.ButtonAnimation;
import me.droreo002.oreocore.inventory.animation.button.IButtonFrame;
import me.droreo002.oreocore.inventory.animation.open.DiagonalFill;
import me.droreo002.oreocore.inventory.button.ButtonClickEvent;
import me.droreo002.oreocore.inventory.button.ButtonListener;
import me.droreo002.oreocore.inventory.button.GUIButton;
import me.droreo002.oreocore.utils.item.ItemStackBuilder;
import me.droreo002.oreocore.utils.item.complex.UMaterial;
import me.droreo002.oreocore.utils.misc.SimpleCallback;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Animation test for CustomInventory
 *
 * Status [Success]
 */
public class CInventoryAnimationTest extends OreoInventory {

    private int aSlot = 0;

    public CInventoryAnimationTest() {
        super(54, "Animation ODebug");
        setInventoryAnimationManager(InventoryAnimationManager.getDefault()
                .setInventoryUpdaterSpeed(20L * 5L)
                .setOnInventoryUpdated(aVoid -> {
                    aSlot++;
                    if (aSlot >= getSize()) return;
                    GUIButton button = new GUIButton(UMaterial.OAK_SIGN.getItemStack()).addListener(e -> {
                        closeInventory((Player) e.getWhoClicked());
                        e.getWhoClicked().sendMessage("Clicked on " + aSlot);
                    });
                    button.setInventorySlot(aSlot);
                    addButton(button, true);
                    refresh();
                }));

        GUIButton button = new GUIButton(
                ItemStackBuilder.of(Objects.requireNonNull(UMaterial.DIAMOND.getItemStack()))
                        .setDisplayName("&aHello World").build());
        button.setButtonAnimation(new ButtonAnimation()
            .addFrame(new IButtonFrame() {
                @Override
                public String nextDisplayName(String prevDisplayName) {
                    return "&cHello World";
                }
            })
            .addFrame(new IButtonFrame() {
                @Override
                public long getNextFrameUpdateSpeed() {
                    return 60L;
                }
            })
            .setRepeatingAnimation(true));
        button.setInventorySlot(aSlot);
        addButton(button, true);
    }
}
