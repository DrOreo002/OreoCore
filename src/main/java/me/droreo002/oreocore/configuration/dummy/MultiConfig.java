package me.droreo002.oreocore.configuration.dummy;

import lombok.Getter;
import lombok.NonNull;
import me.droreo002.oreocore.OreoCore;
import me.droreo002.oreocore.configuration.Configuration;
import me.droreo002.oreocore.configuration.ConfigurationMemory;
import me.droreo002.oreocore.configuration.MultiFileConfiguration;
import me.droreo002.oreocore.configuration.annotations.ConfigVariable;

import java.io.File;

public class MultiConfig extends MultiFileConfiguration implements ConfigurationMemory {

    @ConfigVariable(path = "TestString", yamlFileName = "test-config.yml")
    @Getter
    private String testString;

    public MultiConfig(OreoCore plugin) {
        super(plugin, new File(plugin.getDataFolder(), "my-folder"));
        registerMemory(this);
    }

    @Override
    public @NonNull Configuration getParent() {
        return this;
    }
}
