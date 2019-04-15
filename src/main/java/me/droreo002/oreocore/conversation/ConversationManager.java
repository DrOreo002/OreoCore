package me.droreo002.oreocore.conversation;

import lombok.Getter;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ConversationManager implements ConversationAbandonedListener {

    @Getter
    private final ConversationFactory conversationFactory;
    @Getter
    private final List<Conversation> conversations;
    @Getter
    private final JavaPlugin owner;
    @Getter
    private final String conversationAbandonedMessage;
    @Getter
    private final Map<String, Prompt> conversationMap;

    public ConversationManager(JavaPlugin owner, String nonPlayerMessage, String conversationAbandonedMessage) {
        this.owner = owner;
        this.conversationAbandonedMessage = conversationAbandonedMessage;
        this.conversations = new ArrayList<>();
        this.conversationMap = new HashMap<>();
        this.conversationFactory = new ConversationFactory(owner)
                .withModality(false)
                .withLocalEcho(false)
                .addConversationAbandonedListener(this)
                .withEscapeSequence("exit")
                .thatExcludesNonPlayersWithMessage(nonPlayerMessage);
    }

    @Override
    public void conversationAbandoned(ConversationAbandonedEvent abandonedEvent) {
        Player player = (Player) abandonedEvent.getContext().getForWhom();
        remove(player);
        player.sendMessage(conversationAbandonedMessage);
    }

    /**
     * Send the conversation to the player with the specified data
     *
     * @param player : The player
     * @param promptName : The prompt name, register first
     * @param sessionData : The session data in HashMap, where String is data key and object is value of it
     */
    public void sendConversation(Player player, String promptName, Map<String, Object> sessionData, int timeOut) {
        Conversation conversation = conversationFactory.withFirstPrompt(conversationMap.get(promptName)).withTimeout((timeOut != 0) ? timeOut : Integer.MAX_VALUE).buildConversation(player);
        if (!isOnConversation(player)) conversations.add(conversation);
        for (Map.Entry ent : sessionData.entrySet()) {
            String key = (String) ent.getKey();
            conversation.getContext().setSessionData(key, ent.getValue());
        }
        if (!isOnConversation(player)) conversations.add(conversation);
        conversation.begin();
    }

    /**
     * Check if player is cached on the conversations list
     *
     * @param player : The player to check
     * @return true if available, false otherwise
     */
    public boolean isOnConversation(Player player) {
        for (Conversation s : conversations) {
            if (s.getContext().getForWhom().equals(player)) return true;
        }
        return false;
    }

    /**
     * Remove the player from the conversations list cache
     *
     * @param player : The player
     */
    public void remove(Player player) {
        if (!isOnConversation(player)) return;
        List<Conversation> conver = new ArrayList<>();
        for (Conversation con : conversations) {
            if (con.getContext().getForWhom().equals(player)) continue;
            conver.add(con);
        }
        conversations.clear();
        conversations.addAll(conver);
    }
}