package me.droreo002.oreocore.inventory.test.normal;

import me.droreo002.oreocore.inventory.OreoInventory;
import me.droreo002.oreocore.inventory.button.ButtonClickEvent;
import me.droreo002.oreocore.inventory.button.ButtonListener;
import me.droreo002.oreocore.inventory.button.GUIButton;
import me.droreo002.oreocore.utils.item.complex.XMaterial;
import org.jetbrains.annotations.NotNull;

/**
 * Simple api test for CustomInventory
 *
 * Status [Success]
 */
public class CustomInventoryTest extends OreoInventory {

    public CustomInventoryTest() {
        super(27, "CustomInventory");
        addButton(new GUIButton(XMaterial.DIRT.getItemStack(), 5)
                .addListener(new ButtonListener() {
                    @Override
                    public @NotNull Priority getListenerPriority() {
                        return Priority.HIGH;
                    }

                    @Override
                    public void onClick(ButtonClickEvent e) {
                        System.out.println("This is first");
                    }
                })
                .addListener(new ButtonListener() {
                    @Override
                    public @NotNull Priority getListenerPriority() {
                        return Priority.MEDIUM;
                    }

                    @Override
                    public void onClick(ButtonClickEvent e) {
                        System.out.println("This is second");
                    }
                })
                .addListener(new ButtonListener() {
                    @Override
                    public @NotNull Priority getListenerPriority() {
                        return Priority.LOW;
                    }

                    @Override
                    public void onClick(ButtonClickEvent e) {
                        System.out.println("This is third");
                    }
                })
                .addListener(new ButtonListener() {
                    @Override
                    public @NotNull Priority getListenerPriority() {
                        return Priority.DEFAULT;
                    }

                    @Override
                    public void onClick(ButtonClickEvent e) {
                        System.out.println("This is last");
                    }
                })
        , true);
    }
}
