package me.droreo002.oreocore.utils.item.namingSupport;

import org.bukkit.entity.EntityType;
import org.bukkit.inventory.meta.ItemMeta;

interface SpawnerMeta extends ItemMeta {
    void setType(EntityType type);
    EntityType getType();
    SpawnerMeta clone();
}
