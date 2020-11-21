package uk.lewdev.entitylib;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.comphenix.protocol.wrappers.EnumWrappers.EntityPose;

import uk.lewdev.entitylib.entity.FakePlayer;
import uk.lewdev.entitylib.entity.protocol.EntityAsyncRenderTicker;
import uk.lewdev.entitylib.entity.protocol.FakeEntity;
import uk.lewdev.entitylib.events.ProtocolLibListeners;

/**
 * DEBUG ONLY
 * @author Lewys Davies (Lew_)
 */
public class FakeEntityPlugin extends JavaPlugin implements Listener {

	public static FakeEntityPlugin instance;
	private OfflinePlayer lew;
	
	private Map<Player, FakeEntity> moveTask = new HashMap<>();
	
	@Override
	public void onEnable() {
		instance = this;
		
		lew = Bukkit.getOfflinePlayer("Lew_");
		
		Bukkit.getPluginManager().registerEvents(this, this);
		
		new EntityAsyncRenderTicker().runTaskTimerAsynchronously(this, 20, 20);
		new ProtocolLibListeners();
		
		new BukkitRunnable() {
			public void run() {
				moveTask.forEach((p, entity) -> {
					if (!p.isOnline()) {
						this.cancel();
						entity.destroy();
						return;
					}
					
					entity.move(p.getLocation().add(0, 2.5, 0));
				});
			}
		}.runTaskTimerAsynchronously(this, 1, 1);
	}

	@EventHandler
	public void onLogin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		Location l = p.getLocation();
		
		FakePlayer fplayer = new FakePlayer(ChatColor.DARK_RED + "Entity Poses!", l, l.getYaw());
		
		fplayer.show(p);
		fplayer.setPose(EntityPose.CROUCHING);
		
//		if(p.getName().equals("Lew_")) {
//			return;
//		}
//		
//		new BukkitRunnable() { 
//			public void run() {
//				FakePlayer fplayer = new FakePlayer(ChatColor.DARK_RED + "Entity Poses!", l, l.getYaw());
//				
//				fplayer.show(p);
//				if(lew.isOnline()) fplayer.show(lew.getPlayer());
//				
//				fplayer.showSecondSkinLayer(true);
//				moveTask.put(p, fplayer);
//			}
//		}.runTaskLaterAsynchronously(this, 20);
	}
}