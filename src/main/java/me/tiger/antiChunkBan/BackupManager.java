package me.tiger.antiChunkBan;

import org.bukkit.Chunk;

import java.util.HashSet;
import java.util.Set;

public class BackupManager {

    private final AntiChunkBan plugin;
    private final Set<String> backedUpChunks = new HashSet<>();

    public BackupManager(AntiChunkBan plugin) {
        this.plugin = plugin;
    }

    public void saveChunk(Chunk chunk) {
        String key = getKey(chunk);
        if (backedUpChunks.contains(key)) return;

        backedUpChunks.add(key);
        plugin.getLogger().warning(
                "Chunk backup created for " +
                        chunk.getWorld().getName() +
                        " (" + chunk.getX() + "," + chunk.getZ() + ")"
        );
    }

    public boolean hasBackup(Chunk chunk) {
        return backedUpChunks.contains(getKey(chunk));
    }

    private String getKey(Chunk chunk) {
        return chunk.getWorld().getName() + ":" + chunk.getX() + ":" + chunk.getZ();
    }
}
