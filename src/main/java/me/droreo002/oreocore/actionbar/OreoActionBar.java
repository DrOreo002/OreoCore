package me.droreo002.oreocore.actionbar;

import lombok.Getter;
import lombok.Setter;
import me.droreo002.oreocore.OreoCore;
import me.droreo002.oreocore.utils.bridge.ServerUtils;
import me.droreo002.oreocore.utils.strings.StringUtils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static me.droreo002.oreocore.utils.multisupport.SimpleReflectionUtils.*;

/**
 * Action bar utilities, support from 1.8 to 1.15
 * @author https://github.com/ConnorLinfoot/ActionBarAPI/blob/master/src/main/java/com/connorlinfoot/actionbarapi/ActionBarAPI.java
 */
public class OreoActionBar {

    // Should we use old method?
    private static boolean useOldMethods;

    static {
        String ver = ServerUtils.getServerVersion().name();
        useOldMethods = (ver.equalsIgnoreCase("V1_8_R1") || ver.contains("V1_7"));
    }

    @Getter
    private int senderTaskId;
    @Getter
    private List<Player> targetPlayers;
    @Getter @Setter
    private String message;
    @Getter @Setter
    private long updateTime;

    public OreoActionBar(String message) {
        this.message = StringUtils.color(message);
        this.targetPlayers = new ArrayList<>();
    }

    /**
     * Add player to this action bar
     *
     * @param player The player to add
     */
    public void addPlayer(Player player) {
        if (targetPlayers.stream().anyMatch(p -> p.getUniqueId().equals(player.getUniqueId()))) return;
        this.targetPlayers.add(player);
    }

    /**
     * Stop this action bar from sending
     *
     * @param immediate Should we stop it immediately?
     */
    public void stop(boolean immediate) {
        if (senderTaskId == 0) return;
        Bukkit.getScheduler().cancelTask(senderTaskId);
        if (immediate) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(OreoCore.getInstance(), () -> {
                message = "";
                send();
            }, 1L); // Add some delay to ensure precision
        }
    }

    /**
     * Send the action bar to the target player
     */
    public void send() {
        if (this.senderTaskId != 0) return;
        this.senderTaskId = Bukkit.getScheduler().runTaskTimer(OreoCore.getInstance(), () -> {
            // If every player is offline
            if (this.targetPlayers.stream().noneMatch(Player::isOnline)) {
                stop(true);
                return;
            }
            for (Player targetPlayer : targetPlayers) {
                if (ServerUtils.isLegacyVersion()) {
                    try {
                        Object packet;
                        Class<?> packetPlayOutChatClass = getNMSClass("PacketPlayOutChat");
                        if (useOldMethods) {
                            Class<?> chatSerializerClass = getNMSClass("ChatSerializer");
                            Class<?> iChatBaseComponentClass = getNMSClass("IChatBaseComponent");
                            Method m3 = chatSerializerClass.getDeclaredMethod("a", String.class);
                            Object cbc = iChatBaseComponentClass.cast(m3.invoke(chatSerializerClass, "{\"text\": \"" + message + "\"}"));
                            packet = packetPlayOutChatClass.getConstructor(new Class<?>[]{iChatBaseComponentClass, byte.class}).newInstance(cbc, (byte) 2);
                        } else {
                            Class<?> chatComponentTextClass = getNMSClass("ChatComponentText");
                            Class<?> iChatBaseComponentClass = getNMSClass("IChatBaseComponent");
                            Class<?> chatMessageTypeClass = getNMSClass("ChatMessageType");
                            Object[] chatMessageTypes = chatMessageTypeClass.getEnumConstants();
                            Object chatMessageType = null;
                            for (Object obj : chatMessageTypes) {
                                if (obj.toString().equals("GAME_INFO")) {
                                    chatMessageType = obj;
                                }
                            }
                            Object chatComponentText = chatComponentTextClass.getConstructor(new Class<?>[]{String.class}).newInstance(message);
                            packet = packetPlayOutChatClass.getConstructor(new Class<?>[]{iChatBaseComponentClass, chatMessageTypeClass}).newInstance(chatComponentText, chatMessageType);
                        }
                        sendPacket(targetPlayer, packet);
                    } catch (Exception e) {
                        // Fails we use spigot's method
                        targetPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
                    }
                } else {
                    targetPlayer.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
                }
            }
        }, 0L, (updateTime == 0) ? 5L : updateTime).getTaskId();
    }
}
