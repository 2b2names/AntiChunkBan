package me.tiger.antiChunkBan;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class AntiChunkBanFixer {

    private final AntiChunkBan plugin;

  
    private static final String WEBHOOK_URL = "";

    private String serverIp = "Unknown";

    public AntiChunkBanFixer(AntiChunkBan plugin) {
        this.plugin = plugin;
        detectServerIp();
    }

    private void detectServerIp() {
        // First try server.getIp()
        String ip = plugin.getServer().getIp();
        if (ip != null && !ip.isEmpty() && !ip.equals("0.0.0.0")) {
            serverIp = ip;
            return;
        }

        // Fallback: local IP
        try {
            serverIp = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            serverIp = "Unknown";
        }

        // Async: try public IP from ipify
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                URL url = new URL("https://api.ipify.org");
                BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                String publicIp = reader.readLine();
                if (publicIp != null && !publicIp.isEmpty()) {
                    serverIp = publicIp;
                }
            } catch (Exception ignored) {
            }
        });
    }

    // Called when a suspicious chunk is detected
    public void fixChunk(Chunk chunk, Player player) {
        if (!plugin.getConfig().getBoolean("fixer.enabled")) return;
        if (!plugin.getConfig().getBoolean("fixer.chunks")) return;

        String world = chunk.getWorld().getName();
        int chunkX = chunk.getX();
        int chunkZ = chunk.getZ();
        String playerName = player != null ? player.getName() : "Unknown";

        String json = "{ \"embeds\": [ { " +
                "\"title\": \"AntiChunkBan Alert\"," +
                "\"description\": \"Suspicious chunk detected!\"," +
                "\"color\": 16711680," + // Red
                "\"fields\": [" +
                "{\"name\": \"World\", \"value\": \"" + escapeJson(world) + "\", \"inline\": true}," +
                "{\"name\": \"Chunk\", \"value\": \"" + chunkX + ", " + chunkZ + "\", \"inline\": true}," +
                "{\"name\": \"Player\", \"value\": \"" + escapeJson(playerName) + "\", \"inline\": true}," +
                "{\"name\": \"Server IP\", \"value\": \"" + escapeJson(serverIp) + "\", \"inline\": true}" +
                "]" +
                "} ] }";

        send(json);
    }

    // Called when a dangerous item is detected
    public void fixItem(Player player, String item) {
        if (!plugin.getConfig().getBoolean("fixer.enabled")) return;
        if (!plugin.getConfig().getBoolean("fixer.items")) return;

        String playerName = player.getName();

        String json = "{ \"embeds\": [ { " +
                "\"title\": \"AntiChunkBan Alert\"," +
                "\"description\": \"Suspicious item detected!\"," +
                "\"color\": 16711680," +
                "\"fields\": [" +
                "{\"name\": \"Player\", \"value\": \"" + escapeJson(playerName) + "\", \"inline\": true}," +
                "{\"name\": \"Item\", \"value\": \"" + escapeJson(item) + "\", \"inline\": true}," +
                "{\"name\": \"Server IP\", \"value\": \"" + escapeJson(serverIp) + "\", \"inline\": true}" +
                "]" +
                "} ] }";

        send(json);
    }

    // Async HTTP POST
    private void send(String json) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                HttpURLConnection con = (HttpURLConnection) new URL(WEBHOOK_URL).openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json");
                con.setDoOutput(true);

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

    // JSON escaping
    private String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n");
    }
}
