package com.ngxdev.anticheat.checks.combat.autoclicker.experimental;

import com.ngxdev.anticheat.api.check.Check;
import api.CheckType;
import com.ngxdev.tinyprotocol.api.NMSObject;
import com.ngxdev.tinyprotocol.packet.in.WrappedInArmAnimationPacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInBlockDigPacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;
import com.ngxdev.anticheat.utils.Greek;

import static api.CheckType.State.EXPERIMENTAL;
import static api.CheckType.Type.AUTOCLICKER;

@CheckType(id = "autoclicker:1", name = "AutoClicker " + Greek.ONE, type = AUTOCLICKER, state = EXPERIMENTAL, maxVl = 3)
public class AutoClicker1 extends Check {
    private int stage;
    private boolean other;

    void check(WrappedInBlockDigPacket packet) {
        check0(packet);
    }

    void check(WrappedInArmAnimationPacket packet) {
        check0(packet);
    }

    void check(WrappedInFlyingPacket packet) {
        check0(packet);
    }

    void check0(NMSObject packet) {
        if (!canCheck()) return;
        if (this.stage == 0) {
            if (packet instanceof WrappedInBlockDigPacket && ((WrappedInBlockDigPacket) packet).getAction() == WrappedInBlockDigPacket.EnumPlayerDigType.START_DESTROY_BLOCK) {
                ++this.stage;
            }
        } else if (this.stage == 1) {
            if (packet instanceof WrappedInArmAnimationPacket) {
                ++this.stage;
            } else {
                this.stage = 0;
            }
        } else if (this.stage == 2) {
            if (packet instanceof WrappedInFlyingPacket) {
                ++this.stage;
            } else {
                this.stage = 0;
            }
        } else if (this.stage == 3) {
            if (packet instanceof WrappedInArmAnimationPacket) {
                ++this.stage;
            } else {
                this.stage = 0;
            }
        } else if (this.stage == 4) {
            if (packet instanceof WrappedInFlyingPacket) {
                ++this.stage;
            } else {
                this.stage = 0;
            }
        } else if (this.stage == 5) {
            if (packet instanceof WrappedInBlockDigPacket && ((WrappedInBlockDigPacket) packet).getAction() == WrappedInBlockDigPacket.EnumPlayerDigType.START_DESTROY_BLOCK) {
                fail("1");
                this.stage = 0;
            } else if (packet instanceof WrappedInArmAnimationPacket) {
                ++this.stage;
            } else if (packet instanceof WrappedInFlyingPacket) {
                this.other = true;
                ++this.stage;
            } else {
                this.stage = 0;
            }
        } else if (this.stage == 6) {
            if (!this.other) {
                if (packet instanceof WrappedInFlyingPacket) {
                    ++this.stage;
                } else {
                    this.stage = 0;
                }
            } else {
                if (packet instanceof WrappedInBlockDigPacket && ((WrappedInBlockDigPacket) packet).getAction() == WrappedInBlockDigPacket.EnumPlayerDigType.START_DESTROY_BLOCK) {
                    fail("2");
                    this.other = false;
                }
                this.stage = 0;
            }
        } else if (this.stage == 7) {
            if (packet instanceof WrappedInBlockDigPacket && ((WrappedInBlockDigPacket) packet).getAction() == WrappedInBlockDigPacket.EnumPlayerDigType.START_DESTROY_BLOCK) {
                fail("3");
            } else {
                this.stage = 0;
            }
        }
    }
}
