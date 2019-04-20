package me.droreo002.oreocore.utils.item.helper;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TextPlaceholder {

    @Getter
    private final List<TextPlaceholder> placeholders;
    @Getter
    private final String from;
    @Getter
    private final String to;
    @Getter
    private final ItemMetaType type;

    public TextPlaceholder(ItemMetaType type, String from, String to) {
        this.type = type;
        this.from = from;
        this.to = to;
        this.placeholders = new ArrayList<>();

        placeholders.add(this);
    }

    /**
     * Add a text placeholder object
     *
     * @param type : The meta type
     * @param from : From x string
     * @param to : To x string
     * @return the TextPlaceholder object
     */
    public TextPlaceholder add(ItemMetaType type, String from, String to) {
        placeholders.add(new TextPlaceholder(type, from, to));
        return this;
    }
}
