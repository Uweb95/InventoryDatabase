package de.lunarsoftware.inventorydatabase.sync;

import de.lunarsoftware.inventorydatabase.InventoryDatabase;
import de.lunarsoftware.inventorydatabase.database.PlayerInventory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Sync {
    private final Set<Player> currentlySyncingPlayers = new HashSet<>();
    private final boolean enableDebug;
    private final boolean inventorySync;
    private final boolean armorSync;

    private final SyncTask syncTask;

    public Sync() {
        enableDebug = InventoryDatabase.getInstance().getConfig().getBoolean("debug.syncMessages", false);
        inventorySync = InventoryDatabase.getInstance().getConfig().getBoolean("general.syncInventory", false);
        armorSync = InventoryDatabase.getInstance().getConfig().getBoolean("general.syncArmor", false);

        syncTask = new SyncTask();
    }

    public void loadPlayerInventory(Player player) {
        if (currentlySyncingPlayers.contains(player)) return;
        currentlySyncingPlayers.add(player);
        PlayerInventory playerInventory = PlayerInventory.getInventory(player);

        if (playerInventory == null) {
            PlayerInventory.createInventory(player);
            saveData(player);
        } else {
            loadData(player);
        }
        currentlySyncingPlayers.remove(player);
    }

    public void savePlayerInventory(Player player) {
        if (currentlySyncingPlayers.contains(player)) return;
        currentlySyncingPlayers.add(player);

        PlayerInventory playerInventory = PlayerInventory.getInventory(player);
        if (playerInventory == null) PlayerInventory.createInventory(player);

        saveData(player);

        currentlySyncingPlayers.remove(player);
    }

    public void saveData(final Player player) {
        saveData(player, player.getInventory().getContents(), player.getInventory().getArmorContents());
    }

    public void saveData(final Player player, final ItemStack[] inventory, final ItemStack[] armor) {
        if (enableDebug) InventoryDatabase.logger().info("Start saving inventory of player " + player.getName());
        if (!inventorySync && !armorSync) return;

        String b64Inventory = itemsToBase64(inventory);
        String b64Armor = itemsToBase64(armor);

        PlayerInventory playerInventory;
        playerInventory = PlayerInventory.getInventory(player);
        if (playerInventory == null) playerInventory = PlayerInventory.createInventory(player);

        if (b64Inventory != null && b64Armor != null && playerInventory != null) {
            if (inventorySync) playerInventory.inventory = b64Inventory;
            if (armorSync) playerInventory.armor = b64Armor;
            playerInventory.save();
            if (enableDebug) InventoryDatabase.logger().info("Inventory of player " + player.getName() + " saved!");
        } else {
            InventoryDatabase.logger().warning("Can't save Data from player " + player.getName());
        }
    }

    public void saveOnlinePlayers() {
        if (enableDebug) InventoryDatabase.logger().info("Start saving task");
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());

        if (players.size() == 0) {
            if (enableDebug) InventoryDatabase.logger().info("No players online to save.");
            return;
        }

        for (Player player : players) {
            if (player.isOnline()) InventoryDatabase.getInstance().getSync().savePlayerInventory(player);
        }

        if (enableDebug) InventoryDatabase.logger().info("Inventory of " + players.size() + " players saved.");
    }

    public void loadData(final Player player) {
        PlayerInventory playerInventory = PlayerInventory.getInventory(player);
        if (!inventorySync && !armorSync) return;

        if (playerInventory == null) return;

        if (inventorySync) {
            ItemStack[] newInventory = base64ToItems(playerInventory.inventory);
            if (newInventory != null) player.getInventory().setContents(newInventory);
        }

        if (armorSync) {
            ItemStack[] newArmor = base64ToItems(playerInventory.armor);
            if (newArmor != null) player.getInventory().setArmorContents(newArmor);
        }
    }

    private String itemsToBase64(final ItemStack[] items) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            dataOutput.writeInt(items.length);
            for (ItemStack item : items) dataOutput.writeObject(item);
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (IOException e) {
            InventoryDatabase.logger().warning("Can't encode itemStack: " + e.getMessage());
        }

        return null;
    }

    private ItemStack[] base64ToItems(final String base64) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(base64));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack[] items = new ItemStack[dataInput.readInt()];

            for (int i = 0; i < items.length; i++) items[i] = (ItemStack) dataInput.readObject();
            dataInput.close();
            return items;
        } catch (Exception e) {
            InventoryDatabase.logger().warning("Can't decode itemStack: " + e.getMessage());
        }

        return null;
    }
}
