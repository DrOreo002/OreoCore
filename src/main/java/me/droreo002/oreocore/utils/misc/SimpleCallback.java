package me.droreo002.oreocore.utils.misc;

import org.bukkit.craftbukkit.libs.jline.internal.Nullable;

public interface SimpleCallback<T> {

    /**
     * Simple callback
     *
     * @param t Object
     */
    void success(@Nullable T t);

    /**
     * Called when something bad happens!
     *
     * @param e The error log
     */
    default void error(Exception e) {
        e.printStackTrace();
    }
}
