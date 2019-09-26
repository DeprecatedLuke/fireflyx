package api;

import com.ngxdev.anticheat.data.playerdata.PlayerData;
import org.bukkit.entity.Player;

public class FireflyAPI {
	public static void setIgnored(Player p, boolean ignored) {
		PlayerData.get(p).state.isIgnored = ignored;
	}
}
