package me.droreo002.oreocore.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

public interface SQLDatabase {

    /**
     * Try to close the connection
     *
     * @throws SQLException : If the connection cannot be closed
     * @throws NullPointerException : If the data connection is currently null
     */
     void close() throws SQLException;

    /**
     * Execute a new SQL Command into the connection on the main server thread
     * keep in mind that running on the main thread will cause some lags to the server
     *
     * @param sql : The sql command
     * @return a new ResultSet class if succeeded, null otherwise
     */
     ResultSet query(String sql);

    /**
     * Execute a new SQL Command into the connection on the main server thread
     * keep in mind that running on the main thread will cause some lags to the server
     *
     * @param sql : The sql command
     * @return true if the command successfully executed, false otherwise
     */
     boolean execute(String sql);

    /**
     * Execute a new SQL Command into the connection on a async thread
     * to get the bool, use #.join method. This will block and wait until that boolean is available
     *
     * @param sql : The sql command
     */
    Future<Boolean> executeAsync(String sql);

    /**
     * Query a command to get its value in an async task
     *
     * @param statement : The statement
     * @param row : The row
     */
     Future<Object> queryValueAsync(String statement, String row);

    /**
     * Query a command to get its value in an async task
     *
     * @param statement : The statement
     * @param toSelect : What row that will be selected
     */
     Future<Object> queryRowAsync(String statement, String... toSelect);

    /**
     * Query a multiple row to get its values in an async task
     *
     * @param statement : The statement
     * @param row : The rows
     */
     Future<Map<String, List<Object>>> queryMultipleRowsAsync(String statement, String... row);

    /**
     * Check if the data exists
     * Usage is : .isExists("custom_nickname", "DrOreo002", "player_settings");
     * This will run on another thread
     *
     * @param column : The column
     * @param data : The data
     * @param table : The table
     */
    Future<Boolean> isExistsAsync(String column, String data, String table);

    /**
     * Query a command to get its value
     *
     * @param statement : The statement
     * @param row : The row
     * @return The specified value if there's any, null otherwise
     */
     Object queryValue(String statement, String row);

    /**
     * Query a command to get its value
     *
     * @param statement : The statement
     * @param toSelect : What row that will be selected
     * @return The specified value as a list if there's any, empty list otherwise
     */
     List<Object> queryRow(String statement, String... toSelect);

    /**
     * Query a multiple row to get its values
     *
     * @param statement : The statement
     * @param row : The rows
     * @return a HashMap contained the result values if there's any, empty HashMap otherwise
     */
     Map<String, List<Object>> queryMultipleRow(String statement, String... row);

    /**
     * Check if the data exists
     * Usage is : .isExists("custom_nickname", "DrOreo002", "player_settings");
     *
     * @param column : The column
     * @param data : The data
     * @param table : The table
     * @return true if exists, false otherwise
     */
     boolean isExists(String column, String data, String table);

    /**
     * Check the connection
     *
     * @return true if the connection is not interrupted. False otherwise
     * @throws SQLException : If there's something wrong happened
     */
     boolean checkConnection();
    
    /**
     * Get a new connection
     *
     * @return a new Connection if its a success, null otherwise
     */
    Connection getNewConnection();

}
