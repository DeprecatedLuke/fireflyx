package com.ngxdev.anticheat.api.inventory;

import com.ngxdev.anticheat.utils.Utils;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;

public class CustomInventory {
    public static Map<Inventory, CustomInventory> inventoryStorage = new HashMap<>();

    public Map<String, Item.ClickAction> actions = new HashMap<>();
    public Map<Integer, Item> items = new HashMap<>();
    public Map<Integer, AnimatedItem> animated = new HashMap<>();
    public Map<Integer, ConditionalItem> conditional = new HashMap<>();
    public Map<Integer, ConditionalAnimatedItem> conditionalAnimated = new HashMap<>();
    public String name = "Default";
    public int size;

    public CustomInventory(int size) {
        this.size = size * 9;
    }

    public CustomInventory(int size, String name) {
        this(size);
        this.name = Utils.convert(name);
    }

    public void normal(Integer i, Item item) {
        items.put(i, add(item));
    }

    public void conditional(Integer i, Item item, Conditional cond) {
        conditional.put(i, new ConditionalItem(add(item), cond));
    }

    public void conditional(Integer i, AnimatedItem item, Conditional cond) {
        conditionalAnimated.put(i, new ConditionalAnimatedItem(item, cond));
    }

    public void animated(Integer i, AnimatedItem item) {
        animated.put(i, item);
    }

    public void open(Player p) {
        try {
            Inventory inv = name.equalsIgnoreCase(p.getOpenInventory().getTopInventory().getTitle()) ?
                    p.getOpenInventory().getTopInventory() : Bukkit.createInventory(null, size, name);
            inv.clear();
            items.forEach((slot, item) -> inv.setItem(slot, item.clone().setName(item.getItemMeta().getDisplayName())));
            animated.forEach((slot, item) -> {
                Item stack = add(item.getStack(p));
                inv.setItem(slot, stack.setName(stack.getItemMeta().getDisplayName()));
            });
            conditionalAnimated.entrySet().stream().filter(entry -> entry.getValue().conditional.validate(p)).forEach((entry) -> {
                Item stack = add(entry.getValue().item.getStack(p));
                inv.setItem(entry.getKey(), stack.setName(stack.getItemMeta().getDisplayName()));
            });
            conditional.entrySet().stream().filter(entry -> entry.getValue().conditional.validate(p)).forEach(entry -> inv.setItem(entry.getKey(), entry.getValue().item.clone().setName(entry.getValue().item.getItemMeta().getDisplayName())));
            p.openInventory(inv);
            inventoryStorage.put(inv, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Item add(Item item) {
        actions.put(Item.getHashedMeta(item), item.action);
        return item;
    }

    @AllArgsConstructor
    public static class ConditionalItem {
        public Item item;
        public Conditional conditional;
    }

    @AllArgsConstructor
    public static class ConditionalAnimatedItem {
        public AnimatedItem item;
        public Conditional conditional;
    }


    public interface AnimatedItem {
        public Item getStack(Player p);
    }


    public interface Conditional {
        public boolean validate(Player p);
    }
}
