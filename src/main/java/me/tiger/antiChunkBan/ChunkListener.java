package me.tiger.antiChunkBan;

import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

public class ChunkListener implements Listener {

    private final AntiChunkBan plugin;

    public ChunkListener(AntiChunkBan plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();

        if (isSuspicious(chunk)) {
            plugin.getLogger().warning("Suspicious chunk detected at " +
                    chunk.getX() + ", " + chunk.getZ() +
                    " in world " + chunk.getWorld().getName());
            // Optional: unload or rollback chunk here
            // chunk.unload(true);
        }
    }

    private boolean isSuspicious(Chunk chunk) {
        // Placeholder logic for testing
        int maxTileEntities = plugin.getConfig().getInt("chunk.max-tile-entities", 200);

        if (chunk.getTileEntities().length > maxTileEntities) return true;

        return false;
    }
}
