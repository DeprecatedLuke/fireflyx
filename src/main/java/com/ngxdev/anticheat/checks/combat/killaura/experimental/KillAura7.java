package com.ngxdev.anticheat.checks.combat.killaura.experimental;

import api.CheckType;
import com.ngxdev.anticheat.api.HumanNPC;
import com.ngxdev.anticheat.api.check.Check;
import com.ngxdev.anticheat.data.playerdata.PlayerData;
import com.ngxdev.tinyprotocol.api.ProtocolVersion;
import com.ngxdev.tinyprotocol.packet.in.WrappedInUseEntityPacket;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import static api.CheckType.Type.KILLAURA;
import static com.ngxdev.tinyprotocol.packet.in.WrappedInUseEntityPacket.EnumEntityUseAction.ATTACK;

@CheckType(id = "killaura:7", name = "KillAura 7", type = KILLAURA)
public class KillAura7 extends Check {
	int npchits;
	int playerhits;

	void check(WrappedInUseEntityPacket packet) {
		if (!canCheck() || data.protocolVersion.isAbove(ProtocolVersion.V1_8_9) || packet.getAction() == ATTACK) return;
//
//		Entity e = packet.getEntity();
//		if (e instanceof Player) {
//			Player p = (Player) e;
//			PlayerData other = PlayerData.get(p);
//			HumanNPC npc = other.npc.npc;
//			other.npc.npcs.add(npc);
//			npc.spawn(data, p.getLocation());
//		}

		//essentially if only npc gets hit or only the player gets hit, they are cheating lol.
	}
}
