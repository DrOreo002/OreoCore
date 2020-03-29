package me.droreo002.oreocore.database.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

@Getter
@AllArgsConstructor
public class SqlDataKey {
    private String keyName;
    private boolean primary;
    private String keyType;
    private boolean nullAble;
    private @Nullable String defaultValue;

    public SqlDataKey(String keyName, boolean primary, KeyType keyType, boolean nullAble, @Nullable String defaultValue) {
        this.keyName = keyName;
        this.primary = primary;
        this.keyType = keyType.asString;
        this.nullAble = nullAble;
        this.defaultValue = defaultValue;
    }

    @Override
    public String toString() {
        return "`" + keyName + "` " + keyType + " " +
                ((nullAble) ? "" : "NOT NULL ") +
                ((defaultValue != null) ? "DEFAULT " + "'" + defaultValue + "'" : "");
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
