package de.lunarsoftware.inventorydatabase;

import java.io.File;

public class Configuration {
    public static void loadConfig() {
        InventoryDatabase plugin = InventoryDatabase.getInstance();
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) dataFolder.mkdir();

        File configurationFile = new File(dataFolder + System.getProperty("file.separator") + "config.yml");
        if (!configurationFile.exists()) plugin.saveDefaultConfig();

        try {
            plugin.getConfig().load(configurationFile);
        } catch (Exception e) {
            plugin.getLogger().severe("Could not load the config file! Please check if the permissions are set! Error: " + e.getMessage());
        }
    }
}
