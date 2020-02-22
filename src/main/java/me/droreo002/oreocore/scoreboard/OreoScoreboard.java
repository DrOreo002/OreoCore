package me.droreo002.oreocore.scoreboard;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import me.droreo002.oreocore.utils.bridge.ServerUtils;
import me.droreo002.oreocore.utils.entity.PlayerUtils;
import me.droreo002.oreocore.utils.strings.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * OreoScoreboard, renamed from a gist.
 * @author https://gist.github.com/mkotb/d99eccdcc78a43ffb707
 */
public class OreoScoreboard {

    private static final Map<ChatColor, OfflinePlayer> CACHE = new HashMap<>();

    private Scoreboard scoreboard;
    private String title;
    private Map<ChatColor, String> scores;
    private Objective objective;
    private List<Team> teams;
    private List<ChatColor> removed;

    public OreoScoreboard(String title) {
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.title = StringUtils.color(title);
        this.scores = new ConcurrentHashMap<>();
        this.teams = Collections.synchronizedList(Lists.newArrayList());
        this.removed = new CopyOnWriteArrayList<>();
    }

    /**
     * Add a text
     *
     * @param text The text to add
     * @param score The text'score
     */
    public void add(String text, int score) {
        text = StringUtils.color(text);
        remove(score);
        scores.put(fromInteger(score), text);
    }

    /**
     * Remove a text on that score
     *
     * @param score The score to remove
     */
    public void remove(int score) {
        ChatColor prefixColor = fromInteger(score);
        removed.add(prefixColor);
        scores.remove(prefixColor);
    }

    /**
     * Get the text on specified score
     *
     * @param prefixColor The text's prefix color
     * @return The text
     */
    @Nullable
    public String getString(ChatColor prefixColor) {
        for (Map.Entry<ChatColor, String> entry : scores.entrySet()) {
            if (entry.getKey().equals(prefixColor)) {
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * Get chat color from integer, will throw an error
     * if integer is out of bound
     *
     * @param i The integer
     * @return ChatColor
     */
    @NotNull
    public ChatColor fromInteger(int i) {
        try {
            return ChatColor.values()[i];
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Scoreboard score must be a value between 0 to 21!");
            throw e;
        }
    }

    /**
     * Create a scoreboard team
     *
     * @param text The team text
     * @param color The team's color
     * @return Map of generated team
     */
    private Map.Entry<Team, OfflinePlayer> createTeam(String text, ChatColor color) {
        if (!CACHE.containsKey(color)) CACHE.put(color, PlayerUtils.getOfflinePlayerSkipLookup(color.toString()));
        OfflinePlayer offlinePlayer = CACHE.get(color);
        Team team;

        try {
            team = scoreboard.registerNewTeam("text-" + (teams.size() + 1));
        } catch (IllegalArgumentException e) {
            team = scoreboard.getTeam("text-" + (teams.size()));
        }

        if (team == null) throw new NullPointerException("Failed to create a new scoreboard team!");

        applyText(team, text, offlinePlayer);

        teams.add(team);
        return new AbstractMap.SimpleEntry<>(team, offlinePlayer);
    }

    /**
     * Apply text on the scoreboard
     * this also contains some deprecated method usage
     * do not change it or you will break it
     *
     * @param team The text team
     * @param text The text
     * @param fakePlayer The offline player a.k.a fake player
     */
    @SuppressWarnings("deprecation")
    private void applyText(Team team, String text, OfflinePlayer fakePlayer) {
        text = StringUtils.color(text);
        Iterator<String> iterator = Splitter.fixedLength(16).split(text).iterator();
        String prefix = iterator.next(); // First from 16 character

        team.setPrefix(prefix);

        if (!team.hasPlayer(fakePlayer))
            team.addPlayer(fakePlayer);

        if (ServerUtils.isLegacyVersion()) {
            if (text.length() > 16) {
                String prefixColor = ChatColor.getLastColors(prefix);
                String suffix = iterator.next();

                if (prefix.endsWith(String.valueOf(ChatColor.COLOR_CHAR))) {
                    prefix = prefix.substring(0, prefix.length() - 1);
                    team.setPrefix(prefix);
                    prefixColor = ChatColor.getByChar(suffix.charAt(0)).toString();
                    suffix = suffix.substring(1);
                }

                if (suffix.length() > 16) {
                    suffix = suffix.substring(0, (13 - prefixColor.length())); // cut off suffix, done if text is over 30 characters
                }

                team.setSuffix((prefixColor.equals("") ? ChatColor.RESET : prefixColor) + suffix);
            }
        } else {
            // In 1.13+ we do not want to do those logic. We simply just set it
            if (text.length() > 64) throw new IllegalStateException("Non legacy version only accept below 64 character!");
            team.setPrefix(text);
        }
    }

    /**
     * Update this scoreboard, contains deprecated method
     * do not remove or things will broke
     */
    @SuppressWarnings("deprecation")
    public void update() {
        if (objective == null) {
            objective = scoreboard.registerNewObjective((title.length() > 16 ? title.substring(0, 15) : title), "dummy", "dummy");
            objective.setDisplayName(title);
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        }

        removed.forEach((remove) -> {
            for (String s : scoreboard.getEntries()) {
                Score score = objective.getScore(s);

                if (score.getScore() != remove.ordinal())
                    continue;

                scoreboard.resetScores(s);
            }
        });

        removed.clear();

        int index = scores.size();

        for (Map.Entry<ChatColor, String> scoreEntry : scores.entrySet()) {
            ChatColor color = scoreEntry.getKey();
            String colorString = color.toString();

            Team t = scoreboard.getTeam(colorString);
            Map.Entry<Team, OfflinePlayer> team;

            if (t != null) {
                if (!CACHE.containsKey(color)) {
                    CACHE.put(color, PlayerUtils.getOfflinePlayerSkipLookup(colorString));
                }

                team = new AbstractMap.SimpleEntry<>(t, CACHE.get(color));
                applyText(team.getKey(), scoreEntry.getValue(), team.getValue());
                index -= 1;

                continue;
            } else {
                team = createTeam(scoreEntry.getValue(), scoreEntry.getKey());
            }

            int score = scoreEntry.getValue() != null ? scoreEntry.getKey().ordinal() : index;

            objective.getScore(team.getValue()).setScore(score);
            index -= 1;
        }
    }

    /**
     * Set this scoreboard's title
     *
     * @param title The scoreboard title
     */
    public void setTitle(String title) {
        this.title = StringUtils.color(title);
        if (objective != null) objective.setDisplayName(this.title);
    }

    /**
     * Reset this scoreboard
     */
    public void reset() {
        teams.forEach(Team::unregister);
        teams.clear();
        scores.clear();
    }

    /**
     * Send this scoreboard to target player
     *
     * @param players The target players
     */
    public void send(Player... players) {
        update(); // Update the scoreboard
        for (Player p : players) {
            p.setScoreboard(scoreboard);
        }
    }

    /**
     * Generate a totally random chat color
     * used for debug purpose
     *
     * @return A randomized chat color
     */
    public static ChatColor randomChatColor() {
        return ChatColor.values()[ThreadLocalRandom.current().nextInt(0, 21)];
    }
}