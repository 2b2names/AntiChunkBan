# AntiChunkBan

**Protect your Minecraft server from malicious chunk exploits.**

AntiChunkBan is a lightweight Paper/Spigot plugin for Minecraft 1.21.4 that helps server administrators detect and prevent chunk-related exploits, including:

- Excessive tile entities
- Illegal command blocks
- Potentially malicious chunks that could crash the server

---

## Features

- Detect suspicious chunks automatically on load
- Track the last player who modified a chunk
- Configurable thresholds for tile entities and command blocks
- Console alerts for flagged chunks
- Safe chunk unload to prevent server crashes
- Easy-to-configure via `config.yml`

---

## Installation

1. Download the latest `AntiChunkBan.jar`
2. Place it in your server’s `plugins` folder
3. Start the server to generate default config
4. Configure `config.yml` if needed
5. Reload or restart the server

---

## Configuration (`config.yml`)

```yaml
chunk:
  max-tile-entities: 200
  max-command-blocks: 50
  alert-console: true
  alert-staff: true
