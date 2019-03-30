package me.droreo002.oreocore.utils.misc;

import me.droreo002.oreocore.OreoCore;
import me.droreo002.oreocore.utils.logging.Debug;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class MathUtils {

    private static final List<Integer> DUPLICATED_RANDOM = new ArrayList<>();

    static {
        // Auto remove every 10 minute. Will start after 2 second
        Bukkit.getScheduler().scheduleSyncRepeatingTask(OreoCore.getInstance(), () -> {
            Debug.log("&eDUPLICATE_RANDOM &flist has been cleared!", true);
            if (DUPLICATED_RANDOM.size() < 5) {
                DUPLICATED_RANDOM.clear();
                return;
            }
            DUPLICATED_RANDOM.subList(0, DUPLICATED_RANDOM.size() - 5).clear();
        }, 40L, 20L * 600L);
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

    public static boolean isNumber(String text) {
        return NumberUtils.isNumber(text);
    }
}
