# Regional Chat
Limits the range a player can talk and be heard via text chat.

Join [my Discord server](https://discord.gg/PuqmUMaJWk) for support and updates

### Commands
- `/shout <message>` - Sends a message to all players in the server. Op required

### Configuration
The file is located at `config/regional_chat.json`

#### Default
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

#### Properties
| Property          | Type     | Description |
| ----------------- | -------- | ----------- |
| `range`           | number   | The range in blocks a player can talk and be heard. |
| `notify_distance` | boolean  | Whether to notify players about the distance a message was sent from. |
| `distance_prefix` | string   | The prefix to use when notifying players about the distance a message was sent from. |
| `op_bypass`       | boolean  | Whether to allow operators to bypass the range limit. |
| `op_required_permission_level` | number | The permission level required to bypass the range limit. |
| `op_unlimited_range` | boolean | Whether operators should have an unlimited hearing range. |
