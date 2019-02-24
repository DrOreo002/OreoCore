package me.droreo002.oreocore.database.debug;

import me.droreo002.oreocore.OreoCore;
import me.droreo002.oreocore.database.DatabaseManager;
import me.droreo002.oreocore.database.SQLType;
import me.droreo002.oreocore.database.object.DatabaseSQL;
import me.droreo002.oreocore.database.object.interfaces.SqlCallback;
import me.droreo002.oreocore.utils.logging.Debug;
import org.bukkit.entity.Player;

import java.io.File;

public class SqlDebug extends DatabaseSQL {

    public SqlDebug() {
        super(OreoCore.getInstance(), "hello", new File(OreoCore.getInstance().getDataFolder(), "hello"), SQLType.HIKARI_CP);
        DatabaseManager.registerDatabase(OreoCore.getInstance(), this);
    }

    @Override
    public void loadData() {
        Debug.log("Loading data....", true);
    }

    @Override
    public String getFirstCommand() {
        return "CREATE TABLE IF NOT EXISTS `csl` (\n"
                + "  `UUID` VARCHAR(36) NOT NULL,\n" // UUID Length is 36 according to google
                + "  `name` VARCHAR(16) NOT NULL,\n" // Minecraft usename length is 16 according to google
                + "  `shopCreated` int(11) NOT NULL DEFAULT '0',\n"
                + "  `maxShop` int(11) NOT NULL DEFAULT '0',\n"
                + "PRIMARY KEY (UUID));";
    }

    public void insertNew(Player player) {
        // Check if the player contains on the db first
        queryValueAsync("SELECT * FROM `csl` WHERE `UUID` is '" + player.getUniqueId().toString() + "'", "name", new SqlCallback<Object>() {
            @Override
            public void onSuccess(Object done) {
                if (!(done instanceof String)) return;
                String str = String.valueOf(done);
                if (str.equalsIgnoreCase(player.getName())) return;
                execute("INSERT INTO `csl` (UUID,name,shopCreated,maxShop) VALUES ('" + player.getUniqueId().toString() + "','" + player.getName() + "','0','0');", true);
            }

            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace(); // Just print
            }
        });
    }

    public void isAvailable(Player player) {
        queryValueAsync("SELECT * FROM `csl` WHERE `UUID` is '" + player.getUniqueId().toString() + "'", "name",new SqlCallback<Object>() {
            @Override
            public void onSuccess(Object done) {
                if (!(done instanceof String)) return;
                String str = String.valueOf(done);
                if (str.equalsIgnoreCase(player.getName())) return;
                execute("INSERT INTO `csl` (UUID,name,shopCreated,maxShop) VALUES ('" + player.getUniqueId().toString() + "','" + player.getName() + "','0','0');", true);
            }

            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace(); // Just print
            }
        });
    }
}
