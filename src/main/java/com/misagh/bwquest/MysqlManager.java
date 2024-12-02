package com.misagh.bwquest;

import java.sql.*;

public class MysqlManager {
    private static Connection connection;

    public static void initialize(String host, int port, String database, String user, String password) {
        try {
            connection = DriverManager.getConnection(
                    "jdbc:mysql://" + host + ":" + port + "/" + database, user, password
            );
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS quests (player_name VARCHAR(64), quest_name VARCHAR(64), PRIMARY KEY (player_name))");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void saveQuestData(String playerName, String questName) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "REPLACE INTO quests (player_name, quest_name) VALUES (?, ?)"
            );
            statement.setString(1, playerName);
            statement.setString(2, questName);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String getQuestData(String playerName) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT quest_name FROM quests WHERE player_name = ?"
            );
            statement.setString(1, playerName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("quest_name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void clearQuestData() {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM quests");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
