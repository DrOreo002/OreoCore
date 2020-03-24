package me.droreo002.oreocore.utils.entity;

import me.droreo002.oreocore.enums.ArmorStandBody;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public final class ArmorStandUtils {

    /**
     * Convert string into an euler angle
     *
     * @param format The string
     * @return EulerAngle
     */
    @Nullable
    public static EulerAngle stringToEuler(String format) {
        String[] sp = format.split(";");
        if (!sp[0].equalsIgnoreCase("EulerAngle")) return null;
        return new EulerAngle(Math.toRadians(Double.parseDouble(sp[1])), Math.toRadians(Double.parseDouble(sp[2])), Math.toRadians(Double.parseDouble(sp[3])));
    }

    /**
     * Convert euler angle into a string
     *
     * @param angle The angle to convert
     * @return String result
     */
    @NotNull
    public static String eulerToString(EulerAngle angle) {
        return "EulerAngle;" + angle.getX() + ";" + angle.getY() + ";" + angle.getZ();
    }

    /**
     * Get the ArmorStand angles
     *
     * @param armorStand The target armor stand
     * @return Map of ArmorStand angles
     */
    @NotNull
    public static Map<ArmorStandBody, EulerAngle> getArmorStandAngle(ArmorStand armorStand) {
        Map<ArmorStandBody, EulerAngle> angle = new HashMap<>();
        angle.put(ArmorStandBody.HEAD, armorStand.getHeadPose());
        angle.put(ArmorStandBody.BODY, armorStand.getBodyPose());
        angle.put(ArmorStandBody.LEFT_ARM, armorStand.getLeftArmPose());
        angle.put(ArmorStandBody.RIGHT_ARM, armorStand.getRightArmPose());
        angle.put(ArmorStandBody.LEFT_LEG, armorStand.getLeftLegPose());
        angle.put(ArmorStandBody.RIGHT_LEG, armorStand.getRightLegPose());
        return angle;
    }

    /**
     * Get the ArmorStand items
     *
     * @param armorStand The target armor stand
     * @return Map of ArmorStand item
     */
    @NotNull
    public static Map<EquipmentSlot, ItemStack> getArmorStandItems(ArmorStand armorStand) {
        Map<EquipmentSlot, ItemStack> items = new HashMap<>();
        EntityEquipment inventory = armorStand.getEquipment();
        items.put(EquipmentSlot.HEAD, inventory.getHelmet());
        items.put(EquipmentSlot.CHEST, inventory.getChestplate());
        items.put(EquipmentSlot.HAND, inventory.getItemInMainHand());
        items.put(EquipmentSlot.OFF_HAND, inventory.getItemInOffHand());
        items.put(EquipmentSlot.LEGS, inventory.getLeggings());
        items.put(EquipmentSlot.FEET, inventory.getBoots());
        return items;
    }
}
