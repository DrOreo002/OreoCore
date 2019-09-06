package me.droreo002.oreocore.inventory.test.normal;

import me.droreo002.oreocore.inventory.OreoInventory;
import me.droreo002.oreocore.inventory.button.GUIButton;
import me.droreo002.oreocore.utils.item.CustomSkull;

public class LagInventoryTest extends OreoInventory {

    public LagInventoryTest() {
        super(9, "Lag Test");
        addButton(new GUIButton(
                CustomSkull.getSkullUrl("http://textures.minecraft.net/texture/753c89a2adc4ee5ba1f05e5d64e9b4bb6b3232c72028e0cbe35e1b73d0c57dc1")
                , 0
        ), true);
        addButton(new GUIButton(
                CustomSkull.getSkullUrl("http://textures.minecraft.net/texture/1ea5078e6d4f75b0f593ee005d02d14a49bd647ea08b61d24846db23c3b223ff")
                , 1
        ), true);
    }
}
