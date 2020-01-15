package me.droreo002.oreocore.database;

public enum DatabaseType {
    FLAT_FILE,
    MYSQL,
    SQL;

    /**
     * Check if database is flat file
     * based
     *
     * @return True if flat file based, false otherwise
     */
    public boolean isFlatFile() {
        switch (this) {
            case FLAT_FILE:
                return true;
            case MYSQL:
            case SQL:
                return false;
        }
        return false;
    }

    /**
     * Check if database is sql based
     *
     * @return True if sql based, false otherwise
     */
    public boolean isSql() {
        switch (this) {
            case FLAT_FILE:
                return false;
            case MYSQL:
            case SQL:
                return true;
        }
        return false;
    }
}
