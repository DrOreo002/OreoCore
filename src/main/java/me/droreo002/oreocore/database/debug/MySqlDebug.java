package me.droreo002.oreocore.database.debug;

import me.droreo002.oreocore.OreoCore;
import me.droreo002.oreocore.database.DatabaseManager;
import me.droreo002.oreocore.database.SQLType;
import me.droreo002.oreocore.database.object.DatabaseMySQL;
import me.droreo002.oreocore.database.utils.MySqlConnection;
import me.droreo002.oreocore.database.utils.SqlDataKey;
import me.droreo002.oreocore.database.utils.SqlDatabaseTable;
import me.droreo002.oreocore.debugging.ODebug;

public class MySqlDebug extends DatabaseMySQL {

    public MySqlDebug() {
        super(OreoCore.getInstance(), new MySqlConnection("localhost", 3306, "hello", "", "root"), 300, SQLType.SQL_BASED);
        DatabaseManager.registerDatabase(OreoCore.getInstance(), this);
    }

    @Override
    public void loadAllData() {
        ODebug.log(owningPlugin,"Loading data!", true);
    }

    @Override
    public SqlDatabaseTable getSqlDatabaseTable() {
        return new SqlDatabaseTable("csl")
                .addKey(new SqlDataKey("UUID", true, SqlDataKey.KeyType.UUID, false, null))
                .addKey(new SqlDataKey("name", false, SqlDataKey.KeyType.MINECRAFT_USERNAME, false, null));
    }
}
