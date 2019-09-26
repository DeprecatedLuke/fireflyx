package com.ngxdev.anticheat.guis;

import api.CheckWrapper;
import com.ngxdev.anticheat.Firefly;
import com.ngxdev.anticheat.api.check.Check;
import api.CheckType;
import com.ngxdev.anticheat.api.inventory.CustomInventory;
import com.ngxdev.anticheat.api.inventory.Item;
import com.ngxdev.anticheat.handler.CheckHandler;
import com.ngxdev.anticheat.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;

public class ModulesGUI extends CustomInventory {
	public static SectionGUI[] sections = new SectionGUI[CheckType.Type.values().length];

	public ModulesGUI() {
		super(2, "§6§lModules");

		for (int i = 0; i < sections.length; i++) {
			sections[i] = new SectionGUI(CheckType.Type.values()[i]);
		}

		for (CheckType.Type type : CheckType.Type.values()) {
			normal(type.ordinal(), new Item(type.icon).setName("§e§l" + Utils.capitalize(type.name().toLowerCase())).click(p -> {
				sections[type.ordinal()].open(p, 0);
			}));
		}
	}

	static class SectionGUI extends CustomInventory {
		private int maxPage;
		private CheckType.Type type;

		public SectionGUI(CheckType.Type type) {
			super(6, "§e§l" + Utils.capitalize(type.name().toLowerCase()) + " checks");
			this.type = type;
			int modules = 0;
			for (Class<? extends Check> t : CheckHandler.getWrappers().keySet()) {
				if (t.getAnnotation(CheckType.class).type() == type) modules++;
			}
			maxPage = (modules / 5) + 1;
		}

