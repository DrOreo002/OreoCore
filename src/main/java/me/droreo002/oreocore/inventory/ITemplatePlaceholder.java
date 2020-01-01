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
import java.util.List;
import java.util.Map;

import static me.droreo002.oreocore.utils.item.helper.TextPlaceholder.*;

public class ITemplatePlaceholder implements SerializableConfigVariable<ITemplatePlaceholder> {

    @Getter
    private String placeholder;
    @Getter
    private Map<String, List<GUIButton>> placeholderButtons;
    @Getter
    private ConfigurationSection placeholderDataSection;

    public ITemplatePlaceholder() {}

    public ITemplatePlaceholder(@NotNull String placeholder, @NotNull ConfigurationSection placeholderDataSection) {
        if (!isContainsPlaceholder(placeholder)) throw new IllegalStateException("Must be a placeholder!");
        this.placeholder = placeholder;
        this.placeholderButtons = new HashMap<>();
        this.placeholderDataSection = placeholderDataSection;

        List<String> placeholderLayout = placeholderDataSection.getStringList("placeholder-layout");
        if (placeholderLayout.stream().anyMatch(StringUtils::hasSpecialCharacter)) throw new IllegalStateException("No special character allowed on placeholder-layout! (" + placeholderDataSection.getName() + ")");

        ConfigurationSection placeholderItemData = placeholderDataSection.getConfigurationSection("placeholder-item");
        for (String layout : placeholderLayout) {
            for (char c : layout.toCharArray()) {
                String buttonKey = String.valueOf(c);
                if (!placeholderButtons.containsKey(buttonKey)) placeholderButtons.put(buttonKey, new ArrayList<>());

                ConfigurationSection itemSection = placeholderItemData.getConfigurationSection(buttonKey);
                if (itemSection == null)
                    throw new NullPointerException("Item section with the id of " + buttonKey + " does not exists on layout " + placeholderItemData.getName());
                final GUIButton guiButton = new GUIButton(itemSection, null);
                if (ItemUtils.isEmpty(guiButton.getItem())) continue; // Skip this button
                placeholderButtons.get(buttonKey).add(guiButton);
            }
        }
    }

    /**
     * Format this placeholder to target template
     *
     * @param startSlot The start slot of where the button should be added to
     * @return The modified gui buttons
     */
    public Map<String, List<GUIButton>> format(InventoryTemplate targetTemplate, int startSlot) {
        if ((startSlot % 9) != 0) throw new IllegalStateException("Start slot must be a multiple of 9!");
        Map<String, List<GUIButton>> buttonData = new HashMap<>();
        // Change it slot
        for (Map.Entry<String, List<GUIButton>> entry : placeholderButtons.entrySet()) {
            String key = entry.getKey();
            if (!targetTemplate.getGUIButtons(key).isEmpty()) throw new IllegalStateException("Cannot replace placeholder because GUIButton with key of " + key + " is already occupied!");
            List<GUIButton> buttons = new ArrayList<>();
            for (GUIButton b : entry.getValue()) {
                b.setInventorySlot(startSlot);
                buttons.add(b);
            }
            buttonData.put(key, buttons);
            startSlot++;
        }
        return buttonData;
    }

    @Override
    public ITemplatePlaceholder getFromConfig(ConfigurationSection section) {
        ITemplatePlaceholder placeholder = new ITemplatePlaceholder(section.getString("placeholder"), section);
        ITemplatePlaceholderManager.register(placeholder);
        return placeholder;
    }

    @Override
    public void saveToConfig(String path, FileConfiguration config) {

    }
}
