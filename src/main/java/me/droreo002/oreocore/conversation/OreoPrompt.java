package me.droreo002.oreocore.conversation;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;

public abstract class OreoPrompt<T> extends StringPrompt {

    public static final String DATA_KEY = "DATA";

    @Getter @Setter
    private String nextPrompt;
    @Getter @Setter
    private String identifier;


    public OreoPrompt(String identifier) {
        this.identifier = identifier;
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
    public Prompt acceptInput(ConversationContext conversationContext, String s) {
        T t = onInput(conversationContext, s);
        conversationContext.getAllSessionData().put(DATA_KEY, t);

        OreoConversation<T> conversation = ((OreoConversation) conversationContext.getAllSessionData().get(OreoConversation.CONVERSATION_DATA));

        Prompt result = null;
        if (nextPrompt != null) {
            OreoPrompt nextPrompt = conversation.nextOf(this);
            result = (t == null) ? null : nextPrompt;
        }

        /*
         * We can say that if the nextPrompt is null
         * then its the last one
         */
        if (result == null) conversation.getLastCallback().success(t);
        return result;
    }
}
