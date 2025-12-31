package me.tiger.antiChunkBan;

import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CommandBlock;
import org.bukkit.block.Container;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.Material;

public class ChunkListener implements Listener {

    private final AntiChunkBan plugin;

    public ChunkListener(AntiChunkBan plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        Chunk chunk = event.getChunk();

        boolean suspicious = plugin.chunkTracker.scanChunk(chunk, null);

        if (suspicious) {
            Player player = plugin.reputationManager.getLastModifier(chunk);
            plugin.backupManager.saveChunk(chunk);

            if (player != null) plugin.reputationManager.addOffense(player);

            plugin.fixer.fixChunk(chunk, player);

            if (plugin.getConfig().getBoolean("settings.auto-unload", true)) {
                chunk.unload(true);
            }

            if (plugin.getConfig().getBoolean("settings.log-detections", true)) {
                plugin.getLogger().warning(
                        "Suspicious chunk detected at " + chunk.getX() + "," + chunk.getZ() +
                                " in " + chunk.getWorld().getName() +
                                " | Player: " + (player != null ? player.getName() : "Unknown")
                );
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        Chunk chunk = block.getChunk();

        plugin.reputationManager.recordModification(chunk, player);

        if (isDangerousBlock(block)) {
            plugin.backupManager.saveChunk(chunk);
            plugin.fixer.fixChunk(chunk, player);
            plugin.reputationManager.addOffense(player);
        }
    }

    private boolean isDangerousBlock(Block block) {
        BlockState state = block.getState();

        if (state instanceof Container) return true;
        if (state instanceof Sign) return true;
        if (state instanceof CommandBlock) return true;

        Material type = block.getType();
        return type == Material.COMMAND_BLOCK
                || type == Material.CHAIN_COMMAND_BLOCK
                || type == Material.REPEATING_COMMAND_BLOCK;
    }
}
