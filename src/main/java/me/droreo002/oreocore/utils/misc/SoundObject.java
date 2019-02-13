package me.droreo002.oreocore.utils.misc;

import lombok.Getter;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundObject {

    // Pre - Initialized
    public static final SoundObject SUCCESS_SOUND = new SoundObject(Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
    public static final SoundObject ERROR_SOUND = new SoundObject(Sound.ENTITY_BLAZE_DEATH);

    @Getter
    private float volume;
    @Getter
    private float pitch;
    @Getter
    private Sound sound;

    public SoundObject(Sound sound, float volume, float pitch) {
        this.volume = volume;
        this.pitch = pitch;
        this.sound = sound;
    }

    public SoundObject(Sound sound) {
        this.volume = 1.0f;
        this.pitch = 1.0f;
        this.sound = sound;
    }

    public SoundObject(String toParse) {
        if (!toParse.contains(",")) {
            throw new IllegalStateException("Invalid string to parse!");
        }
        String[] contains = toParse.split(",");
        this.volume = Float.parseFloat(contains[1]);
        this.pitch = Float.parseFloat(contains[2]);
        this.sound = Sound.valueOf(contains[0]);
    }

    public void send(Player player) {
        player.playSound(player.getLocation(), sound, volume, pitch);
    }
}
