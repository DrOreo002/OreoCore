package me.droreo002.oreocore.utils.item.helper;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class TextPlaceholder {

    @Getter
    private final List<TextPlaceholder> placeholders;
    @Getter
    private final String from;
    @Getter
    private final String to;

    public TextPlaceholder(String from, String to) {
        this.from = from;
        this.to = to;
        this.placeholders = new ArrayList<>();

        placeholders.add(this);
    }

    public TextPlaceholder add(String from, String to) {
        placeholders.add(new TextPlaceholder(from, to));
        return this;
    }
}
