package me.droreo002.oreocore.utils.misc;

import lombok.Getter;
import me.droreo002.oreocore.configuration.SerializableConfigVariable;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import static me.droreo002.oreocore.utils.strings.StringUtil.*;

public class TitleObject implements SerializableConfigVariable<TitleObject> {

    @Getter
    private String title;
    @Getter
    private String subTitle;
    @Getter
    private int fadeIn;
    @Getter
    private int stay;
    @Getter
    private int fadeOut;

    public TitleObject() {
        this.title = "Dummy";
        this.subTitle = "Dummy";
        this.fadeIn = 1;
        this.stay = 1;
        this.fadeOut = 1;
    }

    public TitleObject(String title, String subTitle, int fadeIn, int stay, int fadeOut) {
        this.title = title;
        this.subTitle = subTitle;
        this.fadeIn = fadeIn;
        this.stay = stay;
        this.fadeOut = fadeOut;
    }

    public void send(Player player) {
        player.sendTitle(color(title), color(subTitle), fadeIn, stay, fadeOut);
    }


    @Override
    public TitleObject getFromConfig(ConfigurationSection section) {
        String title = section.getString("title");
        String subTitle = section.getString("sub-title");
        int fadeIn = section.getInt("fade-in");
        int stay = section.getInt("stay");
        int fadeOut = section.getInt("fade-out");
        return new TitleObject(title, subTitle, fadeIn, stay, fadeOut);
    }

    @Override
    public void saveToConfig(String path, FileConfiguration config) {
        config.set(path + ".title", title);
        config.set(path + ".sub-title", subTitle);
        config.set(path + ".fade-in", fadeIn);
        config.set(path + ".stay", stay);
        config.set(path + ".fade-out", fadeIn);
    }
}
