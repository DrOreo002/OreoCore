package me.droreo002.oreocore.utils.strings;

import me.droreo002.oreocore.utils.multisupport.BukkitReflectionUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;
import java.util.logging.Level;

public final class ToolTip {

    private static String convertItemStackToJson(ItemStack itemStack) throws ClassNotFoundException, NoSuchMethodException {
        // ItemStack methods to get a net.minecraft.server.ItemStack object for serialization
        Class<?> craftItemStackClazz = BukkitReflectionUtils.PackageType.CRAFTBUKKIT_INVENTORY.getClass("CraftItemStack");
        Method asNMSCopyMethod = BukkitReflectionUtils.getMethod(craftItemStackClazz, "asNMSCopy", ItemStack.class);

        // NMS Method to serialize a net.minecraft.server.ItemStack to a valid Json string
        Class<?> nmsItemStackClazz = BukkitReflectionUtils.PackageType.MINECRAFT_SERVER.getClass("ItemStack");
        Class<?> nbtTagCompoundClazz = BukkitReflectionUtils.PackageType.MINECRAFT_SERVER.getClass("NBTTagCompound");
        Method saveNmsItemStackMethod = BukkitReflectionUtils.getMethod(nmsItemStackClazz, "save", nbtTagCompoundClazz);

        Object nmsNbtTagCompoundObj; // This will just be an empty NBTTagCompound instance to invoke the saveNms method
        Object nmsItemStackObj; // This is the net.minecraft.server.ItemStack object received from the asNMSCopy method
        Object itemAsJsonObject; // This is the net.minecraft.server.ItemStack after being put through saveNmsItem method

        try {
            nmsNbtTagCompoundObj = nbtTagCompoundClazz.newInstance();
            nmsItemStackObj = asNMSCopyMethod.invoke(null, itemStack);
            itemAsJsonObject = saveNmsItemStackMethod.invoke(nmsItemStackObj, nmsNbtTagCompoundObj);
        } catch (Throwable t) {
            Bukkit.getLogger().log(Level.SEVERE, "failed to serialize itemstack to nms item", t);
            return null;
        }

        // Return a string representation of the serialized object
        return itemAsJsonObject.toString();
    }

    public static TextComponent getTextComponent(String display, ItemStack item) {
        String itemJson;
        try {
            itemJson = convertItemStackToJson(item);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }

        if (itemJson == null) return new TextComponent("ERROR");
        // Prepare a BaseComponent array with the itemJson as a text component
        BaseComponent[] hoverEventComponents = new BaseComponent[] { new TextComponent(itemJson) };

        // Create the hover event
        HoverEvent event = new HoverEvent(HoverEvent.Action.SHOW_ITEM, hoverEventComponents);

        /* And now we create the text component (this is the actual text that the player sees)
         * and set it's hover event to the item event */
        TextComponent component = new TextComponent(StringUtils.color(display));
        component.setHoverEvent(event);

        // Finally, send the message to the player
        return component;
    }
}
