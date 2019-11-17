package me.droreo002.oreocore.conversation;

import lombok.Getter;
import lombok.Setter;
import me.droreo002.oreocore.utils.list.ListUtils;
import me.droreo002.oreocore.utils.misc.SimpleCallback;
import me.droreo002.oreocore.utils.misc.SoundObject;
import me.droreo002.oreocore.utils.misc.TitleAnimation;
import me.droreo002.oreocore.utils.misc.TitleFrame;
import me.droreo002.oreocore.utils.misc.TitleObject;
import me.droreo002.oreocore.utils.time.SimplifiedTime;
import me.droreo002.oreocore.utils.time.TimestampBuilder;
import me.droreo002.oreocore.utils.time.TimestampUtils;
import org.bukkit.ChatColor;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OreoConversation<T> implements ConversationAbandonedListener {

    public static final String CONVERSATION_DATA = "CONVERSATION_DATA";
    private static final int DEFAULT_TIME_OUT = 1800; // 30 Minutes

    @Getter
    private ConversationFactory conversationFactory;
    @Getter
    private List<OreoPrompt<T>> prompts;
    @Getter
    private SimpleCallback<T> lastCallback;
    @Getter
    private String abandonedMessage;
    @Getter
    private SoundObject abandonedSound;
    @Getter
    private TitleAnimation titleAnimation;
    @Getter
    private TimestampBuilder conversationTimeOut;
    @Getter
    private TitleObject titleCountdown;
    @Getter
    private boolean useTitle;
    @Getter
    private int timeOut;
    @Getter
    private String timeOutFormat;

    public OreoConversation(String nonPlayerMessage, String escapeSequence, JavaPlugin owner) {
        this.prompts = new ArrayList<>();
        this.conversationFactory = new ConversationFactory(owner)
                .withModality(false)
                .withLocalEcho(false)
                .addConversationAbandonedListener(this)
                .withEscapeSequence(escapeSequence)
                .thatExcludesNonPlayersWithMessage(nonPlayerMessage);
        this.useTitle = false;
    }

    /**
     * Set the title countdown
     *
     * @param titleCountdown The title countdown
     */
    public OreoConversation<T> withTitleCountdown(TitleObject titleCountdown, int timeOut, SimpleCallback<Player> whenDone) {
        this.useTitle = true;
        this.timeOut = timeOut;
        this.titleCountdown = titleCountdown;
        this.titleAnimation = new TitleAnimation(this.titleCountdown,20L)
            .addFrame(new TitleFrame() {

                @Override
                public SoundObject getSound() {
                    return titleCountdown.getSoundOnSend();
                }

                @Override
                public String getNextSubTitle(String prevSubTitle) {
                    if (conversationTimeOut == null) return " ";
                    Date date = new Date();
                    return TimestampUtils.getDifference(date, new Date(conversationTimeOut.getTimestamp().getTime()), timeOutFormat);
                }
            })
            .setOnDone(whenDone);
        return this;
    }

    /**
     * Set the TimeOut format
     *
     * @param timeOutFormat The format
     */
    public OreoConversation<T> withTimeOutFormat(String timeOutFormat) {
        this.timeOutFormat = timeOutFormat;
        return this;
    }

    /**
     * Get the next prompt of target
     *
     * @param prompt Target
     * @return The next of target prompt is there's any. Null otherwise
     */
    public OreoPrompt<T> nextOf(OreoPrompt<T> prompt) {
        return this.prompts.stream().filter(p -> p.getIdentifier().equals(prompt.getNextPrompt())).findAny().orElse(null);
    }

    /**
     * With an abandoned message
     *
     * @param message The message
     * @return Object
     */
    public OreoConversation<T> withAbandonedMessage(String message) {
        this.abandonedMessage = message;
        return this;
    }

    /**
     * With an abandoned sound
     *
     * @param abandonedSound The sound
     * @return Object
     */
    public OreoConversation<T> withAbandonedSound(SoundObject abandonedSound) {
        this.abandonedSound = abandonedSound;
        return this;
    }

    /**
     * Add the first prompt
     *
     * @param prompt The prompt to add
     * @return the Conversation
     */
    public OreoConversation<T> first(OreoPrompt<T> prompt) {
        this.prompts.add(prompt);
        return this;
    }

    /**
     * Add prompt after the last added
     *
     * @param prompt The prompt to add
     * @return The conversation
     */
    public OreoConversation<T> then(OreoPrompt<T> prompt) {
        if (prompts.isEmpty()) throw new IllegalStateException("Please add the first prompt first!");
        OreoPrompt<T> pr = this.prompts.get(this.prompts.size() - 1); // Get last one
        pr.setNextPrompt(prompt.getIdentifier());
        prompts.add(prompt);
        return this;
    }

    /**
     * Add the last data management
     *
     * @param lastCallback The last callback
     * @return The data
     */
    public OreoConversation<T> lastly(SimpleCallback<T> lastCallback) {
        this.lastCallback = lastCallback;
        return this;
    }

    @Override
    public void conversationAbandoned(ConversationAbandonedEvent abandonedEvent) {
        Player player = (Player) abandonedEvent.getContext().getForWhom();
        if (abandonedMessage != null) {
            if (ListUtils.isSerializedList(abandonedMessage)) {
                List<String> converted = ListUtils.toList(abandonedMessage);
                converted.forEach(player::sendMessage);
            } else {
                player.sendMessage(abandonedMessage);
            }
        }
        if (abandonedSound != null) abandonedSound.send(player);
        if (titleAnimation != null) titleAnimation.stop(true);
    }

    /**
     * Get prompt by name
     *
     * @param promptName The prompt name
     * @return The prompt
     */
    public OreoPrompt<T> getPrompt(String promptName) {
        return this.prompts.stream().filter(p -> p.getIdentifier().equals(promptName)).findAny().orElse(null);
    }

    /**
     * Send the conversation to the player with the specified data
     *
     * @param promptName The prompt name
     * @param player The player
     * @param sessionData The session data in HashMap, where String is data key and object is value of it
     * @param timeOut The execution time out
     */
    public void send(String promptName, Player player, Map<String, Object> sessionData, int timeOut) {
        OreoPrompt prompt = getPrompt(promptName);
        if (prompt == null) throw new NullPointerException("Failed to get prompt with the identifier of " + promptName);

        sessionData.put(CONVERSATION_DATA, this);
        Conversation conversation = conversationFactory.withFirstPrompt(prompt).withTimeout((timeOut == 0) ? DEFAULT_TIME_OUT : timeOut).buildConversation(player);
        for (Map.Entry ent : sessionData.entrySet()) {
            String key = (String) ent.getKey();
            conversation.getContext().setSessionData(key, ent.getValue());
        }
        if (useTitle) {
            this.timeOutFormat = (timeOutFormat == null) ? TimestampBuilder.TICKING_TIME_FORMAT : timeOutFormat;
            this.conversationTimeOut = TimestampUtils.fromSeconds(TimestampBuilder.DEFAULT_FORMAT, timeOut);
            this.titleAnimation.start(player);
        }
        conversation.begin();
    }

    /**
     * Send (simplified)
     *
     * @param player The target player
     */
    public void send(Player player) {
        OreoPrompt prompt = this.prompts.get(0); // First index
        this.send(prompt.getIdentifier(), player, new HashMap<>(), timeOut);
    }

    /**
     * Send conversation
     *
     * @param player The target player
     * @param timeOut The timeout
     */
    public void send(Player player, int timeOut) {
        OreoPrompt prompt = this.prompts.get(0); // First index
        this.send(prompt.getIdentifier(), player, new HashMap<>(), timeOut);
    }
}