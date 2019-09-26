package com.ngxdev.tinyprotocol.packet.out;

import com.ngxdev.tinyprotocol.api.NMSObject;
import lombok.Getter;

@Getter
public class WrappedOutEntityDestroy extends NMSObject {
	private static final String packet = Server.ENTITY_DESTROY;

	public WrappedOutEntityDestroy(int[] ids) {
		setPacket(packet, ids);
	}
}
