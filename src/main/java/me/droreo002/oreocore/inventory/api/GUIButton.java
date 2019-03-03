package me.droreo002.oreocore.inventory.api;

import lombok.Getter;
import lombok.Setter;
import me.droreo002.oreocore.utils.entity.PlayerUtils;
import me.droreo002.oreocore.utils.misc.SoundObject;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class GUIButton {

    public static final ButtonListener CLOSE_LISTENER = e -> PlayerUtils.closeInventory((Player) e.getWhoClicked());

    @Getter
    @Setter
    private ItemStack item;
    @Getter
    private ButtonListener listener;
    @Getter
    private SoundObject soundOnClick;

    public GUIButton(ItemStack item) {
        this.item = item;
    }

    public GUIButton setListener(ButtonListener listener) {
        this.listener = listener;
        return this;
    }

    public GUIButton setSoundOnClick(SoundObject soundOnClick) {
        this.soundOnClick = soundOnClick;
        return this;
    }

    public interface ButtonListener {
        void onClick(InventoryClickEvent e);
    }
}
