package me.droreo002.oreocore.utils.item.complex;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

public class UPotion {

    private static final String v = Bukkit.getVersion();
    private static boolean eight = v.contains("1.8"), nine = v.contains("1.9"), ten = v.contains("1.10"), eleven = v.contains("1.11"), twelve = v.contains("1.12");

    @Getter
    private final ItemStack itemStack;
    @Getter
    private final Object potiondata;

    public UPotion(UMaterial.PotionBase base, String type, boolean extended, boolean upgraded) {
        type = type.toUpperCase();
        final PotionType t = eight && (type.equals("AWKWARD") || type.equals("LUCK") || type.equals("MUNDANE") || type.equals("THICK")) || (eight || nine || ten || eleven || twelve) && (type.equals("TURTLE_MASTER") || type.equals("SLOW_FALLING"))
                ? PotionType.WATER : PotionType.valueOf(type);
        final String bn = base.name();
        if (eight) {
            itemStack = t.equals(PotionType.WATER) ? new Potion(t).toItemStack(1) : new Potion(t, upgraded ? 2 : 1, bn.equals("SPLASH")).toItemStack(1);
            potiondata = itemStack.getItemMeta();
        } else {
            final ItemStack is = new ItemStack(Material.matchMaterial(bn.equals("NORMAL") ? "POTION" : bn.contains("ARROW") ? bn.contains("TIPPED") ? "TIPPED_ARROW" : "ARROW" : bn + "_POTION"));
            final boolean a = !is.getType().equals(Material.ARROW);
            org.bukkit.inventory.meta.PotionMeta pm = null;
            org.bukkit.potion.PotionData pd = null;
            if(a) {
                pm = (org.bukkit.inventory.meta.PotionMeta) is.getItemMeta();
                pd = new org.bukkit.potion.PotionData(t, t.isExtendable() && extended, t.isUpgradeable() && upgraded);
            }
            potiondata = pd;
            if(a) {
                pm.setBasePotionData(pd);
                is.setItemMeta(pm);
            }
            itemStack = is;
        }
    }
}
