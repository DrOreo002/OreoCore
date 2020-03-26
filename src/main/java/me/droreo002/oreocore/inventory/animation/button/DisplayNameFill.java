package me.droreo002.oreocore.inventory.animation.button;

import lombok.Getter;
import me.droreo002.oreocore.utils.item.ItemUtils;
import org.bukkit.inventory.ItemStack;

import static me.droreo002.oreocore.utils.strings.StringUtils.stripColor;

public class DisplayNameFill extends ButtonAnimation {

    @Getter
    private String fillColor, fillBaseColor;

    public DisplayNameFill(String fillColor, String fillBaseColor) {
        super(ButtonAnimationType.FILL_ANIMATION.name());
        this.fillColor = fillColor;
        this.fillBaseColor = fillBaseColor;
    }

    @Override
    public void initializeFrame(ItemStack buttonItem) {
        String displayName = stripColor(ItemUtils.getName(buttonItem, false));
        String[] fill = ButtonAnimationUtils.colorFillString(displayName, this.fillColor, this.fillBaseColor);
        for (String s : fill) {
            addFrame(new IButtonFrame() {
                @Override
                public String nextDisplayName(String previousDisplayName) {
                    return s;
                }
            });
        }
        setRepeating(true);
    }

    public static DisplayNameFill build() {
        return new DisplayNameFill("&b&l", "&f&l");
    }
}
