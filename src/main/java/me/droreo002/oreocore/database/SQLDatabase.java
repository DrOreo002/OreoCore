package me.droreo002.oreocore.database;

import me.droreo002.oreocore.database.object.interfaces.SqlCallback;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

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
     * @param throwError : Should the api throw the sql error if there's any?
     * @return a new ResultSet class if succeeded, null otherwise
     */
     ResultSet query(String sql, boolean throwError);

    /**
     * Execute a new SQL Command into the connection on a async thread
     *
     * @param sql : The sql command
     */
     void executeAsync(String sql, SqlCallback<Boolean> callback);

    /**
     * Execute a new SQL Command into the connection on the main server thread
     * keep in mind that running on the main thread will cause some lags to the server
     *
     * @param sql : The sql command
     * @param throwError : Should the api throw the sql error if there's any?
     * @return true if the command successfully executed, false otherwise
     */
     boolean execute(String sql, boolean throwError);

    /**
     * Query a command to get its value in an async task
     *
     * @param statement : The statement
     * @param row : The row
     */
     void queryValueAsync(String statement, String row, SqlCallback<Object> callback);

    /**
     * Query a command to get its value in an async task
     *
     * @param statement : The statement
     * @param toSelect : What row that will be selected
     */
     void queryRowAsync(String statement, String[] toSelect, SqlCallback<List<Object>> callback);

    /**
     * Query a multiple row to get its values in an async task
     *
     * @param statement : The statement
     * @param row : The rows
     */
     void queryMultipleRowsAsync(String statement, SqlCallback<Map<String, List<Object>>> callback, String... row);

    /**
     * Query a command to get its value
     *
     * @param statement : The statement
     * @param row : The row
     * @param throwError : Should the api throw the error if there's any?
     * @return The specified value if there's any, null otherwise
     */
     Object queryValue(String statement, String row, boolean throwError);

    /**
     * Query a command to get its value
     *
     * @param statement : The statement
     * @param toSelect : What row that will be selected
     * @param throwError : Should the api throw the error if there's any?
     * @return The specified value as a list if there's any, empty list otherwise
     */
     List<Object> queryRow(String statement, String[] toSelect, boolean throwError);

    /**
     * Query a multiple row to get its values
     *
     * @param statement : The statement
     * @param row : The rows
     * @param throwError : Should the api throw the error if there's any?
     * @return a HashMap contained the result values if there's any, empty HashMap otherwise
     */
     Map<String, List<Object>> queryMultipleRow(String statement, boolean throwError, String... row);

    /**
     * Check if the data exists
     * Usage is : .isExists("custom_nickname", "DrOreo002", "player_settings");
     * @param column : The column
     * @param data : The data
     * @param table : The table
     * @return true if exists, false otherwise
     */
     boolean isExists(String column, String data, String table, boolean throwError);

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
