package me.droreo002.oreocore.inventory.test.normal;

import me.droreo002.oreocore.inventory.CustomInventory;
import me.droreo002.oreocore.inventory.linked.Linkable;
import me.droreo002.oreocore.inventory.linked.LinkedButton;
import me.droreo002.oreocore.utils.item.CustomItem;
import me.droreo002.oreocore.utils.item.complex.UMaterial;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirstLinkedInventory extends CustomInventory implements Linkable {

    private LinkedButton nextButton;

    public FirstLinkedInventory() {
        super(27, "First Inventory");
        this.nextButton = new LinkedButton(new CustomItem(UMaterial.ARROW.getItemStack(), "&aNext"), 26,"Second Inventory", this);
    }

    @Override
    public Map<String, Object> onLinkRequestData() {
        final Map<String, Object> m = new HashMap<>();
        m.put("Hello", "World");
        return m;
    }

    @Override
    public String getInventoryName() {
        return getTitle();
    }

    @Override
    public List<LinkedButton> getLinkedButtons() {
        return Collections.singletonList(nextButton);
    }
}
