package me.droreo002.oreocore.configuration.dummy;

import lombok.Getter;
import me.droreo002.oreocore.OreoCore;
import me.droreo002.oreocore.configuration.ConfigMemory;
import me.droreo002.oreocore.configuration.ConfigMemoryManager;
import me.droreo002.oreocore.configuration.ConfigVariable;
import me.droreo002.oreocore.configuration.CustomConfig;
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

        Memory(CustomConfig customConfig) {
            this.customConfig = customConfig;
        }

        @Override
        public CustomConfig getParent() {
            return customConfig;
        }
    }
}
