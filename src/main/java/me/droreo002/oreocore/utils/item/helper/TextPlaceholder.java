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

    public TextPlaceholder(String from, Object to) {
        this.type = ItemMetaType.NONE;
        this.from = from;
        this.to = (to instanceof String) ? (String) to : ListUtils.toString((List<String>) to);
        this.placeholders = new ArrayList<>();

        placeholders.add(this);
    }

    public TextPlaceholder(ItemMetaType type, String from, Object to) {
        this.type = type;
        this.from = from;
        this.to = (to instanceof String) ? (String) to : ListUtils.toString((List<String>) to);
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
    public TextPlaceholder add(String from, Object to) {
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
    public TextPlaceholder add(ItemMetaType type, String from, Object to) {
        placeholders.add(new TextPlaceholder(type, from, to));
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
     * @return the formatted string
     */
    public String format(String s) {
        for (TextPlaceholder p : placeholders) {
            s = s.replace(p.getFrom(), p.getTo());
        }
        return s;
    }

    /**
     * Format the string one by one
     * @param s The string
     * @param separator The string's separator
     * @return the formatted string
     */
    public String formatOneByOne(String s, String separator) {
        final String[] args = s.split(separator);
        final StringBuilder builder = new StringBuilder();
        for (String arg : args) {
            for (TextPlaceholder p : placeholders) {
                arg = arg.replace(p.getFrom(), p.getTo());
            }
            builder.append(arg).append(" ");
        }
        return builder.toString().substring(0, builder.toString().lastIndexOf(" "));
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
