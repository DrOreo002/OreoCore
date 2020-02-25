package me.droreo002.oreocore.inventory.button;

import lombok.Getter;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

public interface ButtonListener {

    /**
     * Get the button listener's ClickType
     *
     * @return The click type
     */
    default ClickType getClickType() {
        return ClickType.LEFT;
    }

    /**
     * Get this listener priority
     *
     * @return The listener priority
     */
    default Priority getListenerPriority() {
        return Priority.DEFAULT;
    }

    /**
     * Called when button is clicked
     *
     * @param e The inventory click event
     */
    void onClick(ButtonClickEvent e);

    enum Priority {

        DEFAULT(0),
        LOW(1),
        MEDIUM(2),
        HIGH(3);

        @Getter
        private int level;

        Priority(int level) {
            this.level = level;
        }
    }
}