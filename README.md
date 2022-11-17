# Regional Chat
Limits the range a player can talk and be heard via text chat.

Join [my Discord server](https://discord.gg/PuqmUMaJWk) for support and updates

### Commands
- `/shout <message>` - Sends a message to all players in the server. Op required

### Configuration
Since 1.1.0, you can use TOML or JSON5 for your config file! (**[Quilt Loader](https://quiltmc.org/) Required**).
Files for older versions of the mod (1.0.x) are automatically converted to TOML, but you may keep your file in json by moving it to the path below.

The file is located at `config/regional_chat/regional_chat.toml`/`regional_chat.json5` in Quilt, or `config/regional_chat.json` in Fabric

#### Default
TOML:
```toml
# The range in blocks a player can talk and be heard
# range[0, 32767]
# default: 100
range = 100
# Whether to notify players about the distance a message was sent from
# default: true
notifyDistance = true
# The prefix to use when notifying players about the distance a message was sent from
# default: [From %.0f blocks away] 
distancePrefix = "[From %.0f blocks away] "
# Whether to allow operators to bypass the range limit
# default: true
opBypass = true
# The permission level required to bypass the range limit
# range[0, 4]
# default: 3
opRequiredPermissionLevel = 3
# Whether operators should have an unlimited hearing range
# default: false
opUnlimitedRange = false
```

JSON:
```json
{
  "range": 100,
  "notify_distance": true,
  "distance_prefix": "[From %.0f blocks away] ",
  "op_bypass": true,
  "op_required_permission_level": 3,
  "op_unlimited_range": false
}
```
