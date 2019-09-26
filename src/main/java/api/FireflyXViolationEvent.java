package api;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;


public class FireflyXViolationEvent extends Event implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	@Getter
	private Player player;
	@Getter
	private String type;
	@Getter
	private String debug;
	public boolean autoban, cancel, alert;
	@Getter
	@Setter
	private boolean cancelled = false;
	@Getter
	private int violations;

	public FireflyXViolationEvent(Player player, String type, boolean alert, boolean cancel, boolean autoban, String debug, int violations) {
		super(!Bukkit.isPrimaryThread());
		this.player = player;
		this.type = type;
		this.alert = alert;
		this.cancel = cancel;
		this.autoban = autoban;
		this.debug = debug;
		this.violations = violations;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
