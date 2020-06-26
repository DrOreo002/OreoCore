package me.droreo002.oreocore.utils.inventory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import lombok.SneakyThrows;
import me.droreo002.oreocore.utils.bridge.ServerUtils;
import net.minecraft.server.v1_16_R1.PacketPlayOutOpenWindow;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import static me.droreo002.oreocore.utils.multisupport.SimpleReflectionUtils.*;

/**
 * A inventory helper that allows you to change the title of the current
 * opened inventory.
 * @author https://gist.github.com/justisr/0f706a6ce097b86ef43bf0b97eadb426
 */
public final class InventoryTitleHelper {

    private static Field activeContainerField, windowIdField;
    private static Constructor<?> chatMessageConstructor, packetPlayOutOpenWindowConstructor;
    private static boolean newVer = false;

    static {
        try {
            chatMessageConstructor = getNMSClass("ChatMessage").getConstructor(String.class, Object[].class);
            Class<?> nmsPlayer = getNMSClass("EntityPlayer");
            activeContainerField = nmsPlayer.getField("activeContainer");
            windowIdField = getNMSClass("Container").getField("windowId");
            switch (ServerUtils.getServerVersion()) {
                case V1_8_R1:
                case V1_8_R2:
                case V1_8_R3:
                case V1_9_R1:
                case V1_9_R2:
                case V1_10_R1:
                case V1_11_R1:
                case V1_12_R1:
                case V1_13_R1:
                case V1_13_R2:
                    packetPlayOutOpenWindowConstructor = getNMSClass("PacketPlayOutOpenWindow").getConstructor(Integer.TYPE, String.class, getNMSClass("IChatBaseComponent"), Integer.TYPE);
                    break;
                case V1_14_R1:
                case V1_15_R1:
                case V1_16_R1:
                    packetPlayOutOpenWindowConstructor = getNMSClass("PacketPlayOutOpenWindow").getConstructor(Integer.TYPE, getNMSClass("Containers"), getNMSClass("IChatBaseComponent"));
                    newVer = true;
                    break;
                case UNKNOWN:
                    throw new IllegalStateException("Something went wrong");
            }
        } catch (NoSuchMethodException | SecurityException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    @SneakyThrows
    public static void updateTitle(Player p, String title) {
        try {
            // There's a bug where client will crash if it founds an %
            title = title.replace("%", ""); // We have to replace it with other thing for now

            Object handle = getHandle(p);
            Object message = chatMessageConstructor.newInstance(title, new Object[0]);
            Object container = activeContainerField.get(handle);
            Object windowId = windowIdField.get(container);
            Object packet;
            Inventory topInventory = p.getOpenInventory().getTopInventory();
            if (!newVer) {
                 packet = packetPlayOutOpenWindowConstructor.newInstance(windowId, "minecraft:chest", message, topInventory.getSize());
            } else {
                packet = packetPlayOutOpenWindowConstructor.newInstance(windowId, getNMSClass("Containers").getDeclaredField("GENERIC_9X" + InventoryUtils.getInventoryRows(topInventory).size()).get(null), message);
            }
            sendPacket(p, packet);
        } catch (IllegalArgumentException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
        p.updateInventory();
    }
}