/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 */

package com.ngxdev.anticheat;

import api.ConfigValueX;
import api.FireflyAPI;
import api.FireflyXViolationEvent;
import com.ngxdev.anticheat.api.cmd.FireflyCommand;
import com.ngxdev.anticheat.data.playerdata.PlayerData;
import com.ngxdev.anticheat.dev.DevServerListener;
import com.ngxdev.anticheat.handler.CheckHandler;
import com.ngxdev.anticheat.handler.TinyProtocolHandler;
import com.ngxdev.anticheat.storage.LocalStorage;
import com.ngxdev.anticheat.storage.MongoStorage;
import com.ngxdev.anticheat.storage.MySQLStorage;
import com.ngxdev.anticheat.storage.Storage;
import com.ngxdev.anticheat.storage.mongo.MongoDatabase;
import com.ngxdev.anticheat.storage.mysql.MySQL;
import com.ngxdev.anticheat.utils.EntityIdCache;
import com.ngxdev.anticheat.utils.Init;
import com.ngxdev.anticheat.utils.Utils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Method;
import java.util.*;

public class Firefly extends JavaPlugin {
    public static List<String> ignored = new ArrayList<>();
    public static boolean devServer = System.getProperty("devServer", "false").equalsIgnoreCase("true");
    @Getter
    private static String prefix = "§6Firefly§f§lX §7//";
    private static Firefly instance;
    private static File ignoredFile;
    public static FileConfiguration config;
    public static Storage storage;
    private static String ap = "instance";
	public static long lastTick = System.currentTimeMillis();

    public static Firefly getInstance() {
        return instance;
    }

	public Firefly(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file, Collection<byte[]> files, JavaPlugin nloader) {
		super(loader, description, dataFolder, file);
		instance = this;
		Utils.injectorClassLoader = nloader.getClass().getClassLoader();
		try {
			Method m = loader.getClass().getDeclaredMethod("setClass", String.class, Class.class);
			m.setAccessible(true);
			m.invoke(loader, "api.FireflyXViolationEvent", FireflyXViolationEvent.class);
			m.invoke(loader, "api.ConfigValueX", ConfigValueX.class);
			m.invoke(loader, "api.FireflyAPI", FireflyAPI.class);
			ap = "remote";
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    public Firefly() {
        instance = this;
        devServer = true;
	    ap = "local";
    }

	@Override
    public void onEnable() {
		EntityIdCache.getNextId();
		Bukkit.getScheduler().runTaskTimer(this, () -> {
			long delta = System.currentTimeMillis() - lastTick;

			if (delta > 75) {
				PlayerData.getAll().forEach(data -> {
					data.lag.skips += (delta / 50) * 3;
					data.lag.skips = Math.max(data.lag.skips, 2);
				});
			}
			lastTick = System.currentTimeMillis();
		}, 0, 0);

		new TinyProtocolHandler();
        File file = new File(getDataFolder(), "config.yml");
        if (!file.exists()) {
            try {
                file.getParentFile().mkdir();
                file.createNewFile();
                new FileWriter(file)
                        .append("# #####################################\n")
                        .append("#                                     #\n")
                        .append("# FireflyX configuration file         #\n")
                        .append("#                                     #\n")
                        .append("# StorageTypes | SQLITE, MYSQL, MONGO #\n")
                        .append("#                                     #\n")
                        .append("# #####################################\n")
                        .flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ignoredFile = new File(getDataFolder(), "ignored.yml");
        if (!ignoredFile.exists()) {
            try {
                ignoredFile.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        config = getConfig();
        setDefault("storage.type", "SQLITE");
        setDefault("storage.host", "localhost");
        setDefault("storage.port", "3307");
        setDefault("storage.user", "root");
        setDefault("storage.password", "password");
        setDefault("storage.database", "ffx");
		new CheckHandler();
        switch (config.getString("storage.type").toLowerCase()) {
            case "mongo": {
                MongoDatabase.init();
                storage = new MongoStorage();
                break;
            }
            case "mysql": {
                MySQL.init();
                storage = new MySQLStorage();
                break;
            }
            default: {
                storage = new LocalStorage();
                break;
            }
        }
        storage.init();
        ConfigValueX.values();
        save();
	    if (devServer) {
		    Bukkit.getPluginManager().registerEvents(new DevServerListener(), this);
	    } else {
		    if (ap.equals("instance") || ap.equals("local")) return;
	    }
	    try {
		    Init.Dynamic.get().forEach(clazz -> {
			    try {
				    Object obj = clazz.newInstance();
				    if (obj instanceof Listener) {
					    Bukkit.getPluginManager().registerEvents((Listener) obj, this);
				    }
			    } catch (Exception e) {
				    e.printStackTrace();
			    }
		    });
	    } catch (Throwable e) {
		    e.printStackTrace();
	    }

        FireflyCommand.initCommands();
        FileConfiguration ignoredCfg = new YamlConfiguration();
        try {
            ignoredCfg.load(ignoredFile);
            ignored = ignoredCfg.getStringList("ignored");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	private void printStackTrace(StackTraceElement[] var1) {
		for (StackTraceElement elem : var1) {
			System.err.println(elem);
		}
	}


	public void setDefault(String path, String value) {
        if (!config.isSet(path)) config.set(path, value);
    }

    @Override
    public void onDisable() {
    	try {
		    PlayerData.getAll().forEach(d -> {
			    d.scheduledTasks.values().forEach(BukkitTask::cancel);
			    d.scheduledTasks.clear();
		    });
		    instance = null;
		    TinyProtocolHandler.instance.close();
		    FireflyCommand.unregisterAll();
		    Bukkit.getScheduler().cancelTasks(this);
		    HandlerList.unregisterAll(this);
	    } catch (Exception e) {
    		e.printStackTrace();
	    }
    }

    public static void save() {
        instance.saveConfig();
    }

    public static void saveIgnored() {
        FileConfiguration ignoredCfg = new YamlConfiguration();
        try {
            ignoredCfg.set("ignored", ignored);
            ignoredCfg.save(ignoredFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
