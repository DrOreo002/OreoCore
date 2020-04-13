package me.droreo002.oreocore.utils.item;

import lombok.Getter;
import me.droreo002.oreocore.utils.item.complex.UMaterial;
import me.droreo002.oreocore.utils.misc.SimpleCallback;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    private Map<Boolean, Map<ChangeType, Object>> conditionChanges;

    public ItemStackBuilderCondition(String conditionName, ConfigurationSection section) {
        this.conditionName = conditionName.toLowerCase();
        this.conditionChanges = new HashMap<>();

        Map<ChangeType, Object> onTrue = new HashMap<>();
        ConfigurationSection onTrueSection = section.getConfigurationSection("true");
        if (onTrueSection != null) {
            for (String key : onTrueSection.getKeys(false)) {
                onTrue.put(ChangeType.valueOf(key.toUpperCase()), section.get("true." + key));
            }
        }

        Map<ChangeType, Object> onFalse = new HashMap<>();
        ConfigurationSection onFalseSection = section.getConfigurationSection("false");
        if (onFalseSection != null) {
            for (String key : onFalseSection.getKeys(false)) {
                onFalse.put(ChangeType.valueOf(key.toUpperCase()), section.get("false." + key));
            }
        }

        this.conditionChanges.put(true, onTrue);
        this.conditionChanges.put(false, onFalse);
    }

    /**
     * Try to apply this condition
     *
     * @param condition The condition to apply
     * @param itemStackBuilder The builder to apply to
     * @param onConditionApplied Called when the condition is successfully applied
     * @return ItemStackBuilder
     */
    public ItemStackBuilder applyCondition(ItemStackBuilder itemStackBuilder, boolean condition, @Nullable SimpleCallback<Void> onConditionApplied) {
        Map<ChangeType, Object> changeMap = this.conditionChanges.get(condition);
        if (changeMap.isEmpty()) return itemStackBuilder;
        for (Map.Entry<ChangeType, Object> changesEntry : changeMap.entrySet()) {
            Object value = changesEntry.getValue();
            switch (changesEntry.getKey()) {
                case MATERIAL:
                    itemStackBuilder.setMaterial(UMaterial.match((String) value).getMaterial());
                    break;
                case NAME:
                    itemStackBuilder.setDisplayName((String) value);
                    break;
                case LORE:
                    itemStackBuilder.setLore((List<String>) value);
                    break;
            }
        }
        if (onConditionApplied != null) onConditionApplied.success(null);
        return itemStackBuilder;
    }

    /**
     * Serialize this object
     *
     * @return HashMap of serialized object
     */
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        for (Map.Entry<Boolean, Map<ChangeType, Object>> changesEntry : this.conditionChanges.entrySet()) {
            for (Map.Entry<ChangeType, Object> data : changesEntry.getValue().entrySet()) {
                map.put("conditions." + conditionName + "." + changesEntry.getKey() + "." + data.getKey().name().toLowerCase(), data.getValue());
            }
        }
        return map;
    }

    public enum ChangeType {
        MATERIAL,
        NAME,
        LORE
    }
}
