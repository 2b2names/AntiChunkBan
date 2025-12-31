package me.tiger.antiChunkBan;

import org.bukkit.plugin.java.JavaPlugin;

public class AntiChunkBan extends JavaPlugin {

    private static AntiChunkBan instance;

    @Override
    public void onEnable() {
        instance = this;
        getLogger().info("AntiChunkBan has been enabled!");
        getServer().getPluginManager().registerEvents(new ChunkListener(this), this);

        // Load config or create default
        saveDefaultConfig();
    }

    @Override
    public void onDisable() {
        getLogger().info("AntiChunkBan has been disabled!");
    }

    public static AntiChunkBan getInstance() {
        return instance;
    }
}
