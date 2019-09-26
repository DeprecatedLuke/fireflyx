package com.ngxdev.anticheat.checks.combat.killaura.experimental;

import com.ngxdev.anticheat.api.check.Check;
import api.CheckType;

import static api.CheckType.State.EXPERIMENTAL;
import static api.CheckType.Type.KILLAURA;
import static api.ConfigValueX.BLOCK_HITS_TIME;

@CheckType(id = "killaura:4", name = "KillAura 4", type = KILLAURA, state = EXPERIMENTAL)
public class KillAura4 extends Check {
    private int doubleSwings;
    private int doubleAttacks;
    private int bareSwings;
    private double vl;

    void check(int[] ints) {
        if (!canCheck()) return;
        final int swings = ints[0];
        final int attacks = ints[1];
        if (swings > 1 && attacks == 0) {
            ++this.doubleSwings;
        } else if (swings == 1 && attacks == 0) {
            ++this.bareSwings;
        } else if (attacks > 1) {
            ++this.doubleAttacks;
        }
        if (this.doubleSwings + this.doubleAttacks == 20) {
            if (this.doubleSwings == 0) {
                if (this.bareSwings > 10 && ++vl > 3.0) {
                    if (fail("b=%d,d=%d", this.bareSwings, vl)) data.state.cancelHits = BLOCK_HITS_TIME.asInteger();
                }
            } else {
                --vl;
            }
            this.doubleSwings = 0;
            this.doubleAttacks = 0;
            this.bareSwings = 0;
        }
    }
}
