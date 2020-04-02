package me.droreo002.oreocore.conversation;

import lombok.Getter;
import lombok.Setter;
import me.droreo002.oreocore.utils.misc.DoubleValueCallback;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Callable;

public abstract class OreoPrompt<T> extends StringPrompt {

    public static final String DATA_KEY = "DATA";

    @Getter @Setter
    private String nextPrompt;
    @Getter @Setter
    private String identifier;
    @Getter @Setter
    private String customDataKey;
    @Getter @Setter
    private State state;

    public OreoPrompt(String identifier, String customDataKey) {
        this.identifier = identifier;
        this.customDataKey = customDataKey;
        this.state = State.CONTINUE;
    }

    /**
     * Called when input is given
     *
     * @param conversationContext The context or temporary data
     * @param s Whole string
     * @return The input result null for skip
     */
    public abstract T onInput(ConversationContext conversationContext, String s);

    @Override
    public Prompt acceptInput(@NotNull ConversationContext conversationContext, String s) {
        T t = onInput(conversationContext, s);
        if (state != State.CONTINUE) {
            state = State.CONTINUE;
            return null; // Cancel the conversation and reset the state. Don't ask me why
        }
        conversationContext.getAllSessionData().put(DATA_KEY, t); // You can say DATA_KEY is a Universal data
        if (customDataKey != null) {
            conversationContext.getAllSessionData().put(customDataKey, t); // Also add to custom data key
        }

        OreoConversation<T> conversation = ((OreoConversation) conversationContext.getAllSessionData().get(OreoConversation.CONVERSATION_DATA));

        Prompt result = null;
        if (nextPrompt != null) {
            OreoPrompt<?> nextPrompt = conversation.nextOf(this);
            result = (t == null) ? null : nextPrompt;
        }

        /*
         * We can say that if the nextPrompt is null
         * then its the last one
         */
        if (result == null) {
            Object dataResult = t;
            if (conversation.getDataBuilder() != null) {
                dataResult = conversation.getDataBuilder().build(conversationContext);
            }
            conversation.getLastCallback().success((T) dataResult, conversationContext);
        }
        return result;
    }

    public enum State {
        FAIL,
        CONTINUE;
    }
}
