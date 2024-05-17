package me.xingyan.advancementnametag;

import org.bukkit.Bukkit;

import java.sql.*;
import java.util.UUID;

public class Database {

    private final Connection connection;


    public Database(String path) throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:" + path);
        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                            CREATE TABLE IF NOT EXISTS Players (
                            UUID TEXT PRIMARY KEY,
                            Username TEXT NOT NULL,
                            Nametag TEXT,
                            Colored TEXT)
                    """);
        }
    }

    public void closeConneciton() throws SQLException {
        if(connection != null && !connection.isClosed()){
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    //add player to database
    public void addPlayer(String uuid) throws SQLException {
        //if player is already in database
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM Players WHERE UUID = ?")) {
            statement.setString(1, uuid);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return;
                }
            }
        }

        //add player to database
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO Players (UUID, Username, Nametag, Colored) VALUES (?, ?, ?, ?)")) {
            statement.setString(1, uuid);
            statement.setString(2, Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName());
            statement.setString(3, null);
            statement.setString(4, null);
            statement.executeUpdate();
        }

    }

    //get player's nametag
    public String getNametag(String uuid) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT Nametag FROM Players WHERE UUID = ?")) {
            statement.setString(1, uuid);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("Nametag");
                }
            }
        }
        return null;
    }

    //get player's colored
    public String getColored(String uuid) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT Colored FROM Players WHERE UUID = ?")) {
            statement.setString(1, uuid);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("Colored");
                }
            }
        }
        return null;
    }

    //set player's nametag and colored
    public void setNametag(String uuid, String nametag, String colored) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("UPDATE Players SET Nametag = ?, Colored = ? WHERE UUID = ?")) {
            statement.setString(1, nametag);
            statement.setString(2, colored);
            statement.setString(3, uuid);
            statement.executeUpdate();
        }
    }

}
