package me.droreo002.oreocore.inventory;

import lombok.Data;
import lombok.Getter;
import me.droreo002.oreocore.configuration.SerializableConfigVariable;
import me.droreo002.oreocore.inventory.button.GUIButton;
import me.droreo002.oreocore.utils.item.helper.TextPlaceholder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryData implements SerializableConfigVariable<InventoryData> {

    @Getter
    private final ConfigurationSection layoutDatabase;
    @Getter
    private final ConfigurationSection layoutItemDatabase;
    @Getter
    private int size;
    @Getter
    private String title;
    @Getter
    private List<String> rawLayout; // The raw layout
    @Getter
    private Map<Integer, String> layout; // The layout, where integer is the slot and string is the button 'id'
    @Getter
    private Map<String, ButtonData> buttonDatas; // The item definition, where string is the button 'id' and item definition is the item data

    /**
     * For default initializer, do not remove
     */
    public InventoryData() {
        this.layoutDatabase = null;
        this.layoutItemDatabase = null;
    }

    public InventoryData(ConfigurationSection layoutDatabase) {
        this.layoutDatabase = layoutDatabase;
        this.layoutItemDatabase = layoutDatabase.getConfigurationSection("layout-item");
        this.rawLayout = new ArrayList<>();
        this.layout = new HashMap<>();
        this.buttonDatas = new HashMap<>();
        this.size = layoutDatabase.getInt("size");
        this.title = layoutDatabase.getString("title");
        this.rawLayout = layoutDatabase.getStringList("layout");

        int slot = 0;
        for (String s : rawLayout) {
            if (s.length() != 9) throw new IllegalStateException("Invalid layout length! " + s.length() + " expected are 9");
            for (char c : s.toCharArray()) {
                layout.put(slot, String.valueOf(c));
                slot++;
            }
        }

        for (String value : layout.values()) {
            ConfigurationSection itemSection = layoutItemDatabase.getConfigurationSection(value);
            if (itemSection == null) throw new NullPointerException("Item section with the id of " + value + " is not exists!");
            final ButtonData data = new ButtonData();
            data.setItemData(itemSection);
            buttonDatas.put(value, data);
        }
    }

    /**
     * Apply placeholder into the buttpn
     *
     * @param buttonId The button id
     * @param textPlaceholder The placeholder
     */
    public void applyPlaceholder(String buttonId, TextPlaceholder textPlaceholder) {
        if (!buttonDatas.containsKey(buttonId)) return;
        buttonDatas.get(buttonId).setPlaceholder(textPlaceholder);
    }

    /**
     * Apply listener into the button
     *
     * @param buttonId The button id
     * @param listener The listener
     */
    public void applyListener(String buttonId, GUIButton.ButtonListener listener) {
        if (!buttonDatas.containsKey(buttonId)) return;
        buttonDatas.get(buttonId).setListener(listener);
    }

    @Override
    public InventoryData getFromConfig(ConfigurationSection section) {
        return new InventoryData(section);
    }

    @Override
    public void saveToConfig(String path, FileConfiguration config) {
        config.set(path + ".title", getTitle());
        config.set(path + ".size", getSize());
        config.set(path + ".layout", getRawLayout());

        for (Map.Entry ent : buttonDatas.entrySet()) {
            String key = (String) ent.getKey();
            ButtonData data = (ButtonData) ent.getValue();

            config.set(path + ".layout-item." + key, data.getItemData());
        }
    }

    @Data
    public class ButtonData {
        private ConfigurationSection itemData;
        private TextPlaceholder placeholder;
        private GUIButton.ButtonListener listener;
    }
}
