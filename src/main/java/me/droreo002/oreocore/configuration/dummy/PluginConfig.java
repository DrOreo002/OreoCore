package me.droreo002.oreocore.configuration.dummy;

import lombok.Getter;
import lombok.NonNull;
import me.droreo002.oreocore.OreoCore;
import me.droreo002.oreocore.configuration.ConfigMemory;
import me.droreo002.oreocore.configuration.ConfigMemoryManager;
import me.droreo002.oreocore.configuration.ConfigUpdater;
import me.droreo002.oreocore.configuration.annotations.ConfigVariable;
import me.droreo002.oreocore.configuration.CustomConfig;
import me.droreo002.oreocore.enums.ArmorStandBody;
import me.droreo002.oreocore.utils.misc.TitleObject;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Documented;

public class PluginConfig extends CustomConfig {

    @Getter
    private Memory memory;

    public PluginConfig(JavaPlugin plugin) {
        super(plugin, new File(plugin.getDataFolder(), "config.yml"));
        memory = new Memory(this);
        ConfigMemoryManager.registerMemory(plugin, memory);
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
    }

    public class Memory implements ConfigMemory {

        @Getter
        private final CustomConfig customConfig;

        @ConfigVariable(path = "Annotation.test")
        @Getter
        private String working;

        @ConfigVariable(path = "Title.test", isSerializableObject = true)
        @Getter
        private TitleObject titleObject = new TitleObject();

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

        Memory(CustomConfig customConfig) {
            this.customConfig = customConfig;
        }

        @Override
        public @NonNull CustomConfig getParent() {
            return customConfig;
        }
    }
}
