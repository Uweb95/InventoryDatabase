## Inventory Database

This plugin syncs players' inventory and armor using a database.
Currently, the data is only saved when the user disconnects or gets kicked.

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

debug:
  # Inventory sync debug messages.
  syncMessages: false
```