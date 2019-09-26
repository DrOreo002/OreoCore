package me.droreo002.oreocore.inventory.test.normal;

import me.droreo002.oreocore.inventory.OreoInventory;
import me.droreo002.oreocore.inventory.linked.Linkable;
import me.droreo002.oreocore.inventory.linked.LinkedData;
import me.droreo002.oreocore.inventory.linked.LinkedDatas;

public class SecondLinkedInventory extends OreoInventory implements Linkable {

    public SecondLinkedInventory() {
        super(18, "Second Inventory");
    }

    @Override
    public String getInventoryName() {
        return getTitle();
    }

    @Override
    public void acceptData(LinkedDatas data, Linkable previousInventory) {
        LinkedData linkedData = data.getData("hello");
        if (linkedData == null) return;
        System.out.println((String) linkedData.getDataValue());
    }
}
