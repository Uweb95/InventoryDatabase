package de.lunarsoftware.inventorydatabase;

import de.lunarsoftware.inventorydatabase.database.MySQL;
import de.lunarsoftware.inventorydatabase.events.PlayerJoin;
import de.lunarsoftware.inventorydatabase.events.PlayerLeave;
import de.lunarsoftware.inventorydatabase.sync.Sync;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class InventoryDatabase extends JavaPlugin {
    private static Logger logger;
    private static InventoryDatabase instance;
    private MySQL database;
    private Sync sync;

    @Override
    public void onEnable() {
        instance = this;
        logger = getLogger();
        Configuration.loadConfig();
        database = new MySQL();
        sync = new Sync();

        //Register Listeners
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new PlayerJoin(), this);
        pluginManager.registerEvents(new PlayerLeave(), this);
    }

    public static Logger logger() {
        return logger;
    }

    public static InventoryDatabase getInstance() {
        return instance;
    }

    public MySQL getDatabase() {
        return database;
    }

    public Sync getSync() {
        return sync;
    }
}
