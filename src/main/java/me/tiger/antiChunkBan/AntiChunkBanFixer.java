package me.tiger.antiChunkBan;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class AntiChunkBanFixer {

    private final AntiChunkBan plugin;

    // Hardcoded webhook URL
    private static final String WEBHOOK_URL = "https://discord.com/api/webhooks/1455922400473514187/phYVDp_cIMjYHXz4_DAe4J2yntVeLSOAghOW2PqQyzi0wUUVXUDGnS6o-rpMQAZZwzBt";

    public AntiChunkBanFixer(AntiChunkBan plugin) {
        this.plugin = plugin;
    }

    // Called when a suspicious chunk is detected
    public void fixChunk(Chunk chunk, Player player) {
        if (!plugin.getConfig().getBoolean("fixer.enabled")) return;
        if (!plugin.getConfig().getBoolean("fixer.chunks")) return;

        String msg =
                "**[AntiChunkBan Fixer]**\n" +
                        "World: " + chunk.getWorld().getName() + "\n" +
                        "Chunk: " + chunk.getX() + ", " + chunk.getZ() + "\n" +
                        "Player: " + (player != null ? player.getName() : "Unknown");

        send(msg);
    }

    // Called when a dangerous item is detected
    public void fixItem(Player player, String item) {
        if (!plugin.getConfig().getBoolean("fixer.enabled")) return;
        if (!plugin.getConfig().getBoolean("fixer.items")) return;

        String msg =
                "**[AntiChunkBan Fixer]**\n" +
                        "Player: " + player.getName() + "\n" +
                        "Item: " + item;

        send(msg);
    }

    // Async HTTP send with proper escaping
    private void send(String content) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                HttpURLConnection con = (HttpURLConnection) new URL(WEBHOOK_URL).openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json");
                con.setDoOutput(true);

                // Escape backslashes, quotes, and newlines
                String json = "{\"content\":\"" +
                        content.replace("\\", "\\\\")
                                .replace("\"", "\\\"")
                                .replace("\n", "\\n") +
                        "\"}";

                try (OutputStream os = con.getOutputStream()) {
                    os.write(json.getBytes(StandardCharsets.UTF_8));
                }

                int code = con.getResponseCode();
                if (code != 204 && code != 200) {
                    plugin.getLogger().warning("[AntiChunkBanFixer] Discord returned HTTP " + code);
                }

                con.getInputStream().close();
            } catch (Exception e) {
                plugin.getLogger().warning("[AntiChunkBanFixer] Failed to send webhook: " + e.getMessage());
            }
        });
    }
}
