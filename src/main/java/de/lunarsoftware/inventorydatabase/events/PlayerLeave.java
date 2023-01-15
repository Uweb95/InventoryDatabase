package de.lunarsoftware.inventorydatabase.events;

import de.lunarsoftware.inventorydatabase.InventoryDatabase;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerLeave implements Listener {
    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        Player player = event.getPlayer();

        Bukkit.getScheduler().runTaskLaterAsynchronously(InventoryDatabase.getInstance(), () -> {
            InventoryDatabase.getInstance().getSync().savePlayerInventory(player);
        }, 2L);
    }

    @EventHandler
    public void onPlayerKick(final PlayerKickEvent event) {
        Player player = event.getPlayer();

        Bukkit.getScheduler().runTaskLaterAsynchronously(InventoryDatabase.getInstance(), () -> {
            InventoryDatabase.getInstance().getSync().savePlayerInventory(player);
        }, 2L);
    }
}
