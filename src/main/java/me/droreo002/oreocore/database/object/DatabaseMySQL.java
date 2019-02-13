package me.droreo002.oreocore.database.object;

import lombok.Getter;
import me.droreo002.oreocore.database.Database;
import me.droreo002.oreocore.database.DatabaseType;
import me.droreo002.oreocore.database.object.interfaces.SqlCallback;
import me.droreo002.oreocore.database.utils.MySqlConnection;
import me.droreo002.oreocore.utils.logging.Debug;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class DatabaseMySQL extends Database {

    @Getter
    private final MySqlConnection addressData;
    @Getter
    private final int updateTimeSecond;
    @Getter
    private Connection connection;
    @Getter
    private int connectionCheckerTaskID;

    public DatabaseMySQL(JavaPlugin plugin, MySqlConnection addressData, int updateTimeSecond) {
        super(DatabaseType.MYSQL, plugin);
        this.addressData = addressData;
        this.updateTimeSecond = updateTimeSecond;
        init();
        loadData();
    }

    @Override
    public void init() {
        if (checkConnection()) {
            if (execute(getFirstCommand(), true)) {
                Debug.log("&bMySQL Connection for plugin &c" + getPlugin().getName() + "&f has been created!. Data address is &e" + addressData.getHost() + ":" + addressData.getPort() + "&f data is currently stored at &e" + addressData.getDatabaseName() + " &fdatabase", true);
            } else {
                Debug.log("&cFailed to initialize the &bMySQL&f connection on plugin &e" + getPlugin().getName() + "&c Please contact the dev!");
            }
        } else {
            throw new IllegalStateException("MySQL Connection for plugin " + getPlugin().getName() + " cannot be proceeded!, please contact the dev!");
        }
        this.connectionCheckerTaskID = new CheckConnection().runTaskTimerAsynchronously(getPlugin(), updateTimeSecond * 20, updateTimeSecond * 20).getTaskId();
    }

    public abstract void loadData();
    public abstract String getFirstCommand();

    /**
     * Try to close the connection
     *
     * @throws SQLException : If the connection cannot be closed
     * @throws NullPointerException : If the data connection is currently null
     */
    public void close() throws SQLException {
        if (connection != null) {
            connection.close();
        } else {
            throw new NullPointerException("Cannot close while the data connection is null!");
        }
    }

    /**
     * Execute a new SQL Command into the connection on a async thread
     *
     * @param sql : The sql command
     */
    public void executeAsync(String sql, SqlCallback<Boolean> callback) {
        if (!checkConnection()) throw new IllegalStateException("Cannot connect into the database!");
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            Connection con = null;
            Statement statement = null;
            try {
                con = connection;
                statement = con.createStatement();
                statement.execute(sql);
                callback.onSuccess(true);
            } catch (SQLException e) {
                callback.onError(e);
            } finally {
                try {
                    if (statement != null) {
                        statement.close();
                    }
                    if (con != null) {
                        con.close();
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
    public boolean execute(String sql, boolean throwError) {
        if (!checkConnection()) throw new IllegalStateException("Cannot connect into the database!");
        Connection con = null;
        Statement statement = null;
        try {
            con = connection;
            statement = con.createStatement();
            statement.execute(sql);
            return true;
        } catch (SQLException e) {
            if (throwError) e.printStackTrace();
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (con != null) {
                    con.close();
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
    public void queryValueAsync(String statement, String row, SqlCallback<Object> callback) {
        if (!checkConnection()) throw new IllegalStateException("Cannot connect into the database!");
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            PreparedStatement ps = null;
            ResultSet rs;
            try {
                ps = connection.prepareStatement(statement);
                rs = ps.executeQuery();
                if (rs.next()) {
                    callback.onSuccess(rs.getObject(row));
                }
            } catch (SQLException ex) {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                    if (connection != null) {
                        connection.close();
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
                    if (connection != null) {
                        connection.close();
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
     * @param row : The row
     */
    public void queryRowAsync(String statement, String row, SqlCallback<List<Object>> callback) {
        if (!checkConnection()) throw new IllegalStateException("Cannot connect into the database!");
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            PreparedStatement ps = null;
            ResultSet rs;
            List<Object> values = new ArrayList<>();
            try {
                ps = connection.prepareStatement(statement);
                rs = ps.executeQuery();
                while (rs.next()) {
                    values.add(rs.getObject(row));
                }
                callback.onSuccess(values);
            } catch (SQLException ex) {
                try {
                    if (ps != null) {
                        ps.close();
                    }
                    if (connection != null) {
                        connection.close();
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
                    if (connection != null) {
                        connection.close();
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
    public void queryMultipleRowsAsync(String statement, SqlCallback<Map<String, List<Object>>> callback, String... row) {
        if (!checkConnection()) throw new IllegalStateException("Cannot connect into the database!");
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
            PreparedStatement ps = null;
            ResultSet rs;
            final List<Object> objects = new ArrayList<>();
            final Map<String, List<Object>> map = new HashMap<>();
            try {
                ps = connection.prepareStatement(statement);
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
                    if (connection != null) {
                        connection.close();
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
                    if (connection != null) {
                        connection.close();
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
    public Object queryValue(String statement, String row, boolean throwError) {
        if (!checkConnection()) throw new IllegalStateException("Cannot connect into the database!");
        PreparedStatement ps = null;
        ResultSet rs;
        try {
            ps = connection.prepareStatement(statement);
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
                if (connection != null) {
                    connection.close();
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
                if (connection != null) {
                    connection.close();
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
     * @param row : The row
     * @param throwError : Should the api throw the error if there's any?
     * @return The specified value as a list if there's any, empty list otherwise
     */
    public List<Object> queryRow(String statement, String row, boolean throwError) {
        if (!checkConnection()) throw new IllegalStateException("Cannot connect into the database!");
        PreparedStatement ps = null;
        ResultSet rs;
        List<Object> values = new ArrayList<>();
        try {
            ps = connection.prepareStatement(statement);
            rs = ps.executeQuery();
            while (rs.next()) {
                values.add(rs.getObject(row));
            }
            return values;
        } catch (SQLException ex) {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (connection != null) {
                    connection.close();
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
                if (connection != null) {
                    connection.close();
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
    public Map<String, List<Object>> queryMultipleRow(String statement, boolean throwError, String... row) {
        if (!checkConnection()) throw new IllegalStateException("Cannot connect into the database!");
        PreparedStatement ps = null;
        ResultSet rs;
        final List<Object> objects = new ArrayList<>();
        final Map<String, List<Object>> map = new HashMap<>();
        try {
            ps = connection.prepareStatement(statement);
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
                if (connection != null) {
                    connection.close();
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
                if (connection != null) {
                    connection.close();
                }
            }
            catch (SQLException ex2) {
                if (throwError) ex2.printStackTrace();
            }
        }
        return map;
    }

    /**
     * Check the connection
     *
     * @return true if the connection is not interrupted. False otherwise
     * @throws SQLException : If there's something wrong happened
     */
    private boolean checkConnection() {
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
     * Get a new connection
     *
     * @return a new Connection if its a success, null otherwise
     */
    private Connection getNewConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver");

            String url = "jdbc:mysql://" + addressData.getHost() + ":" + addressData.getPort() + "/" + addressData.getDatabaseName();
            return DriverManager.getConnection(url, addressData.getUser(), addressData.getPassword());
        } catch (ClassNotFoundException | SQLException e) {
            // Handle possible Exception where connection can not be established
            e.printStackTrace();
            return null;
        }
    }

    // To ping the MySql Connection
    private class CheckConnection extends BukkitRunnable {

        private CheckConnection() {
            Debug.log("&bMySQL Connection &fchecker for plugin &b" + getPlugin().getName() + "&f has been started!", true);
        }

        @Override
        public void run() {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.createStatement().execute("SELECT 1"); // Dummy statement
                }
            } catch (SQLException e) {
                connection = getNewConnection();
            }
        }
    }
}
