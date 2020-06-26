package me.droreo002.oreocore.inventory.test.normal;

import me.droreo002.oreocore.inventory.OreoInventory;
import me.droreo002.oreocore.inventory.linked.Linkable;
import me.droreo002.oreocore.inventory.linked.LinkedButton;
import me.droreo002.oreocore.inventory.linked.LinkedData;
import me.droreo002.oreocore.inventory.linked.LinkedDataType;
import me.droreo002.oreocore.utils.item.ItemStackBuilder;
import me.droreo002.oreocore.utils.item.complex.XMaterial;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FirstLinkedInventory extends OreoInventory implements Linkable {

    private LinkedButton nextButton;

    public FirstLinkedInventory() {
        super(27, "First Inventory");
        this.nextButton = new LinkedButton(ItemStackBuilder.of(XMaterial.ARROW.getItemStack()).setDisplayName("&aNext").getItemStack(), 26, "Second Inventory", this);
    }

    @Override
    public String getInventoryName() {
        return getTitle();
    }

    @Override
    public List<LinkedData> getInventoryData() {
        List<LinkedData> datas = new ArrayList<>();
        datas.add(new LinkedData("hello", "Hello World", LinkedDataType.GLOBAL));
        return datas;
    }

    @Override
    public List<LinkedButton> getLinkedButtons() {
        return Collections.singletonList(nextButton);
    }
}
