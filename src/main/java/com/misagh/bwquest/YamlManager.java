package com.misagh.bwquest;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class YamlManager {
    private static File file;
    private static FileConfiguration yamlConfig;

    public static void initialize(String fileName) {
        file = new File(Bwquest.getInstance().getDataFolder(), fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        yamlConfig = YamlConfiguration.loadConfiguration(file);
    }

    public static void saveQuestData(String playerName, String questName) {
        yamlConfig.set(playerName, questName);
        saveFile();
    }

    public static String getQuestData(String playerName) {
        return yamlConfig.getString(playerName, null);
    }

    public static void clearQuestData() {
        yamlConfig.getKeys(false).forEach(key -> yamlConfig.set(key, null));
        saveFile();
    }

    private static void saveFile() {
        try {
            yamlConfig.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
