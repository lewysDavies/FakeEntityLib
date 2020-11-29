package uk.lewdev.entitylib;

import org.bukkit.plugin.java.JavaPlugin;
import uk.lewdev.entitylib.entity.protocol.EntityAsyncRenderTicker;

/**
 * @author Lewys Davies (Lew_)
 */
public class FakeEntityPlugin extends JavaPlugin {

	public static FakeEntityPlugin instance;
	
	@Override
	public void onEnable() {
		instance = this;
		
		new EntityAsyncRenderTicker().runTaskTimerAsynchronously(this, 20, 20);
		//new ProtocolLibListeners();
	}
}