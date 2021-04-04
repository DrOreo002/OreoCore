package me.droreo002.oreocore.conversation;

import lombok.Getter;
import me.droreo002.oreocore.title.OreoTitle;
import me.droreo002.oreocore.utils.list.ListUtils;
import me.droreo002.oreocore.utils.misc.DoubleValueCallback;
import me.droreo002.oreocore.utils.misc.SimpleCallback;
import me.droreo002.oreocore.utils.misc.SoundObject;
import me.droreo002.oreocore.title.TitleAnimation;
import me.droreo002.oreocore.title.TitleFrame;
import me.droreo002.oreocore.utils.time.TimestampBuilder;
import me.droreo002.oreocore.utils.time.TimestampUtils;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class OreoConversation<T> implements ConversationAbandonedListener {

    public static final String CONVERSATION_DATA = "CONVERSATION_DATA";
    private static final int DEFAULT_TIME_OUT = 1800; // 30 Minutes

    @Getter
    private final ConversationFactory conversationFactory;
    @Getter
    private List<OreoPrompt<?>> prompts;
    @Getter
    private DoubleValueCallback<T, ConversationContext> lastCallback;
    @Getter
    private SimpleCallback<Player> onConversationSent;
    @Getter
    private SimpleCallback<ConversationAbandonedEvent> onConversationAbandoned;
    @Getter
    private String abandonedMessage, timeoutFormat;
    @Getter
    private SoundObject abandonedSound;
    @Getter
    private TitleAnimation titleAnimation;
    @Getter
    private TimestampBuilder conversationTimeOut;
    @Getter
    private OreoTitle countdownTitle, abandonedTitle;
    @Getter
    private boolean enableTitle;
    @Getter
    private int timeOut;
    @Getter @Nullable
    private DataBuilder<T> dataBuilder;
    @Getter @Nullable
    private Map<String, Object> firstContext;

    public OreoConversation(String nonPlayerMessage, String escapeSequence, JavaPlugin owner) {
        this.prompts = new ArrayList<>();
        this.conversationFactory = new ConversationFactory(owner)
                .withModality(false)
                .withLocalEcho(false)
                .addConversationAbandonedListener(this)
                .withEscapeSequence(escapeSequence)
                .thatExcludesNonPlayersWithMessage(nonPlayerMessage);
        this.enableTitle = false;
    }

    /**
     * Make the data builder
     *
     * @param dataBuilder The data builder
     */
    public OreoConversation<T> withDataBuilder(DataBuilder<T> dataBuilder) {
        this.dataBuilder = dataBuilder;
        return this;
    }

    /**
     * Set the title countdown
     *
     * @param titleCountdown The title countdown
     */
    public OreoConversation<T> withCountdownTitle(OreoTitle titleCountdown, int timeOut, SimpleCallback<Player> whenDone) {
        this.enableTitle = true;
        this.timeOut = timeOut;
        this.countdownTitle = titleCountdown;
        this.titleAnimation = new TitleAnimation(this.countdownTitle,20L)
            .addFrame(new TitleFrame() {

                @Override
                public SoundObject getSound() {
                    return titleCountdown.getSoundOnSend();
                }

                @Override
                public String getNextSubTitle(String prevSubTitle) {
                    if (conversationTimeOut == null) return " ";
                    Date date = new Date();
                    return TimestampUtils.getDifference(date, new Date(conversationTimeOut.getTimestamp().getTime()), timeoutFormat);
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
    public OreoConversation<T> withTimeoutFormat(String timeOutFormat) {
        this.timeoutFormat = timeOutFormat;
        return this;
    }

    /**
     * Add a sent listener
     *
     * @param onConversationSent The callback
     */
    public OreoConversation<T> onConversationSent(SimpleCallback<Player> onConversationSent) {
        this.onConversationSent = onConversationSent;
        return this;
    }

    /**
     * Add a abandoned listener
     *
     * @param onConversationAbandoned The callback
     */
    public OreoConversation<T> onConversationAbandoned(SimpleCallback<ConversationAbandonedEvent> onConversationAbandoned) {
        this.onConversationAbandoned = onConversationAbandoned;
        return this;
    }

    /**
     * Set the abandoned title, will be sent if the conversation
     * is abandoned
     *
     * @param object The title object
     */
    public OreoConversation<T> withAbandonedTitle(OreoTitle object) {
        this.abandonedTitle = object;
        return this;
    }

    /**
     * Get the next prompt of target
     *
     * @param prompt Target
     * @return The next of target prompt is there's any. Null otherwise
     */
    public OreoPrompt<?> nextOf(OreoPrompt<?> prompt) {
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
    public OreoConversation<T> first(OreoPrompt<?> prompt) {
        this.prompts.add(prompt);
        return this;
    }

    /**
     * Add prompt after the last added
     *
     * @param prompt The prompt to add
     * @return The conversation
     */
    public OreoConversation<T> then(OreoPrompt<?> prompt) {
        if (prompts.isEmpty()) throw new IllegalStateException("Please add the first prompt first!");
        OreoPrompt<?> pr = this.prompts.get(this.prompts.size() - 1); // Get last one
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
    public OreoConversation<T> lastly(DoubleValueCallback<T, ConversationContext> lastCallback) {
        this.lastCallback = lastCallback;
        return this;
    }

    /**
     * Add first conversation context data
     *
     * @param key The data key
     * @param value The data value
     * @return OreoConversation
     */
    public OreoConversation<?> addFirstContext(String key, Object value) {
        if (this.firstContext == null) this.firstContext = new HashMap<>();
        this.firstContext.put(key, value);
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
        if (onConversationAbandoned != null) onConversationAbandoned.success(abandonedEvent);
        if (abandonedSound != null) abandonedSound.send(player);
        if (titleAnimation != null) titleAnimation.stop(true);
        if (abandonedTitle != null) abandonedTitle.send(player);
    }

    /**
     * Get prompt by name
     *
     * @param promptName The prompt name
     * @return The prompt
     */
    public OreoPrompt<?> getPrompt(String promptName) {
        return this.prompts.stream().filter(p -> p.getIdentifier().equals(promptName)).findAny().orElse(null);
    }

    /**
     * Send the conversation to the player with the specified data
     *
     * @param promptName The prompt name
     * @param player The player
     * @param timeOut The execution time out
     */
    public void send(String promptName, Player player, int timeOut) {
        OreoPrompt<?> prompt = getPrompt(promptName);
        if (prompt == null) throw new NullPointerException("Failed to get prompt with the identifier of " + promptName);

        Map<String, Object> sessionData = new HashMap<>();
        sessionData.put(CONVERSATION_DATA, this);
        if (firstContext != null) sessionData.putAll(firstContext);
        Conversation conversation = conversationFactory.withFirstPrompt(prompt).withTimeout((timeOut == 0) ? DEFAULT_TIME_OUT : timeOut).buildConversation(player);
        for (Map.Entry<String, Object> ent : sessionData.entrySet()) {
            String key = ent.getKey();
            conversation.getContext().setSessionData(key, ent.getValue());
        }
        if (enableTitle) {
            this.timeoutFormat = (timeoutFormat == null) ? TimestampBuilder.TICKING_TIME_FORMAT : timeoutFormat;
            this.conversationTimeOut = TimestampUtils.fromSeconds(TimestampBuilder.DEFAULT_FORMAT, timeOut);
            this.titleAnimation.start(player);
        }
        if (onConversationSent != null) onConversationSent.success(player);
        conversation.begin();
    }

    /**
     * Send (simplified)
     *
     * @param player The target player
     */
    public void send(Player player) {
        OreoPrompt<?> prompt = this.prompts.get(0); // First index
        this.send(prompt.getIdentifier(), player, timeOut);
    }

    /**
     * Send conversation
     *
     * @param player The target player
     * @param timeOut The timeout
     */
    public void send(Player player, int timeOut) {
        OreoPrompt<?> prompt = this.prompts.get(0); // First index
        this.send(prompt.getIdentifier(), player, timeOut);
    }

    public interface DataBuilder<T> {
        T build(ConversationContext context);
    }
}