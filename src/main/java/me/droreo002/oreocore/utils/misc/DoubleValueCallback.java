package me.droreo002.oreocore.utils.misc;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface DoubleValueCallback<T, S> {
    /**
     * Simple callback
     *
     * @param t Object
     */
    void success(@Nullable T t, @NotNull S s);

    /**
     * Called when something bad happens!
     *
     * @param e The error log
     */
    default void error(Exception e) {
        e.printStackTrace();
    }
}
