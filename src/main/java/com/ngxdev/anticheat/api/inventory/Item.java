package com.ngxdev.anticheat.api.inventory;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Item extends ItemStack implements Cloneable {
    public ClickAction action;

    public Item() {
    }

    public Item(Material type) {
        super(type);
    }

    public Item(Material type, int amount) {
        super(type, amount);
    }

    public Item(Material type, int amount, short damage) {
        super(type, amount, damage);
    }

    public Item(ItemStack stack) throws IllegalArgumentException {
        super(stack);
    }

    public Item setName(String name) {
        ItemMeta meta = this.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        this.setItemMeta(meta);
        return this;
    }

    public Item setLore(List<String> lore) {
        ItemMeta meta = this.getItemMeta();
        List<String> converted = new ArrayList<>();
        lore.forEach(l -> converted.add(ChatColor.translateAlternateColorCodes('&', l)));
        meta.setLore(converted);
        this.setItemMeta(meta);
        return this;
    }

    public Item setLore(String... lore) {
        ItemMeta meta = this.getItemMeta();
        List<String> converted = new ArrayList<>();
        for (String l : lore) converted.add(ChatColor.translateAlternateColorCodes('&', l));
        meta.setLore(converted);
        this.setItemMeta(meta);
        return this;
    }

    public Item setEnchant(Enchantment ench, int level) {
        ItemMeta meta = this.getItemMeta();
        meta.addEnchant(ench, level, true);
        this.setItemMeta(meta);
        return this;
    }

    public Item setEnchants(Map<Enchantment, Integer> enchants) {
        ItemMeta meta = this.getItemMeta();
        enchants.forEach((ench, level) -> meta.addEnchant(ench, level, true));
        this.setItemMeta(meta);
        return this;
    }

    public Item click(ClickAction action) {
        this.action = action;
        return this;
    }

    public Item clickEvent(ClickActionWithEvent action) {
        this.action = action;
        return this;
    }

    @Override
    public Item clone() {
        return new Item(this);
    }

    public interface ClickAction {
        void click(Player player);
    }

    public interface ClickActionWithEvent extends ClickAction {
        @Override
        default void click(Player player) {}

        void clickEvent(Player player, InventoryClickEvent event);
    }

    public static String getHashedMeta(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
       return item.getType().name() + meta.getDisplayName();
    }
}
