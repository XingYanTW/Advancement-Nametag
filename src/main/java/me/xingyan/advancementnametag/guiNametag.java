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
import java.util.*;

import static me.xingyan.advancementnametag.AdvancementNametag.plugin;

public class guiNametag implements Listener {

    FileConfiguration config = plugin.getConfig();

    private static Map<UUID, Integer> playerPages = new HashMap<>();
    private static Map<UUID, List<Inventory>> playerInventories = new HashMap<>();

    private static final int INVENTORY_SIZE = 54;
    private static final int MAX_ITEMS_PER_PAGE = INVENTORY_SIZE - 9; // Last row is for navigation

    private Component title = Component.text("Tags").color(NamedTextColor.GOLD);

    public void openInventory(Player player) {
        List<Inventory> inventories = new ArrayList<>();
        playerInventories.put(player.getUniqueId(), inventories);
        playerPages.put(player.getUniqueId(), 0);

        Inventory firstPage = createNewPage(inventories);
        addResetButton(firstPage);
        addAdvancementsToInventory(player, inventories);

        addNavigationButtons(inventories);
        player.openInventory(inventories.get(0));
    }

    private Inventory createNewPage(List<Inventory> inventories) {
        Inventory newInventory = Bukkit.createInventory(null, INVENTORY_SIZE, title);
        inventories.add(newInventory);
        return newInventory;
    }

    private void addResetButton(Inventory inventory) {
        ItemStack reset = new ItemStack(Material.BARRIER);
        ItemMeta resetMeta = reset.getItemMeta();
        resetMeta.displayName(Component.text("Reset").color(NamedTextColor.RED));
        reset.setItemMeta(resetMeta);
        inventory.addItem(reset);
    }

    private void addAdvancementsToInventory(Player player, List<Inventory> inventories) {
        final Inventory[] currentInventory = {inventories.get(inventories.size() - 1)};

        Iterator<Advancement> advancements = Bukkit.getServer().advancementIterator();
        advancements.forEachRemaining(advancement -> {
            if (advancement.getKey().toString().contains("recipes")) return;
            if (advancement.getDisplay() == null) return;
            if (player.getAdvancementProgress(advancement).isDone()) {
                ItemStack item = new ItemStack(advancement.getDisplay().icon().getType());
                ItemMeta meta = item.getItemMeta();
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DYE, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_PLACED_ON, ItemFlag.HIDE_UNBREAKABLE);

                if (advancement.getDisplay().frame().equals(AdvancementDisplay.Frame.CHALLENGE)) {
                    meta.displayName(advancement.getDisplay().title().color(NamedTextColor.DARK_PURPLE).decoration(TextDecoration.ITALIC, false));
                    meta.addEnchant(org.bukkit.enchantments.Enchantment.DURABILITY, 1, false);
                } else if (advancement.getDisplay().frame().equals(AdvancementDisplay.Frame.GOAL)) {
                    meta.displayName(advancement.getDisplay().title().color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false));
                    meta.addEnchant(org.bukkit.enchantments.Enchantment.DURABILITY, 1, false);
                } else {
                    meta.displayName(advancement.getDisplay().title().color(NamedTextColor.GREEN).decoration(TextDecoration.ITALIC, false));
                }

                List<Component> itemLore = new ArrayList<>();
                itemLore.add(advancement.getDisplay().description().decoration(TextDecoration.ITALIC, false));
                meta.lore(itemLore);
                item.setItemMeta(meta);

                if (currentInventory[0].firstEmpty() == -1 || currentInventory[0].firstEmpty() >= MAX_ITEMS_PER_PAGE) {
                    currentInventory[0] = createNewPage(inventories);
                }
                currentInventory[0].addItem(item);
            }
        });
    }

    private void addNavigationButtons(List<Inventory> inventories) {
        for (int i = 0; i < inventories.size(); i++) {
            Inventory inv = inventories.get(i);
            if (i < inventories.size() - 1) {
                inv.setItem(53, createNavigationItem("Next Page", NamedTextColor.GREEN));
            }
            if (i > 0) {
                inv.setItem(45, createNavigationItem("Previous Page", NamedTextColor.GREEN));
            }
        }
    }

    private ItemStack createNavigationItem(String name, NamedTextColor color) {
        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(name).color(color));
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) throws SQLException {
        Player player = (Player) e.getWhoClicked();
        UUID playerId = player.getUniqueId();

        List<Inventory> inventories = playerInventories.get(playerId);
        Integer currentPage = playerPages.get(playerId);

        if (inventories == null || currentPage == null || !inventories.contains(e.getInventory())) return;

        e.setCancelled(true);

        if (e.getClickedInventory() == player.getInventory()) return;

        if (e.getCurrentItem() != null && e.getCurrentItem().getType() == Material.BARRIER) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString("message.reset"))));
            player.closeInventory();
            player.playSound(Sound.sound(org.bukkit.Sound.UI_BUTTON_CLICK, Sound.Source.PLAYER, 1f, 1f));
            plugin.getDatabase().setNametag(playerId.toString(), null, null);
            return;
        }

        if (e.getCurrentItem() != null && e.getCurrentItem().getType() == Material.ARROW) {
            Component displayName = e.getCurrentItem().getItemMeta().displayName();
            if (displayName.equals(Component.text("Next Page").color(NamedTextColor.GREEN))) {
                currentPage++;
                playerPages.put(playerId, currentPage);
                player.openInventory(inventories.get(currentPage));
                player.playSound(Sound.sound(org.bukkit.Sound.UI_BUTTON_CLICK, Sound.Source.PLAYER, 1f, 1f));
                return;
            }

            if (displayName.equals(Component.text("Previous Page").color(NamedTextColor.GREEN))) {
                currentPage--;
                playerPages.put(playerId, currentPage);
                player.openInventory(inventories.get(currentPage));
                player.playSound(Sound.sound(org.bukkit.Sound.UI_BUTTON_CLICK, Sound.Source.PLAYER, 1f, 1f));
                return;
            }
        }

        if (e.getCurrentItem() != null) {
            if (e.getCurrentItem().getItemMeta().displayName().equals(Component.text("Current Nametag").color(NamedTextColor.GREEN))) {
                return;
            }

            String plain = PlainTextComponentSerializer.plainText().serialize(e.getCurrentItem().getItemMeta().displayName());
            String legacy = LegacyComponentSerializer.legacyAmpersand().serialize(e.getCurrentItem().getItemMeta().displayName());

            player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("message.set").replace("%tag%", plain)));
            player.closeInventory();
            player.playSound(Sound.sound(org.bukkit.Sound.UI_BUTTON_CLICK, Sound.Source.PLAYER, 1f, 1f));

            plugin.getDatabase().setNametag(playerId.toString(), plain, ChatColor.translateAlternateColorCodes('&', legacy));
        }
    }
}
