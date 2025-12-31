package me.tiger.antiChunkBan;

import org.bukkit.plugin.java.JavaPlugin;

public class AntiChunkBan extends JavaPlugin {

    private static AntiChunkBan instance;

    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("AntiChunkBan enabled!");

        saveDefaultConfig();

        // Register listeners
        getServer().getPluginManager().registerEvents(new ChunkListener(this), this);
    }

    @Override
    public void onDisable() {
        getLogger().info("AntiChunkBan disabled!");
    }

    public static AntiChunkBan getInstance() {
        return instance;
    }
}
