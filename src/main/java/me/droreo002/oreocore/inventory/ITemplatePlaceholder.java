package me.droreo002.oreocore.inventory;

import lombok.Getter;
import me.droreo002.oreocore.configuration.SerializableConfigVariable;
import me.droreo002.oreocore.inventory.button.GUIButton;
import me.droreo002.oreocore.utils.item.ItemUtils;
import me.droreo002.oreocore.utils.strings.StringUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static me.droreo002.oreocore.utils.item.helper.TextPlaceholder.*;

public class ITemplatePlaceholder implements SerializableConfigVariable {

    public static final String SECTION_KEY_PREFIX = "Placeholder|";

    @Getter
    private String placeholder;
    @Getter
    private LinkedHashMap<String, List<GUIButton>> placeholderItems;
    @Getter
    private ConfigurationSection placeholderDataSection;

    public ITemplatePlaceholder() {}

    public ITemplatePlaceholder(@NotNull String placeholder, @NotNull ConfigurationSection placeholderDataSection) {
        if (!isContainsPlaceholder(placeholder)) throw new IllegalStateException("Must be a placeholder!");
        this.placeholder = placeholder;
        this.placeholderItems = new LinkedHashMap<>();
        this.placeholderDataSection = placeholderDataSection;

        List<String> placeholderLayout = placeholderDataSection.getStringList("placeholder-layout");
        if (placeholderLayout.stream().anyMatch(StringUtils::hasSpecialCharacter)) throw new IllegalStateException("No special character allowed on placeholder-layout! (" + placeholderDataSection.getName() + ")");

        int slot = 0;
        ConfigurationSection placeholderItemData = placeholderDataSection.getConfigurationSection("placeholder-item");
        for (String layout : placeholderLayout) {
            for (char c : layout.toCharArray()) {
                String buttonKey = SECTION_KEY_PREFIX + c;
                if (!placeholderItems.containsKey(buttonKey)) placeholderItems.put(buttonKey, new ArrayList<>());

                ConfigurationSection itemSection = placeholderItemData.getConfigurationSection(buttonKey.replace(SECTION_KEY_PREFIX, ""));
                if (itemSection == null)
                    throw new NullPointerException("Item section with the id of " + buttonKey.replace(SECTION_KEY_PREFIX, "") + " does not exists on layout " + placeholderItemData.getName());
                GUIButton guiButton = new GUIButton(itemSection, null);
                guiButton.setInventorySlot(slot);
                placeholderItems.get(buttonKey).add(guiButton);
                slot++;
            }
        }
    }

    /**
     * Format this placeholder to target template
     *
     * @param slot The start slot of where the button should be added to
     * @return The remaining slot of addition
     */
    public int format(InventoryTemplate targetTemplate, int slot) {
        // Change it slot
        int lastSlot = 0;
        for (Map.Entry<String, List<GUIButton>> entry : placeholderItems.entrySet()) {
            String key = entry.getKey();
            List<GUIButton> buttons = new ArrayList<>();
            for (GUIButton b : entry.getValue()) {
                if (ItemUtils.isEmpty(b.getItem())) continue;
                GUIButton guiButton = b.clone();
                guiButton.setInventorySlot(slot + guiButton.getInventorySlot());
                if (lastSlot < guiButton.getInventorySlot()) lastSlot = guiButton.getInventorySlot();
                buttons.add(guiButton);
            }
            targetTemplate.getGUIButtons().put(key, buttons);
        }
        return lastSlot;
    }

    public static ITemplatePlaceholder deserialize(ConfigurationSection section) {
        ITemplatePlaceholder placeholder = new ITemplatePlaceholder(section.getString("placeholder"), section);
        ITemplatePlaceholderManager.register(placeholder);
        return placeholder;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        return new HashMap<>(); // TODO: 25/02/2020 Make?
    }
}
