package api;

import com.ngxdev.anticheat.Firefly;

public enum ConfigValueX {
    ALERT_TIMEOUT("alert.timeout", 5000),
    REACH_CANCEL("reach.cancel", 3.0),
    REACH_BAN("reach.flag", 3.3),
    BLOCK_HITS_TIME("block.hits.time", 10),
    ANTICHEAT_NAME("anticheat.name", "&6Firefly&f&lX"),
    ALERT_DEBUG("alert.debug", "&8/&7{debug}"),
    ALERT_VIOLATIONS("alert.violations", "&7{vl}&8/&7{max}&8/&7{total}"),
    COMMAND_BAN("command.ban", "ban {player} {name} &7// &cUnfair Advantage|broadcast {player} has been banned for cheating!"),
    COMMAND_ALERT("command.alert", "ffx notifystaff {name} &7// &e{player} &7has failed: &e{module} &8[{vl}{debug}&8]"),;

    public String id;
    public Object value;
    public Class<?> type;

    ConfigValueX(String id, Object value) {
        this.id = id;
        this.type = value.getClass();
        try {
            populate(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void populate(Object value) {
        if (Firefly.storage.getString(id) == null) {
            Firefly.storage.set(id, value);
        }
        if (value instanceof String) {
            this.value = Firefly.storage.getString(id, (String) value);
        } else if (value instanceof Boolean) {
            this.value = Firefly.storage.getBoolean(id, (Boolean) value);
        } else if (value instanceof Integer) {
            this.value = (int) Firefly.storage.getInt(id, (Integer) value);
        } else if (value instanceof Double) {
            this.value = Firefly.storage.getDouble(id, (Double) value);
        }
    }

    public String asString() {
        return (String) value;
    }

    public Boolean asBoolean() {
        return (Boolean) value;
    }

    public Double asDouble() {
        return (Double) value;
    }

    public Integer asInteger() {
        return (Integer) value;
    }

    public static ConfigValueX get(String val) {
        for (ConfigValueX value : ConfigValueX.values()) if (value.id.equalsIgnoreCase(val)) return value;
        return null;
    }
}
