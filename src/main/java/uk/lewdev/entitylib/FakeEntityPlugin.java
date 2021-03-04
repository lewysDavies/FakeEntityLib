package uk.lewdev.entitylib;

import org.bukkit.plugin.java.JavaPlugin;
import uk.lewdev.entitylib.entity.protocol.EntityAsyncRenderTicker;
import uk.lewdev.entitylib.entity.protocol.FakeEntity;

/**
 * @author Lewys Davies (Lew_)
 */
public class FakeEntityPlugin extends JavaPlugin {

	public static FakeEntityPlugin instance;
	
	@Override
	public void onEnable() {
		instance = this;
		
		new EntityAsyncRenderTicker().runTaskTimerAsynchronously(this, 10, 10);
		//new ProtocolLibListeners();
	}
	
	@Override
	public void onDisable() {
	    FakeEntity.ALL_ALIVE_INSTANCES.values().forEach(FakeEntity::destroy);
	}
}