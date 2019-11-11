package me.droreo002.oreocore.utils.list;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Utilities for iterations
 *
 * @author Luck
 * {https://github.com/lucko/LuckPerms/blob/master/common/src/main/java/me/lucko/luckperms/common/util/Iterators.java}
 */
public final class Iterators {
    private Iterators() {}

    public static <I> void iterate(Iterable<I> iterable, Consumer<I> action) {
        for (I i : iterable) {
            try {
                action.accept(i);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static <I, O> void iterate(Iterable<I> iterable, Function<I, O> mapping, Consumer<O> action) {
        for (I i : iterable) {
            try {
                action.accept(mapping.apply(i));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static <I> void iterate(I[] array, Consumer<I> action) {
        for (I i : array) {
            try {
                action.accept(i);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static <I, O> void iterate(I[] array, Function<I, O> mapping, Consumer<O> action) {
        for (I i : array) {
            try {
                action.accept(mapping.apply(i));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static <T> List<List<T>> divideIterable(Iterable<T> source, int size) {
        List<List<T>> lists = new ArrayList<>();
        Iterator<T> it = source.iterator();
        while (it.hasNext()) {
            List<T> subList = new ArrayList<>();
            for (int i = 0; it.hasNext() && i < size; i++) {
                subList.add(it.next());
            }
            lists.add(subList);
        }
        return lists;
    }

}