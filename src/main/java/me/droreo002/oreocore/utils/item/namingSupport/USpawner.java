package me.droreo002.oreocore.utils.item.namingSupport;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class USpawner extends ItemStack {

    @Getter @Setter
    private EntityType entityType;
    @Getter
    private final ItemStack is;
    @Getter
    private final SpawnerMeta sm;

    public USpawner(EntityType type) {
        this.entityType = type;
        final ItemStack is = UMaterial.SPAWNER.getItemStack();
        final SpawnerMeta sm = (SpawnerMeta) is.getItemMeta();
        sm.setType(type);
        this.sm = sm;
        this.is = is;
    }

    public ItemStack getItemStack() {
        return is.clone();
    }
}
