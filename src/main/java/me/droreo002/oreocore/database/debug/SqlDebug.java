package me.droreo002.oreocore.database.debug;

import me.droreo002.oreocore.OreoCore;
import me.droreo002.oreocore.database.DatabaseManager;
import me.droreo002.oreocore.database.SQLType;
import me.droreo002.oreocore.database.object.DatabaseSQL;
import me.droreo002.oreocore.debugging.ODebug;

import java.io.File;

public class SqlDebug extends DatabaseSQL {

    public SqlDebug() {
        super(OreoCore.getInstance(), "hello", new File(OreoCore.getInstance().getDataFolder(), "hello"), SQLType.HIKARI_CP);
        DatabaseManager.registerDatabase(OreoCore.getInstance(), this);
    }

    @Override
    public void loadData() {
        ODebug.log("Loading data....", true);
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
}
