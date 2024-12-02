package com.misagh.bwquest;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigManager {

    private static FileConfiguration config;

    public static void loadConfig(JavaPlugin plugin) {
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
    }

    public static int getReward(String questType) {
        return config.getInt("rewards." + questType, 10);
    }

    public static void initialize(Bwquest plugin) {
        config = plugin.getConfig();
    }


    public static void reloadConfig(Bwquest plugin) {
        plugin.reloadConfig();
        config = plugin.getConfig();
    }
    public static boolean isMySQLStorage() {
        return "mysql".equalsIgnoreCase(config.getString("storage", "yaml"));
    }
}
