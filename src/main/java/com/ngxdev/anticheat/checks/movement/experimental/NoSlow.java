package com.ngxdev.anticheat.checks.movement.experimental;

import com.ngxdev.anticheat.api.check.Check;
import com.ngxdev.anticheat.api.check.Priority;
import api.CheckType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;

import static com.ngxdev.anticheat.api.check.Priority.Value.NORMAL;
import static api.CheckType.Type.MOVEMENT;

@CheckType(id = "noslow", name = "NoSlow", type = MOVEMENT, state = CheckType.State.EXPERIMENTAL)
public class NoSlow extends Check {
    @Priority(NORMAL)
    void check(PlayerInteractEvent e) {
        if (e.getItem() != null && e.getItem().getType() == Material.BOW) {
            if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                data.state.isUsingBow = true;
            }
        }
    }

    @Priority(NORMAL)
    void check(EntityShootBowEvent e) {
        if (e.getEntity() instanceof Player) {
            data.state.isUsingBow = false;
        }
    }

    @Priority(NORMAL)
    void check(PlayerItemHeldEvent e) {
        data.state.isUsingBow = false;
    }
}
