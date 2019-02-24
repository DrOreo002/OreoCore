package me.droreo002.oreocore.database.object;

import lombok.Getter;
import me.droreo002.oreocore.database.Database;
import me.droreo002.oreocore.database.DatabaseType;
import me.droreo002.oreocore.database.SQLDatabase;
import me.droreo002.oreocore.database.SQLType;
import me.droreo002.oreocore.database.object.interfaces.SqlCallback;
import me.droreo002.oreocore.database.utils.ConnectionPoolManager;
import me.droreo002.oreocore.utils.logging.Debug;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class DatabaseSQL extends Database implements SQLDatabase {

    @Getter
    private ConnectionPoolManager poolManager;
    @Getter
    private Connection connection; // Do not directly get this var.
    @Getter
    private String databaseName;
    @Getter
    private File databaseFolder;
    @Getter
    private SQLType sqlType;

    public DatabaseSQL(JavaPlugin plugin, String databaseName, File databaseFolder, SQLType sqlType) {
        super(DatabaseType.SQL, plugin);
        if (databaseName.contains(".db")) throw new IllegalStateException("Database name cannot have database extension in it!");
        this.databaseName = databaseName;
        this.databaseFolder = databaseFolder;
        this.sqlType = sqlType;
        if (!databaseFolder.exists()) databaseFolder.mkdir();
        init();
    }

    @Override
    public void init() {
        if (checkConnection()) {
            if (execute(getFirstCommand(), true)) {
                Debug.log("&fSQL Connection for plugin &c" + getOwningPlugin().getName() + "&f has been created!. Database will now be stored at &e" + databaseFolder.getAbsolutePath() + "\\" + databaseName + ".db &f, database type is currently &e" + sqlType, true);
            } else {
                Debug.log("&cFailed to initialize the SQL connection on plugin &e" + getOwningPlugin().getName() + "&c Please contact the dev!");
            }
        } else {
            throw new IllegalStateException("SQL Connection for plugin " + getOwningPlugin().getName() + " cannot be proceeded!, please contact the dev!");
        }
    }

    public abstract void loadData();
    public abstract String getFirstCommand();

    /**
     * Try to close the connection
     *
     * @throws SQLException : If the connection cannot be closed
     * @throws NullPointerException : If the data connection is currently null
     */
    @Override
    public void close() throws SQLException {
        if (poolManager != null) {
            poolManager.getDataSource().close();
        } else {
            if (connection != null) {
                connection.close();
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
     * @param throwError : Should the api throw the sql error if there's any?
     * @return a new ResultSet class if succeeded, null otherwise
     */
    @Override
    public ResultSet query(String sql, boolean throwError) {
        if (!checkConnection()) throw new IllegalStateException("Cannot connect into the database!");
        Connection con = getNewConnection();
        PreparedStatement statement = null;
        try {
            statement = con.prepareStatement(sql);
            return statement.executeQuery();
        } catch (SQLException e) {
            if (throwError) e.printStackTrace();
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (!sqlType.equals(SQLType.SQL_BASED)) {
                    // Close if not normal sql
                    if (con != null) {
                        con.close();
                    }
                }
            } catch (SQLException e) {
                if (throwError) e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Execute a new SQL Command into the connection on a async thread
     *
     * @param sql : The sql command
     */
    @Override
    public void executeAsync(String sql, SqlCallback<Boolean> callback) {
        if (!checkConnection()) throw new IllegalStateException("Cannot connect into the database!");
        Bukkit.getScheduler().runTaskAsynchronously(getOwningPlugin(), () -> {
            Connection con = getNewConnection();
            PreparedStatement statement = null;
            try {
                statement = con.prepareStatement(sql);
                statement.execute();
                callback.onSuccess(true);
            } catch (SQLException e) {
                callback.onError(e);
            } finally {
                try {
                    if (statement != null) {
                        statement.close();
                    }
                    if (!sqlType.equals(SQLType.SQL_BASED)) {
                        // Close if not normal sql
                        if (con != null) {
                            con.close();
                        }
                    }
                } catch (SQLException e) {
                    callback.onError(e);
                }
            }
        });
    }

    /**
     * Execute a new SQL Command into the connection on the main server thread
     * keep in mind that running on the main thread will cause some lags to the server
     *
     * @param sql : The sql command
     * @param throwError : Should the api throw the sql error if there's any?
     * @return true if the command successfully executed, false otherwise
     */
    @Override
    public boolean execute(String sql, boolean throwError) {
        if (!checkConnection()) throw new IllegalStateException("Cannot connect into the database!");
        Connection con = getNewConnection();
        PreparedStatement statement = null;
        try {
            statement = con.prepareStatement(sql);
            statement.execute();
            return true;
        } catch (SQLException e) {
            if (throwError) e.printStackTrace();
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (!sqlType.equals(SQLType.SQL_BASED)) {
                    // Close if not normal sql
                    if (con != null) {
                        con.close();
                    }
                }
            } catch (SQLException e) {
                if (throwError) e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * Query a command to get its value in an async task
     *
     * @param statement : The statement
     * @param row : The row
     */
    @Override
    public void queryValueAsync(String statement, String row, SqlCallback<Object> callback) {
        if (!checkConnection()) throw new IllegalStateException("Cannot connect into the database!");
        Bukkit.getScheduler().runTaskAsynchronously(getOwningPlugin(), () -> {
            Connection con = getNewConnection();
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                ps = con.prepareStatement(statement);
                rs = ps.executeQuery();
                if (rs.next()) {
                    callback.onSuccess(rs.getObject(row));
                }
            } catch (SQLException ex) {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                    if (rs != null) {
                        rs.close();
                    }
                    if (!sqlType.equals(SQLType.SQL_BASED)) {
                        // Close if not normal sql
                        con.close();
                    }
                    callback.onError(ex);
                } catch (SQLException ex2) {
                    callback.onError(ex2);
                }
            }
            finally {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                    if (!sqlType.equals(SQLType.SQL_BASED)) {
                        // Close if not normal sql
                        if (con != null) {
                            con.close();
                        }
                    }
                }
                catch (SQLException ex2) {
                    callback.onError(ex2);
                }
            }
        });
    }

    /**
     * Query a command to get its value in an async task
     *
     * @param statement : The statement
     * @param toSelect : What row that will be selected
     */
    @Override
    public void queryRowAsync(String statement, String[] toSelect, SqlCallback<List<Object>> callback) {
        if (!checkConnection()) throw new IllegalStateException("Cannot connect into the database!");
        Bukkit.getScheduler().runTaskAsynchronously(getOwningPlugin(), () -> {
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
                callback.onSuccess(values);
            } catch (SQLException ex) {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                    if (rs != null) {
                        rs.close();
                    }
                    if (!sqlType.equals(SQLType.SQL_BASED)) {
                        // Close if not normal sql
                        con.close();
                    }
                    callback.onError(ex);
                } catch (SQLException ex2) {
                    callback.onError(ex2);
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
                    if (!sqlType.equals(SQLType.SQL_BASED)) {
                        // Close if not normal sql
                        if (con != null) {
                            con.close();
                        }
                    }
                }
                catch (SQLException ex2) {
                    callback.onError(ex2);
                }
            }
        });
    }

    /**
     * Query a multiple row to get its values in an async task
     *
     * @param statement : The statement
     * @param row : The rows
     */
    @Override
    public void queryMultipleRowsAsync(String statement, SqlCallback<Map<String, List<Object>>> callback, String... row) {
        if (!checkConnection()) throw new IllegalStateException("Cannot connect into the database!");
        Bukkit.getScheduler().runTaskAsynchronously(getOwningPlugin(), () -> {
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
                callback.onSuccess(map);
            }
            catch (SQLException ex) {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                    if (rs != null) {
                        rs.close();
                    }
                    if (!sqlType.equals(SQLType.SQL_BASED)) {
                        // Close if not normal sql
                        con.close();
                    }
                    callback.onError(ex);
                }
                catch (SQLException ex2) {
                    callback.onError(ex2);
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
                    if (!sqlType.equals(SQLType.SQL_BASED)) {
                        // Close if not normal sql
                        if (con != null) {
                            con.close();
                        }
                    }
                }
                catch (SQLException ex2) {
                    callback.onError(ex2);
                }
            }
        });
    }

    /**
     * Query a command to get its value
     *
     * @param statement : The statement
     * @param row : The row
     * @param throwError : Should the api throw the error if there's any?
     * @return The specified value if there's any, null otherwise
     */
    @Override
    public Object queryValue(String statement, String row, boolean throwError) {
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
            if (throwError) ex.printStackTrace();
            try {
                if (ps != null) {
                    ps.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (!sqlType.equals(SQLType.SQL_BASED)) {
                    // Close if not normal sql
                    con.close();
                }
            } catch (SQLException ex2) {
                if (throwError) ex2.printStackTrace();
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
                if (!sqlType.equals(SQLType.SQL_BASED)) {
                    // Close if not normal sql
                    if (con != null) {
                        con.close();
                    }
                }
            }
            catch (SQLException ex2) {
                if (throwError) ex2.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Query a command to get its value
     *
     * @param statement : The statement
     * @param toSelect : What row that will be selected
     * @param throwError : Should the api throw the error if there's any?
     * @return The specified value as a list if there's any, empty list otherwise
     */
    @Override
    public List<Object> queryRow(String statement, String[] toSelect, boolean throwError) {
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
                if (!sqlType.equals(SQLType.SQL_BASED)) {
                    // Close if not normal sql
                    con.close();
                }
                if (throwError) ex.printStackTrace();
            } catch (SQLException ex2) {
                if (throwError) ex2.printStackTrace();
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
                if (!sqlType.equals(SQLType.SQL_BASED)) {
                    // Close if not normal sql
                    if (con != null) {
                        con.close();
                    }
                }
            }
            catch (SQLException ex2) {
                if (throwError) ex2.printStackTrace();
            }
        }
        return values;
    }

    /**
     * Query a multiple row to get its values
     *
     * @param statement : The statement
     * @param row : The rows
     * @param throwError : Should the api throw the error if there's any?
     * @return a HashMap contained the result values if there's any, empty HashMap otherwise
     */
    @Override
    public Map<String, List<Object>> queryMultipleRow(String statement, boolean throwError, String... row) {
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
                if (!sqlType.equals(SQLType.SQL_BASED)) {
                    // Close if not normal sql
                    con.close();
                }
                if (throwError) ex.printStackTrace();
            }
            catch (SQLException ex2) {
                if (throwError) ex2.printStackTrace();
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
                if (!sqlType.equals(SQLType.SQL_BASED)) {
                    // Close if not normal sql
                    if (con != null) {
                        con.close();
                    }
                }
            }
            catch (SQLException ex2) {
                if (throwError) ex2.printStackTrace();
            }
        }
        return map;
    }

    /**
     * Check if the data exists
     * Usage is : .isExists("custom_nickname", "DrOreo002", "player_settings");
     * @param column : The column
     * @param data : The data
     * @param table : The table
     * @return true if exists, false otherwise
     */
    @Override
    public boolean isExists(String column, String data, String table, boolean throwError) {
        table = "`" + table + "`";
        column = "`" + column + "`";
        PreparedStatement pre = null;
        Connection con = null;
        ResultSet res = null;
        try {
            con = getConnection();
            pre = con.prepareStatement("SELECT * FROM " + table + " WHERE " + column + " = ?");
            pre.setString(1, data);
            res = pre.executeQuery();
            return res.next();
        } catch (Exception e) {
            if (throwError) e.printStackTrace();
        } finally {
            try {
                if (pre != null) {
                    pre.close();
                }
                if (res != null) {
                    res.close();
                }
                if (!sqlType.equals(SQLType.SQL_BASED)) {
                    // Close if not normal sql
                    if (con != null) {
                        con.close();
                    }
                }
            } catch (SQLException e) {
                if (throwError) e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * Check the connection
     *
     * @return true if the connection is not interrupted. False otherwise
     * @throws SQLException : If there's something wrong happened
     */
    @Override
    public boolean checkConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = getNewConnection();
                if (connection == null || connection.isClosed()) return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Get a new connection. Method to get will be different, depend on what type of sql you set
     * 
     * @return a new Connection if its a success, null otherwise
     */
    @Override
    public Connection getNewConnection() {
        File folder = new File(databaseFolder, databaseName + ".db");
        if (!folder.exists()) {
            try {
                if (folder.createNewFile())
                    Debug.log("&fDatabase &b" + databaseName + ".db &fon &e" + folder.getAbsolutePath() + "&f from plugin &e" + getOwningPlugin().getName() + "&f has been created successfully!", true);
            } catch (IOException e) {
                Debug.log("&fFailed to create database file on &b" + folder.getAbsolutePath() + "&f. Plugin &e" + getOwningPlugin().getName() + "&f will now disabling itself!", true);
                Bukkit.getPluginManager().disablePlugin(getOwningPlugin());
                return null;
            }
        }
        switch (sqlType) {
            case SQL_BASED:
                try {
                    if (this.connection != null && !this.connection.isClosed()) {
                        return this.connection;
                    }
                    return this.connection = DriverManager.getConnection("jdbc:sqlite:" + folder);
                } catch (SQLException e) {
                    Debug.log("&fFailed to create database file on &b" + folder.getAbsolutePath() + "&f. Plugin &e" + getOwningPlugin().getName() + "&f will now disabling itself!", true);
                    Bukkit.getPluginManager().disablePlugin(getOwningPlugin());
                    return null;
                }
            case HIKARI_CP:
                poolManager = new ConnectionPoolManager("jdbc:sqlite:" + folder, owningPlugin);
                poolManager.setup();

                try {
                    return poolManager.getConnection();
                } catch (SQLException e) {
                    e.printStackTrace();
                    return null;
                }
            case MARIA_DB:
                // TODO : Make
                return null;
            default:
                return null;
        }
    }
}
