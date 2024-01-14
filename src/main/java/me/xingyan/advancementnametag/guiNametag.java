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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static me.xingyan.advancementnametag.AdvancementNametag.plugin;

public class guiNametag implements Listener {

    FileConfiguration config = plugin.getConfig();

    private static final boolean isFolia = Bukkit.getVersion().contains("Folia");

    public static Inventory inv;
    public static Inventory inv2;
    public static Inventory inv3;

    Component title = Component.text("Tags").color(NamedTextColor.GOLD);


    // You can open the inventory with this
    public void openInventory(Player player) {
        inv = Bukkit.createInventory(null, 54, title);
        inv2 = Bukkit.createInventory(null, 54, title);
        inv3 = Bukkit.createInventory(null, 54, title);
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
            if(player.getAdvancementProgress(advancement).isDone()) {
                ItemStack item = new ItemStack(advancement.getDisplay().icon().getType());
                ItemMeta meta = item.getItemMeta();
                //hide flags
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
                meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
                meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

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
                //lore
                List<String> itemlore = new ArrayList<>();
                String plaintext = ChatColor.GRAY + PlainTextComponentSerializer.plainText().serialize(advancement.getDisplay().description());
                //if lore is too long, cut it to different lines
                if(plaintext.length() > 30) {
                    String[] words = plaintext.split(" ");
                    String line = "";
                    for(String word : words) {
                        if(line.length() + word.length() > 30) {
                            itemlore.add(ChatColor.GRAY+line);
                            line = "";
                        }
                        line += word + " ";
                    }
                    itemlore.add(ChatColor.GRAY+line);
                } else {
                    itemlore.add(ChatColor.GRAY+plaintext);
                }
                meta.setLore(itemlore);
                item.setItemMeta(meta);

                //if the item is more than 45, add to inv2, inv3
                if(inv.getItem(44) == null) {
                    inv.addItem(item);
                } else if(inv2.getItem(44) == null) {
                    inv2.addItem(item);
                } else {
                    inv3.addItem(item);
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
        inv2.setItem(45, previous);
        inv3.setItem(45, previous);





    }


    // Cancel dragging in our inventory
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().equals(inv)) {
            e.setCancelled(true);
        }


        //click reset and process console command
        if(e.getCurrentItem() != null && e.getCurrentItem().getType() == Material.BARRIER) {
            String command = config.getString("command.reset");
            command = command.replace("%player%", e.getWhoClicked().getName());
            String finalCommand = command;
            if(isFolia){
                Bukkit.getGlobalRegionScheduler().run(plugin, t -> {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCommand);
                });
            } else {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCommand);
            }
            e.getWhoClicked().sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("message.reset")));
            e.getWhoClicked().closeInventory();
            //add sound effect when click
            e.getWhoClicked().playSound(Sound.sound(org.bukkit.Sound.UI_BUTTON_CLICK, Sound.Source.PLAYER, 1f, 1f));
            return;
        }

        //click next page
        if(e.getCurrentItem() != null && e.getCurrentItem().getType() == Material.ARROW) {
            if(e.getCurrentItem().getItemMeta().displayName().equals(Component.text("Next Page").color(NamedTextColor.GREEN))) {
                e.getWhoClicked().closeInventory();
                //check current page and open next page
                if(e.getClickedInventory().equals(inv)) {
                    e.getWhoClicked().openInventory(inv2);
                } else if(e.getClickedInventory().equals(inv2)) {
                    e.getWhoClicked().openInventory(inv3);
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

        }

    }
}
