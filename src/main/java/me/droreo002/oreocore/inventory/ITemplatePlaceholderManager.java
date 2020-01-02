package me.droreo002.oreocore.inventory;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class ITemplatePlaceholderManager {

    private static final List<ITemplatePlaceholder> PLACEHOLDERS = new ArrayList<>();

    /**
     * Get placeholder by that name
     *
     * @param placeholder The placeholder to get
     * @return ITemplatePlaceholder if there's any
     */
    @Nullable
    public static ITemplatePlaceholder getPlaceholder(String placeholder) {
        for (ITemplatePlaceholder p : PLACEHOLDERS) {
            if (p.getPlaceholder().equalsIgnoreCase(placeholder)) {
                if (p.getPlaceholderItems().isEmpty()) throw new IllegalStateException("Placeholder is registered but invalid! (" + placeholder + "), no button was found!");
                return p;
            }
        }
        return null;
    }

    /**
     * Register placeholder to array
     *
     * @param placeholder Placeholder to register
     */
    public static void register(ITemplatePlaceholder placeholder) {
        if (getPlaceholder(placeholder.getPlaceholder()) != null) return;
        PLACEHOLDERS.add(placeholder);
    }
}
