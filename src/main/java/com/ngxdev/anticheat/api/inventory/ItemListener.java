package com.ngxdev.anticheat.api.inventory;

import com.ngxdev.anticheat.utils.Init;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

@Init
public class ItemListener implements Listener {
    @EventHandler
    public void onEvent(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        CustomInventory inv = CustomInventory.inventoryStorage.get(e.getClickedInventory());
        if (inv != null) {
            e.setCancelled(true);
            ItemStack clicked = e.getCurrentItem();
            if (clicked != null && clicked.hasItemMeta()) {
                Item.ClickAction action = inv.actions.get(Item.getHashedMeta(clicked));
                if (action != null) {
                    action.click(p);
                    if (action instanceof Item.ClickActionWithEvent) {
                        ((Item.ClickActionWithEvent) action).clickEvent(p, e);
                    }
                }
            }
        }
    }
}
