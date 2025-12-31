package me.tiger.antiChunkBan;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class PlayerReputationManager {

    private final AntiChunkBan plugin;
    private final Map<Player, Integer> reputation = new HashMap<>();
    private final Map<String, Player> chunkLastModifier = new HashMap<>();

    public PlayerReputationManager(AntiChunkBan plugin) {
        this.plugin = plugin;
    }

    public void addOffense(Player player) {
        int level = reputation.getOrDefault(player, 0) + 1;
        reputation.put(player, level);
        handlePunishment(player, level);
    }

    public void handlePunishment(Player player, int level) {
        if (level == plugin.getConfig().getInt("punishments.warn-level", 1)) {
            player.sendMessage("Warning: Suspicious activity detected!");
        } else if (level == plugin.getConfig().getInt("punishments.kick-level", 3)) {
            player.kickPlayer("You have triggered AntiChunkBan multiple times!");
        } else if (level >= plugin.getConfig().getInt("punishments.tempban-level", 5)) {
            // Temporary ban (example, needs a proper ban system)
            player.kickPlayer("Temporarily banned for repeated suspicious actions.");
        }
    }

    public void setLastModifier(Chunk chunk, Player player) {
        String key = chunk.getWorld().getName() + ":" + chunk.getX() + ":" + chunk.getZ();
        chunkLastModifier.put(key, player);
    }

    public Player getLastModifier(Chunk chunk) {
        String key = chunk.getWorld().getName() + ":" + chunk.getX() + ":" + chunk.getZ();
        return chunkLastModifier.get(key);
    }
}
