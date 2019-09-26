package com.ngxdev.anticheat.storage;

import api.CheckWrapper;
import api.ViolationX;
import com.ngxdev.anticheat.Firefly;
import com.ngxdev.anticheat.handler.CheckHandler;
import com.ngxdev.anticheat.storage.mysql.MySQL;
import com.ngxdev.anticheat.storage.sqlite.Query;
import com.ngxdev.anticheat.utils.Utils;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class MySQLStorage implements Storage {
    private ConcurrentLinkedQueue<ViolationX> violations = new ConcurrentLinkedQueue<>();

    @Override
    public void init() {
        MySQL.init();
        Query.prepare("CREATE TABLE IF NOT EXISTS `ALERTS` (" +
                "`ID` INT PRIMARY KEY AUTO_INCREMENT," +
                "`UUID` VARCHAR(32) NOT NULL," +
                "`MODULE` TEXT NOT NULL," +
                "`VL` SMALLINT NOT NULL," +
                "`TIME` LONG NOT NULL," +
                "`EXTRA` TEXT)").execute();
        Query.prepare("CREATE TABLE IF NOT EXISTS `CHECKS` (" +
                "`MODULE` VARCHAR(16) PRIMARY KEY NOT NULL," +
                "`ALERT` BOOLEAN NOT NULL," +
                "`CANCEL` BOOLEAN NOT NULL," +
                "`AUTOBAN` BOOLEAN NOT NULL," +
                "`BANVL` SMALLINT NOT NULL," +
                "`CANCELVL` SMALLINT NOT NULL," +
                "`ALERTVL` SMALLINT NOT NULL)").execute();
        Query.prepare("CREATE TABLE IF NOT EXISTS `CONFIG` (" +
                "`KEY` VARCHAR(64) PRIMARY KEY NOT NULL," +
                "`VALUE` VARCHAR(1028) NOT NULL)").execute();

        new Thread(() -> {
            //Update configuration from mysql every 1 minute
            MySQL.use();
            List<CheckWrapper> existing = new ArrayList<>();
            Query.prepare("SELECT * FROM CHECKS").execute(rs -> {
                CheckWrapper type = CheckHandler.getWrapper(rs.getString(1));
                if (type == null) return;
                existing.add(type);
            });
            for (CheckWrapper type : CheckHandler.getWrappers().values()) {
                if (!existing.contains(type.id())) {
                    updateValue(type);
                }
            }
            while (Firefly.getInstance() != null && Firefly.getInstance().isEnabled()) {
                try {
                    MySQL.use();
                    Query.prepare("SELECT * FROM CHECKS").execute(rs -> {
                        CheckWrapper type = CheckHandler.getWrapper(rs.getString(1));
                        if (type == null) return;
                        type.alert(rs.getBoolean(2));
                        type.cancel(rs.getBoolean(3));
                        type.ban(rs.getBoolean(4));
                        type.banOffset(rs.getInt(5));
                        type.cancelOffset(rs.getInt(6));
                        type.alertOffset(rs.getInt(7));
                    });
                    Utils.sleep(TimeUnit.MINUTES.toMillis(1));
                } catch (Exception e) {
                    e.printStackTrace();
                    Utils.sleep(TimeUnit.MINUTES.toMillis(1));
                }
            }
        }, "FireflyMySQLConfigUpdater").start();
        new Thread(() -> {
            while (Firefly.getInstance() != null && Firefly.getInstance().isEnabled()) {
                try {
                    Utils.sleep(1000);
                    if (violations.isEmpty()) continue;
                    for (ViolationX violation : violations) {
                        try {
                            MySQL.use();
                            Query.prepare("INSERT INTO `ALERTS` (`UUID`, `MODULE`, `VL`, `TIME`, `EXTRA`) VALUES (?,?,?,?,?)")
                                    .append(violation.player).append(violation.type).append(violation.vl)
                                    .append(violation.time).append(violation.extra).execute();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    violations.clear();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, "FireflyMySQLCommitter").start();

    }

    @Override
    public String getString(String key) {
        try {
            return Query.prepare("SELECT `VALUE` FROM `CONFIG` WHERE KEY = ?").append(key).executeQuery().getString(1);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void set(String key, Object value) {
        Query.prepare("DELETE FROM `CONFIG` WHERE KEY = ?").append(key).execute();
        Query.prepare("INSERT INTO `CONFIG` (`KEY`,`VALUE`) VALUES(?,?)").append(key).append(value).execute();
    }

    @Override
    public void updateValue(CheckWrapper type) {
        MySQL.use();
        Query.prepare("DELETE FROM `CHECKS` WHERE `MODULE` = ?").append(type.id()).execute();
        Query.prepare("INSERT INTO `CHECKS` (`MODULE`,`ALERT`,`CANCEL`,`AUTOBAN`,`BANVL`,`CANCELVL`,`ALERTVL`) VALUES (?,?,?,?,?,?,?)")
                .append(type.id())
                .append(type.alert())
                .append(type.cancel())
                .append(type.ban())
                .append(type.banOffset())
                .append(type.cancelOffset())
                .append(type.alertOffset()).execute();
    }

    @Override
    public void addAlert(ViolationX violation) {
        violations.add(violation);
    }

    @Override
    public List<ViolationX> getViolations(UUID uuid, CheckWrapper type, int page, int limit, long from, long to) {
        MySQL.use();
        List<ViolationX> violations = new ArrayList<>();
        Query.prepare("SELECT `MODULE`, `VL`, `TIME`, `EXTRA` FROM `ALERTS` WHERE `UUID` = ? ORDER BY `ID` DESC LIMIT ?,?")
                .append(uuid).append(page * limit).append(limit).execute(rs -> {
            violations.add(new ViolationX(uuid, rs.getString(1), rs.getInt(2), rs.getLong(3), rs.getString(4)));
        });
        return violations;
    }

    @Override
    public Map<CheckWrapper, Integer> getHighestViolations(UUID uuid, CheckWrapper type, long from, long to) {
        MySQL.use();
        Map<CheckWrapper, Integer> map = new LinkedHashMap<>();
        Query.prepare("SELECT MODULE,COUNT(*) AS COUNT FROM ALERTS WHERE UUID = ? GROUP BY UUID,MODULE ORDER BY COUNT DESC").append(uuid).execute(rs -> {
            map.put(CheckHandler.getWrapper(rs.getString(1)), rs.getInt(2));
        });
        return map;
    }
}
