package me.xingyan.advancementnametag.Listeners;

import me.xingyan.advancementnametag.Database;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import java.sql.SQLException;

import static me.xingyan.advancementnametag.AdvancementNametag.plugin;

public class onJoin implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) throws SQLException {
        Player player = event.getPlayer();
        plugin.getDatabase().addPlayer(player.getUniqueId().toString());
    }

}
