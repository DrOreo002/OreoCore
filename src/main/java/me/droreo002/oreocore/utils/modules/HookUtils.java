package me.droreo002.oreocore.utils.modules;

import lombok.Getter;
import me.droreo002.oreocore.OreoCore;
import me.droreo002.oreocore.hook.HookManager;

public final class HookUtils {

    private static HookUtils instance;

    public static HookUtils getInstance() {
        if (instance == null) {
            instance = new HookUtils();
            return instance;
        }
        return instance;
    }

    @Getter
    private EssentialsUtils essentialsUtils;

    private HookUtils() {
        this.essentialsUtils = new EssentialsUtils();

        /*
         * Checks
         */
        if (!HookManager.registerHook(OreoCore.getInstance(), essentialsUtils)) {
            essentialsUtils = null;
        }
    }
}
