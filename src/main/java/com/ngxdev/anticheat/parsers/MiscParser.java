package com.ngxdev.anticheat.parsers;

import com.ngxdev.anticheat.api.check.Check;
import com.ngxdev.anticheat.api.check.type.Parser;
import com.ngxdev.anticheat.data.playerdata.PlayerData;
import com.ngxdev.tinyprotocol.packet.in.*;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPistonExtendEvent;

import static com.ngxdev.tinyprotocol.packet.in.WrappedInBlockDigPacket.EnumPlayerDigType.ABORT_DESTROY_BLOCK;
import static com.ngxdev.tinyprotocol.packet.in.WrappedInBlockDigPacket.EnumPlayerDigType.START_DESTROY_BLOCK;
import static com.ngxdev.tinyprotocol.packet.in.WrappedInBlockDigPacket.EnumPlayerDigType.STOP_DESTROY_BLOCK;
import static com.ngxdev.tinyprotocol.packet.in.WrappedInEntityActionPacket.EnumPlayerAction.START_SPRINTING;
import static com.ngxdev.tinyprotocol.packet.in.WrappedInEntityActionPacket.EnumPlayerAction.STOP_SPRINTING;
import static com.ngxdev.tinyprotocol.packet.in.WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK;

@Parser
public class MiscParser extends Check {
    boolean sprinting;

    void check(WrappedInEntityActionPacket packet) {
        if (packet.getAction() == START_SPRINTING) {
            sprinting = true;
        } else if (packet.getAction() == STOP_SPRINTING)  {
            sprinting = false;
        }
        if (sprinting) data.timers.lastSprint.reset();
    }

    void check(WrappedInUseEntityPacket packet) {
        if (packet.getAction() == ATTACK) data.timers.lastAttack.reset();
    }

    void check(WrappedInArmAnimationPacket packet) {
        data.timers.lastArmAnimation.reset();
    }

    void check(WrappedInBlockPlacePacket packet) {
        data.state.isPlacing = true;
    }

    void check(WrappedInBlockDigPacket packet) {
        if (packet.getAction() == START_DESTROY_BLOCK) {
            data.state.isDigging = true;
        } else if (packet.getAction() == ABORT_DESTROY_BLOCK || packet.getAction() == STOP_DESTROY_BLOCK) {
            data.state.isDigging = false;
        }
    }

    void check(WrappedInFlyingPacket packet) {
        data.state.isPlacing = false;
    }

    void check(WrappedInClientCommandPacket packet) {
        if (packet.getCommand() == WrappedInClientCommandPacket.EnumClientCommand.OPEN_INVENTORY_ACHIEVEMENT) {
            data.state.isInventoryOpen = true;
        }
    }
}
