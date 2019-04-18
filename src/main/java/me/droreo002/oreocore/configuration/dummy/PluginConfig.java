package me.droreo002.oreocore.configuration.dummy;

import lombok.Getter;
import me.droreo002.oreocore.OreoCore;
import me.droreo002.oreocore.configuration.ConfigMemory;
import me.droreo002.oreocore.configuration.ConfigMemoryManager;
import me.droreo002.oreocore.configuration.annotations.ConfigVariable;
import me.droreo002.oreocore.configuration.CustomConfig;
import me.droreo002.oreocore.enums.ArmorStandBody;
import me.droreo002.oreocore.utils.misc.TitleObject;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class PluginConfig extends CustomConfig {

    @Getter
    private Memory memory;

    public PluginConfig(JavaPlugin plugin) {
        super(plugin, new File(OreoCore.getInstance().getDataFolder(), "config.yml"));
        memory = new Memory(this);
        ConfigMemoryManager.registerMemory(plugin, memory);
    }

    public void reload() {
        super.reloadConfig();
        memory = new Memory(this);
        ConfigMemoryManager.reloadMemory(getPlugin(), memory);
    }

    public class Memory implements ConfigMemory {

        @Getter
        private final CustomConfig customConfig;

        @ConfigVariable(path = "Annotation.test", errorWhenNull = true)
        @Getter
        private String working;

        @ConfigVariable(path = "Title.test", isSerializableObject = true)
        @Getter
        private TitleObject titleObject = new TitleObject();

        @ConfigVariable(path = "Enum.test")
        @Getter
        private ArmorStandBody body;

        Memory(CustomConfig customConfig) {
            this.customConfig = customConfig;
        }

        @Override
        public CustomConfig getParent() {
            return customConfig;
        }
    }
}
