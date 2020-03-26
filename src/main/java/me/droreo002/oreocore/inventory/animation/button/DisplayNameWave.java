package me.droreo002.oreocore.inventory.animation.button;

import lombok.Getter;
import me.droreo002.oreocore.utils.item.ItemUtils;
import org.bukkit.inventory.ItemStack;

import static me.droreo002.oreocore.utils.strings.StringUtils.stripColor;

public class DisplayNameWave extends ButtonAnimation {

    @Getter
    private String waveColor, waveBaseColor;

    public DisplayNameWave(String waveColor, String waveBaseColor) {
        super(ButtonAnimationType.WAVE_ANIMATION.name());
        this.waveColor = waveColor;
        this.waveBaseColor = waveBaseColor;
    }

    @Override
    public void initializeFrame(ItemStack buttonItem) {
        String displayName = stripColor(ItemUtils.getName(buttonItem, false));
        String[] wave = ButtonAnimationUtils.colorWaveString(displayName, this.waveColor, this.waveBaseColor);
        for (String s : wave) {
            addFrame(new IButtonFrame() {
                @Override
                public String nextDisplayName(String previousDisplayName) {
                    return s;
                }
            });
        }
        setRepeating(true);
    }

    public static DisplayNameWave build() {
        return new DisplayNameWave("&b&l", "&f&l");
    }
}