package me.droreo002.oreocore.utils.entity;

import me.droreo002.oreocore.enums.ArmorStandBody;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

import java.util.HashMap;
import java.util.Map;

public final class ArmorStandUtils {

    public static EulerAngle toEulerAngle(String format) {
        String[] sp = format.split(";");
        if (!sp[0].equalsIgnoreCase("EulerAngle")) return null;
        double x = Double.valueOf(sp[1]);
        double y = Double.valueOf(sp[2]);
        double z = Double.valueOf(sp[3]);
        return new EulerAngle(Math.toRadians(x), Math.toRadians(y), Math.toRadians(z));
    }

    public static String convertToString(EulerAngle angle) {
        return "EulerAngle;" + angle.getX() + ";" + angle.getY() + ";" + angle.getZ();
    }

    public static Map<ArmorStandBody, EulerAngle> getArmorStandAngle(ArmorStand armorStand) {
        Map<ArmorStandBody, EulerAngle> angle = new HashMap<>();
        angle.put(ArmorStandBody.HEAD, armorStand.getHeadPose());
        angle.put(ArmorStandBody.BODY, armorStand.getBodyPose());
        angle.put(ArmorStandBody.L_ARM, armorStand.getLeftArmPose());
        angle.put(ArmorStandBody.R_ARM, armorStand.getRightArmPose());
        angle.put(ArmorStandBody.L_LEG, armorStand.getLeftLegPose());
        angle.put(ArmorStandBody.R_LEG, armorStand.getRightLegPose());
        return angle;
    }

    public static Map<EquipmentSlot, ItemStack> getArmorStandItems(ArmorStand armorStand) {
        Map<EquipmentSlot, ItemStack> items = new HashMap<>();
        EntityEquipment inventory = armorStand.getEquipment();
        if (inventory.getHelmet() != null) items.put(EquipmentSlot.HEAD, inventory.getHelmet());
        if (inventory.getChestplate() != null) items.put(EquipmentSlot.CHEST, inventory.getChestplate());
        if (inventory.getItemInMainHand() != null) items.put(EquipmentSlot.HAND, inventory.getItemInMainHand());
        if (inventory.getItemInOffHand() != null) items.put(EquipmentSlot.OFF_HAND, inventory.getItemInOffHand());
        if (inventory.getLeggings() != null) items.put(EquipmentSlot.LEGS, inventory.getLeggings());
        if (inventory.getBoots() != null) items.put(EquipmentSlot.FEET, inventory.getBoots());
        return items;
    }
}
