package com.ngxdev.anticheat;

import api.CheckType;
import api.CheckWrapper;
import api.Command;
import api.ConfigValueX;
import api.ViolationX;
import com.ngxdev.anticheat.api.check.Check;
import com.ngxdev.anticheat.api.cmd.FireflyCommand;
import com.ngxdev.anticheat.data.playerdata.PlayerData;
import com.ngxdev.anticheat.guis.ModulesGUI;
import com.ngxdev.anticheat.handler.CheckHandler;
import com.ngxdev.anticheat.utils.MojangAPI;
import com.ngxdev.anticheat.utils.Utils;
import com.ngxdev.anticheat.utils.profiler.InstantProfiler;
import com.ngxdev.anticheat.utils.profiler.NoOpProfiler;
import com.ngxdev.anticheat.utils.profiler.Profiler;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@Command(name = "fireflyx", alias = {"ac", "ff", "ffx", "yesthisserveriusingfireflylol"}, permission = "firefly")
public class FireflyXCommand extends FireflyCommand {
	public static Profiler profiler = new NoOpProfiler();

	@Override
	protected void consoleSender(CommandSender sender, String command, String[] args) {
		try {
			if (args.length > 0) {
				switch (args[0]) {
					case "notifystaff": {
						for (Player who : Bukkit.getOnlinePlayers()) {
							if ((who.hasPermission("firefly.notify") || who.isOp() || Firefly.devServer) && !Firefly.ignored.contains(who.getName().toLowerCase()))
								who.sendMessage(Utils.convert(getArgs(args, 1)));
						}
						return;
					}
					case "broadcast": {
						for (Player who : Bukkit.getOnlinePlayers()) {
							who.sendMessage(Utils.convert(getArgs(args, 1)));
						}
						return;
					}
					case "export": {
						for (Class<? extends Check> check : CheckHandler.getWrappers().keySet().stream().sorted(Comparator.comparing(c -> c.getAnnotation(CheckType.class).id())).collect(Collectors.toList())) {
							CheckType type = check.getAnnotation(CheckType.class);
							System.out.println(type.id() + " (" + type.name() + ") | T:" + type.type().name() + " V:" + type.state() + " | ");
						}
					}
				}
			}
			sender.sendMessage(Firefly.getPrefix() + " §7Help");
			sender.sendMessage("");
			if (sender.hasPermission("firefly.togglealerts") || sender.isOp())
				sender.sendMessage("§e/ff togglealerts,ta §7- §fToggle anticheat alerts");
			if (sender.hasPermission("firefly.lookup") || sender.isOp())
				sender.sendMessage("§e/ff lookup §7- §fOverview of player alerts.");
			if (sender.hasPermission("firefly.query") || sender.isOp())
				sender.sendMessage("§e/ff query §7- §fView in-depth alert information.");
			if (sender.hasPermission("firefly.edit") || sender.isOp())
				sender.sendMessage("§e/ff edit §7- §fView anticheat settings.");
			if (sender.hasPermission("firefly.gui") || sender.isOp())
				sender.sendMessage("§e/ff gui §7- §fView anticheat GUI.");
			if (sender.hasPermission("firefly.reload") || sender.isOp())
				sender.sendMessage("§e/ff reload §7- §fReload the anticheat configuration.");
			if (sender.hasPermission("firefly.settings") || sender.isOp())
				sender.sendMessage("§e/ff settings §7- §fView & edit anticheat check config.");
			if (sender.hasPermission("firefly.nobypass") || sender.isOp())
				sender.sendMessage("§e/ff nobypass §7- §fDisable permissions bypass for yourself.");
		} catch (NumberFormatException e) {
			sender.sendMessage("§cInvalid number.");
		} catch (IllegalArgumentException e) {
			sender.sendMessage("§c" + e.getMessage());
		}
	}

