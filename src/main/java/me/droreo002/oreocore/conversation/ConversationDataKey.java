package me.droreo002.oreocore.conversation;

import lombok.Getter;
import org.bukkit.conversations.ConversationContext;

public class ConversationDataKey<T> {

    @Getter
    private String dataKey;
    @Getter
    private Processor<T> processor;

    public ConversationDataKey(String dataKey) {
        this.dataKey = dataKey;
        this.processor = conversationContext -> (T) conversationContext.getSessionData(dataKey);
    }

    public ConversationDataKey(String dataKey, Processor<T> processor) {
        this.dataKey = dataKey;
        this.processor = processor;
    }

    public T get(ConversationContext conversationContext) {
        return this.processor.get(conversationContext);
    }

    public interface Processor<T> {
        T get(ConversationContext conversationContext);
    }
}
