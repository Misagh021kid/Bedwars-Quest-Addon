package com.misagh.bwquest;

import com.andrei1058.bedwars.proxy.database.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import net.milkbowl.vault.economy.Economy;

import static com.misagh.bwquest.Quests2023.resetQuests;
import static com.misagh.bwquest.Quests1058.resetQuests;


public class Bwquest extends JavaPlugin {

    private static Bwquest instance;
    private static Economy economy;

    @Override
    public void onEnable() {
        instance = this;
        ConfigManager.initialize(this);
        setupEconomy();
        saveDefaultConfig();
        getCommand("quests").setExecutor(new CommandHandler());
        getCommand("bwr").setExecutor(new CommandHandler());
        sendStartupMessage();
        Bukkit.getScheduler().runTask(this, this::checkPlugins);
        YamlManager.initialize("quests.yaml");


    }
    private void checkPlugins() {
        boolean isBedWars2023 = isPluginEnabled("BedWars2023");
        boolean isBedWars1058 = isPluginEnabled("BedWars1058");

        if (isBedWars2023 && isBedWars1058) {
            getLogger().severe("Both BedWars2023 and BedWars1058 are installed and enabled! This plugin cannot run with both plugins simultaneously.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (isBedWars2023) {
            registerListener(new Quests2023());
            Quests2023.resetQuests();
            getLogger().info("Hooked with BedWars2023.");
        } else if (isBedWars1058) {
            registerListener(new Quests1058());
            Quests1058.resetQuests();
            getLogger().info("Hooked with BedWars1058.");
        } else {
            getLogger().severe("No compatible BedWars plugin found or plugin is disabled!");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    private void registerListener(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, this);
    }

    private boolean isPluginEnabled(String pluginName) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
        return plugin != null && plugin.isEnabled();
    }

    private void sendStartupMessage() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "[BW-Quest]" + ChatColor.UNDERLINE + "                                                        ");
        Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "[BW-Quest]" + ChatColor.GREEN + "                 Misagh - Bedwars Quest Addon");
        Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "[BW-Quest]" + ChatColor.YELLOW + "                     Thanks for Using My Plugin!");
        Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "[BW-Quest]");
        Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "[BW-Quest]" + ChatColor.AQUA + " Plugin Information:");
        Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "[BW-Quest]  + "+ ChatColor.WHITE +"Author: " + ChatColor.GOLD + "Misagh");
        Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "[BW-Quest]  + "+ ChatColor.WHITE +"Discord: " + ChatColor.GOLD + "https://discord.gg/irdevs");
        Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "[BW-Quest]" + ChatColor.UNDERLINE + "                                                        ");
        Bukkit.getConsoleSender().sendMessage(" ");
    }
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            getLogger().severe("Vault plugin not found! Disabling plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return false;
        economy = rsp.getProvider();
        return economy != null;
    }

    public static Economy getEconomy() {
        return economy;
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Misagh - Bedwars Quest Addon is now disabled.");
    }

    public static Bwquest getInstance() {
        return instance;
    }
}