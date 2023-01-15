package de.lunarsoftware.inventorydatabase.database;

import de.lunarsoftware.inventorydatabase.InventoryDatabase;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerInventory {
    public int id;
    public String player_uuid;
    public String player_name;
    public String player_last_seen;
    public String inventory;
    public String armor;

    PlayerInventory(String player_uuid, String player_name, String player_last_seen, String inventory, String armor) {
        this.player_uuid = player_uuid;
        this.player_name = player_name;
        this.player_last_seen = player_last_seen;
        this.inventory = inventory;
        this.armor = armor;
    }

    public static PlayerInventory getInventory(Player player) {
        if (!MySQL.databaseAvailable) return null;

        PreparedStatement stmt = null;
        ResultSet resultSet = null;
        Connection conn = InventoryDatabase.getInstance().getDatabase().getConnection();
        PlayerInventory result = null;

        try {
            String query = "SELECT * FROM `" + MySQL.getTableName() + "` WHERE `player_uuid` = ?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, player.getUniqueId().toString());
            resultSet = stmt.executeQuery();

            while (resultSet.next()) {
                result = new PlayerInventory(
                        resultSet.getString("player_uuid"),
                        resultSet.getString("player_name"),
                        resultSet.getString("player_last_seen"),
                        resultSet.getString("inventory"),
                        resultSet.getString("armor")
                );
                result.id = resultSet.getInt("id");
            }
        } catch (SQLException e) {
            InventoryDatabase.logger().severe("Can't get inventory for player with uuid " + player.getUniqueId() + ": " + e.getMessage());
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                InventoryDatabase.logger().warning("Can't close database connection: " + e.getMessage());
            }
        }

        return result;
    }

    public static PlayerInventory createInventory(Player player) {
        if (!MySQL.databaseAvailable) return null;

        PreparedStatement stmt = null;
        Connection conn = InventoryDatabase.getInstance().getDatabase().getConnection();

        try {
            String query = "INSERT INTO `" + MySQL.getTableName() + "` " +
                    "(`player_uuid`, `player_name`, `player_last_seen`, `inventory`, `armor`) " +
                    "VALUES(?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, player.getUniqueId().toString());
            stmt.setString(2, player.getName());
            stmt.setString(3, String.valueOf(System.currentTimeMillis()));
            stmt.setString(4, "empty");
            stmt.setString(5, "empty");
            stmt.executeUpdate();
        } catch (SQLException e) {
            InventoryDatabase.logger().severe("Can't get inventory for player with uuid " + player.getUniqueId() + ": " + e.getMessage());
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                InventoryDatabase.logger().warning("Can't close database connection: " + e.getMessage());
            }
        }

        return getInventory(player);
    }

    public void save() {
        if (!MySQL.databaseAvailable) return;

        PreparedStatement stmt = null;
        Connection conn = InventoryDatabase.getInstance().getDatabase().getConnection();

        try {
            String query = "UPDATE `" + MySQL.getTableName() + "` SET " +
                    "`player_uuid` = ?, " +
                    "`player_name` = ?, " +
                    "`player_last_seen` = ?, " +
                    "`inventory` = ?, " +
                    "`armor` = ?" +
                    "WHERE `id` = ?";

            stmt = conn.prepareStatement(query);
            stmt.setString(1, player_uuid);
            stmt.setString(2, player_name);
            stmt.setString(3, String.valueOf(System.currentTimeMillis()));
            stmt.setString(4, inventory);
            stmt.setString(5, armor);
            stmt.setInt(6, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            InventoryDatabase.logger().severe("Can't save inventory with id " + id + ": " + e.getMessage());
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                InventoryDatabase.logger().warning("Can't close database connection: " + e.getMessage());
            }
        }
    }
}
