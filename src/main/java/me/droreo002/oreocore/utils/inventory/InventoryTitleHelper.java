package me.droreo002.oreocore.utils.inventory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import lombok.SneakyThrows;
import me.droreo002.oreocore.utils.multisupport.SimpleReflectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import static me.droreo002.oreocore.utils.multisupport.SimpleReflectionUtils.*;

/**
 * A inventory helper that allows you to change the title of the current
 * opened inventory.
 * @author https://gist.github.com/justisr/0f706a6ce097b86ef43bf0b97eadb426
 */
public final class InventoryTitleHelper {

    private static Method sendPacket;
    private static Field activeContainerField, windowIdField, playerConnectionField;
    private static Constructor<?> chatMessageConstructor, packetPlayOutOpenWindowConstructor;

    static {
        try {
            chatMessageConstructor = getNMSClass("ChatMessage").getConstructor(String.class, Object[].class);
            Class<?> nmsPlayer = getNMSClass("EntityPlayer");
            activeContainerField = nmsPlayer.getField("activeContainer");
            windowIdField = getNMSClass("Container").getField("windowId");
            playerConnectionField = nmsPlayer.getField("playerConnection");
            packetPlayOutOpenWindowConstructor = getNMSClass("PacketPlayOutOpenWindow").getConstructor(Integer.TYPE, String.class, getNMSClass("IChatBaseComponent"), Integer.TYPE);
            sendPacket = getNMSClass("PlayerConnection").getMethod("sendPacket", getNMSClass("Packet"));
        } catch (NoSuchMethodException | SecurityException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    @SneakyThrows
    public static void updateTitle(Player p, String title) {
        try {
            Object handle = getHandle(p);
            Object message = chatMessageConstructor.newInstance(title, new Object[0]);
            Object container = activeContainerField.get(handle);
            Object windowId = windowIdField.get(container);
            Object packet = packetPlayOutOpenWindowConstructor.newInstance(windowId, "minecraft:chest", message, p.getOpenInventory().getTopInventory().getSize());
            sendPacket(p, packet);
        } catch (IllegalArgumentException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
        p.updateInventory();
    }
}