package me.droreo002.oreocore.utils.item.complex;

import org.bukkit.entity.EntityType;
import org.bukkit.inventory.meta.ItemMeta;

interface SpawnerMeta extends ItemMeta {
    void setType(EntityType type);
    EntityType getType();
    SpawnerMeta clone();
}
