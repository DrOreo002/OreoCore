package me.droreo002.oreocore.utils.item.helper;

import lombok.Getter;
import lombok.Setter;
import me.droreo002.oreocore.utils.list.ListUtils;
import me.droreo002.oreocore.utils.strings.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
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
    @Getter @Setter
    private boolean lorePlaceholder;

    public TextPlaceholder(ItemMetaType type, String from, String to) {
        this.type = type;
        this.from = from;
        this.to = to;
        this.lorePlaceholder = false;
        this.placeholders = new ArrayList<>();

        placeholders.add(this);
    }

    public TextPlaceholder(String from, String to) {
        this.type = ItemMetaType.NONE;
        this.from = from;
        this.to = to;
        this.lorePlaceholder = false;
        this.placeholders = new ArrayList<>();

        placeholders.add(this);
    }

    public TextPlaceholder(ItemMetaType type, String from, String to, boolean lorePlaceholder) {
        this.type = type;
        this.from = from;
        this.to = to;
        this.lorePlaceholder = lorePlaceholder;
        this.placeholders = new ArrayList<>();

        placeholders.add(this);
    }

    /**
     * Add a text placeholder object
     *
     * @param from : From x string
     * @param to : To x string
     * @return the TextPlaceholder object
     */
    public TextPlaceholder add(String from, String to) {
        placeholders.add(new TextPlaceholder(from, to));
        return this;
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

    /**
     * Add a text placeholder object, but using list
     *
     * @param type : The type
     * @param from : From x string
     * @param to : To x list (Will fill)
     * @return the TextPlaceholder object
     */
    public TextPlaceholder add(ItemMetaType type, String from, List<String> to) {
        if (type != ItemMetaType.LORE) throw new IllegalStateException("Trying to add list placeholder to non lore");
        placeholders.add(new TextPlaceholder(type, from, ListUtils.toString(to), true));
        return this;
    }

    /**
     * Add all text placeholder into this placeholder
     *
     * @param textPlaceholder : The placeholder
     */
    public void addAll(TextPlaceholder textPlaceholder) {
        this.placeholders.addAll(textPlaceholder.getPlaceholders());
    }

    /**
     * Format the string
     *
     * @param s The string to format
     */
    public String format(String s) {
        for (TextPlaceholder p : placeholders) {
            s = s.replace(p.getFrom(), p.getTo());
        }
        return s;
    }

    /**
     * Format the list
     *
     * @param list The list
     */
    public void format(List<String> list) {
        for (TextPlaceholder p : placeholders) {
            list.replaceAll(s -> s.replace(p.getFrom(), p.getTo()));
        }
    }
}
