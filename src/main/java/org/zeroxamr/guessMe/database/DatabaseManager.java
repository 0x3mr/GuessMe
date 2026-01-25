package org.zeroxamr.guessMe.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.*;

public class DatabaseManager {
    private final JavaPlugin plugin;
    private HikariDataSource dataSource;

    public DatabaseManager(JavaPlugin plugin) {
        this.plugin = plugin;

        connectToDatabase();
        databaseProtocol();
    }

    public boolean isOnline() {
        if (dataSource == null || dataSource.isClosed()) {
            return false;
        }

        try (Connection con = dataSource.getConnection()) {
            return true; // DB reachable
        } catch (SQLException e) {
            return false;
        }
    }


    public void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            plugin.getLogger().info("HikariCP pool closed.");
        }
    }


    private void databaseProtocol() {
        if (isOnline()) {
            if (!doTablesExist()) {
                createDatabaseTables();
            } else {
                printExistingTables();
            }
        } else {
            plugin.getLogger().info("Failed database setup while database is offline!");
        }
    }

    private void createDatabaseTables() {
        try (Connection con = dataSource.getConnection();
            Statement statement = con.createStatement()) {

            statement.execute("PRAGMA journal_mode=WAL;");

            statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS Arenas (
                    gameMapName TEXT PRIMARY KEY,
                    gameAdmin TEXT NOT NULL,
                    gameMaxPlayers INTEGER NOT NULL,
                    gameMapSchematic TEXT DEFAULT NULL,
                    playerLocations TEXT DEFAULT NULL,
                    waitingLocation TEXT DEFAULT NULL
                )
            """);

            statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS Statistics (
                    playerUUID TEXT PRIMARY KEY,
                    playerName TEXT NOT NULL,
                    correctGuesses INTEGER DEFAULT 0 CHECK(correctGuesses >= 0),
                    gamesPlayed INTEGER DEFAULT 0 CHECK(gamesPlayed >= 0),
                    wins INTEGER DEFAULT 0 CHECK(wins >= 0),
                    losses INTEGER DEFAULT 0 CHECK(losses >= 0)
                )
            """);

            plugin.getLogger().info("Database tables created/verified successfully.");
            plugin.getLogger().info("List of tables after creation:");
            printExistingTables();
        } catch (SQLException error) {
            plugin.getLogger().log(
                    java.util.logging.Level.SEVERE,
                    "Failed to create database tables",
                    error
            );
        }
    }

    private void printExistingTables() {
        try (Connection con = dataSource.getConnection()) {
            DatabaseMetaData meta = con.getMetaData();

            try (ResultSet tables = meta.getTables(null, null, "%", new String[]{"TABLE"})) {
                while (tables.next()) {
                    String tableName = tables.getString("TABLE_NAME");
                    plugin.getLogger().info("Table: " + tableName);
                }
            }

        } catch (SQLException error) {
            plugin.getLogger().log(
                    java.util.logging.Level.SEVERE,
                    "Failed to list existing database tables",
                    error
            );
        }
    }

    private void connectToDatabase() {
        File folder = plugin.getDataFolder();
        if (!folder.exists() && !folder.mkdirs()) return;

        File database = new File(folder, "database.db");

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:sqlite:" + database.getAbsolutePath());
        config.setMaximumPoolSize(2);
        config.setPoolName("GuessMe-SQLite");
        config.setConnectionInitSql("PRAGMA journal_mode=WAL;");

        dataSource = new HikariDataSource(config);

        plugin.getLogger().info("Database initialized using HikariCP pool.");
        plugin.getLogger().info("SQLite loaded to: " + database.getAbsolutePath());
    }

    public boolean doTablesExist() {
        String sql = """
            SELECT name
            FROM sqlite_master
            WHERE type='table'
              AND name NOT LIKE 'sqlite_%';
        """;

        try (Connection con = dataSource.getConnection();
             Statement statement = con.createStatement();
             ResultSet result = statement.executeQuery(sql)) {

            return result.next();

        } catch (SQLException error) {
            plugin.getLogger().log(
                    java.util.logging.Level.SEVERE,
                    "Failed to check if tables exist",
                    error
            );
            return false;
        }
    }

    public HikariDataSource getDataSource() { return dataSource; }
}
