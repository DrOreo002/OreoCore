package me.droreo002.oreocore.utils.strings;

import lombok.Getter;
import lombok.Setter;
import me.droreo002.oreocore.utils.list.ListUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static me.droreo002.oreocore.utils.strings.StringUtils.color;

/**
 * From Realizedd's Duel plugin (With some changes)
 */
public class TextBuilder {

    @Getter
    @Setter
    private List<BaseComponent> list = new ArrayList<>();

    public TextBuilder() {}

    private TextBuilder(String base, ClickEvent.Action clickAction, String clickValue, HoverEvent.Action hoverAction, String hoverValue) {
        if (base == null) {
            return;
        }
        base = color(base);

        Arrays.stream(TextComponent.fromLegacyText(base)).forEach(component -> {
            if (clickValue != null) {
                component.setClickEvent(new ClickEvent(clickAction, clickValue));
            }

            if (hoverValue != null) {
                component.setHoverEvent(new HoverEvent(hoverAction, TextComponent.fromLegacyText(hoverValue)));
            }

            list.add(component);
        });
    }

    /**
     * Create a new TextBuilder, parameter are self explanatory
     *
     * @return The TextBuilder
     */
    public static TextBuilder of(String base, ClickEvent.Action clickAction, String clickValue, HoverEvent.Action hoverAction, String hoverValue) {
        return new TextBuilder(base, clickAction, clickValue, hoverAction, hoverValue);
    }

    /**
     * Create a new TextBuilder, parameter are self explanatory
     *
     * @return The TextBuilder
     */
    public static TextBuilder of(String base) {
        return of(base, null, null, null, null);
    }

    /**
     * Add a text into the builder
     *
     * @param text The text to add
     * @return the TextBuilder
     */
    public TextBuilder addText(String text) {
        if (text == null) {
            return this;
        }

        list.addAll(Arrays.asList(TextComponent.fromLegacyText(color(text))));
        return this;
    }

    /**
     * Add a click event on the text
     *
     * @param text Text to add
     * @param action Click event action
     * @param value The value of the event
     * @return the TextBuilder
     */
    public TextBuilder addClickEvent(String text, ClickEvent.Action action, String value) {
        if (text == null || value == null) {
            return this;
        }

        Arrays.stream(TextComponent.fromLegacyText(color(text))).forEach(component -> {
            component.setClickEvent(new ClickEvent(action, color(value)));
            list.add(component);
        });
        return this;
    }

    /**
     * Add a hover event on the text
     *
     * @param text Text to add
     * @param action Hover event action
     * @param value The value of the event as a list
     * @return the TextBuilder
     */
    public TextBuilder addHoverEvent(String text, HoverEvent.Action action, List<String> value) {
        if (text == null || value == null) {
            return this;
        }
        List<TextComponent> components = new ArrayList<>();
        for (int i = 0; i < value.size(); i++) {
            TextComponent c = new TextComponent(color(value.get(i)) + "\n");
            if (i == value.size() - 1) { // Last one
                c = new TextComponent(color(value.get(i)));
            }
            components.add(c);
        }

        Arrays.stream(TextComponent.fromLegacyText(color(text))).forEach(component -> {
            component.setHoverEvent(new HoverEvent(action, components.toArray(new TextComponent[components.size() - 1])));
            list.add(component);
        });
        return this;
    }

    /**
     * Add a hover event on the text
     *
     * @param text Text to add
     * @param action Hover event action
     * @param value The value of the event as a string
     * @return the TextBuilder
     */
    public TextBuilder addHoverEvent(String text, HoverEvent.Action action, String value) {
        if (text == null || value == null) {
            return this;
        }

        Arrays.stream(TextComponent.fromLegacyText(color(text))).forEach(component -> {
            component.setHoverEvent(new HoverEvent(action, TextComponent.fromLegacyText(color(value))));
            list.add(component);
        });
        return this;
    }

    /**
     * Add a click and hover event on the text
     *
     * @param text Text to add
     * @param clickAction The click event action
     * @param clickValue The click event's value
     * @param hoverAction The hover event action
     * @param hoverValue The hover event's value
     * @return the TextBuilder
     */
    public TextBuilder addClickEvent(String text, ClickEvent.Action clickAction, String clickValue, HoverEvent.Action hoverAction, String hoverValue) {
        if (text == null) {
            return this;
        }

        Arrays.stream(TextComponent.fromLegacyText(color(text))).forEach(component -> {
            if (clickValue != null) {
                component.setClickEvent(new ClickEvent(clickAction, color(clickValue)));
            }

            if (hoverValue != null) {
                component.setHoverEvent(new HoverEvent(hoverAction, TextComponent.fromLegacyText(color(hoverValue))));
            }

            list.add(component);
        });
        return this;
    }

    /**
     * Set the click event of the current text
     *
     * @param action The click event action
     * @param value The click event value
     * @return the TextBuilder
     */
    public TextBuilder setClickEvent(ClickEvent.Action action, String value) {
        if (value == null) {
            return this;
        }

        list.forEach(component -> component.setClickEvent(new ClickEvent(action, color(value))));
        return this;
    }

    /**
     * Set the hover event of current text
     *
     * @param action The hover event action
     * @param value The hover event value
     * @return the TextBuilder
     */
    public TextBuilder setHoverEvent(HoverEvent.Action action, String value) {
        if (value == null) {
            return this;
        }

        list.forEach(component -> component.setHoverEvent(new HoverEvent(action, TextComponent.fromLegacyText(color(value)))));
        return this;
    }

    /**
     * Merge another TextBuilder object
     *
     * @param textBuilder Other object
     * @return the TextBuilder
     */
    public TextBuilder add(TextBuilder textBuilder) {
        if (textBuilder == null) {
            return this;
        }
        list.addAll(textBuilder.getList());
        return this;
    }

    /**
     * Send the text to the player
     *
     * @param players The targeted players
     */
    public void send(Collection<Player> players) {
        BaseComponent[] message = list.toArray(new BaseComponent[0]);
        players.forEach(player -> {
            if (player.isOnline()) {
                player.spigot().sendMessage(message);
            }
        });
    }

    /**
     * Send the text to the player
     *
     * @param players The targeted players
     */
    public void send(Player... players) {
        send(Arrays.asList(players));
    }

    /**
     * Replace string inside the text to a new TextComponent
     *
     * @param text The text to find and replace
     * @param components The component as the replacement
     */
    public void replace(String text, List<BaseComponent> components) {
        int foundIndex = 0;
        boolean found = false;
        for (BaseComponent c : list) {
            String check = StringUtils.stripColor(c.toLegacyText().trim());
            if (check.equals(text)) {
                found = true;
                break;
            }
            foundIndex++;
        }
        if (!found) return;
        list.remove(foundIndex);
        list.addAll(foundIndex, components);
    }
}
