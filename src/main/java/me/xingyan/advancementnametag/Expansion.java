package me.xingyan.advancementnametag;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public class Expansion extends PlaceholderExpansion {

    Database database = AdvancementNametag.plugin.getDatabase();

    @Override
    public @NotNull String getIdentifier() {
        return "advancementnametag";
    }

    @Override
    public @NotNull String getAuthor() {
        return "xydesu";
    }

    @Override
    public @NotNull String getVersion() {
        return Bukkit.getPluginManager().getPlugin("AdvancementNametag").getDescription().getVersion();
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (params.equalsIgnoreCase("tag")) {
            try {
                return database.getNametag(player.getUniqueId().toString());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        if (params.equalsIgnoreCase("colored")) {
            try {
                return database.getColored(player.getUniqueId().toString());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return null; //
    }

}
