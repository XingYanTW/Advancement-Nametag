package me.xingyan.advancementnametag;

import io.papermc.paper.advancement.AdvancementDisplay;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.advancement.Advancement;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import static me.xingyan.advancementnametag.AdvancementNametag.plugin;

public class guiNametag implements Listener {

    FileConfiguration config = plugin.getConfig();

    private static final boolean isFolia = Bukkit.getVersion().contains("Folia");

    Database database = plugin.getDatabase();

    public static Inventory inv;
    public static Inventory inv2;
    public static Inventory inv3;

    public static Inventory inv4;

    public static Inventory inv5;

    public static Inventory inv6;

    public static Inventory inv7;

    public static Inventory inv8;

    public static Inventory inv9;

    public static Inventory inv10;

    Component title = Component.text("Tags").color(NamedTextColor.GOLD);


    // You can open the inventory with this
    public void openInventory(Player player) {
        inv = Bukkit.createInventory(null, 54, title);
        inv2 = Bukkit.createInventory(null, 54, title);
        inv3 = Bukkit.createInventory(null, 54, title);
        inv4 = Bukkit.createInventory(null, 54, title);
        inv5 = Bukkit.createInventory(null, 54, title);
        inv6 = Bukkit.createInventory(null, 54, title);
        inv7 = Bukkit.createInventory(null, 54, title);
        inv8 = Bukkit.createInventory(null, 54, title);
        inv9 = Bukkit.createInventory(null, 54, title);
        inv10 = Bukkit.createInventory(null, 54, title);

        //Get Player's all advencement
        player.openInventory(inv);

        //reset nametag item
        ItemStack reset = new ItemStack(Material.BARRIER);
        ItemMeta resetMeta = reset.getItemMeta();
        resetMeta.displayName(Component.text("Reset").color(NamedTextColor.RED));
        reset.setItemMeta(resetMeta);
        inv.addItem(reset);

        //get player all advencement
        Iterator<Advancement> advancements = Bukkit.getServer().advancementIterator();
        advancements.forEachRemaining(advancement -> {
            if(advancement.getKey().toString().contains("recipes")) return;
            if(advancement.getDisplay() == null) return;
            if(player.getAdvancementProgress(advancement).isDone()) {
                ItemStack item = new ItemStack(advancement.getDisplay().icon().getType());
                ItemMeta meta = item.getItemMeta();
                //hide flags
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DYE, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_PLACED_ON, ItemFlag.HIDE_UNBREAKABLE);

                if(advancement.getDisplay().frame().equals(AdvancementDisplay.Frame.CHALLENGE)){
                    meta.displayName(advancement.getDisplay().title().color(NamedTextColor.DARK_PURPLE).decoration(TextDecoration.ITALIC, false));
                    //add glow
                    meta.addEnchant(org.bukkit.enchantments.Enchantment.DURABILITY, 1, false);
                }else if(advancement.getDisplay().frame().equals(AdvancementDisplay.Frame.GOAL)) {
                    meta.displayName(advancement.getDisplay().title().color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
                    //add glow
                    meta.addEnchant(org.bukkit.enchantments.Enchantment.DURABILITY, 1, false);
                } else {
                    meta.displayName(advancement.getDisplay().title().color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false));
                }
                List<Component> itemlore = new ArrayList<>();
                itemlore.add(advancement.getDisplay().description().decoration(TextDecoration.ITALIC, false));
                meta.lore(itemlore);
                item.setItemMeta(meta);

                //if the item is more than 45, add to inv2, inv3
                if(inv.getItem(44) == null) {
                    inv.addItem(item);
                } else if(inv2.getItem(44) == null) {
                    inv2.addItem(item);
                } else if(inv3.getItem(44) == null){
                    inv3.addItem(item);
                } else if(inv4.getItem(44) == null){
                    inv4.addItem(item);
                } else if(inv5.getItem(44) == null){
                    inv5.addItem(item);
                } else if(inv6.getItem(44) == null){
                    inv6.addItem(item);
                } else if(inv7.getItem(44) == null){
                    inv7.addItem(item);
                } else if(inv8.getItem(44) == null){
                    inv8.addItem(item);
                } else if(inv9.getItem(44) == null){
                    inv9.addItem(item);
                } else {
                    inv10.addItem(item);
                }
            }
        });

        //next page
        ItemStack next = new ItemStack(Material.ARROW);
        ItemMeta nextMeta = next.getItemMeta();
        nextMeta.displayName(Component.text("Next Page").color(NamedTextColor.GREEN));
        next.setItemMeta(nextMeta);
        //previous page
        ItemStack previous = new ItemStack(Material.ARROW);
        ItemMeta previousMeta = previous.getItemMeta();
        previousMeta.displayName(Component.text("Previous Page").color(NamedTextColor.GREEN));
        previous.setItemMeta(previousMeta);

        if(!inv2.isEmpty()) {
            inv.setItem(53, next);
        }
        if (!inv3.isEmpty()) {
            inv2.setItem(53, next);
        }
        if (!inv4.isEmpty()) {
            inv3.setItem(53, next);
        }
        if (!inv5.isEmpty()) {
            inv4.setItem(53, next);
        }
        if (!inv6.isEmpty()) {
            inv5.setItem(53, next);
        }
        if (!inv7.isEmpty()) {
            inv6.setItem(53, next);
        }
        if (!inv8.isEmpty()) {
            inv7.setItem(53, next);
        }
        if (!inv9.isEmpty()) {
            inv8.setItem(53, next);
        }
        if (!inv10.isEmpty()) {
            inv9.setItem(53, next);
        }

