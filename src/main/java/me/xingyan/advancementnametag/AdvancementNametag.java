package me.xingyan.advancementnametag;

import org.bukkit.plugin.java.JavaPlugin;

public final class AdvancementNametag extends JavaPlugin {

    public static AdvancementNametag plugin;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;

        saveDefaultConfig();

        //register command
        getCommand("tags").setExecutor(new commandTags());

        //register event
        this.getServer().getPluginManager().registerEvents(new guiNametag(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }



    //if line too long, cut it
    public static String cutString(String string, int length) {
        if (string.length() > length) {
            string = string.substring(0, length);
        }
        return string;
    }
}
