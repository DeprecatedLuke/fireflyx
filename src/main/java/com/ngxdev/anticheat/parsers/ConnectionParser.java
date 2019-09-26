package com.ngxdev.anticheat.parsers;

import com.ngxdev.anticheat.api.check.Check;
import com.ngxdev.anticheat.api.check.type.Parser;
import com.ngxdev.tinyprotocol.api.ProtocolVersion;
import com.ngxdev.tinyprotocol.packet.in.WrappedInKeepAlivePacket;
import com.ngxdev.tinyprotocol.packet.in.WrappedInTransactionPacket;
import com.ngxdev.tinyprotocol.packet.out.WrappedOutKeepAlivePacket;
import com.ngxdev.tinyprotocol.packet.out.WrappedOutTransaction;
import com.ngxdev.anticheat.utils.evicting.EvictingMap;

@Parser
public class ConnectionParser extends Check {
    EvictingMap<Long, Long> sent = new EvictingMap<>(20);
    EvictingMap<Short, Long> sentTransaction = new EvictingMap<>(20);
    long packet = 0;

    public ConnectionParser() {
        schedule(() -> {
            long next = packet++;
            short nextTransaction = (short) (next & Short.MAX_VALUE);
            if (ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_12))
                sendPacket(new WrappedOutKeepAlivePacket(next));
            sendPacket(new WrappedOutTransaction((byte) 0, nextTransaction, false));
            sent.put(next, System.currentTimeMillis());
            sentTransaction.put(nextTransaction, System.currentTimeMillis());
        }, 20, 20);
    }

    void parse(WrappedInTransactionPacket packet) {
        // we just ignore spoofed transactions
        Long sent = sentTransaction.get(packet.getAction());
        if (sent != null) {
            packet.setCancelled(true);
            int ping = (int) (System.currentTimeMillis() - sent);
            data.lag.transactionPing = ping;
            data.lag.currentTime = System.currentTimeMillis() - (ping / 2);
            data.lag.differencial = 0;
        }
    }

    void parse(WrappedInKeepAlivePacket packet) {
        // we ignore all the packets not sent by the ac so we can have a consistent measure of possible ping miscalculation and prevent false-bans using it.
        Long lastSent = sent.get(packet.getTime());
        if (lastSent != null) {
            packet.setCancelled(true);
            int ping = (int) (System.currentTimeMillis() - lastSent);
            data.lag.keepAlivePing = ping;
        }
    }
}
