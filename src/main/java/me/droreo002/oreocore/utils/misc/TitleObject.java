package me.droreo002.oreocore.utils.misc;

import lombok.Getter;
import lombok.Setter;
import me.droreo002.oreocore.configuration.SerializableConfigVariable;
import me.droreo002.oreocore.enums.Sounds;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import static me.droreo002.oreocore.utils.strings.StringUtils.*;

public class TitleObject implements SerializableConfigVariable<TitleObject> {

    @Getter @Setter
    private String title;
    @Getter @Setter
    private String subTitle;
    @Getter @Setter
    private int fadeIn;
    @Getter @Setter
    private int stay;
    @Getter @Setter
    private int fadeOut;
    @Getter @Setter
    private SoundObject soundOnSend;

    public TitleObject() {
        this.title = "Dummy";
        this.subTitle = "Dummy";
        this.fadeIn = 1;
        this.stay = 1;
        this.fadeOut = 1;
        this.soundOnSend = null;
    }

    public TitleObject(String title, String subTitle, int fadeIn, int stay, int fadeOut, SoundObject soundOnSend) {
        this.title = title;
        this.subTitle = subTitle;
        this.fadeIn = fadeIn;
        this.stay = stay;
        this.fadeOut = fadeOut;
        this.soundOnSend = soundOnSend;
    }

    public void send(Player player) {
        if (soundOnSend != null) soundOnSend.send(player);
        player.sendTitle(color(title), color(subTitle), fadeIn, stay, fadeOut);
    }

    @Override
    public TitleObject getFromConfig(ConfigurationSection section) {
        String title = section.getString("title");
        String subTitle = section.getString("sub-title");
        int fadeIn = section.getInt("fade-in");
        int stay = section.getInt("stay");
        int fadeOut = section.getInt("fade-out");
        SoundObject soundOnSend = null;
        if (section.isSet("soundOnSend")) {
            final Sounds s = Sounds.fromString(section.getString("soundOnSend"));
            if (s != null) soundOnSend = new SoundObject(s);
        }
        return new TitleObject(title, subTitle, fadeIn, stay, fadeOut, soundOnSend);
    }

    @Override
    public void saveToConfig(String path, FileConfiguration config) {
        config.set(path + ".title", title);
        config.set(path + ".sub-title", subTitle);
        config.set(path + ".fade-in", fadeIn);
        config.set(path + ".stay", stay);
        config.set(path + ".fade-out", fadeIn);
    }

    /**
     * Get from config (Static)
     *
     * @param section The ConfigSection containing the data
     * @return the TitleObject
     */
    public static TitleObject fromConfig(ConfigurationSection section) {
        return new TitleObject().getFromConfig(section); // We don't need config because we'll not use it anyway
    }
}
