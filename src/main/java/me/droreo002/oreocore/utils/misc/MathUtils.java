package me.droreo002.oreocore.utils.misc;

import me.droreo002.oreocore.OreoCore;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class MathUtils {

    private static final List<Integer> DUPLICATED_RANDOM = new ArrayList<>();

    static {
        // Auto remove every 5 minute. Will start after 2 second
        Bukkit.getScheduler().scheduleSyncRepeatingTask(OreoCore.getInstance(), DUPLICATED_RANDOM::clear, 40L, 20L * 300L);
    }

    public static boolean chanceOf(double value) {
        return chanceOf(value, 100.0);
    }

    public static boolean chanceOf(double value, final double check) {
        return Math.random() * check <= value;
    }

    public static int random(int min, int max, boolean preventDuplicate) {
        if (preventDuplicate) {
            int random = ThreadLocalRandom.current().nextInt((max - min) + 1) + min;
            // Do 500 try!
            for (int i = 0; i < 500; i++) {
                if (DUPLICATED_RANDOM.contains(random)) {
                    random = ThreadLocalRandom.current().nextInt((max - min) + 1) + min; // Try another one!
                    continue;
                }
                return random; // Stop!
            }
            return random; // Prevent error, I'm sure it will not reach this line
        } else {
            return ThreadLocalRandom.current().nextInt((max - min) + 1) + min;
        }
    }

    public static <T> T randomOnList(List<T> list) {
        return list.get(random(0, list.size() - 1, false));
    }

    public static boolean isNumber(String text) {
        return NumberUtils.isNumber(text);
    }

    public static double getPercentage(double first, double second) {
        return (first / second) * 100;
    }
}
