package me.droreo002.oreocore.database.utils;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SqlDatabaseTable {

    @Getter
    private List<SqlDataKey> dataKeys;
    @Getter
    private String tableName;

    public SqlDatabaseTable(String tableName, SqlDataKey... dataKeys) {
        this.tableName = tableName;
        this.dataKeys = Arrays.asList(dataKeys);
    }

    public SqlDatabaseTable(String tableName) {
        this.tableName = tableName;
        this.dataKeys = new ArrayList<>();
    }

    public SqlDatabaseTable addKey(SqlDataKey sqlDataKey) {
        this.dataKeys.add(sqlDataKey);
        return this;
    }

    /**
     * Get the create command of this database table
     *
     * @return The create command
     */
    public String getCreateCommand() {
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE IF NOT EXISTS `").append(tableName).append("` (");
        String primaryKey = null;
        for (int i = 0; i < this.dataKeys.size(); i++) {
            SqlDataKey dataKey = this.dataKeys.get(i);
            if (dataKey.isPrimary()) primaryKey = dataKey.getKeyName();
            if (i == (this.dataKeys.size() - 1) && primaryKey == null) {
                builder.append(dataKey.toString());
            } else {
                builder.append(dataKey.toString()).append(", ");
            }
        }
        if (primaryKey != null) {
            builder.append("PRIMARY KEY (").append(primaryKey).append("));");
        } else {
            builder.append(");");
        }
        return builder.toString();
    }
}
