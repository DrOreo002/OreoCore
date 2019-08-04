package me.droreo002.oreocore.inventory.test.normal;

import me.droreo002.oreocore.inventory.CustomInventory;
import me.droreo002.oreocore.inventory.button.GUIButton;
import me.droreo002.oreocore.inventory.linked.LinkedInventory;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class LinkedInventoryTest extends LinkedInventory {

    public LinkedInventoryTest() {
        super(27, "Linked");
        GUIButton next = GUIButton.DEFAULT_NEXT_BUTTON;
        next.setInventorySlot(26);

        setMainNextButton(next);

        addLinkedInventory(null, null, new CustomInventory(9, "Another Inventory!") {
            @Override
            public void onOpen(Player player, Map<String, Object> linkedData) {
                System.out.println("DataCache from first inventory is " + linkedData.get("Hello"));
            }
        });
    }

    @Override
    public Map<String, Object> onLinkRequestData() {
        final Map<String, Object> m = new HashMap<>();
        m.put("Hello", "World");
        return m;
    }
}
