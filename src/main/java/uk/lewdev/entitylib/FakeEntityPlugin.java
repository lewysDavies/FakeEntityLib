package uk.lewdev.entitylib;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import uk.lewdev.entitylib.entity.protocol.EntityAsyncRenderTicker;
import uk.lewdev.entitylib.events.ProtocolLibListeners;

/**
 * @author Lewys Davies (Lew_)
 */
public class FakeEntityPlugin extends JavaPlugin implements Listener {

	public static FakeEntityPlugin instance;
	
	@Override
	public void onEnable() {
		instance = this;
		
		Bukkit.getPluginManager().registerEvents(this, this);
		
		new EntityAsyncRenderTicker().runTaskTimerAsynchronously(this, 20, 20);
		new ProtocolLibListeners();
	}
}