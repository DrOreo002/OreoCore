package me.droreo002.oreocore.utils.misc;

public interface SimpleCallback<T> {

    /**
     * Simple callback
     *
     * @param t Object
     */
    void success(T t);

    /**
     * Called when something bad happens!
     *
     * @param e The error log
     */
    default void error(Exception e) {
        e.printStackTrace();
    }
}