		public void open(Player p, int page) {
			try {
				Inventory inv = name.equalsIgnoreCase(p.getOpenInventory().getTopInventory().getTitle()) ?
						p.getOpenInventory().getTopInventory() : Bukkit.createInventory(null, size, name);
				inv.clear();
				for (int pg = 0; pg < maxPage; pg++) {
					final int fPg = pg;
					inv.setItem(pg, add(new Item(pg == page ? Material.SNOW : Material.SNOW_BLOCK).setName("§7§lPage " + (pg + 1)).click(p2 -> open(p, fPg))));
				}
				int n = 0;
				try {
					inv.setItem(8, add(new Item(Material.ARROW).setName("§1§7§lBack").clickEvent((c, e) -> {
						p.closeInventory();
						new ModulesGUI().open(p);
					})));
					for (int i = 5 * page; i < 5 + (5 * page); i++) {
						List<CheckWrapper> values = CheckHandler.getWrapper(this.type);

						CheckWrapper type = values.get(i);
						CheckType ct = CheckHandler.getClass(type.id()).getAnnotation(CheckType.class);
						inv.setItem(9 + (n * 9), new Item(this.type.icon)
								.setName("§6§l" + ct.name())
								.setLore(
										"§7Alerts: §" + (type.alert() ? "a" : "c") + "§l" + (type.alert() ? "ON" : "OFF"),
										"§7Cancel/Setback: §" + (type.cancel() ? "a" : "c") + "§l" + (type.cancel() ? "ON" : "OFF"),
										"§7Autobans: §" + (type.ban() ? "a" : "c") + "§l" + (type.ban() ? "ON" : "OFF"),
										"",
										"§7Alert Offset: §e" + ct.maxVl() + " §7(" + (type.alertOffset() > 0 ? "§a+" : "§c") + type.alertOffset() + "§7)",
										"§7Cancel/Setback Offset: §e" + ct.maxVl() + " §7(" + (type.cancelOffset() > 0 ? "§a+" : "§c") + type.cancelOffset() + "§7)",
										"§7Ban Offset: §e" + ct.maxVl() + " §7(" + (type.banOffset() > 0 ? "§a+" : "§c") + type.banOffset() + "§7)",
										"§7Expiration Offset: §e" + ct.timeout() + " §7(" + (type.expirationOffset() > 0 ? "§a+" : "§c") + type.expirationOffset() + "§7)"
								));
						inv.setItem(10 + (n * 9), add(new Item(Material.STAINED_GLASS_PANE, 1, (short) (type.alert() ? 5 : 14))
								.setName("§" + n + "§7Alerts: §" + (type.alert() ? "a" : "c") + "§l" + (type.alert() ? "ON" : "OFF"))
								.clickEvent((c, e) -> {
									type.alert(!type.alert());
									Firefly.storage.updateValue(type);
									open(p, page);
								})));
						inv.setItem(11 + (n * 9), add(new Item(Material.STAINED_GLASS_PANE, 1, (short) (type.cancel() ? 5 : 14))
								.setName("§" + n + "§7Cancel/Setback: §" + (type.cancel() ? "a" : "c") + "§l" + (type.cancel() ? "ON" : "OFF"))
								.clickEvent((c, e) -> {
									type.cancel(!type.cancel());
									Firefly.storage.updateValue(type);
									open(p, page);
								})));
						inv.setItem(12 + (n * 9), add(new Item(Material.STAINED_GLASS_PANE, 1, (short) (type.ban() ? 5 : 14))
								.setName("§" + n + "§7Autobans: §" + (type.ban() ? "a" : "c") + "§l" + (type.ban() ? "ON" : "OFF"))
								.clickEvent((c, e) -> {
									type.ban(!type.ban());
									Firefly.storage.updateValue(type);
									open(p, page);
								})));
						inv.setItem(13 + (n * 9), add(new Item(Material.STAINED_GLASS_PANE, 1, (short) 7)));
						inv.setItem(14 + (n * 9), add(new Item(Material.STAINED_GLASS_PANE)
								.setName("§" + n + "§7Alert Offset: §e" + ct.maxVl() + " §7(" + (type.alertOffset() > 0 ? "§a+" : "§c") + type.alertOffset() + "§7)")
								.setLore("§7Left-click to increase, right-click to subtract", "§7Shift-click adds/removes 10 at a time")
								.clickEvent((c, e) -> {
									type.alertOffset(type.alertOffset() + (e.isRightClick() ? -1 * (e.isShiftClick() ? 1 : 10) : (e.isShiftClick() ? 1 : 10)));
									Firefly.storage.updateValue(type);
									open(p, page);
								})));
						inv.setItem(15 + (n * 9), add(new Item(Material.STAINED_GLASS_PANE)
								.setName("§" + n + "§7Cancel/Setback Offset: §e" + ct.maxVl() + " §7(" + (type.cancelOffset() > 0 ? "§a+" : "§c") + type.cancelOffset() + "§7)")
								.setLore("§7Left-click to increase, right-click to subtract", "§7Shift-click adds/removes 10 at a time")
								.clickEvent((c, e) -> {
									type.cancelOffset(type.cancelOffset() + (e.isRightClick() ? -1 * (e.isShiftClick() ? 1 : 10) : (e.isShiftClick() ? 1 : 10)));
									Firefly.storage.updateValue(type);
									open(p, page);
								})));
						inv.setItem(16 + (n * 9), add(new Item(Material.STAINED_GLASS_PANE)
								.setName("§" + n + "§7Ban Offset: §e" + ct.maxVl() + " §7(" + (type.banOffset() > 0 ? "§a+" : "§c") + type.banOffset() + "§7)")
								.setLore("§7Left-click to increase, right-click to subtract", "§7Shift-click adds/removes 10 at a time")
								.clickEvent((c, e) -> {
									type.banOffset(type.banOffset() + (e.isRightClick() ? -1 * (e.isShiftClick() ? 1 : 10) : (e.isShiftClick() ? 1 : 10)));
									Firefly.storage.updateValue(type);
									open(p, page);
								})));
						inv.setItem(17 + (n * 9), add(new Item(Material.STAINED_GLASS_PANE)
								.setName("§" + n + "§7Expiration Offset: §e" + ct.timeout() + " §7(" + (type.expirationOffset() > 0 ? "§a+" : "§c") + type.expirationOffset() + "§7)")
								.setLore("§7Left-click to increase, right-click to subtract", "§7Shift-click adds/removes 10 at a time")
								.clickEvent((c, e) -> {
									type.expirationOffset(type.expirationOffset() + (e.isRightClick() ? -1 * (e.isShiftClick() ? 1 : 10) : (e.isShiftClick() ? 1 : 10)));
									Firefly.storage.updateValue(type);
									open(p, page);
								})));
						n++;
					}
				} catch (IndexOutOfBoundsException e) {

				}
				items.forEach((slot, item) -> {
					inv.setItem(slot, add(item.clone().setName(item.getItemMeta().getDisplayName())));
				});
				animated.forEach((slot, item) -> {
					Item stack = item.getStack(p);
					actions.put(Item.getHashedMeta(stack), stack.action);
					inv.setItem(slot, add(stack.setName(stack.getItemMeta().getDisplayName())));
				});
				conditionalAnimated.entrySet().stream().filter(entry -> entry.getValue().conditional.validate(p)).forEach((entry) -> {
					Item stack = entry.getValue().item.getStack(p);
					actions.put(Item.getHashedMeta(stack), stack.action);
					inv.setItem(entry.getKey(), add(stack.setName(stack.getItemMeta().getDisplayName())));
				});
				conditional.entrySet().stream().filter(entry -> entry.getValue().conditional.validate(p)).forEach(entry ->
						inv.setItem(entry.getKey(), add(entry.getValue().item.clone().setName(entry.getValue().item.getItemMeta().getDisplayName()))));
				inventoryStorage.put(inv, this);
				if (inv == p.getOpenInventory().getTopInventory()) p.updateInventory();
				else p.openInventory(inv);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
