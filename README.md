## Inventory Database

This plugin syncs players' inventory and armor using a database.  
I wrote the plugin for paper, but it should work on Bukkit/Spigot too.  

Tested minecraft version: 1.19.3

### Configuration

```
# MySQL Database
database:
  host: 127.0.0.1
  port: 3306
  # Database name (The database must exist)
  databaseName: 'minecraft'
  username: 'minecraft'
  # Use a strong password!
  password: 'password'

general:
  # Enable inventory sync
  syncInventory: true
  # Enable armor sync
  syncArmor: true
  # Time between data saves in minutes. (0 to only sync when connecting/disconnecting)
  saveInterval: 2

debug:
  # Inventory sync debug messages.
  syncMessages: false
```