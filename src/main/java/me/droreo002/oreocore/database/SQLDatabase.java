package me.droreo002.oreocore.database;

import lombok.SneakyThrows;
import me.droreo002.oreocore.database.utils.ConnectionPoolManager;
import me.droreo002.oreocore.database.utils.SqlDatabaseTable;
import me.droreo002.oreocore.utils.misc.ThreadingUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

public interface SQLDatabase {

    /**
     * Check if this sql database is initialized or not
     *
     * @return Initialized?
     */
    boolean isInitialized();

    /**
     * Get the connection pool manager for this sql database
     *
     * @return The ConnectionPoolManager
     */
    ConnectionPoolManager getConnectionPoolManager();

    /**
     * Load the data on this sql database
     * this should also be called after execution of constructor
     */
    void loadAllData();

    /**
     * Get the sql database table
     *
     * @return The database table
     */
    SqlDatabaseTable getSqlDatabaseTable();

    /**
     * Get the connection of this sql database
     *
     * @return The database connection
     */
    Connection getConnection();

    /**
     * Set a new connection
     *
     * @param connection The connection
     */
    void setConnection(Connection connection);

    /**
     * Get the sql type of this database
     *
     * @return The sql type
     */
    SQLType getSqlType();

    /**
     * Try to close the connection
     *
     * @throws SQLException : If the connection cannot be closed
     * @throws NullPointerException : If the data connection is currently null
     */
    default void close() throws SQLException {
        if (getConnectionPoolManager() != null) {
            getConnectionPoolManager().getDataSource().close();
        } else {
            if (getConnection() != null) {
                getConnection().close();
            } else {
                throw new NullPointerException("Cannot close while the data connection is null!");
            }
        }
    }

