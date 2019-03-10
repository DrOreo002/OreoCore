package me.droreo002.oreocore.inventory.api.animation;

import lombok.Getter;
import lombok.Setter;
import me.droreo002.oreocore.inventory.api.GUIButton;
import me.droreo002.oreocore.utils.strings.StringUtils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ItemAnimation extends ItemStack {

    @Getter
    private ItemStack item;
    @Getter
    private List<String> frames;
    @Getter
    private int slot;
    @Getter
    private int currentFrame;
    @Getter
    @Setter
    private GUIButton.ButtonListener listener;

    public ItemAnimation(ItemStack itemStack, List<String> frames, int slot) {
        super(itemStack);
        this.frames = frames;
        this.slot = slot;
        this.item = itemStack;
        this.currentFrame = 0;
    }

    public String update() {
        ItemMeta meta = getItemMeta();
        meta.setDisplayName(StringUtils.color(frames.get(currentFrame)));
        setItemMeta(meta);
        currentFrame += 1;
        if (currentFrame > frames.size() - 1) {
            currentFrame = 0;
        }
        return meta.getDisplayName();
    }
}

