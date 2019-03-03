package me.droreo002.oreocore.utils.misc;

import lombok.Getter;
import me.droreo002.oreocore.configuration.SerializableConfigVariable;
import me.droreo002.oreocore.enums.Sounds;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class SoundObject implements SerializableConfigVariable<SoundObject> {

    // Pre - Initialized
    public static final SoundObject SUCCESS_SOUND = new SoundObject(Sounds.ORB_PICKUP);
    public static final SoundObject ERROR_SOUND = new SoundObject(Sounds.ORB_PICKUP);

    @Getter
    private float volume;
    @Getter
    private float pitch;
    @Getter
    private Sounds sounds;

    /**
     * Allow null for @ConfigVariable support
     */
    public SoundObject() {

    }

    public SoundObject(Sounds sound, float volume, float pitch) {
        this.volume = volume;
        this.pitch = pitch;
        this.sounds = sound;
    }

    public SoundObject(Sounds sound) {
        this.volume = 1.0f;
        this.pitch = 1.0f;
        this.sounds = sound;
    }

    /**
     * Parsing from (volume,pitch,sound). Recommended if you want fast data saving
     * Example : 1.0,1.0,ENTITY_BLAZE_DEATH
     *
     * @param toParse : The string
     */
    public SoundObject(String toParse) {
        if (!toParse.contains(",")) {
            throw new IllegalStateException("Invalid string to parse!");
        }
        String[] contains = toParse.split(",");
        this.volume = Float.parseFloat(contains[1]);
        this.pitch = Float.parseFloat(contains[2]);
        this.sounds = Sounds.fromString(contains[0]);
        if (sounds == null) throw new NullPointerException("Error!. Cannot find sound with the name of " + contains[0]);
    }

    public void send(Player player) {
        player.playSound(player.getLocation(), sounds.bukkitSound(), volume, pitch);
    }

    @Override
    public SoundObject getFromConfig(ConfigurationSection section) {
        float volume = (float) section.getDouble("volume", 1.0f);
        float pitch = (float) section.getDouble("pitch", 1.0f);
        Sounds sounds = Sounds.fromString(section.getString("sound"));
        if (sounds == null) throw new NullPointerException("Error!. Cannot find sound with the name of " + section.getString("sound"));
        return new SoundObject(sounds, volume, pitch);
    }

    @Override
    public void saveToConfig(String path, FileConfiguration config) {
        config.set(path + ".sound", sounds.toString());
        // Not default value. Then save
        if (volume != 1.0f) config.set(path + ".volume", volume);
        if (pitch != 1.0f) config.set(path + ".pitch", pitch);
    }
}