	@Override
	protected void playerSender(Player player, String command, String[] args) {
		try {
			if (args.length > 0) {
				if (player.hasPermission("firefly." + args[0].toLowerCase())) {
					switch (args[0]) {
						case "profiler": {
							PlayerData data = PlayerData.get(player);
							if (data.scheduledTasks.containsKey("profiler")) {
								data.stop("profiler");
								profiler = new NoOpProfiler();
							} else {
								profiler = new InstantProfiler();
								InstantProfiler profiler = ((InstantProfiler) FireflyXCommand.profiler);
								int[] totalTime = {0};
								data.schedule("profiler", () -> {
									totalTime[0] += 50;
									Map<String, Long> totals = new HashMap<>();
									Map<String, Long> samples = new HashMap<>();
									Map<String, Long> stddev = new HashMap<>();
									long[] total = {0};
									data.allChecks.forEach(m -> {
										profiler.total.forEach((name, sample) -> {
											total[0] = total[0] + sample;
											totals.put(m.getClass().getSimpleName() + ":" + name, sample);
										});
										profiler.samples.forEach((name, sample) -> samples.put(m.getClass().getSimpleName() + ":" + name, sample));
										profiler.stddev.forEach((name, sample) -> stddev.put(m.getClass().getSimpleName() + ":" + name, sample));
									});
									profiler.samples.forEach(samples::put);
									profiler.stddev.forEach(stddev::put);
									Map<String, Long> sorted = Utils.sortByValue(totals);
									int size = sorted.size();
									List<Map.Entry<String, Long>> entries = new ArrayList<>(sorted.entrySet());
									player.sendMessage("-------------------------------------------------");

									for (int i = size - Math.min(size - 10, 10); i < size; i++) {
										Map.Entry<String, Long> entry = entries.get(i);
										String name = entry.getKey();
										Long time = entry.getValue();
										player.sendMessage(Utils.drawUsage(total[0], time) + " §c" + name + "§7: " + Utils.format(time / 1000000D, 3) + ", " + Utils.format(samples.getOrDefault(name, 0L) / 1000000D, 3) + ", " + Utils.format(stddev.getOrDefault(name, 0L) / 1000000D, 3));
									}
									double totalMs = total[0] / 1000000D;
									player.sendMessage(Utils.drawUsage(total[0], total[0]) + " §cTotal§7: " + Utils.format(totalMs, 3) + " §f- §c" + Utils.format(totalMs / totalTime[0], 3) + "%");
								}, 0, 0);
							}
							return;
						}
						case "gui": {
							new ModulesGUI().open(player);
							return;
						}
						case "ping": {
							PlayerData data = PlayerData.get(player);
							player.sendMessage("§7Estimated Ping: §e" + data.lag.transactionPing + "ms");
							return;
						}
						case "viewall": {
							PlayerData data = PlayerData.get(player);
							data.debug.viewall = !data.debug.viewall;
							player.sendMessage("VA: " + data.debug.viewall);
							return;
						}
						case "debugtp": {
							PlayerData data = PlayerData.get(player);
							data.debug.debugtp = !data.debug.debugtp;
							player.sendMessage("§7Debugging setbacks: §c" + data.debug.debugtp);
							return;
						}
						case "testserverlag": {
							Bukkit.getScheduler().runTask(Firefly.getInstance(), () -> {
								try {
									Thread.sleep(Long.parseLong(args[1]));
									player.sendMessage("Testing server lag");
								} catch (Exception e) {
									e.printStackTrace();
								}
							});
							return;
						}
						case "ta":
						case "togglealerts": {
							String name = player.getName().toLowerCase();
							if (Firefly.ignored.contains(name)) {
								Firefly.ignored.remove(name);
								player.sendMessage("§aYou will now see anticheat alerts.");
							} else {
								Firefly.ignored.add(name);
								player.sendMessage("§cYou will no longer see anticheat alerts.");
							}
							Firefly.saveIgnored();
							return;
						}
						case "testvel": {
							for (int i = 0; i < Integer.parseInt(args[1]); i++) {
								Bukkit.getScheduler().runTaskLater(Firefly.getInstance(), () -> {
									Random random = new Random();
									player.setVelocity(new Vector(random.nextDouble() - 0.5, 0.3, random.nextDouble() - 0.5));
								}, i * Integer.parseInt(args[2]));
							}
							return;
						}
						case "nobypass": {
							PlayerData data = PlayerData.get(player);
							data.debug.nobypass = !data.debug.nobypass;
							player.sendMessage("§7NoBypass: §c" + data.debug.nobypass);
							return;
						}
						case "lookup": {
							Bukkit.getScheduler().runTaskAsynchronously(Firefly.getInstance(), () -> {
								try {
									if (args.length == 2) {
										UUID uuid = MojangAPI.getUUID(args[1]);
										if (uuid == null) {
											uuid = Bukkit.getOfflinePlayer(args[1]).getUniqueId();
										}
										if (uuid != null) {
											player.sendMessage("§6Looking up violations for: §e" + args[1]);
											Map<CheckWrapper, Integer> vls = Firefly.storage.getHighestViolations(uuid, null, 0, 0);
											if (vls == null) throw error("Player has no violations.");
											for (Map.Entry<CheckWrapper, Integer> entry : vls.entrySet()) {
												if (entry.getKey() != null) {
													player.sendMessage("§6" + entry.getKey().id() + "§7: §ex" + entry.getValue());
												}
											}
										} else player.sendMessage("§cThat player does not exist.");
										return;
									}
									player.sendMessage("§cUsage /ac lookup <player>");
								} catch (IllegalArgumentException e) {
									player.sendMessage("§c" + e.getMessage());
								}
							});
							return;
						}
						case "query": {
							Bukkit.getScheduler().runTaskAsynchronously(Firefly.getInstance(), () -> {
								try {
									if (args.length >= 2) {
										UUID uuid = MojangAPI.getUUID(args[1]);
										int page = args.length == 3 ? Integer.parseInt(args[2]) : 0;
										if (uuid != null) {
											List<ViolationX> vls = Firefly.storage.getViolations(uuid, null, page, 10, -1, -1);
											if (vls.isEmpty()) throw error("Player has no violations.");
											player.sendMessage("§7Showing alert information of §c" + args[1] + "§7, page " + page);
											for (ViolationX v : vls) {
												player.sendMessage("§7Failed §c" + v.type + (v.extra != null ? "§7: §c" + v.extra : "") + " §7VL: §c" + Utils.format(v.vl, 1) + " §8(§7" + Utils.formatMillis(System.currentTimeMillis() - v.time) + " ago§8)");
											}
										} else throw error("That player does not exist.");
									} else throw error("/ac query <player>");
								} catch (IllegalArgumentException e) {
									player.sendMessage("§c" + e.getMessage());
								}
							});
							return;
						}
						case "cfg":
						case "config": {
							if (args.length == 1) {
								player.sendMessage("§4Configuration values§7:");
								for (ConfigValueX value : ConfigValueX.values()) {
									if (value.value.toString().length() > 20) {
										player.sendMessage("§c" + value.id + "§7:");
										player.sendMessage(" " + value.value);
									} else {
										player.sendMessage("§c" + value.id + "§7: §f" + value.value);
									}
								}
							} else if (args.length >= 3) {
								ConfigValueX value = ConfigValueX.get(args[1]);
								if (value != null) {
									if (value.type == Boolean.class) {
										Boolean val = Boolean.parseBoolean(args[2]);
										value.value = val;
										Firefly.storage.set(value.id, val);
									} else if (value.type == Double.class) {
										Double val = Double.parseDouble(args[2]);
										value.value = val;
										Firefly.storage.set(value.id, val);
									} else {
										String val = getArgs(args, 2);
										value.value = val;
										Firefly.storage.set(value.id, val);
									}
									player.sendMessage("§7Set the value of §c" + value.name().toLowerCase() + " §7to §c" + value.value);
								} else player.sendMessage("§cInvalid value: " + args[1]);
							} else {
								player.sendMessage("§c/ff cfg <id> <value>");
							}
							return;
						}
						case "checks": {
							if (args.length == 1) {
								player.sendMessage("§cUsage /ff checks <view> | <edit> <check> <ban/cancel/alert/banvl/cancelvl/alertvl> <true/false/number>");
							} else {
								switch (args[1].toLowerCase()) {
									case "view":
										if (args.length == 3) {
											CheckType.Type section = CheckType.Type.valueOf(args[2]);
											if (section != null) {
												player.sendMessage("§7Firefly check settings for " + section.name().toLowerCase() + ":");
												for (CheckWrapper type : CheckHandler.getWrapper(section)) {
													CheckType ct = CheckHandler.getClass(type.id()).getAnnotation(CheckType.class);
													player.sendMessage(" §7❘"
															+ (type.alert() ? "§a" : "§c") + " §lA§7(" + ct.maxVl() + (type.alertOffset() >= 0 ? "§a+" : "§c") + type.alertOffset() + "§7) "
															+ (type.cancel() ? "§a" : "§c") + " §lC§7(" + ct.maxVl() + (type.cancelOffset() >= 0 ? "§a+" : "§c") + type.cancelOffset() + "§7) "
															+ (type.ban() ? "§2" : "§4") + " §lB§7(" + ct.maxVl() + (type.banOffset() >= 0 ? "§a+" : "§c") + type.banOffset() + "§7) "
															+ "§7❘ §7TIMEOUT: " + ct.timeout() + (type.expirationOffset() >= 0 ? "§a+" : "§c") + type.expirationOffset()
															+ " §7❘ §c" + type.id());
												}
												return;
											}
										}
										player.sendMessage("§c/ff settings view <" + Utils.join(Arrays.stream(CheckType.Type.values()).map(Enum::name), ",") + ">");
										return;
									case "edit":
										if (args.length == 5) {
											CheckWrapper type = CheckHandler.getWrapper(args[2]);
											if (type == null) {
												player.sendMessage("§cUnknown check: " + args[2]);
												return;
											}
											switch (args[3].toLowerCase()) {
												case "ban": {
													Boolean bol = Boolean.parseBoolean(args[4]);
													type.ban(bol);
													player.sendMessage("§7You have " + (bol ? "§aenabled" : "§cdisabled") + " §7autobans for §c" + type.id());
													break;
												}
												case "cancel": {
													Boolean bol = Boolean.parseBoolean(args[4]);
													type.cancel(bol);
													player.sendMessage("§7You have " + (bol ? "§aenabled" : "§cdisabled") + " §7cancel/setback for §c" + type.id());
													break;
												}
												case "alert": {
													Boolean bol = Boolean.parseBoolean(args[4]);
													type.alert(bol);
													player.sendMessage("§7You have " + (bol ? "§aenabled" : "§cdisabled") + " §7alerts for §c" + type.id());
													break;
												}
												case "banvl": {
													type.banOffset(Integer.parseInt(args[4]));
													player.sendMessage("§7You have set banVl to §c" + type.banOffset() + " §7for §c" + type.id());
													break;
												}
												case "cancelvl": {
													type.cancelOffset(Integer.parseInt(args[4]));
													player.sendMessage("§7You have set alertVl to §c" + type.alertOffset() + " §7for §c" + type.id());
													break;
												}
												case "alertvl": {
													type.alertOffset(Integer.parseInt(args[4]));
													player.sendMessage("§7You have set alertVl to §c" + type.alertOffset() + " §7for §c" + type.id());
													break;
												}
											}
											Firefly.storage.updateValue(type);
											return;
										}
								}
								player.sendMessage("§cUsage /ff checks <view> | <edit> <check> <ban/cancel/alert/banvl/cancelvl/alertvl> <true/false/number>");
							}
							return;
						}
					}
				}
				player.sendMessage(Firefly.getPrefix() + " §7Help");
				player.sendMessage("");
				if (player.hasPermission("firefly.togglealerts") || player.isOp())
					player.sendMessage("§e/ff togglealerts,ta §7- §fToggle anticheat alerts");
				if (player.hasPermission("firefly.lookup") || player.isOp())
					player.sendMessage("§e/ff lookup §7- §fOverview of player alerts.");
				if (player.hasPermission("firefly.query") || player.isOp())
					player.sendMessage("§e/ff query §7- §fView in-depth alert information.");
				if (player.hasPermission("firefly.edit") || player.isOp())
					player.sendMessage("§e/ff edit §7- §fView anticheat settings.");
				if (player.hasPermission("firefly.gui") || player.isOp())
					player.sendMessage("§e/ff gui §7- §fView anticheat GUI.");
				//if (player.hasPermission("firefly.reload") || player.isOp())
				//	player.sendMessage("§e/ff reload §7- §fReload the anticheat configuration.");
				if (player.hasPermission("firefly.settings") || player.isOp())
					player.sendMessage("§e/ff settings §7- §fView & edit anticheat check config.");
				if (player.hasPermission("firefly.nobypass") || player.isOp())
					player.sendMessage("§e/ff nobypass §7- §fDisable permissions bypass for yourself.");
			}
		} catch (NumberFormatException e) {
			player.sendMessage("§cInvalid number.");
		} catch (IllegalArgumentException e) {
			player.sendMessage("§c" + e.getMessage());
		}
	}

	public static IllegalArgumentException error(String error) {
		return new IllegalArgumentException("§c" + error);
	}
}
