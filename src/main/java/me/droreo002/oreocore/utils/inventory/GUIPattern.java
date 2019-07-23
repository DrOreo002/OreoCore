package me.droreo002.oreocore.utils.inventory;

import lombok.Getter;
import me.droreo002.oreocore.inventory.button.GUIButton;

public class GUIPattern {

    @Getter
    private int slot;
    @Getter
    private GUIButton button;

    public GUIPattern(int slot, GUIButton button) {
        this.slot = slot;
        this.button = button;
    }
}
