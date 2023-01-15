package de.lunarsoftware.inventorydatabase.database;

import de.lunarsoftware.inventorydatabase.InventoryDatabase;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

public class MySQL {
    private Connection conn = null;
    public static boolean databaseAvailable = false;

    private final static String tableName = "player_inventories";

    public MySQL() {
        conn = connect();

        if (conn == null) {
            InventoryDatabase.logger().severe("Can't connect to database. Plugin will not work!");
        } else {
            databaseAvailable = true;
            initializeDatabase();
        }
    }

    public void disconnect() {
        if (!databaseAvailable) return;

        try {
            conn.close();
        } catch (SQLException e) {
            InventoryDatabase.logger().warning("Can't close database connection: " + e.getMessage());
        }

        databaseAvailable = false;
    }

    public static String getTableName() {
        return tableName;
    }

    public Connection getConnection() {
        return conn;
    }

    private Connection connect() {
        FileConfiguration config = InventoryDatabase.getInstance().getConfig();

        String url = "jdbc:mysql://" +
                config.getString("database.host", "127.0.0.1") + ":" +
                config.getString("database.port", "3306") + "/" +
                config.getString("database.database", "minecraft") +
                "?user=" + config.getString("database.username", "minecraft") +
                "&password=" + config.getString("database.password", "password");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(url);
        } catch (ClassNotFoundException e) {
            InventoryDatabase.logger().severe("Could not find mysql driver: " + e.getMessage());
        } catch (Exception e) {
            InventoryDatabase.logger().severe("Can't connect to database: " + e.getMessage());
        }

        return null;
    }

    private void initializeDatabase() {
        if (!databaseAvailable) return;
        PreparedStatement stmt = null;

        try {
            String query = "CREATE TABLE IF NOT EXISTS `" + tableName + "` (" +
                    "id int(10) AUTO_INCREMENT," +
                    "player_uuid char(36) NOT NULL UNIQUE," +
                    "player_name varchar(32) NOT NULL," +
                    "player_last_seen char(13) NOT NULL," +
                    "inventory LONGTEXT NOT NULL," +
                    "armor LONGTEXT NOT NULL," +
                    "PRIMARY KEY(id)" +
                    ") CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;";
            stmt = conn.prepareStatement(query);
            stmt.execute();
        } catch (SQLException e) {
            InventoryDatabase.logger().severe("Can't create table: " + e.getMessage());
            databaseAvailable = false;
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                InventoryDatabase.logger().warning("Can't close database connection: " + e.getMessage());
            }
        }
    }
}
