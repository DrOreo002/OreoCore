package me.droreo002.oreocore.database.utils;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SQLTableBuilder {

    @Getter
    private List<SQLDataKey> dataKeys;
    @Getter
    private String tableName;

    SQLTableBuilder(String tableName, SQLDataKey... dataKeys) {
        this.tableName = tableName;
        this.dataKeys = new ArrayList<>(Arrays.asList(dataKeys));
    }

    public static SQLTableBuilder of(String tableName, SQLDataKey... sqlDataKeys) {
        return new SQLTableBuilder(tableName, sqlDataKeys);
    }

    public SQLTableBuilder addKey(SQLDataKey sqlDataKey) {
        this.dataKeys.add(sqlDataKey);
        return this;
    }

    /**
     * Get the create command of this database table
     *
     * @return The create command
     */
    public String build() {
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE IF NOT EXISTS `").append(tableName).append("` (");
        String primaryKey = null;
        for (int i = 0; i < this.dataKeys.size(); i++) {
            SQLDataKey dataKey = this.dataKeys.get(i);
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
