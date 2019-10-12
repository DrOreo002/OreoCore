package me.droreo002.oreocore.conversation;

import me.droreo002.oreocore.utils.entity.PlayerUtils;
import me.droreo002.oreocore.utils.misc.SoundObject;
import me.droreo002.oreocore.utils.strings.StringUtils;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;

/**
 * OreoStringPrompt, it still the same thing as
 * bukkit one, but instead it has that send message feature
 * that will clear char before sending message
 *
 * preventing confusion
 */
public abstract class OStringPrompt extends StringPrompt {

    /**
     * Send a message
     *
     * @param message The message to send
     * @param target The target
     * @param clearChat Should we clear chat?
     * @param soundObject The sound to play, nullable
     */
    public void sendMessage(String message, Player target, boolean clearChat, SoundObject soundObject) {
        if (clearChat) PlayerUtils.clearChat(target);
        if (soundObject != null) soundObject.send(target);
        target.sendMessage(StringUtils.color(message));
    }
}
