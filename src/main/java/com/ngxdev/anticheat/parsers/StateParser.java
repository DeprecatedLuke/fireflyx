package com.ngxdev.anticheat.parsers;

import com.ngxdev.anticheat.api.check.Check;
import com.ngxdev.anticheat.api.check.type.Parser;
import com.ngxdev.anticheat.data.playerdata.PlayerData;
import com.ngxdev.tinyprotocol.packet.in.WrappedInEntityActionPacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

@Parser
public class StateParser extends Check implements Listener {
    boolean isSneaking;

    void check(PlayerToggleFlightEvent e) {
        data.timers.lastFlightToggle.reset();
    }

    void check(PlayerGameModeChangeEvent e) {
        data.timers.lastFlightToggle.reset();
    }

    void check(WrappedInFlyingPacket packet) {
        if (isSneaking) {
            data.timers.lastSneak.reset();
        }
    }

    void check(PlayerToggleSneakEvent e) {
        if (e.isCancelled()) return;
        isSneaking = e.isSneaking();
    }
}
