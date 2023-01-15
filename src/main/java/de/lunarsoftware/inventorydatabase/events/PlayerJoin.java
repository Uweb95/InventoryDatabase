package de.lunarsoftware.inventorydatabase.events;

import de.lunarsoftware.inventorydatabase.InventoryDatabase;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener {

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        Player player = event.getPlayer();

        Bukkit.getScheduler().runTaskLaterAsynchronously(InventoryDatabase.getInstance(), () -> {
            if (player == null || !player.isOnline()) return;
            InventoryDatabase.getInstance().getSync().loadPlayerInventory(player);
        }, 5L);
    }
}
