package me.droreo002.oreocore.utils.strings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import me.droreo002.oreocore.debugging.Debug;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * From Realizedd's Duel plugin (With some changes)
 */
public final class TextBuilder {

    @Getter @Setter
    private List<BaseComponent> list = new ArrayList<>();

    public TextBuilder() {}

    private TextBuilder(final String base, final ClickEvent.Action clickAction, final String clickValue, final HoverEvent.Action hoverAction, final String hoverValue) {
        if (base == null) {
            return;
        }

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

    public static TextBuilder of(final String base, final ClickEvent.Action clickAction, final String clickValue, final HoverEvent.Action hoverAction, final String hoverValue) {
        return new TextBuilder(base, clickAction, clickValue, hoverAction, hoverValue);
    }

    public static TextBuilder of(final String base) {
        return of(base, null, null, null, null);
    }

    public TextBuilder addText(final String text) {
        if (text == null) {
            return this;
        }

        list.addAll(Arrays.asList(TextComponent.fromLegacyText(text)));
        return this;
    }

    public TextBuilder addClickEvent(final String text, final ClickEvent.Action action, final String value) {
        if (text == null || value == null) {
            return this;
        }

        Arrays.stream(TextComponent.fromLegacyText(text)).forEach(component -> {
            component.setClickEvent(new ClickEvent(action, value));
            list.add(component);
        });
        return this;
    }

    public TextBuilder addHoverEvent(final String text, final HoverEvent.Action action, final List<String> value) {
        if (text == null || value == null) {
            return this;
        }

        List<TextComponent> components = new ArrayList<>();
        value.forEach(s -> components.add(new TextComponent(StringUtils.color(s) + "\n")));

        Arrays.stream(TextComponent.fromLegacyText(text)).forEach(component -> {
            component.setHoverEvent(new HoverEvent(action, components.toArray(new TextComponent[components.size() - 1])));
            list.add(component);
        });
        return this;
    }

    public TextBuilder addHoverEvent(final String text, final HoverEvent.Action action, final String value) {
        if (text == null || value == null) {
            return this;
        }

        Arrays.stream(TextComponent.fromLegacyText(text)).forEach(component -> {
            component.setHoverEvent(new HoverEvent(action, TextComponent.fromLegacyText(value)));
            list.add(component);
        });
        return this;
    }

    public TextBuilder addClickEvent(final String text, final ClickEvent.Action clickAction, final String clickValue, final HoverEvent.Action hoverAction, final String hoverValue) {
        if (text == null) {
            return this;
        }

        Arrays.stream(TextComponent.fromLegacyText(text)).forEach(component -> {
            if (clickValue != null) {
                component.setClickEvent(new ClickEvent(clickAction, clickValue));
            }

            if (hoverValue != null) {
                component.setHoverEvent(new HoverEvent(hoverAction, TextComponent.fromLegacyText(hoverValue)));
            }

            list.add(component);
        });
        return this;
    }

    public TextBuilder setClickEvent(final ClickEvent.Action action, final String value) {
        if (value == null) {
            return this;
        }

        list.forEach(component -> component.setClickEvent(new ClickEvent(action, value)));
        return this;
    }

    public TextBuilder setHoverEvent(final HoverEvent.Action action, final String value) {
        if (value == null) {
            return this;
        }

        list.forEach(component -> component.setHoverEvent(new HoverEvent(action, TextComponent.fromLegacyText(value))));
        return this;
    }

    public void send(final Collection<Player> players) {
        final BaseComponent[] message = list.toArray(new BaseComponent[0]);
        players.forEach(player -> {
            if (player.isOnline()) {
                player.spigot().sendMessage(message);
            }
        });
    }

    public void send(final Player... players) {
        send(Arrays.asList(players));
    }
}
