package me.droreo002.oreocore.utils.item.namingSupport;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

public class UPotion {

    private static final String v = Bukkit.getVersion();

    @Getter
    private final ItemStack potion;
    @Getter
    private final Object potiondata;

    public UPotion(UMaterial.PotionBase base, PotionType type, boolean extended, boolean upgraded) {
        final String bn = base.name();
        if(v.contains("1.8")) {
            potion = type.name().equals("WATER") ? new Potion(type).toItemStack(1) : new Potion(type, upgraded ? 2 : 1, bn.equals("SPLASH")).toItemStack(1);
            potiondata = potion.getItemMeta();
        } else {
            final ItemStack is = new ItemStack(Material.matchMaterial(bn.equals("NORMAL") ? "POTION" : bn.equals("ARROW") ? v.contains("1.8") || v.contains("1.9") || v.contains("1.11") ? "ARROW" : "TIPPED_ARROW" : bn + "_POTION"));
            final boolean a = !is.getType().equals(Material.ARROW);
            org.bukkit.inventory.meta.PotionMeta pm = null;
            org.bukkit.potion.PotionData pd = null;
            if(a) {
                pm = (org.bukkit.inventory.meta.PotionMeta) is.getItemMeta();
                pd = new org.bukkit.potion.PotionData(type, type.isExtendable() && extended, type.isUpgradeable() && upgraded);
            }
            potiondata = pd;
            if(a) {
                pm.setBasePotionData(pd);
                is.setItemMeta(pm);
            }
            potion = is;
        }
    }

    public ItemStack getItemStack() {
        return potion.clone();
    }
}
