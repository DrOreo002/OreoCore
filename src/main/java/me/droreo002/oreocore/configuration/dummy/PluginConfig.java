package me.droreo002.oreocore.configuration.dummy;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.droreo002.oreocore.configuration.ConfigurationMemory;
import me.droreo002.oreocore.configuration.annotations.ConfigVariable;
import me.droreo002.oreocore.configuration.CustomConfiguration;
import me.droreo002.oreocore.debugging.ODebug;
import me.droreo002.oreocore.enums.ArmorStandBody;
import me.droreo002.oreocore.inventory.ITemplatePlaceholder;
import me.droreo002.oreocore.inventory.InventoryTemplate;
import me.droreo002.oreocore.title.OreoTitle;
import me.droreo002.oreocore.utils.item.ItemStackBuilder;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class PluginConfig extends CustomConfiguration implements ConfigurationMemory {

    private static final String LATEST_VERSION = "11.1";

    @ConfigVariable(path = "Annotation.test")
    @Getter
    private String working;

    @ConfigVariable(path = "Title.test", isSerializableObject = true, isUpdateAbleObject = true)
    @Getter
    private OreoTitle oreoTitle;

    @ConfigVariable(path = "Enum.test")
    @Getter
    private ArmorStandBody body;

    @ConfigVariable(path = "Settings.disable-notification-on-join")
    @Getter
    private boolean disableNotif;

    @ConfigVariable(path = "Settings.cache-player-information")
    @Getter
    private boolean cachePlayerInformation;

    @ConfigVariable(path = "Settings.cache-player-head")
    @Getter
    private boolean cachePlayerHead;

    @ConfigVariable(path = "Inventory.Test", isSerializableObject = true, isUpdateAbleObject = true)
    @Getter
    private InventoryTemplate testTemplate = new InventoryTemplate();

    @ConfigVariable(path = "Inventory-Placeholder", isSerializableObject = true, loadPriority = 1)
    @Getter
    private ITemplatePlaceholder placeholder = new ITemplatePlaceholder();

    @ConfigVariable(path = "Settings.debugging", isUpdateAbleObject = true)
    @Getter @Setter
    private ConfigurationSection debuggingData;

    @ConfigVariable(path = "ItemStackBuilderTest", isSerializableObject = true)
    @Getter
    private ItemStackBuilder itemStackBuilderTest;

    public PluginConfig(JavaPlugin plugin) {
        super(plugin, new File(plugin.getDataFolder(), "config.yml"));
        if (tryUpdate("ConfigVersion", LATEST_VERSION)) {
            ODebug.log(plugin, "Successfully updated &7config.yml &fto version &c" + LATEST_VERSION, true);
        }
        registerMemory(this);
    }

    @Override
    public @NonNull CustomConfiguration getParent() {
        return this;
    }
}
