package me.droreo002.oreocore.utils.item.complex;

import lombok.Getter;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class UCreatureSpawner {

    @Getter
    private CreatureSpawner cs;
    @Getter
    private EntityType type;

    public UCreatureSpawner(Block b) {
        if (b.getType().name().contains("SPAWNER")) {
            cs = (CreatureSpawner) b.getState();
            this.type = cs.getSpawnedType();
        }
    }

    public UCreatureSpawner(EntityType type) {
        this.type = type;
    }

    public CreatureSpawner getCreatureSpawner() {
        return cs;
    }

    private ItemStack getItemStack(EntityType type) {
        return new USpawner(type).getItemStack();
    }

    public void setType(EntityType type) {
        this.type = type;
        if (cs != null) {
            cs.setSpawnedType(type);
            cs.update();
        }
    }
}
