package me.droreo002.oreocore.database.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Getter
@AllArgsConstructor
public class SQLDataKey {
    private String keyName, keyType;
    private boolean primary, nullAble, autoIncrement;
    private @Nullable String defaultValue;

    SQLDataKey(String keyName, KeyType keyType) {
        this.keyName = keyName;
        this.keyType = keyType.asString;
    }

    @NotNull
    public static SQLDataKey create(String keyName, KeyType keyType) {
        return new SQLDataKey(keyName, keyType);
    }

    public SQLDataKey primary() {
        this.primary = true;
        return this;
    }

    public SQLDataKey nullable() {
        this.primary = true;
        return this;
    }

    public SQLDataKey defaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public SQLDataKey autoIncrement() {
        this.autoIncrement = true;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("`" + keyName + "` " + keyType);
        if (!nullAble) builder.append(" ").append("NOT NULL");
        if (autoIncrement) builder.append(" ").append("AUTO_INCREMENT");
        if (defaultValue != null) builder.append(" DEFAULT ").append("'").append(defaultValue).append("'");
        return builder.toString();
    }

    public enum KeyType {
        UUID("VARCHAR(36)"),
        TEXT("TEXT"),
        MINECRAFT_USERNAME("VARCHAR(16)"),
        OPTIMIZED_INTEGER("int(11)");

        @Getter
        private String asString;

        KeyType(String asString) {
            this.asString = asString;
        }
    }
}
