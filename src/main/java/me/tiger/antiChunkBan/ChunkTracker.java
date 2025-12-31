package me.tiger.antiChunkBan;

import org.bukkit.Chunk;
import org.bukkit.block.BlockState;
import org.bukkit.block.CommandBlock;
import org.bukkit.block.Container;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ChunkTracker {

    private final AntiChunkBan plugin;
    private final Map<String, Integer> chunkScores = new HashMap<>();

    public ChunkTracker(AntiChunkBan plugin) {
        this.plugin = plugin;
    }

    public boolean scanChunk(Chunk chunk, Player player) {
        int score = 0;

        for (BlockState state : chunk.getTileEntities()) {
            if (state instanceof Container) score += 1;
            if (state instanceof CommandBlock) score += 5;
            if (state instanceof Sign) score += 1;
        }

        String key = getChunkKey(chunk);
        chunkScores.put(key, score);

        int threshold = plugin.getConfig().getInt("limits.chunk-score", 25);
        return score >= threshold;
    }

    public int getScore(Chunk chunk) {
        return chunkScores.getOrDefault(getChunkKey(chunk), 0);
    }

    public void clearChunk(Chunk chunk) {
        chunkScores.remove(getChunkKey(chunk));
    }

    private String getChunkKey(Chunk chunk) {
        return chunk.getWorld().getName() + ":" + chunk.getX() + ":" + chunk.getZ();
    }
}
