package me.tiger.antiChunkBan;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.world.ChunkLoadEvent;

import java.util.HashMap;
import java.util.Map;

public class ChunkListener implements Listener {

    private final AntiChunkBan plugin;

    // Track last modifying player per chunk
    private final Map<String, String> chunkPlayerMap = new HashMap<>();

    public ChunkListener(AntiChunkBan plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();

        if (isSuspicious(chunk)) {
            String chunkKey = chunk.getWorld().getName() + ":" + chunk.getX() + ":" + chunk.getZ();
            String player = chunkPlayerMap.getOrDefault(chunkKey, "Unknown");

            if (plugin.getConfig().getBoolean("chunk.alert-console", true)) {
                plugin.getLogger().warning("Suspicious chunk at " +
                        chunk.getX() + "," + chunk.getZ() +
                        " in world " + chunk.getWorld().getName() +
                        " | Last modified by: " + player);
            }

            // Unload chunk to prevent crash
            chunk.unload(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Chunk chunk = event.getBlock().getChunk();
        String chunkKey = chunk.getWorld().getName() + ":" + chunk.getX() + ":" + chunk.getZ();
        chunkPlayerMap.put(chunkKey, player.getName());
    }

    private boolean isSuspicious(Chunk chunk) {
        int maxTileEntities = plugin.getConfig().getInt("chunk.max-tile-entities", 200);
        int maxCommandBlocks = plugin.getConfig().getInt("chunk.max-command-blocks", 50);

        // Tile entity count check
        if (chunk.getTileEntities().length > maxTileEntities) return true;

        // Command block count check
        int commandBlockCount = 0;
        for (var tile : chunk.getTileEntities()) {
            Block block = tile.getBlock();
            if (block.getType() == Material.COMMAND_BLOCK
                    || block.getType() == Material.REPEATING_COMMAND_BLOCK
                    || block.getType() == Material.CHAIN_COMMAND_BLOCK) {
                commandBlockCount++;
            }
        }
        return commandBlockCount > maxCommandBlocks;
    }
}
