package me.droreo002.oreocore.utils.misc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * A utility class to assist in checking for updates for plugins uploaded to
 * <a href="https://spigotmc.org/resources/">SpigotMC</a>. Before any members of this
 * class are accessed, {@link #init(JavaPlugin, int)} must be invoked by the plugin,
 * preferrably in its {@link JavaPlugin#onEnable()} method, though that is not a
 * requirement.
 * <p>
 * This class performs asynchronous queries to <a href="https://spiget.org">SpiGet</a>,
 * an REST server which is updated periodically. If the results of {@link #requestUpdateCheck()}
 * are inconsistent with what is published on SpigotMC, it may be due to SpiGet's cache.
 * Results will be updated in due time.
 *
 * @author Parker Hawke - 2008Choco
 */
public final class UpdateChecker {

    public static final VersionScheme VERSION_SCHEME_DECIMAL = (first, second) -> {
        String[] firstSplit = splitVersionInfo(first), secondSplit = splitVersionInfo(second);
        if (firstSplit == null || secondSplit == null) return null;

        for (int i = 0; i < Math.min(firstSplit.length, secondSplit.length); i++) {
            int currentValue = NumberUtils.toInt(firstSplit[i]), newestValue = NumberUtils.toInt(secondSplit[i]);

            if (newestValue > currentValue) {
                return second;
            } else if (newestValue < currentValue) {
                return first;
            }
        }

        return (secondSplit.length > firstSplit.length) ? second : first;
    };

    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2";
    private static final String UPDATE_URL = "https://api.spiget.org/v2/resources/%d/versions?size=1&sort=-releaseDate";
    private static final String UPDATE_URL_SPIGOT = "https://api.spigotmc.org/legacy/update.php?resource=%d";
    private static final Pattern DECIMAL_SCHEME_PATTERN = Pattern.compile("\\d+(?:\\.\\d+)*");

    private UpdateResult lastResult = null;

    @Getter
    private final JavaPlugin plugin;
    @Getter
    private final int pluginID;
    @Getter
    private final VersionScheme versionScheme;
    @Getter @Setter
    private boolean premium;

    private UpdateChecker(JavaPlugin plugin, int pluginID, VersionScheme versionScheme) {
        this.plugin = plugin;
        this.pluginID = pluginID;
        this.versionScheme = versionScheme;
        this.premium = false;
    }

    /**
     * Request an update check to SpiGet. This request is asynchronous and may not complete
     * immediately as an HTTP GET request is published to the SpiGet API.
     *
     * @return a future update result
     */
    public CompletableFuture<UpdateResult> requestUpdateCheck() {
        return CompletableFuture.supplyAsync(() -> {
            int responseCode = -1;
            try {
                String latest = null;
                String current = plugin.getDescription().getVersion();
                String newest = "";

                if (!premium) {
                    URL url = new URL(String.format(UPDATE_URL, pluginID));
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.addRequestProperty("User-Agent", USER_AGENT);

                    InputStreamReader reader = new InputStreamReader(connection.getInputStream());
                    responseCode = connection.getResponseCode();

                    JsonElement element = new JsonParser().parse(reader);
                    if (!element.isJsonArray()) {
                        return new UpdateResult(UpdateReason.INVALID_JSON);
                    }

                    reader.close();

                    JsonObject versionObject = element.getAsJsonArray().get(0).getAsJsonObject();
                    newest = versionObject.get("name").getAsString();

                } else {
                    URL url = new URL(String.format(UPDATE_URL_SPIGOT, pluginID));
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.addRequestProperty("User-Agent", USER_AGENT);

                    InputStreamReader reader = new InputStreamReader(connection.getInputStream());
                    responseCode = connection.getResponseCode();
                    BufferedReader in = new BufferedReader(reader);
                    newest = in.readLine(); // The response is only 1 line
                    in.close();
                }

                latest = versionScheme.compareVersions(current, newest);

                if (latest == null) {
                    return new UpdateResult(UpdateReason.UNSUPPORTED_VERSION_SCHEME);
                } else if (latest.equals(current)) {
                    return new UpdateResult(current.equals(newest) ? UpdateReason.UP_TO_DATE : UpdateReason.UNRELEASED_VERSION);
                } else if (latest.equals(newest)) {
                    return new UpdateResult(UpdateReason.NEW_UPDATE, latest);
                }
            } catch (IOException e) {
                return new UpdateResult(UpdateReason.COULD_NOT_CONNECT);
            } catch (JsonSyntaxException e) {
                return new UpdateResult(UpdateReason.INVALID_JSON);
            }

            return new UpdateResult(responseCode == 401 ? UpdateReason.UNAUTHORIZED_QUERY : UpdateReason.UNKNOWN_ERROR);
        });
    }

    /**
     * Get the last update result that was queried by {@link #requestUpdateCheck()}. If no update
     * check was performed since this class' initialization, this method will return null.
     *
     * @return the last update check result. null if none.
     */
    public UpdateResult getLastResult() {
        return lastResult;
    }

    private static String[] splitVersionInfo(String version) {
        Matcher matcher = DECIMAL_SCHEME_PATTERN.matcher(version);
        if (!matcher.find()) return null;

        return matcher.group().split("\\.");
    }

    /**
     * Initialize this update checker with the specified values and return its instance
     *
     * @param plugin the plugin for which to check updates. Cannot be null
     * @param pluginID the ID of the plugin as identified in the SpigotMC resource link. For example,
     * "https://www.spigotmc.org/resources/veinminer.<b>12038</b>/" would expect "12038" as a value. The
     * value must be greater than 0
     * @param versionScheme a custom version scheme parser. Cannot be null
     *
     * @return the UpdateChecker instance
     */
    public static UpdateChecker init(JavaPlugin plugin, int pluginID, VersionScheme versionScheme) {
        Preconditions.checkArgument(plugin != null, "Plugin cannot be null");
        Preconditions.checkArgument(pluginID > 0, "Plugin ID must be greater than 0");
        Preconditions.checkArgument(versionScheme != null, "null version schemes are unsupported");

        return new UpdateChecker(plugin, pluginID, versionScheme);
    }

    /**
     * Initialize this update checker with the specified values and return its instance
     *
     * @param plugin the plugin for which to check updates. Cannot be null
     * @param pluginID the ID of the plugin as identified in the SpigotMC resource link. For example,
     * "https://www.spigotmc.org/resources/veinminer.<b>12038</b>/" would expect "12038" as a value. The
     * value must be greater than 0
     *
     * @return the UpdateChecker instance
     */
    public static UpdateChecker init(JavaPlugin plugin, int pluginID) {
        return init(plugin, pluginID, VERSION_SCHEME_DECIMAL);
    }

    /**
     * A functional interface to compare two version Strings with similar version schemes.
     */
    @FunctionalInterface
    public interface VersionScheme {

        /**
         * Compare two versions and return the higher of the two. If null is returned, it is assumed
         * that at least one of the two versions are unsupported by this version scheme parser.
         *
         * @param first the first version to check
         * @param second the second version to check
         *
         * @return the greater of the two versions. null if unsupported version schemes
         */
        String compareVersions(String first, String second);
    }

    /**
     * A constant reason for the result of {@link UpdateResult}.
     */
    public enum UpdateReason {

        /**
         * A new update is available for download on SpigotMC.
         */
        NEW_UPDATE, // The only reason that requires an update

        /**
         * A successful connection to the SpiGet API could not be established.
         */
        COULD_NOT_CONNECT,

        /**
         * The JSON retrieved from SpiGet was invalid or malformed.
         */
        INVALID_JSON,

        /**
         * A 401 error was returned by the SpiGet API.
         */
        UNAUTHORIZED_QUERY,

        /**
         * The version of the plugin installed on the server is greater than the one uploaded
         * to SpigotMC's resources section.
         */
        UNRELEASED_VERSION,

        /**
         * An unknown error occurred.
         */
        UNKNOWN_ERROR,

        /**
         * The plugin uses an unsupported version scheme, therefore a proper comparison between
         * versions could not be made.
         */
        UNSUPPORTED_VERSION_SCHEME,

        /**
         * The plugin is up to date with the version released on SpigotMC's resources section.
         */
        UP_TO_DATE;

    }

    /**
     * Represents a result for an update query performed by {@link UpdateChecker#requestUpdateCheck()}.
     */
    public final class UpdateResult {

        private final UpdateReason reason;
        private final String newestVersion;

        { // An actual use for initializer blocks. This is madness!
            UpdateChecker.this.lastResult = this;
        }

        private UpdateResult(UpdateReason reason, String newestVersion) {
            this.reason = reason;
            this.newestVersion = newestVersion;
        }

        private UpdateResult(UpdateReason reason) {
            Preconditions.checkArgument(reason != UpdateReason.NEW_UPDATE, "Reasons that require updates must also provide the latest version String");
            this.reason = reason;
            this.newestVersion = plugin.getDescription().getVersion();
        }

        /**
         * Get the constant reason of this result.
         *
         * @return the reason
         */
        public UpdateReason getReason() {
            return reason;
        }

        /**
         * Check whether or not this result requires the user to update.
         *
         * @return true if requires update, false otherwise
         */
        public boolean requiresUpdate() {
            return reason == UpdateReason.NEW_UPDATE;
        }

        /**
         * Get the latest version of the plugin. This may be the currently installed version, it
         * may not be. This depends entirely on the result of the update.
         *
         * @return the newest version of the plugin
         */
        public String getNewestVersion() {
            return newestVersion;
        }

    }
}