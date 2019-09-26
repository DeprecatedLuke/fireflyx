package com.ngxdev.tinyprotocol.packet.out;

import com.ngxdev.tinyprotocol.api.NMSObject;
import com.ngxdev.tinyprotocol.api.ProtocolVersion;
import com.ngxdev.tinyprotocol.reflection.FieldAccessor;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class WrappedOutGameState extends NMSObject {
    private static final String packet = Server.GAME_STATE;

    private static FieldAccessor<Integer> fieldReason = fetchField(packet, int.class, 0);
    private static FieldAccessor<Float> fieldValue = fetchField(packet, float.class, 0);

    private int reason;
    private float value;

    public WrappedOutGameState(Object packet) {
        super(packet);
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        reason = fetch(fieldReason);
        value = fetch(fieldValue);
    }
}
