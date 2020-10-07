package me.droreo002.oreocore.utils.inventory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A inventory helper that allows you to change the title of the current
 * opened inventory.
 *
 * @author https://gist.github.com/Cybermaxke/7f0a315aea70c9d62535
 */
public class InventoryTitleHelper {
    // Methods
    private static Method m_Player_GetHandle;
    private static Method m_PlayerConnection_sendPacket;
    private static Method m_CraftChatMessage_fromString;
    private static Method m_EntityPlayer_updateInventory;

    // Fields
    private static Field f_EntityPlayer_playerConnection;
    private static Field f_EntityPlayer_activeContainer;
    private static Field f_Container_windowId;

    // Constructors
    private static Constructor<?> c_PacketOpenWindow;

    // The version of the server (nms version like v1_5_R3)
    private static String nms_version;
    private static String nms_package;
    private static String crb_package;

    /**
     * Sends a new inventory title to the client.
     *
     * @param player the player
     * @param title the new title
     */
    public static void sendInventoryTitle(Player player, String title) {
        checkNotNull(player, "player");

        try {
            sendInventoryTitle0(player, title);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void sendInventoryTitle0(Player player, String title) throws Exception {
        Inventory inventory = player.getOpenInventory().getTopInventory();
        if (inventory == null) {
            return;
        }
        if (m_Player_GetHandle == null) {
            m_Player_GetHandle = player.getClass().getMethod("getHandle");
        }
        Object nms_EntityPlayer = m_Player_GetHandle.invoke(player);
        if (f_EntityPlayer_playerConnection == null) {
            f_EntityPlayer_playerConnection = nms_EntityPlayer.getClass().getField("playerConnection");
        }
        Object nms_PlayerConnection = f_EntityPlayer_playerConnection.get(nms_EntityPlayer);
        if (f_EntityPlayer_activeContainer == null) {
            f_EntityPlayer_activeContainer = nms_EntityPlayer.getClass().getField("activeContainer");
        }
        Object nms_Container = f_EntityPlayer_activeContainer.get(nms_EntityPlayer);
        if (f_Container_windowId == null) {
            f_Container_windowId = nms_Container.getClass().getField("windowId");
        }
        int windowId = f_Container_windowId.getInt(nms_Container);
        String version = getNmsVersion();
        if (version.startsWith("v1_5_") || version.startsWith("v1_6_")) {
            sendPacket15a16a17(nms_PlayerConnection, nms_EntityPlayer, nms_Container, windowId, inventory, title, false);
        } else if (version.startsWith("v1_7_")) {
            sendPacket15a16a17(nms_PlayerConnection, nms_EntityPlayer, nms_Container, windowId, inventory, title, true);
        } else if (version.equals("v1_8_R1") || version.equals("v1_8_R2")) {
            sendPacket18(nms_PlayerConnection, nms_EntityPlayer, nms_Container, windowId, inventory, title);
        }
    }

    private static void sendPacket15a16a17(Object nms_playerConnection, Object nms_EntityPlayer, Object nms_Container, int windowId, Inventory inventory, String title, boolean flag) throws Exception {
        if (c_PacketOpenWindow == null) {
            if (flag) {
                c_PacketOpenWindow = findNmsClass("PacketPlayOutOpenWindow").getConstructor(int.class, int.class, String.class, int.class, boolean.class);
            } else {
                c_PacketOpenWindow = findNmsClass("Packet100OpenWindow").getConstructor(int.class, int.class, String.class, int.class, boolean.class);
            }
        }

        int id;
        int size;

        switch (inventory.getType()) {
            case ANVIL:
                id = 8;
                size = 9;
                break;
            case BEACON:
                id = 7;
                size = 1;
                break;
            case BREWING:
                id = 5;
                size = 4;
                break;
            case CRAFTING:
                return;
            case CREATIVE:
                return;
            case DISPENSER:
                id = 3;
                size = 9;
                break;
            case DROPPER:
                id = 10;
                size = 9;
                break;
            case ENCHANTING:
                id = 4;
                size = 9;
                break;
            case ENDER_CHEST:
            case CHEST:
                id = 0;
                size = inventory.getSize();
                break;
            case FURNACE:
                id = 2;
                size = 2;
                break;
            case HOPPER:
                id = 9;
                size = 5;
                break;
            case MERCHANT:
                id = 6;
                size = 3;
                break;
            case PLAYER:
                return;
            case WORKBENCH:
                id = 1;
                size = 9;
                break;
            default:
                return;
        }

        if (title != null && title.length() > 32) {
            title = title.substring(0, 32);
        }

        if (m_EntityPlayer_updateInventory == null) {
            m_EntityPlayer_updateInventory = nms_EntityPlayer.getClass().getMethod("updateInventory", findNmsClass("Container"));
        }

        Object packet = c_PacketOpenWindow.newInstance(windowId, id, title != null ? title : "", size, true);
        sendPacket(nms_playerConnection, packet);

        m_EntityPlayer_updateInventory.invoke(nms_EntityPlayer, nms_Container);
    }

    private static void sendPacket18(Object nms_playerConnection, Object nms_EntityPlayer, Object nms_Container, int windowId, Inventory inventory, String title) throws Exception {
        if (c_PacketOpenWindow == null) {
            c_PacketOpenWindow = findNmsClass("PacketPlayOutOpenWindow").getConstructor(int.class, String.class, findNmsClass("IChatBaseComponent"), int.class);
        }

        String id;
        int size = 0;

        switch (inventory.getType()) {
            case ANVIL:
                id = "minecraft:anvil";
                break;
            case BEACON:
                id = "minecraft:beacon";
                break;
            case BREWING:
                id = "minecraft:brewing_stand";
                break;
            case CRAFTING:
                return;
            case CREATIVE:
                return;
            case DISPENSER:
                id = "minecraft:dispenser";
                break;
            case DROPPER:
                id = "minecraft:dropper";
                break;
            case ENCHANTING:
                id = "minecraft:enchanting_table";
                break;
            case ENDER_CHEST:
            case CHEST:
                id = "minecraft:chest";
                size = inventory.getSize();
                break;
            case FURNACE:
                id = "minecraft:furnace";
                break;
            case HOPPER:
                id = "minecraft:hopper";
                break;
            case MERCHANT:
                id = "minecraft:villager";
                size = 3;
                break;
            case PLAYER:
                return;
            case WORKBENCH:
                id = "minecraft:crafting_table";
                break;
            default:
                return;
        }

        if (m_CraftChatMessage_fromString == null) {
            m_CraftChatMessage_fromString = findCrbClass("util.CraftChatMessage").getMethod("fromString", String.class);
        }
        if (m_EntityPlayer_updateInventory == null) {
            m_EntityPlayer_updateInventory = nms_EntityPlayer.getClass().getMethod("updateInventory", findNmsClass("Container"));
        }

        Object nms_title = ((Object[]) m_CraftChatMessage_fromString.invoke(null, title))[0];
        Object nms_packet = c_PacketOpenWindow.newInstance(windowId, id, nms_title, size);
        sendPacket(nms_playerConnection, nms_packet);

        m_EntityPlayer_updateInventory.invoke(nms_EntityPlayer, nms_Container);
    }

    private static void sendPacket(Object playerConnection, Object packet) throws Exception {
        if (m_PlayerConnection_sendPacket == null) {
            m_PlayerConnection_sendPacket = playerConnection.getClass().getMethod("sendPacket", findNmsClass("Packet"));
        }
        m_PlayerConnection_sendPacket.invoke(playerConnection, packet);
    }

    private static String getNmsVersion() {
        if (nms_version == null) {
            nms_version = Bukkit.getServer().getClass().getPackage().getName().replace("org.bukkit.craftbukkit.", "");
        }

        return nms_version;
    }

    private static String getNmsPackage() {
        if (nms_package == null) {
            nms_package = "net.minecraft.server." + getNmsVersion();
        }

        return nms_package;
    }

    private static String getCrbPackage() {
        if (crb_package == null) {
            crb_package = "org.bukkit.craftbukkit." + getNmsVersion();
        }

        return crb_package;
    }

    private static Class<?> findNmsClass(String name) throws Exception {
        return Class.forName(getNmsPackage() + "." + name);
    }

    private static Class<?> findCrbClass(String name) throws Exception {
        return Class.forName(getCrbPackage() + "." + name);
    }
}