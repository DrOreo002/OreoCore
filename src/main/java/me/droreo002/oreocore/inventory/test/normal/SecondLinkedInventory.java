package me.droreo002.oreocore.inventory.test.normal;

import me.droreo002.oreocore.inventory.CustomInventory;
import me.droreo002.oreocore.inventory.linked.Linkable;

import java.util.Map;

public class SecondLinkedInventory extends CustomInventory implements Linkable {

    public SecondLinkedInventory() {
        super(18, "Second Inventory");
    }

    @Override
    public String getInventoryName() {
        return getTitle();
    }

    @Override
    public void onLinkAcceptData(Map<String, Object> data, Linkable previousInventory) {
        System.out.println("Received data from inventory (" + previousInventory.getInventoryName() + ") " + data.get("Hello"));
    }
}
