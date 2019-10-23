package me.droreo002.oreocore.database.debug;

import me.droreo002.oreocore.OreoCore;
import me.droreo002.oreocore.database.DatabaseManager;
import me.droreo002.oreocore.database.SQLType;
import me.droreo002.oreocore.database.object.DatabaseMySQL;
import me.droreo002.oreocore.database.utils.MySqlConnection;
import me.droreo002.oreocore.debugging.ODebug;

public class MySqlDebug extends DatabaseMySQL {

    public MySqlDebug() {
        super(OreoCore.getInstance(), new MySqlConnection("localhost", 3306, "hello", "", "root"), 300, SQLType.SQL_BASED);
        DatabaseManager.registerDatabase(OreoCore.getInstance(), this);
    }

    @Override
    public void loadData() {
        ODebug.log(owningPlugin,"Loading data!", true);
    }

    @Override
    public String getFirstCommand() {
        return "CREATE TABLE IF NOT EXISTS `csl` (\n"
                + "  `UUID` VARCHAR(36) NOT NULL,\n" // UUID Length is 36 according to google
                + "  `name` VARCHAR(16) NOT NULL,\n" // Minecraft usename length is 16 according to google
                + "  `shopCreated` int(11) NOT NULL DEFAULT '0',\n"
                + "  `maxShop` int(11) NOT NULL DEFAULT '0',\n"
                + "PRIMARY KEY (UUID);";
    }
}
