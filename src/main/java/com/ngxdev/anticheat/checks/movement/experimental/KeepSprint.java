package com.ngxdev.anticheat.checks.movement.experimental;

import com.ngxdev.anticheat.api.check.Check;
import api.CheckType;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import static api.CheckType.Type.MOVEMENT;

@CheckType(id = "keepsprint", name = "KeepSprint", type = MOVEMENT, maxVl = 2, state = CheckType.State.EXPERIMENTAL)
public class KeepSprint extends Check implements Listener {
    boolean hasSlowedDown;

    void check(EntityDamageByEntityEvent e) {
        if (!canCheckMovement()
                || !isMoving()) return;
        if (e.getDamager() instanceof Player) {
            if (e.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) return;
            if (player.isSprinting()) hasSlowedDown = true;
            if (e.getEntity() instanceof LivingEntity) data.timers.lastSprintCancel.reset();
        }
    }

    void check(WrappedInFlyingPacket packet) {
        if (!canCheck()
                || !isMoving()
                || !packet.isPos()) return;
        if (hasSlowedDown) {
            if (!player.isSprinting()) hasSlowedDown = false;
            if (data.movement.deltaH == 0.0) hasSlowedDown = false;
            int delta = Math.abs((int) ((data.movement.lastDeltaH - data.movement.deltaH) * 100000));
            if (delta > 750) {
                hasSlowedDown = false;
            }
            if (data.timers.lastSprintCancel.hasPassed(10)) {
                if (hasSlowedDown) if (fail()) setback();
                hasSlowedDown = false;
            }
        }
    }
}

