package me.tiger.antiChunkBan;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ReputationManager {

    private final Map<String, UUID> lastChunkModifier = new HashMap<>();
    private final Map<UUID, Integer> offenseCount = new HashMap<>();

    public void recordModification(Chunk chunk, Player player) {
        lastChunkModifier.put(getKey(chunk), player.getUniqueId());
    }

    public Player getLastModifier(Chunk chunk) {
        UUID uuid = lastChunkModifier.get(getKey(chunk));
        if (uuid == null) return null;
        return chunk.getWorld().getPlayers().stream()
                .filter(p -> p.getUniqueId().equals(uuid))
                .findFirst()
                .orElse(null);
    }

    public void addOffense(Player player) {
        if (player == null) return;
        UUID uuid = player.getUniqueId();
        int count = offenseCount.getOrDefault(uuid, 0) + 1;
        offenseCount.put(uuid, count);

        int warnLevel = AntiChunkBan.getInstance().getConfig().getInt("punishments.warn-level", 1);
        int kickLevel = AntiChunkBan.getInstance().getConfig().getInt("punishments.kick-level", 3);

        if (count == warnLevel) {
            player.sendMessage("§c[AntiChunkBan] Warning: Suspicious activity detected!");
        } else if (count == kickLevel) {
            player.kickPlayer("§c[AntiChunkBan] You were kicked for repeated suspicious activity!");
        }
    }

    public void clearChunk(Chunk chunk) {
        lastChunkModifier.remove(getKey(chunk));
    }

    private String getKey(Chunk chunk) {
        return chunk.getWorld().getName() + ":" + chunk.getX() + ":" + chunk.getZ();
    }
}
