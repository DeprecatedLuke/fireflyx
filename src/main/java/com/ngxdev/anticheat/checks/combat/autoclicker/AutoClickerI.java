package com.ngxdev.anticheat.checks.combat.autoclicker;

import com.ngxdev.anticheat.api.check.Check;
import api.CheckType;
import com.ngxdev.tinyprotocol.api.NMSObject;
import com.ngxdev.tinyprotocol.packet.in.WrappedInArmAnimationPacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInBlockDigPacket;

import static api.CheckType.Type.AUTOCLICKER;
import static com.ngxdev.tinyprotocol.packet.in.WrappedInBlockDigPacket.EnumPlayerDigType.ABORT_DESTROY_BLOCK;
import static com.ngxdev.tinyprotocol.packet.in.WrappedInBlockDigPacket.EnumPlayerDigType.START_DESTROY_BLOCK;

@CheckType(id = "autoclicker:i", name = "AutoClicker I", type = AUTOCLICKER)
public class AutoClickerI extends Check {
    private int stage;
    private double vl;

    void check(WrappedInArmAnimationPacket packet) {
        check0(packet);
    }

    void check(WrappedInBlockDigPacket packet) {
        check0(packet);
    }

    void check0(NMSObject packet) {
        if (!canCheck()) return;
        if (this.stage == 0) {
            if (packet instanceof WrappedInArmAnimationPacket) {
                ++this.stage;
            }
        } else if (packet instanceof WrappedInBlockDigPacket) {
            if (data.state.isPlacing || data.state.isDigging) return;
            //if (this.playerData.getFakeBlocks().contains(((PacketPlayInBlockDig) packet).a())) {
            //	return;
            //}

            WrappedInBlockDigPacket.EnumPlayerDigType digType = ((WrappedInBlockDigPacket) packet).getAction();
            if (digType == ABORT_DESTROY_BLOCK) {
                if (this.stage == 1) {
                    ++this.stage;
                } else {
                    this.stage = 0;
                }
            } else if (digType == START_DESTROY_BLOCK) {
                if (this.stage == 2) {
                    if ((vl += 1.4) >= 15.0) {
                        fail("v=%.1f", vl);
                    }
                } else {
                    this.stage = 0;
                    vl -= 0.25;
                }
            } else {
                this.stage = 0;
            }
        } else {
            this.stage = 0;
        }
    }
}
