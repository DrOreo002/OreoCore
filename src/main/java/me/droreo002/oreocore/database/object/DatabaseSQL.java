package me.droreo002.oreocore.database.object;

import lombok.Getter;
import me.droreo002.oreocore.database.Database;
import me.droreo002.oreocore.database.DatabaseType;
import me.droreo002.oreocore.database.SQLDatabase;
import me.droreo002.oreocore.database.SQLType;
import me.droreo002.oreocore.database.utils.ConnectionPoolManager;
import me.droreo002.oreocore.debugging.Debug;
import me.droreo002.oreocore.utils.misc.ThreadingUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

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
    @Getter
    private File databaseFile;

    public DatabaseSQL(JavaPlugin plugin, String databaseName, File databaseFolder, SQLType sqlType) {
        super(DatabaseType.SQL, plugin);
        this.databaseName = databaseName.replace(".db", "");
        this.databaseFolder = databaseFolder;
        this.sqlType = sqlType;
        if (!databaseFolder.exists()) databaseFolder.mkdir();
        init();
    }

    @Override
    public void init() {
        databaseFile = new File(databaseFolder, databaseName + ".db");
        if (!databaseFile.exists()) {
            try {
                if (databaseFile.createNewFile())
                    Debug.log("&fDatabase &b" + databaseName + ".db &fon &e" + databaseFile.getAbsolutePath() + "&f from plugin &e" + getOwningPlugin().getName() + "&f has been created successfully!", true);
            } catch (IOException e) {
                Debug.log("&fFailed to create database file on &b" + databaseFile.getAbsolutePath() + "&f. Plugin &e" + getOwningPlugin().getName() + "&f will now disabling itself!", true);
                Bukkit.getPluginManager().disablePlugin(getOwningPlugin());
                return;
            }
        }
        
        poolManager = new ConnectionPoolManager("jdbc:sqlite:" + databaseFile, owningPlugin);
        poolManager.setup();
        
        if (checkConnection()) {
            if (execute(getFirstCommand())) {
                Debug.log("&eSQL &fConnection for plugin &c" + getOwningPlugin().getName() + "&f has been created!. Database will now be stored at &e" + databaseFolder.getAbsolutePath() + "\\" + databaseName + ".db &f, database type is currently &e" + sqlType, true);
            } else {
                Debug.log("&cFailed to initialize the SQL connection on plugin &e" + getOwningPlugin().getName() + "&c Please contact the dev!");
            }
        } else {
            throw new IllegalStateException("SQL Connection for plugin " + getOwningPlugin().getName() + " cannot be proceeded!, please contact the dev!");
        }
    }

    @Override
    public void onDisable() {
        try {
            close();
            Debug.log("&fDatabase &bSQL &ffrom plugin &e" + owningPlugin.getName() + "&f has been disabled!", true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public abstract void loadData();
    public abstract String getFirstCommand();

    /*
    Non Async
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

    @Override
    public ResultSet query(String sql) {
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
                if (!sqlType.equals(SQLType.SQL_BASED)) {
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

    @Override
    public boolean execute(String sql) {
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
                if (!sqlType.equals(SQLType.SQL_BASED)) {
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

    @Override
    public boolean isExists(String column, String data, String table) {
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
                if (!sqlType.equals(SQLType.SQL_BASED)) {
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

    @Override
    public Object queryValue(String statement, String row) {
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
                if (!sqlType.equals(SQLType.SQL_BASED)) {
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
                if (!sqlType.equals(SQLType.SQL_BASED)) {
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

    @Override
    public List<Object> queryRow(String statement, String... toSelect) {
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
                if (!sqlType.equals(SQLType.SQL_BASED)) {
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

    @Override
    public Map<String, List<Object>> queryMultipleRow(String statement, String... row) {
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
                if (!sqlType.equals(SQLType.SQL_BASED)) {
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

    /*
    Async!
     */

    @Override
    public Future<Boolean> executeAsync(String sql) {
        if (!checkConnection()) throw new IllegalStateException("Cannot connect into the database!");
        return ThreadingUtils.makeFuture(() -> execute(sql));
    }

    @Override
    public Future<Object> queryValueAsync(String statement, String row) {
        if (!checkConnection()) throw new IllegalStateException("Cannot connect into the database!");
        return ThreadingUtils.makeFuture(() -> queryValue(statement, row));
    }

    @Override
    public Future<Object> queryRowAsync(String statement, String... toSelect) {
        if (!checkConnection()) throw new IllegalStateException("Cannot connect into the database!");
        return ThreadingUtils.makeFuture(() -> queryRow(statement, toSelect));
    }

    @Override
    public Future<Map<String, List<Object>>> queryMultipleRowsAsync(String statement, String... row) {
        if (!checkConnection()) throw new IllegalStateException("Cannot connect into the database!");
        return ThreadingUtils.makeFuture(() -> queryMultipleRow(statement, row));
    }

    @Override
    public Future<Boolean> isExistsAsync(String column, String data, String table) {
        if (!checkConnection()) throw new IllegalStateException("Cannot connect into the database!");
        return ThreadingUtils.makeFuture(() -> isExists(column, data, table));
    }
    
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
    
    @Override
    public Connection getNewConnection() {
        switch (sqlType) {
            case SQL_BASED:
                try {
                    if (this.connection != null && !this.connection.isClosed()) {
                        return this.connection;
                    }
                    return this.connection = DriverManager.getConnection("jdbc:sqlite:" + databaseFile);
                } catch (SQLException e) {
                    Debug.log("&fFailed to create database file on &b" + databaseFile.getAbsolutePath() + "&f. Plugin &e" + getOwningPlugin().getName() + "&f will now disabling itself!", true);
                    Bukkit.getPluginManager().disablePlugin(getOwningPlugin());
                    return null;
                }
            case HIKARI_CP:
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
