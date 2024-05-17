package me.xingyan.advancementnametag;

import me.xingyan.advancementnametag.Listeners.onJoin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public final class AdvancementNametag extends JavaPlugin {

    public static AdvancementNametag plugin;

    private Database database;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;

        saveDefaultConfig();

        //register command
        getCommand("tags").setExecutor(new commandTags());

        //register event
        this.getServer().getPluginManager().registerEvents(new guiNametag(), this);
        this.getServer().getPluginManager().registerEvents(new onJoin(), this);

        try{

            if(!getDataFolder().exists()){
                getDataFolder().mkdirs();
            }

            database = new Database( getDataFolder().getAbsolutePath() + "/players.db");

        }catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Faild to Connect to Database");
            Bukkit.getPluginManager().disablePlugin(this);
        }

        if(Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new Expansion().register();
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        try {
            database.closeConneciton();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //get database
    public Database getDatabase() {
        return database;
    }







    //if line too long, cut it
    public static String cutString(String string, int length) {
        if (string.length() > length) {
            string = string.substring(0, length);
        }
        return string;
    }
}
