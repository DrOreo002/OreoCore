package me.droreo002.oreocore.utils.misc;
import lombok.Getter;
import me.droreo002.oreocore.utils.bridge.ServerUtils;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Callable;

import static me.droreo002.oreocore.utils.strings.StringUtils.color;

/**
 * A license manager. Not used by OreoCore
 * to use just copy and paste this into your project
 */
public final class LicenseManager {

    private static final String USER_INFORMATION = "%%__USER__%%";

    @Getter
    @Nullable
    private String buyerName, informationString;
    @Getter
    private final String crackedMessage, failedToFetchMessage, registeredMessage;
    @Getter
    private boolean errorWhenCracked;
    @Getter
    private JavaPlugin owner;
    @Getter
    @Nullable
    private Callable<Void> onCrackedFound;

    public LicenseManager(JavaPlugin owner, boolean errorWhenCracked, String crackedMessage, String failedToFetchMessage, String registeredMessage, @Nullable Callable<Void> onCrackedFound) {
        this.errorWhenCracked = errorWhenCracked;
        this.crackedMessage = crackedMessage;
        this.failedToFetchMessage = failedToFetchMessage;
        this.registeredMessage = registeredMessage;
        this.owner = owner;
        this.onCrackedFound = onCrackedFound;
        init();
    }

    /**
     * Initialize the license information
     */
    private void init() {
        if (USER_INFORMATION.startsWith("%%")) {
            this.informationString = color(crackedMessage);
            if (this.errorWhenCracked) {
                ServerUtils.disablePlugin(owner);
                try {
                    if (this.onCrackedFound != null) this.onCrackedFound.call();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
        }
        try {
            final URLConnection openConnection = new URL("https://www.spigotmc.org/members/" + USER_INFORMATION + "/").openConnection();
            openConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.106 Safari/537.36");
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(openConnection.getInputStream()));
            final StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            this.buyerName = sb.toString().split("<title>")[1].split("</title>")[0].split(" \\| ")[0];
            this.informationString = color(registeredMessage.replace("%buyer%", buyerName));
        } catch (IOException ex) {
            this.informationString = color(failedToFetchMessage);
        }
    }
}
