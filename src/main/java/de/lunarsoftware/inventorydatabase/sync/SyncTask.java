package de.lunarsoftware.inventorydatabase.sync;

import de.lunarsoftware.inventorydatabase.InventoryDatabase;
import org.bukkit.Bukkit;

public class SyncTask {
    private final long saveInterval;

    public SyncTask() {
        saveInterval = InventoryDatabase.getInstance().getConfig().getLong("general.saveInterval", 0) * 60 * 20;

        // Only activate task when saveInterval is set higher than 0
        if (saveInterval > 0) runTask();
    }

    private void runTask() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(InventoryDatabase.getInstance(), () -> InventoryDatabase.getInstance().getSync().saveOnlinePlayers(), saveInterval, saveInterval);
    }
}
