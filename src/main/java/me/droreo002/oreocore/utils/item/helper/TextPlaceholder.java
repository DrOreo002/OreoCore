package me.droreo002.oreocore.utils.item.helper;

import lombok.Getter;
import lombok.Setter;
import me.droreo002.oreocore.utils.item.ItemUtils;
import me.droreo002.oreocore.utils.list.ListUtils;
import me.droreo002.oreocore.utils.strings.StringUtils;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static me.droreo002.oreocore.utils.strings.StringUtils.color;

public class TextPlaceholder {

    public static final Pattern PAPI_REGEX_SINGLE = Pattern.compile("[%]([^%]+)[%]");

    @Getter
    private final List<TextPlaceholder> placeholders;
    @Getter
    private final String from;
    @Getter
    private final String to;
    @Getter @Setter
    private ItemMetaType type;

    public static TextPlaceholder empty() {
        return new TextPlaceholder("%EMPTY_PLACEHOLDER%", "MARKED AS EMPTY PLACEHOLDER");
    }

    /**
     * Construct via static useful for shortening the code
     *
     * @param from Replace from
     * @param to Replace to
     * @return resulted object
     */
    public static TextPlaceholder of(String from, Object to) {
        return new TextPlaceholder(from, to);
    }

    /**
     * Construct via static useful for shortening the code
     *
     * @param type What to replace
     * @param from Replace from
     * @param to Replace to
     * @return resulted object
     */
    public static TextPlaceholder of(ItemMetaType type, String from, Object to) {
        return new TextPlaceholder(type, from, to);
    }

    public TextPlaceholder(String from, Object to) {
        validate(from);
        this.type = ItemMetaType.NONE;
        this.from = from;
        this.to = (to instanceof List<?>) ? ListUtils.toString((List<String>) to) : String.valueOf(to);
        this.placeholders = new CopyOnWriteArrayList<>();

        placeholders.add(this);
    }

    public TextPlaceholder(ItemMetaType type, String from, Object to) {
        validate(from);
        this.type = type;
        this.from = from;
        this.to = (to instanceof List<?>) ? ListUtils.toString((List<String>) to) : String.valueOf(to);
        this.placeholders = new ArrayList<>();

        placeholders.add(this);
    }

    /**
     * Remove placeholder
     *
     * @param from Placeholder name
     * @return TextPlaceholder
     */
    public TextPlaceholder remove(String from) {
        this.placeholders.removeIf(placeholder -> placeholder.getFrom().equals(from));
        return this;
    }

    /**
     * Apply replace type to all
     *
     * @param type The replace type
     * @return Modified TextPlaceholder
     */
    public TextPlaceholder applyReplaceType(ItemMetaType type) {
        placeholders.forEach(placeholder -> placeholder.setType(type));
        return this;
    }

    /**
     * Add a text placeholder object
     *
     * @param from : From x string
     * @param to : To x string
     * @return the TextPlaceholder object
     */
    public TextPlaceholder add(String from, Object to) {
        validate(from);
        remove(from);
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
        validate(from);
        placeholders.add(new TextPlaceholder(type, from, to));
        return this;
    }

    /**
     * Add all text placeholder into this placeholder
     *
     * @param textPlaceholder : The placeholder
     */
    public TextPlaceholder addAll(TextPlaceholder textPlaceholder) {
        this.placeholders.addAll(textPlaceholder.getPlaceholders());
        return this;
    }

    /**
     * Format the string
     *
     * @param s The string to format
     * @return the formatted string
     */
    public String format(String s) {
        for (TextPlaceholder p : placeholders) {
            s = replacePlaceholder(s, p);
        }
        return s;
    }

    /**
     * Apply placeholder to item
     *
     * @param item The item
     * @return The modified item
     */
    public ItemStack format(ItemStack item) {
        ItemStack cloned = item.clone();
        StringBuilder displayName = new StringBuilder(ItemUtils.getName(cloned, true));
        List<String> lore = ItemUtils.getLore(cloned, false);

        if (!getPlaceholders().isEmpty()) {
            for (TextPlaceholder place : getPlaceholders()) {
                // Default will be true
                boolean doLore = true;
                boolean doDisplay = true;

                switch (place.getType()) {
                    case DISPLAY_NAME:
                        doDisplay = true;
                        doLore = false;
                        break;
                    case LORE:
                        doLore = true;
                        doDisplay = false;
                        break;
                    default: break;
                }

                if (doLore) {
                    lore = format(lore);
                }
                if (doDisplay) {
                    displayName = new StringBuilder(format(displayName.toString()));
                }
            }

            ItemMeta meta = cloned.getItemMeta();
            if (meta == null) return cloned;
            meta.setDisplayName(color(displayName.toString()));
            meta.setLore(lore.stream().map(StringUtils::color).collect(Collectors.toList()));
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
            cloned.setItemMeta(meta);
        }

        return cloned;
    }

    /**
     * Format the list
     *
     * @param list The list
     */
    public List<String> format(List<String> list) {
        final List<String> replaced = new ArrayList<>();
        final List<String> result = new ArrayList<>();

        list.forEach(s -> {
            for (TextPlaceholder p : placeholders) {
                s = replacePlaceholder(s, p);
            }
            replaced.add(s);
        });

        for (String s : replaced) {
            if (ListUtils.isSerializedList(s)) {
                // Replace the serialized list as empty one, and then add the list placeholder at the next line
                String r = s.replace(ListUtils.getSerializedString(s), "");
                if (!StringUtils.stripColor(r.replace(" ", "")).isEmpty()) {
                    result.add(r); // Add the remaining string
                }
                result.addAll(ListUtils.toList(s)); // Add the list placeholder into the next index
            } else {
                result.add(s);
            }
        }
        return ListUtils.color(result);
    }

    /**
     * Check if the string contains a placeholder
     *
     * @param source The source
     * @return true if contains, false otherwise
     */
    public static boolean isContainsPlaceholder(String source) {
        Matcher sMatcher = PAPI_REGEX_SINGLE.matcher(source);
        return sMatcher.find();
    }

    /**
     * Replace the string, will also check if it
     * contains the string
     *
     * @param source The source string
     * @param p The TextPlaceholder
     */
    private String replacePlaceholder(String source, TextPlaceholder p) {
        Matcher sMatcher = PAPI_REGEX_SINGLE.matcher(source);
        while (sMatcher.find()) {
            String curr = sMatcher.group();
            if (p.getFrom().equals(curr)) {
                source = source.replace(p.getFrom(), p.getTo()); // Finally replace
            }
        }
        return source;
    }

    /**
     * Validate the placeholder
     *
     * @param placeholder The placeholder
     */
    private void validate(String placeholder) {
        if (!placeholder.contains("%")) throw new IllegalStateException("Placeholder must contains %!");
        if (!placeholder.endsWith("%")) throw new IllegalStateException("Placeholder must ends with %!");
    }
}