    /**
     * Execute a new SQL Command into the connection on the main server thread
     * keep in mind that running on the main thread will cause some lags to the server
     *
     * @param sql : The sql command
     * @return a new ResultSet class if succeeded, null otherwise
     */
    default ResultSet query(String sql) {
        if (!checkConnection()) throw new IllegalStateException("Cannot connect into the database!");
        Connection con = getNewConnection();
        PreparedStatement statement = null;
        try {
            statement = con.prepareStatement(sql);
            return statement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (!getSqlType().equals(SQLType.SQL_BASED)) {
                    // Close if not normal sql
                    if (con != null) {
                        con.close();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Execute a new SQL Command into the connection on the main server thread
     * keep in mind that running on the main thread will cause some lags to the server
     *
     * @param sql : The sql command
     * @return true if the command successfully executed, false otherwise
     */
     default boolean execute(String sql) {
         if (!checkConnection()) throw new IllegalStateException("Cannot connect into the database!");
         Connection con = getNewConnection();
         PreparedStatement statement = null;
         try {
             statement = con.prepareStatement(sql);
             statement.execute();
             return true;
         } catch (SQLException e) {
             e.printStackTrace();
         } finally {
             try {
                 if (statement != null) {
                     statement.close();
                 }
                 if (!getSqlType().equals(SQLType.SQL_BASED)) {
                     // Close if not normal sql
                     if (con != null) {
                         con.close();
                     }
                 }
             } catch (SQLException e) {
                 e.printStackTrace();
             }
         }
         return false;
     }

    /**
     * Execute a new SQL Command into the connection on a async thread
     * to get the bool, use #.join method. This will block and wait until that boolean is available
     *
     * @param sql : The sql command
     */
    default Future<Boolean> executeAsync(String sql) {
        if (!checkConnection()) throw new IllegalStateException("Cannot connect into the database!");
        return ThreadingUtils.makeFuture(() -> execute(sql));
    }

    /**
     * Query a command to get its value in an async task
     *
     * @param statement : The statement
     * @param row : The row
     */
     default Future<Object> queryValueAsync(String statement, String row) {
         if (!checkConnection()) throw new IllegalStateException("Cannot connect into the database!");
         return ThreadingUtils.makeFuture(() -> queryValue(statement, row));
     }

    /**
     * Query a command to get its value in an async task
     *
     * @param statement : The statement
     * @param toSelect : What row that will be selected
     */
     default Future<Object> queryRowAsync(String statement, String... toSelect) {
         if (!checkConnection()) throw new IllegalStateException("Cannot connect into the database!");
         return ThreadingUtils.makeFuture(() -> queryRow(statement, toSelect));
     }

    /**
     * Query a multiple row to get its values in an async task
     *
     * @param statement : The statement
     * @param row : The rows
     */
     default Future<Map<String, List<Object>>> queryMultipleRowsAsync(String statement, String... row) {
         if (!checkConnection()) throw new IllegalStateException("Cannot connect into the database!");
         return ThreadingUtils.makeFuture(() -> queryMultipleRow(statement, row));
     }

    /**
     * Check if the data exists
     * Usage is : .isExists("custom_nickname", "DrOreo002", "player_settings");
     * This will run on another thread
     *
     * @param column : The column
     * @param data : The data
     * @param table : The table
     */
    default Future<Boolean> isExistsAsync(String column, String data, String table) {
        if (!checkConnection()) throw new IllegalStateException("Cannot connect into the database!");
        return ThreadingUtils.makeFuture(() -> isExists(column, data, table));
    }

    /**
     * Query a command to get its value
     *
     * @param statement : The statement
     * @param row : The row
     * @return The specified value if there's any, null otherwise
     */
     default Object queryValue(String statement, String row) {
         if (!checkConnection()) throw new IllegalStateException("Cannot connect into the database!");
         PreparedStatement ps = null;
         ResultSet rs = null;
         Connection con = getNewConnection();
         try {
             ps = con.prepareStatement(statement);
             rs = ps.executeQuery();
             if (rs.next()) {
                 return rs.getObject(row);
             }
         } catch (SQLException ex) {
             ex.printStackTrace();
             try {
                 if (ps != null) {
                     ps.close();
                 }
                 if (rs != null) {
                     rs.close();
                 }
                 if (!getSqlType().equals(SQLType.SQL_BASED)) {
                     // Close if not normal sql
                     con.close();
                 }
             } catch (SQLException ex2) {
                 ex2.printStackTrace();
             }
         }
         finally {
             try {
                 if (ps != null) {
                     ps.close();
                 }
                 if (rs != null) {
                     rs.close();
                 }
                 if (!getSqlType().equals(SQLType.SQL_BASED)) {
                     // Close if not normal sql
                     if (con != null) {
                         con.close();
                     }
                 }
             }
             catch (SQLException ex2) {
                 ex2.printStackTrace();
             }
         }
         return null;
     }

    /**
     * Query a command to get its value
     *
     * @param statement : The statement
     * @param toSelect : What row that will be selected
     * @return The specified value as a list if there's any, empty list otherwise
     */
     default List<Object> queryRow(String statement, String... toSelect) {
         if (!checkConnection()) throw new IllegalStateException("Cannot connect into the database!");
         PreparedStatement ps = null;
         ResultSet rs = null;
         Connection con = getNewConnection();
         List<Object> values = new ArrayList<>();
         try {
             ps = con.prepareStatement(statement);
             rs = ps.executeQuery();
             while (rs.next()) {
                 for (String s : toSelect) {
                     values.add(rs.getObject(s));
                 }
             }
             return values;
         } catch (SQLException ex) {
             try {
                 if (ps != null) {
                     ps.close();
                 }
                 if (rs != null) {
                     rs.close();
                 }
                 if (!getSqlType().equals(SQLType.SQL_BASED)) {
                     // Close if not normal sql
                     con.close();
                 }
                 ex.printStackTrace();
             } catch (SQLException ex2) {
                 ex2.printStackTrace();
             }
         }
         finally {
             try {
                 if (ps != null) {
                     ps.close();
                 }
                 if (rs != null) {
                     rs.close();
                 }
                 if (!getSqlType().equals(SQLType.SQL_BASED)) {
                     // Close if not normal sql
                     if (con != null) {
                         con.close();
                     }
                 }
             }
             catch (SQLException ex2) {
                 ex2.printStackTrace();
             }
         }
         return values;
     }

    /**
     * Query a multiple row to get its values
     *
     * @param statement : The statement
     * @param row : The rows
     * @return a HashMap contained the result values if there's any, empty HashMap otherwise
     */
     default Map<String, List<Object>> queryMultipleRow(String statement, String... row) {
         if (!checkConnection()) throw new IllegalStateException("Cannot connect into the database!");
         PreparedStatement ps = null;
         ResultSet rs = null;
         Connection con = getNewConnection();
         final List<Object> objects = new ArrayList<>();
         final Map<String, List<Object>> map = new HashMap<>();
         try {
             ps = con.prepareStatement(statement);
             rs = ps.executeQuery();
             while (rs.next()) {
                 for (final String singleRow : row) {
                     objects.add(rs.getObject(singleRow));
                 }
                 for (final String singleRow : row) {
                     map.put(singleRow, objects);
                 }
             }
             return map;
         }
         catch (SQLException ex) {
             try {
                 if (ps != null) {
                     ps.close();
                 }
                 if (rs != null) {
                     rs.close();
                 }
                 if (!getSqlType().equals(SQLType.SQL_BASED)) {
                     // Close if not normal sql
                     con.close();
                 }
                 ex.printStackTrace();
             }
             catch (SQLException ex2) {
                 ex2.printStackTrace();
             }
         }
         finally {
             try {
                 if (ps != null) {
                     ps.close();
                 }
                 if (rs != null) {
                     rs.close();
                 }
                 if (!getSqlType().equals(SQLType.SQL_BASED)) {
                     // Close if not normal sql
                     if (con != null) {
                         con.close();
                     }
                 }
             }
             catch (SQLException ex2) {
                 ex2.printStackTrace();
             }
         }
         return map;
     }

    /**
     * Check if the data exists
     * Usage is : .isExists("custom_nickname", "DrOreo002", "player_settings");
     *
     * @param column : The column
     * @param data : The data
     * @param table : The table
     * @return true if exists, false otherwise
     */
     default boolean isExists(String column, String data, String table) {
         if (!checkConnection()) throw new IllegalStateException("Cannot connect into the database!");
         PreparedStatement pre = null;
         Connection con = null;
         ResultSet res = null;
         try {
             con = getConnection();
             pre = con.prepareStatement("SELECT * FROM `" + table + "` WHERE `" + column + "` = ?");
             pre.setString(1, data);
             res = pre.executeQuery();
             return res.next();
         } catch (Exception e) {
             e.printStackTrace();
         } finally {
             try {
                 if (pre != null) {
                     pre.close();
                 }
                 if (res != null) {
                     res.close();
                 }
                 if (!getSqlType().equals(SQLType.SQL_BASED)) {
                     // Close if not normal sql
                     if (con != null) {
                         con.close();
                     }
                 }
             } catch (SQLException e) {
                 e.printStackTrace();
             }
         }
         return false;
     }

    /**
     * Check the connection
     *
     * @return true if the connection is not interrupted. False otherwise
     */
    @SneakyThrows
    default boolean checkConnection() {
        try {
            if (getConnection() == null || getConnection().isClosed()) {
                setConnection(getNewConnection());
                if (getConnection() == null || getConnection().isClosed()) return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    /**
     * Get a new connection
     *
     * @return a new Connection if its a success, null otherwise
     */
    Connection getNewConnection();

}
