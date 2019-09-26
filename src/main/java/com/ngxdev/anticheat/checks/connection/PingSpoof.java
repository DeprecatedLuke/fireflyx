package com.ngxdev.anticheat.checks.connection;

import com.ngxdev.anticheat.api.check.Check;
import api.CheckType;
import com.ngxdev.tinyprotocol.packet.in.WrappedInTransactionPacket;

import static api.CheckType.State.BETA;
import static api.CheckType.Type.CONNECTION;

@CheckType(id = "pingspoof", name = "Ping Spoof", type = CONNECTION, maxVl = 3, timeout = 20 * 6, state = BETA)
public class PingSpoof extends Check {
    void check(WrappedInTransactionPacket packet) {
        if (!canCheck()) return;
        if (data.lag.keepAlivePing - 100 > data.lag.transactionPing) {
            fail("p=+%d", data.lag.keepAlivePing - data.lag.transactionPing);
            data.lag.keepAlivePing = data.lag.transactionPing;
        } else decrease();
    }
}
