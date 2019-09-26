package com.ngxdev.anticheat.checks.combat.killaura.experimental;

import api.CheckWrapper;
import com.ngxdev.anticheat.api.check.Check;
import api.CheckType;
import com.ngxdev.anticheat.data.playerdata.PlayerData;
import com.ngxdev.tinyprotocol.packet.in.WrappedInArmAnimationPacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInUseEntityPacket;

import static api.CheckType.State.EXPERIMENTAL;
import static api.CheckType.Type.KILLAURA;
import static api.ConfigValueX.BLOCK_HITS_TIME;
import static com.ngxdev.tinyprotocol.packet.in.WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK;

@CheckType(id = "killaura:3", name = "KillAura 3", type = KILLAURA, state = EXPERIMENTAL)
public class KillAura3 extends Check {
    private int swings;
    private int attacks;
    private KillAura4 auraN;

    @Override
    public void init(PlayerData data, CheckWrapper wrapper) {
        super.init(data, wrapper);
        auraN = find(KillAura4.class);
    }

    void check(WrappedInFlyingPacket packet) {
        if (!canCheck()) return;
        if (!data.state.isDigging && !data.state.isPlacing) {
            if (this.attacks > 0 && this.swings > this.attacks) {
                if (fail("s=%d,a=%d", swings, attacks)) data.state.cancelHits = BLOCK_HITS_TIME.asInteger();
            }
            if (auraN != null) {
                auraN.check(new int[]{this.swings, this.attacks});
            }
            this.swings = 0;
            this.attacks = 0;
        }
    }

    void check(WrappedInArmAnimationPacket packet) {
        if (!data.state.isDigging && !data.state.isPlacing) {
            this.swings++;
        }
    }

    void check(WrappedInUseEntityPacket packet) {
        if (!data.state.isDigging && !data.state.isPlacing) {
            if (packet.getAction() == ATTACK) {
                this.attacks++;
            }
        }
    }
}
