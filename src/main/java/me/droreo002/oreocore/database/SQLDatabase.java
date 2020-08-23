package me.droreo002.oreocore.database;

import lombok.Getter;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import me.droreo002.oreocore.database.utils.HikariConnectionPool;
import me.droreo002.oreocore.database.utils.SQLConfiguration;
import me.droreo002.oreocore.database.utils.SQLTableBuilder;
import me.droreo002.oreocore.utils.misc.ThreadingUtils;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.function.Function;

public abstract class SQLDatabase extends Database {

    @Getter
    private HikariConnectionPool connectionPool;
    @Getter
    private SQLConfiguration configuration;
    @Getter
    @Nullable
    private SQLTableBuilder sqlTableBuilder;

    public SQLDatabase(@NotNull JavaPlugin owningPlugin, @NotNull DatabaseType databaseType, @NotNull SQLConfiguration configuration) {
        this(owningPlugin, databaseType, configuration, null);
    }

    public SQLDatabase(@NotNull JavaPlugin owningPlugin, @NotNull DatabaseType databaseType, @NotNull SQLConfiguration configuration, @Nullable SQLTableBuilder sqlTableBuilder) {
        super(owningPlugin, databaseType);
        this.configuration = configuration;
        this.sqlTableBuilder = sqlTableBuilder;
        init();
    }

    @Override
    public void init() {
        this.connectionPool = new HikariConnectionPool(owningPlugin, databaseType, configuration);
        try {
            getConnection();
            if (this.sqlTableBuilder != null) executeUpdate(this.sqlTableBuilder.build());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Try to close the connection
     */
    public void close() {
        this.connectionPool.getDataSource().close();
    }

    /**
     * Execute a batch sql command
     *
     * @param statements The sql command to execute
     * @return Total changes / affected when execution. Separated by each rows
     * @throws SQLException If something goes wrong
     */
    public int[] executeBatch(@NotNull String... statements) throws SQLException {
        Connection con = getConnection();
        Statement statement = con.createStatement();
        for (String sql : statements) {
            sql = getStatementProcessor().apply(sql);
            statement.addBatch(sql);
        }
        int[] result = statement.executeBatch();
        statement.close();
        con.close();

        return result;
    }

    /**
     * Execute a update sql command. Ex: INSERT, UPDATE, DELETE
     *
     * @param sql The sql command
     * @return Total changes / affected when the execution
     * @throws SQLException If something goes wrong
     */
    public int executeUpdate(@NotNull String sql) throws SQLException {
        sql = getStatementProcessor().apply(sql);
        Connection con = getConnection();
        PreparedStatement statement = con.prepareStatement(sql);
        int count = statement.executeUpdate();

        statement.close();
        con.close();

        return count;
    }

    /**
     * Execute a query sql command. Ex: SELECT
     *
     * @param sql The sql command
     * @return a new ResultSet class if succeeded, null otherwise
     * @throws SQLException If something goes wrong
     */
    @NotNull
    public ResultSet executeQuery(@NotNull String sql) throws SQLException {
        sql = getStatementProcessor().apply(sql);
        Connection con = getConnection();
        PreparedStatement statement = con.prepareStatement(sql);
        ResultSet resultSet = statement.executeQuery();

        statement.close();
        con.close();

        return resultSet;
    }

    /**
     * Execute a new SQL command via Async way
     *
     * @param sql The sql command
     * @return a new ResultSet class if succeeded, null otherwise
     */
    @NotNull
    public Future<ResultSet> executeQueryAsync(@NotNull String sql) {
        return ThreadingUtils.makeFuture(() -> executeQuery(sql));
    }

    /**
     * Query a command to get its value via Async way
     *
     * @param statement The statement
     * @param row       The row
     */
    @NotNull
    public Future<Object> queryValueAsync(@NotNull String statement, @NotNull String row) {
        return ThreadingUtils.makeFuture(() -> queryValue(statement, row));
    }

    /**
     * Query a command to get its value via Async way
     *
     * @param statement The statement
     * @param toSelect  What row that will be selected
     */
    @NotNull
    public Future<Object> queryRowAsync(@NotNull String statement, @NotNull String... toSelect) {
        return ThreadingUtils.makeFuture(() -> queryRow(statement, toSelect));
    }

    /**
     * Query a multiple row to get its values via Async way
     *
     * @param statement The statement
     * @param row       The rows
     */
    public Future<Multimap<String, Object>> queryMultipleRowsAsync(@NotNull String statement, @NotNull String... row) {
        return ThreadingUtils.makeFuture(() -> queryMultipleRow(statement, row));
    }

    /**
     * Check if the data exists via Async way
     *
     * @param column The column
     * @param data   The data
     * @param table  The table
     */
    @NotNull
    public Future<Boolean> isExistsAsync(@NotNull String column, @NotNull String data, @NotNull String table) {
        return ThreadingUtils.makeFuture(() -> isExists(column, data, table));
    }

    /**
     * Query a command to get its value
     *
     * @param statement The statement
     * @param row       The row
     * @return The specified value if there's any, null otherwise
     */
    @Nullable
    public Object queryValue(@NotNull String statement, @NotNull String row) throws SQLException {
        try (ResultSet resultSet = executeQuery(statement)) {
            return (!resultSet.wasNull()) ? resultSet.getObject(row) : null;
        }
    }

    /**
     * Query a command to get its value
     *
     * @param statement The statement
     * @param toSelect  Row to select
     * @return The specified value as a list if there's any, empty list otherwise
     */
    @NotNull
    public List<Object> queryRow(@NotNull String statement, @NotNull String... toSelect) throws SQLException {
        try (ResultSet resultSet = executeQuery(statement)) {
            List<Object> values = new ArrayList<>();
            while (resultSet.next()) {
                for (String s : toSelect) {
                    values.add(resultSet.getObject(s));
                }
            }
            return values;
        }
    }

    /**
     * Query a multiple rows to get its values
     *
     * @param statement The statement
     * @param rows      The rows to get
     * @return a HashMap contained the result values if there's any, empty HashMap otherwise
     */
    @NotNull
    public Multimap<String, Object> queryMultipleRow(@NotNull String statement, @NotNull String... rows) throws SQLException {
        try (ResultSet resultSet = executeQuery(statement)) {
            Multimap<String, Object> multimap = ArrayListMultimap.create();
            while (resultSet.next()) {
                for (String row : rows) {
                    multimap.put(row, resultSet.getObject(row));
                }
            }
            return multimap;
        }
    }

    /**
     * Check if the data exists
     *
     * @param column The column
     * @param data   The data
     * @param table  The table
     * @return true if exists, false otherwise
     */
    public boolean isExists(@NotNull String column, @NotNull String data, @NotNull String table) throws SQLException {
        try (ResultSet resultSet = executeQuery(String.format("SELECT * FROM `" + table + "` WHERE `" + column + "` = `%s`;", data))) {
            return resultSet.next();
        }
    }

    /**
     * Get the connection for this sql database
     */
    @NotNull
    public Connection getConnection() throws SQLException {
        if (this.connectionPool == null) throw new NullPointerException("Hikari connection pool is not set!");
        return this.connectionPool.getConnection();
    }

    /**
     * Get the statement processor
     *
     * @return Statement processor function
     */
    @NotNull
    public Function<String, String> getStatementProcessor() {
        return (this.databaseType == DatabaseType.MYSQL) ? s -> s.replace("'", "`") : s -> s;
    }
}
