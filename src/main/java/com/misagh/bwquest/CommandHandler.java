package com.misagh.bwquest;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

public class CommandHandler implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) return false;

        Player player = (Player) sender;
        PluginManager pluginManager = player.getServer().getPluginManager();

        if (cmd.getName().equalsIgnoreCase("bwr")) {
            ConfigManager.reloadConfig(Bwquest.getInstance());
            player.sendMessage(ChatColor.GREEN + "Configuration reloaded successfully.");
            return true;
        } else if (cmd.getName().equalsIgnoreCase("quests")) {
            if (pluginManager.isPluginEnabled("BedWars2023")) {
                Quests2023.openQuestGUI(player);
            }
            else if (pluginManager.isPluginEnabled("BedWars1058")) {
                Quests1058.openQuestGUI(player);
            } else {
                player.sendMessage(ChatColor.RED + "Please Install a BedWars Plugin!");
            }
            return true;
        }

        return false;
    }
}
