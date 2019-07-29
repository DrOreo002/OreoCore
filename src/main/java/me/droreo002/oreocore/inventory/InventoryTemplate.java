package me.droreo002.oreocore.inventory;

import lombok.Data;
import lombok.Getter;
import me.droreo002.oreocore.configuration.SerializableConfigVariable;
import me.droreo002.oreocore.inventory.button.GUIButton;
import me.droreo002.oreocore.utils.item.helper.TextPlaceholder;
import me.droreo002.oreocore.utils.strings.StringUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryTemplate implements SerializableConfigVariable<InventoryTemplate> {

    private static final String PAGINATED_ITEM_ROW_KEY = "!-------!";
    public static final String PAGINATED_NB_KEY = "N"; // NextButton
    public static final String PAGINATED_PB_KEY = "P"; // PreviousButton
    public static final String PAGINATED_IB_KEY = "I"; // InformationButton

    @Getter
    private final ConfigurationSection layoutDatabase;
    @Getter
    private final ConfigurationSection layoutItemDatabase;
    @Getter
    private int size;
    @Getter
    private boolean paginatedInventory;
    @Getter
    private String title;
    @Getter
    private List<String> rawLayout; // The raw layout
    @Getter
    private Map<Integer, String> layout; // The layout, where integer is the slot and string is the button 'id'
    @Getter
    private List<ButtonData> buttonDatas; // The item definition, where string is the button 'id' and item definition is the item data

    /**
     * For default initializer, do not remove
     */
    public InventoryTemplate() {
        this.layoutDatabase = null;
        this.layoutItemDatabase = null;
    }

    public InventoryTemplate(ConfigurationSection layoutDatabase) {
        this.layoutDatabase = layoutDatabase;
        this.layoutItemDatabase = layoutDatabase.getConfigurationSection("layout-item");
        this.paginatedInventory = layoutDatabase.getBoolean("paginated");
        this.rawLayout = new ArrayList<>();
        this.layout = new HashMap<>();
        this.buttonDatas = new ArrayList<>();
        this.size = layoutDatabase.getInt("size");
        this.title = layoutDatabase.getString("title");
        this.rawLayout = layoutDatabase.getStringList("layout");

        int slot = 0;
        for (String s : rawLayout) {
            if (!paginatedInventory) {
                if (StringUtils.hasSpecialCharacter(s)) throw new IllegalStateException("Invalid character at inventory layout!");
                if (s.length() != 9) throw new IllegalStateException("Invalid layout length! " + s.length() + " expected are 9");
            } else {
                if (s.equalsIgnoreCase(PAGINATED_ITEM_ROW_KEY)) {
                    slot += 9;
                    continue; // Ignore this
                }
            }
            for (char c : s.toCharArray()) {
                String str = String.valueOf(c);
                layout.put(slot, str);
                slot++;
            }
        }

        for (Map.Entry ent : layout.entrySet()) {
            int buttonSlot = (int) ent.getKey();
            String buttonKey = (String) ent.getValue();

            ConfigurationSection itemSection = layoutItemDatabase.getConfigurationSection(buttonKey);
            if (itemSection == null) throw new NullPointerException("Item section with the id of " + buttonKey + " is not exists!");
            final ButtonData data = new ButtonData();
            data.setItemData(itemSection);
            data.setInventorySlot(buttonSlot);
            data.setButtonKey(buttonKey);

            buttonDatas.add(data);
        }
    }

    /**
     * Apply placeholder into the buttpn
     *
     * @param buttonKey The button key or identifier
     * @param textPlaceholder The placeholder
     */
    public void applyPlaceholder(String buttonKey, TextPlaceholder textPlaceholder) {
        if (getButton(buttonKey) == null) return;
        getButton(buttonKey).setPlaceholder(textPlaceholder);
    }

    /**
     * Apply listener into the button
     *
     * @param buttonKey The button key or identifier
     * @param listener The listener
     */
    public void applyListener(String buttonKey, GUIButton.ButtonListener listener) {
        if (getButton(buttonKey) == null) return;
        getButton(buttonKey).setListener(listener);
    }

    /**
     * Get the paginated-inv item row
     *
     * @return The item row
     */
    public List<Integer> getPaginatedItemRow() {
        if (!paginatedInventory) throw new UnsupportedOperationException("Cannot get item row if data is not for paginated inventory!");
        final List<Integer> result = new ArrayList<>();

        int row = 0;
        for (String s : rawLayout) {
            if (s.equalsIgnoreCase(PAGINATED_ITEM_ROW_KEY)) {
                result.add(row);
                row++;
            }
        }

        return result;
    }

    /**
     * Get the button data
     *
     * @param key The button key
     * @return The button data if exists, null otherwise
     */
    public ButtonData getButton(String key) {
        return buttonDatas.stream().filter(buttonData -> buttonData.getButtonKey().equalsIgnoreCase(key)).findAny().orElse(null);
    }

    @Override
    public InventoryTemplate getFromConfig(ConfigurationSection section) {
        return new InventoryTemplate(section);
    }

    @Override
    public void saveToConfig(String path, FileConfiguration config) {
        config.set(path + ".title", getTitle());
        config.set(path + ".size", getSize());
        config.set(path + ".layout", getRawLayout());
        config.set(path + ".paginated", isPaginatedInventory());

        for (ButtonData data : buttonDatas) {
            config.set(path + ".layout-item." + data.getButtonKey(), data.getItemData());
        }
    }

    @Data
    public class ButtonData {
        private String buttonKey;
        private ConfigurationSection itemData;
        private TextPlaceholder placeholder;
        private GUIButton.ButtonListener listener;
        private int inventorySlot;
    }
}
