package com.ngxdev.anticheat.dev;

import com.ngxdev.anticheat.Firefly;
import com.ngxdev.anticheat.checks.misc.Debug;
import com.ngxdev.anticheat.data.playerdata.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class DevServerListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e) {
        try {
            e.setJoinMessage(null);
            Player p = e.getPlayer();
            Bukkit.getScheduler().scheduleSyncDelayedTask(Firefly.getInstance(), () -> {
                if (p.isOnline() && p.isOp())
                    PlayerData
		                    .get(p)
		                    .find(Debug.class)
		                    .setDebug(true);
            }, 5);
            if (p.getName().equalsIgnoreCase("jessiep")) return; // fuck thisi shit lol
            p.sendMessage(new String[]{
                    "§7Welcome to the §eFireflyX Anticheat §7test server.",
                    "",
                    "§7Buy now at: §ehttps://ngxdev.com",
            });
            Inventory inv = p.getInventory();
            inv.setItem(0, new ItemStack(Material.DIAMOND_SWORD));
            inv.setItem(1, new ItemStack(Material.DIAMOND_PICKAXE));
            inv.setItem(2, new ItemStack(Material.BOW));
            inv.setItem(9, new ItemStack(Material.ARROW, 64));
        } catch (Throwable ex) {
        }
    }

    @EventHandler
    public void onFoodChange(FoodLevelChangeEvent e) {
        if (e.getFoodLevel() != 20) e.setFoodLevel(20);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        e.setFormat("§e%1$s§7: §f%2$s");
        //e.setCancelled(true);
    }

    @EventHandler
    public void onChat(BlockPlaceEvent e) {
        if (e.getBlock().getType() == Material.STAINED_CLAY && e.getBlock().getData() == 15) e.setCancelled(true);
        // if (e.getPlayer().getGameMode() != GameMode.CREATIVE) e.setCancelled(true);
    }

    @EventHandler
    public void onChat(BlockBreakEvent e) {
        if (e.getPlayer().getGameMode() != GameMode.CREATIVE) e.setCancelled(true);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onHold(InventoryClickEvent e) {
        //if (e.getWhoClicked().getGameMode() != GameMode.CREATIVE) e.setCancelled(true);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
            if (event.getDamager() instanceof Player) {
                if (((Player) event.getDamager()).getItemInHand() != null && ((Player) event.getDamager()).getItemInHand().getType() == Material.GOLD_SWORD) {
                    e.setDamage(1000);
                    return;
                }
            }
        }
        e.setDamage(0);
    }
}
