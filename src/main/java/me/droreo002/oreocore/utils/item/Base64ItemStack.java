package me.droreo002.oreocore.utils.item;

import lombok.Getter;
import me.droreo002.oreocore.utils.misc.Base64;
import org.bukkit.configuration.file.YamlConstructor;
import org.bukkit.configuration.file.YamlRepresenter;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.Tag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Saves items with Metadata in database, which allows for saving items on signs easily.
 * can be found at https://github.com/ChestShop-authors/ChestShop-3/blob/930b2cc07b9000582269f8224e1aad7975094af3/src/main/java/com/Acrobot/ChestShop/Metadata/ItemDatabase.java
 * @author Acrobot
 */
public final class Base64ItemStack {

    @Getter
    private static final Yaml YAML = new Yaml(new YamlBukkitConstructor(), new YamlRepresenter(), new DumperOptions());

    /**
     * Generate a list of {@link StringItemStack} from the inventory
     *
     * @param inventory The inventory
     * @return List of string ItemStack
     */
    @NotNull
    public static List<StringItemStack> asStringItemStack(@NotNull Inventory inventory) {
        List<StringItemStack> res = new ArrayList<>();
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (item == null) continue;
            res.add(new StringItemStack(asBase64(item), i, StringItemStack.StringType.BASE_64));
        }
        return res;
    }

    /**
     * Gets the item code for this item
     *
     * @param item Item
     * @return Item code for this item
     */
    @Nullable
    @SuppressWarnings("deprecation")
    public static String asBase64(@NotNull ItemStack item) {
        try {
            ItemStack clone = new ItemStack(item);
            clone.setAmount(1);
            clone.setDurability((short) 0);

            String dumped = YAML.dump(clone);
            ItemStack loadedItem = YAML.loadAs(dumped, ItemStack.class);
            if (!loadedItem.isSimilar(item)) {
                dumped = YAML.dump(loadedItem);
            }
            return Base64.encodeObject(dumped);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Gets an ItemStack from a item code
     * will throw an error if failed to decrypt
     *
     * @param base64 Item code
     * @return ItemStack represented by this code
     */
    @NotNull
    public static ItemStack fromBase64(String base64) throws Exception {
        return YAML.loadAs((String) Base64.decodeToObject(base64), ItemStack.class);
    }

    private static class YamlBukkitConstructor extends YamlConstructor {
        public YamlBukkitConstructor() {
            this.yamlConstructors.put(new Tag(Tag.PREFIX + "org.bukkit.inventory.ItemStack"), yamlConstructors.get(Tag.MAP));
        }
    }
}
