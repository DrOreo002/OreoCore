package me.droreo002.oreocore.inventory;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import me.droreo002.oreocore.configuration.SerializableConfigVariable;
import me.droreo002.oreocore.debugging.ODebug;
import me.droreo002.oreocore.inventory.animation.open.OpenAnimations;
import me.droreo002.oreocore.inventory.button.ButtonListener;
import me.droreo002.oreocore.inventory.button.GUIButton;
import me.droreo002.oreocore.utils.item.ItemUtils;
import me.droreo002.oreocore.utils.item.helper.TextPlaceholder;
import me.droreo002.oreocore.utils.misc.SoundObject;
import me.droreo002.oreocore.utils.strings.StringUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InventoryTemplate implements SerializableConfigVariable<InventoryTemplate>, Cloneable {

    private static final String PAGINATED_ITEM_ROW_KEY = "!-------!";
    public static final String PAGINATED_NB_KEY = "N"; // NextButton
    public static final String PAGINATED_PB_KEY = "P"; // PreviousButton
    public static final String PAGINATED_IB_KEY = "I"; // InformationButton

    @Getter
    private final ConfigurationSection layoutDatabase;
    @Getter
    private final ConfigurationSection layoutItemDatabase;
    @Getter
    private SoundObject openSound, clickSound, closeSound;
    @Getter
    private int size;
    @Getter
    private boolean paginatedInventory;
    @Getter
    private String openAnimationName;
    @Getter
    private InventoryType inventoryType;
    @Setter
    private Map<String, List<GUIButton>> guiButtons; // No getter, because GUI is not capitalized by lombok
    @Getter @Setter
    private String title;
    @Getter @Setter
    private List<String> rawLayout; // The raw layout
    @Getter @Setter
    private Map<Integer, String> layout; // The layout, where integer is the slot and string is the button 'id'

    /**
     * For default initializer, do not remove
     */
    public InventoryTemplate() {
        this.layoutDatabase = null;
        this.layoutItemDatabase = null;
        this.guiButtons = new HashMap<>();
        this.inventoryType = InventoryType.CHEST;
    }

    public InventoryTemplate(ConfigurationSection layoutDatabase) {
        this.layoutDatabase = layoutDatabase;
        this.layoutItemDatabase = layoutDatabase.getConfigurationSection("layout-item");
        this.paginatedInventory = layoutDatabase.getBoolean("paginated");
        this.rawLayout = new ArrayList<>();
        this.layout = new HashMap<>();
        this.guiButtons = new HashMap<>();
        this.size = layoutDatabase.getInt("size", 0);
        this.title = layoutDatabase.getString("title", "Custom Inventory");
        this.rawLayout = layoutDatabase.getStringList("layout");
        this.openAnimationName = layoutDatabase.getString("openAnimation", "none");
        this.inventoryType = InventoryType.CHEST;

        if (layoutDatabase.isSet("inventoryType"))
            this.inventoryType = InventoryType.valueOf(layoutDatabase.getString("inventoryType"));
        if (layoutDatabase.isSet("openSound"))
            this.openSound = SoundObject.fromConfig(layoutDatabase.getConfigurationSection("openSound"));
        if (layoutDatabase.isSet("closeSound"))
            this.closeSound = SoundObject.fromConfig(layoutDatabase.getConfigurationSection("closeSound"));
        if (layoutDatabase.isSet("clickSound"))
            this.clickSound = SoundObject.fromConfig(layoutDatabase.getConfigurationSection("clickSound"));

        /*
        Validations
         */
        try {
            if (!this.openAnimationName.equals("none")) {
                OpenAnimations.valueOf(this.openAnimationName);
            }
        } catch (Exception e) {
            /*
            Must users have typos here. So we print out the available open animations
            to provider quick fix
             */
            ODebug.log("Failed to get open animation with the name of &a" + this.openAnimationName, true);
            ODebug.log("    &c> Available are: " + Arrays.toString(OpenAnimations.values()), false);
            e.printStackTrace();
            return;
        }


        int slot = 0;
        for (String s : rawLayout) {
            if (!paginatedInventory) {
                if (inventoryType == InventoryType.CHEST) {
                    if (s.length() != 9)
                        throw new IllegalStateException("Invalid layout length! " + s.length() + " expected are 9");
                }
            } else {
                if (s.equalsIgnoreCase(PAGINATED_ITEM_ROW_KEY)) {
                    slot += 9;
                    continue; // Ignore this
                }
            }
            if (StringUtils.hasSpecialCharacter(s)) throw new IllegalStateException("Invalid character at inventory layout!");
            for (char c : s.toCharArray()) {
                String str = String.valueOf(c);
                layout.put(slot, str);
                slot++;
            }
        }

        for (Map.Entry ent : layout.entrySet()) {
            int buttonSlot = (int) ent.getKey();
            String buttonKey = (String) ent.getValue();
            if (!guiButtons.containsKey(buttonKey)) guiButtons.put(buttonKey, new ArrayList<>());

            ConfigurationSection itemSection = layoutItemDatabase.getConfigurationSection(buttonKey);
            if (itemSection == null) throw new NullPointerException("Item section with the id of " + buttonKey + " is not exists!");
            final GUIButton guiButton = new GUIButton(itemSection, null);
            if (ItemUtils.isEmpty(guiButton.getItem())) continue; // Skip this button
            guiButton.setInventorySlot(buttonSlot);
            guiButtons.get(buttonKey).add(guiButton);
        }
    }

    /**
     * Apply placeholder into the buttpn
     *
     * @param buttonKey The button key or identifier
     * @param textPlaceholder The placeholder
     */
    public void applyPlaceholder(String buttonKey, TextPlaceholder textPlaceholder) {
        if (!guiButtons.containsKey(buttonKey)) return;
        for (GUIButton button : guiButtons.get(buttonKey)) {
            button.applyTextPlaceholder(textPlaceholder);
        }
    }

    /**
     * Apply listener into the button
     *
     * @param buttonKey The button key or identifier
     * @param listener The listener
     */
    public void applyListener(String buttonKey, ButtonListener listener) {
        if (!guiButtons.containsKey(buttonKey)) return;
        for (GUIButton button : guiButtons.get(buttonKey)) {
            button.addListener(listener);
        }
    }

    /**
     * Apply the item into the button
     *
     * @param buttonKey The button key or identifier
     * @param item The new item
     * @param updateMeta Should we update the item meta?
     */
    public void applyItem(String buttonKey, ItemStack item, boolean updateMeta, boolean updateFrame) {
        if (!guiButtons.containsKey(buttonKey)) return;
        for (GUIButton button : guiButtons.get(buttonKey)) {
            button.setItem(item, updateMeta, updateFrame);
        }
    }

    /**
     * Update the GUIButton
     *
     * @param newButton The new button
     * @param buttonKey The button key
     */
    public void updateGUIButton(GUIButton newButton, String buttonKey) {
        if (!isKeyAvailable(buttonKey)) return;
        List<GUIButton> buttons = getGUIButtons(buttonKey);
        for (int i = 0; i < buttons.size(); i++) {
            buttons.set(i, newButton);
        }
        guiButtons.put(buttonKey, buttons);
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
            }
            row++;
        }

        return result;
    }

    /**
     * Get the final GUIButton from key
     *
     * @param key The button key
     * @return The GUIButton as array because it's possible to have more than one. Nullable also
     */
    public List<GUIButton> getGUIButtons(String key) {
        if (!isKeyAvailable(key)) return new ArrayList<>();
        return guiButtons.get(key);
    }

    /**
     * Get all GUIButtons
     *
     * @return The GUIButtons as list
     */
    public List<GUIButton> getAllGUIButtons() {
        List<GUIButton> all = new ArrayList<>();
        for (Map.Entry ent : guiButtons.entrySet()) {
            for (GUIButton g : ((List<? extends GUIButton>) ent.getValue())) {
                all.add(g.clone());
            }
        }
        return all;
    }

    /**
     * Check if the key is available or not
     *
     * @param key The key
     * @return true if available, false otherwise
     */
    public boolean isKeyAvailable(String key) {
        return guiButtons.containsKey(key);
    }

    /**
     * Get all gui buttons map
     *
     * @return The gui buttons map
     */
    public Map<String, List<GUIButton>> getGUIButtons() {
        return guiButtons;
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
    }

    @Override
    public InventoryTemplate clone() {
        try {
            InventoryTemplate template = (InventoryTemplate) super.clone();
            template.setLayout(new HashMap<>(this.layout));
            template.setRawLayout(new ArrayList<>(this.rawLayout));

            Map<String, List<GUIButton>> buttons = new HashMap<>();
            for (Map.Entry ent : this.guiButtons.entrySet()) {
                String key = (String) ent.getKey();
                List<GUIButton> but = new ArrayList<>();

                for (GUIButton b : (List<GUIButton>) ent.getValue()) {
                    but.add(b.clone());
                }
                buttons.put(key, but);
            }
            template.setGuiButtons(buttons);
            return template;
        } catch (CloneNotSupportedException e) {
            throw new Error(e);
        }
    }
}
