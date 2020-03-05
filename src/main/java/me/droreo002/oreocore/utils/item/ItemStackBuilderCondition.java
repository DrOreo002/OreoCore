package me.droreo002.oreocore.utils.item;

import lombok.Getter;
import me.droreo002.oreocore.utils.item.helper.ItemMetaType;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An ItemStackBuilderCondition
 * this will open a another level of item modification
 * via config. This accept config data of what should be changed
 * then choose it by checking the condition by given value (true or false)
 */
public class ItemStackBuilderCondition {

    @Getter
    private final String conditionName;
    @Getter
    private ItemMetaType conditionMetaType;
    @Getter
    private Object conditionValue;
    @Getter
    private boolean expectedCondition;

    public ItemStackBuilderCondition(String conditionName, ItemMetaType conditionMetaType, Object conditionValue, boolean expectedCondition) {
        this.conditionName = conditionName;
        this.conditionMetaType = conditionMetaType;
        this.conditionValue = conditionValue;
        this.expectedCondition = expectedCondition;
        if (this.conditionMetaType == ItemMetaType.DISPLAY_AND_LORE || this.conditionMetaType == ItemMetaType.NONE) throw new IllegalStateException("Invalid condition meta type!. Only accept DISPLAY_NAME and LORE");
    }

    public ItemStackBuilderCondition(String conditionName, ConfigurationSection section) {
        this.conditionName = conditionName;
        this.conditionMetaType = ItemMetaType.valueOf(section.getString("metaType"));
        this.conditionValue = section.get("conditionValue");
        this.expectedCondition = section.getBoolean("expectedCondition");
    }

    /**
     * Try to apply this condition
     *
     * @param condition The condition to apply
     * @param itemStackBuilder The builder to apply to
     * @return ItemStackBuilder
     */
    public ItemStackBuilder applyCondition(ItemStackBuilder itemStackBuilder, boolean condition) {
        if (condition == expectedCondition) {
            switch (conditionMetaType) {
                case DISPLAY_NAME:
                    return itemStackBuilder.setDisplayName((String) conditionValue);
                case LORE:
                    return itemStackBuilder.setLore((List<String>) conditionValue);
            }
        }
        return itemStackBuilder;
    }

    /**
     * Serialize this object
     *
     * @return HashMap of serialized object
     */
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("metaType", conditionMetaType.name());
        map.put("conditionValue", conditionValue);
        map.put("expectedCondition", expectedCondition);
        return map;
    }
}