        inv2.setItem(45, previous);
        inv3.setItem(45, previous);
        inv4.setItem(45, previous);
        inv5.setItem(45, previous);
        inv6.setItem(45, previous);
        inv7.setItem(45, previous);
        inv8.setItem(45, previous);
        inv9.setItem(45, previous);
        inv10.setItem(45, previous);

    }


    // Cancel dragging in our inventory
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) throws SQLException {
        if (e.getInventory().equals(inv)) {
            e.setCancelled(true);
        }

        if (e.getClickedInventory().equals(e.getWhoClicked().getInventory())) {
            return;
        }

        //click reset and process console command
        if(e.getCurrentItem() != null && e.getCurrentItem().getType() == Material.BARRIER) {
            String command = config.getString("command.reset");
            assert command != null;
            command = command.replace("%player%", e.getWhoClicked().getName());
            String finalCommand = command;
            if(isFolia){
                Bukkit.getGlobalRegionScheduler().run(plugin, t -> {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCommand);
                });
            } else {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCommand);
            }
            e.getWhoClicked().sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("message.reset"))));
            e.getWhoClicked().closeInventory();
            //add sound effect when click
            e.getWhoClicked().playSound(Sound.sound(org.bukkit.Sound.UI_BUTTON_CLICK, Sound.Source.PLAYER, 1f, 1f));
            return;
        }

        //click next page
        if(e.getCurrentItem() != null && e.getCurrentItem().getType() == Material.ARROW) {
            if(Objects.equals(e.getCurrentItem().getItemMeta().displayName(), Component.text("Next Page").color(NamedTextColor.GREEN))) {
                e.getWhoClicked().closeInventory();
                //check current page and open next page
                if(e.getClickedInventory().equals(inv)) {
                    e.getWhoClicked().openInventory(inv2);
                } else if(e.getClickedInventory().equals(inv2)) {
                    e.getWhoClicked().openInventory(inv3);
                } else if(e.getClickedInventory().equals(inv3)) {
                    e.getWhoClicked().openInventory(inv4);
                } else if(e.getClickedInventory().equals(inv4)) {
                    e.getWhoClicked().openInventory(inv5);
                } else if(e.getClickedInventory().equals(inv5)) {
                    e.getWhoClicked().openInventory(inv6);
                } else if(e.getClickedInventory().equals(inv6)) {
                    e.getWhoClicked().openInventory(inv7);
                } else if(e.getClickedInventory().equals(inv7)) {
                    e.getWhoClicked().openInventory(inv8);
                } else if(e.getClickedInventory().equals(inv8)) {
                    e.getWhoClicked().openInventory(inv9);
                } else if(e.getClickedInventory().equals(inv9)) {
                    e.getWhoClicked().openInventory(inv10);
                }

                e.getWhoClicked().playSound(Sound.sound(org.bukkit.Sound.UI_BUTTON_CLICK, Sound.Source.PLAYER, 1f, 1f));
                return;
            }
        }
        //click previous page
        if(e.getCurrentItem() != null && e.getCurrentItem().getType() == Material.ARROW) {
            if(e.getCurrentItem().getItemMeta().displayName().equals(Component.text("Previous Page").color(NamedTextColor.GREEN))) {
                e.getWhoClicked().closeInventory();
                //check current page and open previous page
                if(e.getClickedInventory().equals(inv2)) {
                    e.getWhoClicked().openInventory(inv);
                } else if(e.getClickedInventory().equals(inv3)) {
                    e.getWhoClicked().openInventory(inv2);
                } else if(e.getClickedInventory().equals(inv4)) {
                    e.getWhoClicked().openInventory(inv3);
                } else if(e.getClickedInventory().equals(inv5)) {
                    e.getWhoClicked().openInventory(inv4);
                } else if(e.getClickedInventory().equals(inv6)) {
                    e.getWhoClicked().openInventory(inv5);
                } else if(e.getClickedInventory().equals(inv7)) {
                    e.getWhoClicked().openInventory(inv6);
                } else if(e.getClickedInventory().equals(inv8)) {
                    e.getWhoClicked().openInventory(inv7);
                } else if(e.getClickedInventory().equals(inv9)) {
                    e.getWhoClicked().openInventory(inv8);
                } else if(e.getClickedInventory().equals(inv10)) {
                    e.getWhoClicked().openInventory(inv9);
                }
                e.getWhoClicked().playSound(Sound.sound(org.bukkit.Sound.UI_BUTTON_CLICK, Sound.Source.PLAYER, 1f, 1f));
                return;
            }
        }

        //click name tag and process console command
        if(e.getCurrentItem() != null){
            //command string


            //if item is name tag, do nothing
            //if item name is current nametag, do nothing
            if(e.getCurrentItem().getItemMeta().displayName().equals(Component.text("Current Nametag").color(NamedTextColor.GREEN))) {
                return;
            }
            String plain = PlainTextComponentSerializer.plainText().serialize(e.getCurrentItem().getItemMeta().displayName());
            String legacy = LegacyComponentSerializer.legacyAmpersand().serialize(e.getCurrentItem().getItemMeta().displayName());
            String command = config.getString("command.set");
            command = command.replace("%player%", e.getWhoClicked().getName());
            command = command.replace("%tag%", legacy);

            String finalCommand = command;
            if(isFolia){
                Bukkit.getGlobalRegionScheduler().run(plugin, t -> {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCommand);
                });
            } else {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCommand);
            }
            e.getWhoClicked().sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("message.set").replace("%tag%", plain)));
            e.getWhoClicked().closeInventory();
            //add sound effect when click
            e.getWhoClicked().playSound(Sound.sound(org.bukkit.Sound.UI_BUTTON_CLICK, Sound.Source.PLAYER, 1f, 1f));

            plugin.getDatabase().setNametag(String.valueOf(e.getWhoClicked().getUniqueId()), plain, legacy);

        }

    }
}
