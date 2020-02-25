package me.droreo002.oreocore.utils.item;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StringItemStack {
    private String itemData;
    private int itemInventorySlot;
    private StringType stringType;

    public enum StringType {
        JSON,
        BASE_64;
    }
}
