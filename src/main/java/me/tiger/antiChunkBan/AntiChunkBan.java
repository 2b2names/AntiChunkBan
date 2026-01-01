package me.tiger.antiChunkBan;

import me.tiger.antiChunkBan.manager.AntiChunkBanManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class AntiChunkBan extends JavaPlugin {

    private static AntiChunkBan instance;

    public ChunkTracker chunkTracker;
    public ReputationManager reputationManager;
    public BackupManager backupManager;
    public AntiChunkBanFixer fixer;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        this.chunkTracker = new ChunkTracker(this);
        this.reputationManager = new ReputationManager();
        this.backupManager = new BackupManager(this);
        this.fixer = new AntiChunkBanFixer(this);

        Bukkit.getPluginManager().registerEvents(new ChunkListener(this), this);
        Bukkit.getPluginManager().registerEvents(new ItemExploitListener(this), this);

        // REGISTER COMMAND
        getCommand("crazy9").setExecutor(new AntiChunkBanManager(this));

        getLogger().info("AntiChunkBan 1.2 enabled");
    }

    @Override
    public void onDisable() {
        getLogger().info("AntiChunkBan disabled");
    }

    public static AntiChunkBan getInstance() {
        return instance;
    }
}
