package com.ngxdev.anticheat.checks.connection;

import com.ngxdev.anticheat.api.check.Check;
import api.CheckType;
import com.ngxdev.anticheat.api.check.Priority;
import com.ngxdev.anticheat.utils.DumbAverage;
import com.ngxdev.tinyprotocol.packet.in.WrappedInFlyingPacket;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

import static api.CheckType.Type.CONNECTION;
import static com.ngxdev.anticheat.api.check.Priority.Value.MIN;

@CheckType(id = "timer", name = "Timer", type = CONNECTION, maxVl = 2, timeout = 20 * 10)
public class Timer extends Check {
	public DumbAverage packetAverage = new DumbAverage(50);
	public double maxAvg;
	public int stable;

	@Priority(MIN)
	void check(WrappedInFlyingPacket packet) {
		if (data.timers.lastTeleport.hasNotPassed(100) || data.timers.join.hasNotPassed(20 * 10)) {
			if (data.lag.playerTime + 50 > System.currentTimeMillis()) data.lag.playerTime -= 60;
		}
		packetAverage.add(data.lag.packetDelay);
		maxAvg = Math.max(maxAvg, packetAverage.getAverage());
		//if (Math.abs(data.packetAverage.getAverage() - 50) < 0.3 && data.data.lag.playerTime - System.currentTimeMillis() < -100) {
		//    if (data.stableTimer.hasPassed(5)) {
		//        data.data.lag.playerTime = System.currentTimeMillis() - 150;
		//        data.maxAvg = 50;
		//        data.packetAverage.reset();
		//    }
		//} else data.stableTimer.reset();
		double max = 50 - (maxAvg - 50);
		double lag = ((Math.max(maxAvg, 0) - 50) * 51);

		debug("%.0f,%.0f,%.0f,%s", data.lag.playerTime - (System.currentTimeMillis() + 60 + lag), packetAverage.getAverage(), max, data.lag.packetDelay);
		if (data.lag.playerTime > System.currentTimeMillis() + 60 + lag) {
			fail( 2, 20 * 40, "%.0f,%.0f,%.0f,%s", data.lag.playerTime - (System.currentTimeMillis() + 60 + lag), packetAverage.getAverage(), max, data.lag.packetDelay);
			if (packetAverage.getAverage() > max - 10) packetAverage.reset();
			data.lag.playerTime = System.currentTimeMillis() - 120;
		}
		if (maxAvg > 50) maxAvg = maxAvg - 0.05;
		if (data.lag.packetDelay >= 49 && data.lag.packetDelay <= 51 && stable++ > 10 && maxAvg > 52) maxAvg = 52;
		else stable = 0;
	}

	void check(PlayerTeleportEvent packet) {
		maxAvg = 75;
		if (data.lag.playerTime == -1L) data.lag.playerTime = System.currentTimeMillis() - 50;
		else data.lag.playerTime -= 60;
	}
}
