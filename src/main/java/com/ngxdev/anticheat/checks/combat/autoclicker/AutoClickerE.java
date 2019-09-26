package com.ngxdev.anticheat.checks.combat.autoclicker;

import com.ngxdev.anticheat.api.check.Check;
import api.CheckType;
import com.ngxdev.tinyprotocol.api.NMSObject;
import com.ngxdev.tinyprotocol.packet.in.WrappedInArmAnimationPacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInBlockDigPacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;

import static api.CheckType.Type.AUTOCLICKER;

@CheckType(id = "autoclicker:e", name = "AutoClicker E", type = AUTOCLICKER, maxVl = 5)
public class AutoClickerE extends Check {
    private int movements;
    private int stage;
    private double vl;

    void check(WrappedInFlyingPacket packet) {
        check0(packet);
    }

    void check(WrappedInBlockDigPacket packet) {
        check0(packet);
    }

    void check(WrappedInArmAnimationPacket packet) {
        check0(packet);
    }

    void check0(NMSObject packet) {
        if (!canCheck()) return;
        if (this.stage == 0) {
            if (packet instanceof WrappedInArmAnimationPacket) {
                ++this.stage;
            }
        } else if (this.stage == 1) {
            if (packet instanceof WrappedInBlockDigPacket && ((WrappedInBlockDigPacket) packet).getAction() == WrappedInBlockDigPacket.EnumPlayerDigType.START_DESTROY_BLOCK) {
                ++this.stage;
            } else {
                this.stage = 0;
            }
        } else if (this.stage == 2) {
            if (packet instanceof WrappedInBlockDigPacket && ((WrappedInBlockDigPacket) packet).getAction() == WrappedInBlockDigPacket.EnumPlayerDigType.ABORT_DESTROY_BLOCK) {
                if (++vl >= 5) {
                    try {
                        if (this.movements > 10) {
                            fail();
                        }
                    } finally {
                        this.movements = 0;
                        vl = 0;
                    }
                }
                this.stage = 0;
            } else if (packet instanceof WrappedInArmAnimationPacket) {
                ++this.stage;
            } else {
                this.movements = 0;
                vl = 0;
                this.stage = 0;
            }
        } else if (this.stage == 3) {
            if (packet instanceof WrappedInFlyingPacket) {
                ++this.stage;
            } else {
                this.movements = 0;
                vl = 0;
                this.stage = 0;
            }
        } else if (this.stage == 4) {
            if (packet instanceof WrappedInBlockDigPacket && ((WrappedInBlockDigPacket) packet).getAction() == WrappedInBlockDigPacket.EnumPlayerDigType.ABORT_DESTROY_BLOCK) {
                ++this.movements;
                this.stage = 0;
            } else {
                this.movements = 0;
                vl = 0;
                this.stage = 0;
            }
        }
    }
}
