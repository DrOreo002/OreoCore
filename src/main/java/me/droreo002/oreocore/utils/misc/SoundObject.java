package me.droreo002.oreocore.utils.misc;

import lombok.Getter;
import me.droreo002.oreocore.OreoCore;
import me.droreo002.oreocore.configuration.SerializableConfigVariable;
import me.droreo002.oreocore.utils.bridge.OSound;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class SoundObject implements SerializableConfigVariable<SoundObject> {

    // Pre - Initialized
    public static final SoundObject SUCCESS_SOUND = new SoundObject(OSound.ENTITY_EXPERIENCE_ORB_PICKUP);
    public static final SoundObject ERROR_SOUND = new SoundObject(OSound.BLOCK_ANVIL_FALL);

    @Getter
    private float volume;
    @Getter
    private float pitch;
    @Getter
    private OSound sound;

    /**
     * Allow null for @ConfigVariable support
     */
    public SoundObject() { }

    public SoundObject(OSound sound, float volume, float pitch) {
        this.volume = volume;
        this.pitch = pitch;
        this.sound = sound;
    }

    public SoundObject(OSound sound) {
        this.volume = 1.0f;
        this.pitch = 1.0f;
        this.sound = sound;
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
        this.sound = OSound.match(toParse);
        if (sound == null) throw new NullPointerException("Error!. Cannot find sound with the name of " + contains[0]);
    }

    /**
     * Send the sound to the player
     *
     * @param player : The target player
     */
    public void send(Player player) {
        Sound s = sound.parseSound();
        if (s == null) throw new NullPointerException("Sound is invalid!");
        player.playSound(player.getLocation(), s, volume, pitch);
    }

    /**
     * Send the sound to the player
     *
     * @param player : The target player
     * @param delay : The delay in second
     */
    public void send(Player player, int delay) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(OreoCore.getInstance(), () -> send(player), 20L * delay);
    }

    public static SoundObject fromConfig(ConfigurationSection section) {
        float volume = (float) section.getDouble("volume", 1.0f);
        float pitch = (float) section.getDouble("pitch", 1.0f);
        OSound sounds = OSound.match(section.getString("sound"));
        if (sounds == null) throw new NullPointerException("Error!. Cannot find sound with the name of " + section.getString("sound"));
        return new SoundObject(sounds, volume, pitch);
    }

    @Override
    public SoundObject getFromConfig(ConfigurationSection section) {
        float volume = (float) section.getDouble("volume", 1.0f);
        float pitch = (float) section.getDouble("pitch", 1.0f);
        OSound sounds = OSound.match(section.getString("sound"));
        if (sounds == null) throw new NullPointerException("Error!. Cannot find sound with the name of " + section.getString("sound"));
        return new SoundObject(sounds, volume, pitch);
    }

    @Override
    public void saveToConfig(String path, FileConfiguration config) {
        config.set(path + ".sound", sound.toString());
        // Not default value. Then save
        if (volume != 1.0f) config.set(path + ".volume", volume);
        if (pitch != 1.0f) config.set(path + ".pitch", pitch);
    }
}
