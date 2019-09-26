package com.ngxdev.anticheat.api;

import com.mojang.authlib.GameProfile;
import com.ngxdev.anticheat.data.playerdata.PlayerData;
import com.ngxdev.anticheat.handler.TinyProtocolHandler;
import com.ngxdev.anticheat.utils.EntityIdCache;
import com.ngxdev.anticheat.utils.PreDefined;
import com.ngxdev.tinyprotocol.packet.out.WrappedOutEntityDestroy;
import com.ngxdev.tinyprotocol.packet.out.WrappedOutEntityTeleport;
import com.ngxdev.tinyprotocol.packet.out.WrappedOutNamedEntitySpawn;
import com.ngxdev.tinyprotocol.packet.out.WrappedOutRelativePosition;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class HumanNPC {
	private final int id = EntityIdCache.getNextId();
	private UUID uuid;
	private String name;

	public void spawn(PlayerData data, Location loc) {
		TinyProtocolHandler.instance.sendPacket(data.player,
				new WrappedOutNamedEntitySpawn(
						data.protocolVersion,
						id, new GameProfile(uuid, name),
						loc.getX(), loc.getY(), loc.getZ(),
						PreDefined.emptyDatawatcher, PreDefined.emptyEntityWatchables
				)
		);
	}

	public void moveEntity(PlayerData data, WrappedOutRelativePosition packet) {
		packet.setId(id);
		TinyProtocolHandler.sendPacket(data.player, packet);
	}

	public void teleportEntity(PlayerData data, WrappedOutEntityTeleport packet) {
		packet.setId(id);
		TinyProtocolHandler.sendPacket(data.player, packet);
	}

	public void destroyEntity(PlayerData data) {
		TinyProtocolHandler.sendPacket(data.player, new WrappedOutEntityDestroy(new int[]{id}));
	}
}
