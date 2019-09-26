/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 */

package com.ngxdev.anticheat.handler;

import com.ngxdev.anticheat.Firefly;
import com.ngxdev.anticheat.api.HumanNPC;
import com.ngxdev.anticheat.data.playerdata.PlayerData;
import com.ngxdev.tinyprotocol.api.AbstractTinyProtocol;
import com.ngxdev.tinyprotocol.api.NMSObject;
import com.ngxdev.tinyprotocol.api.ProtocolVersion;
import com.ngxdev.tinyprotocol.packet.in.*;
import com.ngxdev.tinyprotocol.packet.out.*;
import com.ngxdev.tinyprotocol.reflection.Reflection;
import com.ngxdev.anticheat.utils.exception.ExceptionLog;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class TinyProtocolHandler {
	public static AbstractTinyProtocol instance;

	// Purely for making the code cleaner
	public static void sendPacket(Player player, Object packet) {
		instance.sendPacket(player, packet);
	}

	public static int getProtocolVersion(Player player) {
		return instance.getProtocolVersion(player);
	}

	public TinyProtocolHandler() {
		TinyProtocolHandler self = this;
		instance = new com.comphenix.tinyprotocol.v1_8_R3.TinyProtocol(Firefly.getInstance()) {
			@Override
			public Object onPacketOutAsync(Player receiver, Object packet) {
				return self.onPacketOutAsync(receiver, packet);
			}

			@Override
			public Object onPacketInAsync(Player sender, Object packet) {
				return self.onPacketInAsync(sender, packet);
			}
		};
	}

	public Object onPacketOutAsync(Player receiver, Object packet) {
		if (receiver == null) return packet;
		boolean cancel = false;
		String name = packet.getClass().getName();
		int index = name.lastIndexOf(".");
		String packetName = name.substring(index + 1);
		try {
			PlayerData data = PlayerData.get(receiver);
			switch (packetName) {
				case NMSObject.Server.KEEP_ALIVE: {
					WrappedOutKeepAlivePacket wrapped = new WrappedOutKeepAlivePacket(packet);
					wrapped.process(receiver, data.protocolVersion);
					data.fireChecks(wrapped);
					cancel = wrapped.isCancelled();
					break;
				}
				case NMSObject.Server.ENTITY_VELOCITY: {
					WrappedOutVelocityPacket wrapped = new WrappedOutVelocityPacket(packet);
					wrapped.process(receiver, data.protocolVersion);
					if (wrapped.getId() == receiver.getEntityId()) data.fireChecks(wrapped);
					cancel = wrapped.isCancelled();
					break;
				}
//				case NMSObject.Server.POSITION: {
//					WrappedOutPositionPacket wrapped = new WrappedOutPositionPacket(packet);
//					wrapped.process(receiver, data.protocolVersion);
//					data.fireChecks(wrapped);
//					cancel = wrapped.isCancelled();
//					break;
//				}
				case NMSObject.Server.GAME_STATE: {
					WrappedOutGameState wrapped = new WrappedOutGameState(packet);
					wrapped.process(receiver, data.protocolVersion);
					data.fireChecks(wrapped);
					data.gamemode = GameMode.getByValue((int) wrapped.getValue());
					cancel = wrapped.isCancelled();
					break;
				}
				case NMSObject.Server.ENTITY_TELEPORT: {
					WrappedOutEntityTeleport wrapped = new WrappedOutEntityTeleport(packet);
					for (HumanNPC npc : data.npc.npcs) {
						npc.teleportEntity(data, wrapped);
					}
					break;
				}
				case NMSObject.Server.REL_LOOK:
				case NMSObject.Server.REL_POSITION:
				case NMSObject.Server.REL_POSITION_LOOK:
				case NMSObject.Server.LEGACY_REL_LOOK:
				case NMSObject.Server.LEGACY_REL_POSITION:
				case NMSObject.Server.LEGACY_REL_POSITION_LOOK: {
					WrappedOutRelativePosition wrapped = new WrappedOutRelativePosition(packet);
					for (HumanNPC npc : data.npc.npcs) {
						npc.moveEntity(data, wrapped);
					}
					break;
				}
			}
		} catch (Exception e) {
			ExceptionLog.log(e);
		}
		return cancel ? null : packet;
	}

	public Object onPacketInAsync(Player sender, Object packet) {
		if (sender == null) return packet;
		boolean cancel = false;
		String name = packet.getClass().getName();
		int index = name.lastIndexOf(".");
		String packetName = name.substring(index + 1);
		try {
			PlayerData data = PlayerData.get(sender);

			switch (packetName) {
				case NMSObject.Client.ARM_ANIMATION: {
					WrappedInArmAnimationPacket wrapped = new WrappedInArmAnimationPacket();
					wrapped.process(sender, data.protocolVersion);
					data.fireChecks(wrapped);
					cancel = wrapped.isCancelled();
					break;
				}
				case NMSObject.Client.BLOCK_DIG: {
					WrappedInBlockDigPacket wrapped = new WrappedInBlockDigPacket(packet);
					wrapped.process(sender, data.protocolVersion);
					data.fireChecks(wrapped);
					cancel = wrapped.isCancelled();
					break;
				}
				case NMSObject.Client.BLOCK_PLACE: {
					WrappedInBlockPlacePacket wrapped = new WrappedInBlockPlacePacket(packet);
					wrapped.process(sender, data.protocolVersion);
					data.fireChecks(wrapped);
					cancel = wrapped.isCancelled();
					break;
				}
				case NMSObject.Client.CLIENT_COMMAND: {
					WrappedInClientCommandPacket wrapped = new WrappedInClientCommandPacket(packet);
					wrapped.process(sender, data.protocolVersion);
					data.fireChecks(wrapped);
					cancel = wrapped.isCancelled();
					break;
				}
				case NMSObject.Client.CLOSE_WINDOW: {
					WrappedInCloseWindowPacket wrapped = new WrappedInCloseWindowPacket(packet);
					wrapped.process(sender, data.protocolVersion);
					data.fireChecks(wrapped);
					cancel = wrapped.isCancelled();
					break;
				}
				case NMSObject.Client.ENTITY_ACTION: {
					WrappedInEntityActionPacket wrapped = new WrappedInEntityActionPacket(packet);
					wrapped.process(sender, data.protocolVersion);
					data.fireChecks(wrapped);
					cancel = wrapped.isCancelled();
					break;
				}
				case NMSObject.Client.POSITION:
				case NMSObject.Client.LOOK:
				case NMSObject.Client.POSITION_LOOK:
				case NMSObject.Client.LEGACY_POSITION:
				case NMSObject.Client.LEGACY_LOOK:
				case NMSObject.Client.LEGACY_POSITION_LOOK:
				case NMSObject.Client.FLYING: {
					WrappedInFlyingPacket wrapped = new WrappedInFlyingPacket(packet);
					wrapped.process(sender, data.protocolVersion);
					data.fireChecks(wrapped);
					cancel = wrapped.isCancelled();
					break;
				}
				case NMSObject.Client.HELD_ITEM: {
					WrappedInHeldItemSlotPacket wrapped = new WrappedInHeldItemSlotPacket(packet);
					wrapped.process(sender, data.protocolVersion);
					data.fireChecks(wrapped);
					cancel = wrapped.isCancelled();
					break;
				}
				case NMSObject.Client.KEEP_ALIVE: {
					WrappedInKeepAlivePacket wrapped = new WrappedInKeepAlivePacket(packet);
					wrapped.process(sender, data.protocolVersion);
					data.fireChecks(wrapped);
					cancel = wrapped.isCancelled();
					break;
				}
				case NMSObject.Client.STEER_VEHICLE: {
					WrappedInSteerVehiclePacket wrapped = new WrappedInSteerVehiclePacket(packet);
					wrapped.process(sender, data.protocolVersion);
					data.fireChecks(wrapped);
					cancel = wrapped.isCancelled();
					break;
				}
				case NMSObject.Client.TRANSACTION: {
					WrappedInTransactionPacket wrapped = new WrappedInTransactionPacket(packet);
					wrapped.process(sender, data.protocolVersion);
					data.fireChecks(wrapped);
					cancel = wrapped.isCancelled();
					break;
				}
				case NMSObject.Client.USE_ENTITY: {
					WrappedInUseEntityPacket wrapped = new WrappedInUseEntityPacket(packet);
					wrapped.process(sender, data.protocolVersion);
					data.fireChecks(wrapped);
					cancel = wrapped.isCancelled();
					if (data.state.cancelHits-- > 0) {
						cancel = true;
					}
					break;
				}
				case NMSObject.Client.WINDOW_CLICK: {
					WrappedInWindowClickPacket wrapped = new WrappedInWindowClickPacket(packet);
					wrapped.process(sender, data.protocolVersion);
					data.fireChecks(wrapped);
					cancel = wrapped.isCancelled();
					break;
				}
			}
		} catch (Exception e) {
			ExceptionLog.log(e);
		}
		return cancel ? null : packet;
	}
}
