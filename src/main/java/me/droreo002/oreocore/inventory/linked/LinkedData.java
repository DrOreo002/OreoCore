package me.droreo002.oreocore.inventory.linked;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LinkedData {
    private String dataKey;
    private Object dataValue;
    private LinkedDataType dataType;
}
