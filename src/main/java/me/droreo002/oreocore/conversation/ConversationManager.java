package me.droreo002.oreocore.conversation;

import lombok.Getter;
import lombok.Setter;
import me.droreo002.oreocore.OreoCore;
import me.droreo002.oreocore.utils.entity.PlayerUtils;
import me.droreo002.oreocore.utils.list.ListUtils;
import me.droreo002.oreocore.utils.misc.SoundObject;
import org.bukkit.Bukkit;
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
    private final Map<String, Prompt> conversationMap;
    @Getter @Setter
    private String abandonedMessage;
    @Getter @Setter
    private SoundObject abandonedSound;

    public ConversationManager(JavaPlugin owner, String nonPlayerMessage) {
        this.owner = owner;
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
        if (abandonedMessage != null) {
            if (ListUtils.isSerializedList(abandonedMessage)) {
                List<String> converted = ListUtils.toList(abandonedMessage);
                converted.forEach(player::sendMessage);
            } else {
                player.sendMessage(abandonedMessage);
            }
        }
        if (abandonedSound != null) abandonedSound.send(player);
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

    /**
     * Add a conversation
     *
     * @param name The conversation name
     * @param conversationPrompt The conversation prompt
     */
    public void addConversation(String name, Prompt conversationPrompt) {
        if (conversationMap.containsKey(name)) return;
        conversationMap.put(name, conversationPrompt);
    }
}