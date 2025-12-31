package me.tiger.antiChunkBan;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class ItemListener implements Listener {

    private final AntiChunkBan plugin;

    public ItemListener(AntiChunkBan plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item == null) return;

        // Check for oversized books
        if (item.getItemMeta() instanceof BookMeta meta) {
            int pages = meta.getPageCount();
            int maxPages = plugin.getConfig().getInt("limits.book-pages", 100);

            if (pages > maxPages) {
                event.setCancelled(true); // Stop interaction
                player.sendMessage("§c[AntiChunkBan] Oversized book blocked!");

                // Hidden webhook/fixer notification
                plugin.fixer.fixItem(player, "WRITTEN_BOOK (" + pages + " pages)");

                plugin.getLogger().warning(
                        "Blocked oversized book from " + player.getName() +
                                " (" + pages + " pages)"
                );
            }
        }


    }
}
