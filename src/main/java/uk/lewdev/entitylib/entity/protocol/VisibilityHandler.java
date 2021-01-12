package uk.lewdev.entitylib.entity.protocol;

import java.util.Collection;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * @author Lewys Davies (Lew_)
 */
public class VisibilityHandler {
	
	private final static int RENDER_DISTANCE = 150; // Blocks
	
	private final FakeEntity entity;
	
	private final HashSet<Player> visibleTo  = new HashSet<Player>();   // Players who could see this entity, if render conditions are met
	private final HashSet<Player> renderedTo = new HashSet<Player>();  // Players this entity is currently rendered to
	
	private boolean globalVisibility = false;
	
	protected VisibilityHandler(FakeEntity entity) {
		this.entity = entity;
	}
	
	/**
	 * Called by {@link FakeLivingEntity#destroy()}
	 */
	protected final void destroy() {
		this.visibleTo.clear();
		this.renderedTo.clear();
	}
	
	/**
	 * Update all players who could see this entity
	 * Removes any players who are no longer with us : (
	 */
	protected final void tick() {
		if(this.entity.isDead()) return;
		
		// Remove any logged out players
		this.visibleTo.removeIf(player -> !player.isOnline());
		this.renderedTo.removeIf(player -> !player.isOnline());
		
		// Update players
		if(this.globalVisibility) {
            this.entity.getWorld().getPlayers().forEach(this::update);
        } else {
            this.visibleTo.forEach(this::update);
        }
	}
	
	/**
	 * @return Collection of all players who currently have this entity rendered
	 */
	protected final Collection<Player> renderedTo() {
		return new HashSet<>(this.renderedTo);
	}
	
	/**
	 * @return Collection of all players who could see this entity
	 */
	protected final Collection<? extends Player> visibleTo() {
	    if(this.globalVisibility) return Bukkit.getOnlinePlayers();
		return new HashSet<>(this.visibleTo);
	}
	
	/**
	 * @param player
	 * @return True if Player could see this entity
	 */
	public final boolean isVisible(Player player) {
	    if(this.globalVisibility) return true;
		return this.visibleTo.contains(player);
	}
	
	public final void setVisible(Player player) {
	    if(this.globalVisibility) return;
		if(this.entity.isDead()) return;
		if(this.visibleTo.contains(player)) return;
	
		this.visibleTo.add(player);
		
		if(this.shouldRenderTo(player)) {
			this.render(player);
		}
	}
	
	/**
	 * @param player
	 * @throws IllegalStateException if {@link #isGloballyVisible()}
	 */
	public final void setNotVisible(Player player) {
	    if(this.globalVisibility) {
	        throw new IllegalStateException("Cannot hide a isGloballyVisible() mob from specific players.");
	    }
	    
		if(this.entity.isDead()) return;
		if(! this.visibleTo.contains(player)) return;
		
		this.visibleTo.remove(player);
		this.unRender(player);
	}
	
	/**
	 * @return True if displayed to everyone by default, else False.
	 */
	public final boolean isGloballyVisible() {
	    return this.globalVisibility;
	}
	
	/**
	 * @param visibleToAll
	 */
	public final void setGloballyVisible(boolean visibleToAll) {
	    if(this.globalVisibility == visibleToAll) return;
	    
	    this.globalVisibility = visibleToAll;
	    
	    if(visibleToAll) {
	        this.visibleTo.clear();
	    }
	}
	
	/**
	 * Update render status for a specific player<br>
	 * Note: Call after acquiring the write lock
	 * 
	 * @param player
	 */
	private final void update(Player player) {
		// Calculate current state
		boolean isCurrentlyRendered = this.renderedTo.contains(player);
		boolean shouldRender = this.shouldRenderTo(player);
		
		if(isCurrentlyRendered == shouldRender) {
			return; // Everything is in the correct state : )
		}
		
		// Render or Remove, depending on new state
		if(isCurrentlyRendered && !shouldRender) {
			this.unRender(player);
		} else {
			this.render(player);
		}
	}
	
	/**
	 * @param player
	 * @return True if player is in the same world, and within render distance
	 */
	private final boolean shouldRenderTo(Player player) {
		 return this.entity.getWorld().equals(player.getWorld())
				 && this.isInRange(player);
	}
	
	/**
	 * Manhattan Distance to check player is near enough to render
	 * 
	 * @param player
	 * @return True if player is within {@value #RENDER_DISTANCE}
	 */
	private final boolean isInRange(Player player) {
		return Math.abs(this.entity.getX() - player.getLocation().getX()) < RENDER_DISTANCE 
				&& Math.abs(this.entity.getZ() - player.getLocation().getZ()) < RENDER_DISTANCE;
	}
	
	/**
	 * Send spawn packet and add to rendered list
	 * Will not render if currently is
	 * 
	 * @param player
	 */
	private final void render(Player player) {
		if(! this.renderedTo.contains(player)) {
			this.renderedTo.add(player);
			this.entity.sendSpawnPacket(player);
		}
	}
	
	/**
	 * Send destroy packet and remove from rendered list
	 * Will not un-render if not already rendered
	 * 
	 * @param player
	 */
	private final void unRender(Player player) {
		if(this.renderedTo.contains(player)) {
			this.renderedTo.remove(player);
			this.entity.sendDestroyPacket(player);
		}
	}
}
