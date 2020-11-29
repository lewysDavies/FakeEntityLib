package uk.lewdev.entitylib.entity.protocol;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers.EntityPose;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;

import uk.lewdev.entitylib.entity.EntityId;
import uk.lewdev.entitylib.utils.AngleUtil;
import uk.lewdev.entitylib.utils.EntityID;
import uk.lewdev.entitylib.utils.MCVersion;
import uk.lewdev.entitylib.utils.MaskUtil;

/**
 * @author Lewys Davies (Lew_)
 */
public abstract class FakeEntity {

	/** Entity ID, Fake Entity Instance  */
	public final static ConcurrentHashMap<Integer, FakeEntity> ALL_ALIVE_INSTANCES = new ConcurrentHashMap<Integer, FakeEntity>();
	
	protected static final ProtocolManager protocol = ProtocolLibrary.getProtocolManager();
	
	private AtomicBoolean isDead = new AtomicBoolean(false); // Has destroy been called

	private final PacketContainer destroyPacket = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
	
	private final WrappedDataWatcher dataWatcher = new WrappedDataWatcher();
	
	private final VisibilityHandler visibilityHandler = new VisibilityHandler(this);

	private final WrappedDataWatcherObject entityByteWatcher = new WrappedDataWatcherObject(EntityMetaByte.byteIndex(),
			Registry.get(Byte.class));
	
	private final WrappedDataWatcherObject customNameWatcher = new WrappedDataWatcherObject(EntityMetaData.customNameIndex(),
			Registry.getChatComponentSerializer(true));
	
	private final WrappedDataWatcherObject customNameVisibleWatcher = new WrappedDataWatcherObject(
			EntityMetaData.nameVisible(), Registry.get(Boolean.class));
	
	private final WrappedDataWatcherObject gravityWatcher = new WrappedDataWatcherObject(EntityMetaData.gravityIndex(),
			Registry.get(Boolean.class));
	
	private final WrappedDataWatcherObject poseWatcher = new WrappedDataWatcherObject(EntityMetaData.poseIndex(), 
			Registry.get(EnumWrappers.getEntityPoseClass()));

	private final int entityId;
	private final UUID uuid;

	private final EntityType type;
	private final int protocolID;

	private World world;
	private double x, y, z;
	private float yaw = 0, pitch = 0;
	
	private byte entityDataByte = (byte) 0;
	private boolean isInvisible = false;
	private String customName = "";
	private boolean noGravity = false;
	private boolean customNameVisible = false;
	private boolean glow = false;
	private EntityPose wrappedPose = EntityPose.STANDING;

	protected FakeEntity(EntityType type, UUID uuid, World world, double x, double y, double z, float yaw, float pitch) {
		this.entityId = EntityID.nextAndIncrement();
		this.uuid = uuid;
		this.type = type;
		this.protocolID = EntityId.fromType(type);
		
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;

		this.destroyPacket.getIntegerArrays().write(0, new int[] { this.entityId });

		this.initDataWatcherObjs();
		
		ALL_ALIVE_INSTANCES.put(this.entityId, this);
	}
	
	/**
	 * Init all DataWatchers relevant to the base entity metadata
	 */
	private final void initDataWatcherObjs() {
		this.dataWatcher.setObject(this.entityByteWatcher, this.entityDataByte);
		this.dataWatcher.setObject(this.customNameWatcher, Optional.of(WrappedChatComponent.fromText(this.customName).getHandle()));
		this.dataWatcher.setObject(this.customNameVisibleWatcher, this.customNameVisible);
		this.dataWatcher.setObject(this.gravityWatcher, this.noGravity);
		this.dataWatcher.setObject(this.poseWatcher, this.wrappedPose.toNms());
	}
	
	/**
	 * Destroy this entities instances. Cleans memory and despawns the entity
	 */
	public final void destroy() {
		if (this.isDead()) return;
		
		ALL_ALIVE_INSTANCES.remove(this.entityId);

		// Get copy of current render list and destroy visibility handler
		this.visibilityHandler.visibleTo().forEach(this::sendDestroyPacket);
		this.visibilityHandler.destroy();
		
		this.isDead.set(true);
	}

	/**
	 * Some entity types are spawned differently. i.e. Pig vs Player<br>
	 * Therefore handle spawning in each child class
	 * 
	 * @param player to send packet to
	 */
	protected abstract void sendSpawnPacket(Player player);
	
	/**
	 * All entities handle this the same.
	 * @param player
	 */
	protected final void sendDestroyPacket(Player player) {
		this.assertNotDead();

		try {
			protocol.sendServerPacket(player, this.destroyPacket);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return is dead
	 */
	public final boolean isDead() {
		return this.isDead.get();
	}

	/**
	 * @return the type
	 */
	public final EntityType getType() {
		return this.type;
	}
	
	/**
	 * @return the world
	 */
	public final World getWorld() {
		return world;
	}

	/**
	 * @param world the world to set
	 */
	public final void setWorld(World world) {
		this.assertNotDead();
		this.world = world;
	}

	/**
	 * @return the x
	 */
	public final double getX() {
		return x;
	}

	/**
	 * @param x the x to set
	 */
	public final void setX(double x) {
		this.assertNotDead();
		this.x = x;
		this.setLocation(this.x, this.y, this.z, this.yaw, this.pitch);
	}

	/**
	 * @return the y
	 */
	public final double getY() {
		return y;
	}

	/**
	 * @param y the y to set
	 */
	public final void setY(double y) {
		this.assertNotDead();
		this.y = y;
		this.setLocation(this.x, this.y, this.z, this.yaw, this.pitch);
	}

	/**
	 * @return the z
	 */
	public final double getZ() {
		return z;
	}

	/**
	 * @param z the z to set
	 */
	public final void setZ(double z) {
		this.assertNotDead();
		this.z = z;
		this.setLocation(this.x, this.y, this.z, this.yaw, this.pitch);
	}

	/**
	 * @return the yaw
	 */
	public final float getYaw() {
		return this.yaw;
	}

	/**
	 * @param yaw the yaw to set
	 */
	public final void setYaw(float yaw) {
		this.assertNotDead();
		this.yaw = yaw;
		this.setLocation(this.x, this.y, this.z, this.yaw, this.pitch);
	}

	/**
	 * @return the pitch
	 */
	public final float getPitch() {
		return this.pitch;
	}

	/**
	 * @param pitch the pitch to set
	 */
	public final void setPitch(float pitch) {
		this.assertNotDead();
		this.pitch = pitch;
		this.setLocation(this.x, this.y, this.z, this.yaw, this.pitch);
	}

	/**
	 * @return uuid the uuid
	 */
	public final UUID getUUID() {
		return this.uuid;
	}
	
	/**
	 * @param glow should glow
	 */
	public final void setGlow(boolean glow) {
		this.assertNotDead();
		this.glow = glow;
		this.entityDataByte = MaskUtil.setBit(this.entityDataByte, EntityMetaByte.glowingIndex(), glow);
		this.dataWatcher.setObject(this.entityByteWatcher, this.entityDataByte);
		this.sendMetaUpdate();
	}
	
	/**
	 * @return is glowing (has outline)
	 */
	public final boolean isGlowing() {
		return this.glow;
	}
	
	/**
	 * Set the entity pose
	 * @param pose
	 */
	public final void setPose(EntityPose pose) {
		this.assertNotDead();
		
		this.wrappedPose = pose;
		this.dataWatcher.setObject(this.poseWatcher, this.wrappedPose.toNms());
		this.sendMetaUpdate();
	}
	
	/**
	 * @return the entity pose
	 */
	public final EntityPose getPose() {
		return this.wrappedPose;
	}
	
	/**
	 * @param invisible
	 */
	public final void setInvisible(boolean invisible) {
		this.assertNotDead();
		
		this.isInvisible = invisible;
		this.entityDataByte = MaskUtil.setBit(this.entityDataByte, EntityMetaByte.invisibleIndex(), this.isInvisible);
		this.dataWatcher.setObject(this.entityByteWatcher, this.entityDataByte);
		this.sendMetaUpdate();
	}
	
	/**
	 * @return is invisible
	 */
	public final boolean isInvisible() {
		return this.isInvisible;
	}
	
	public final void setGravity(boolean gravity) {
		this.assertNotDead();
		
		this.noGravity = !gravity;
		this.dataWatcher.setObject(this.gravityWatcher, this.noGravity);
		this.sendMetaUpdate();
	}
	
	/**
	 * @param name
	 */
	public void setCustomName(String name) {
		this.assertNotDead();
		
		this.customName = name;
		this.dataWatcher.setObject(this.customNameWatcher, Optional.of(WrappedChatComponent.fromText(name).getHandle()));
		this.sendMetaUpdate();
	}
	
	/**
	 * @return name
	 */
	public String getCustomName() {
		return this.customName;
	}
	
	/**
	 * @param visible
	 */
	public void setCustomNameVisible(boolean visible) {
		this.assertNotDead();
		
		this.customNameVisible = visible;
		this.dataWatcher.setObject(this.customNameVisibleWatcher, this.customNameVisible);
		
		this.sendMetaUpdate();
	}
	
	/**
	 * @return custom name visible
	 */
	public boolean isCustomNameVisible() {
		return this.customNameVisible;
	}
	
	/**
	 * Move or teleport an entity to the new location, depending on distance<br>
	 * 
	 * @apiNote Calls {@link #setLocation(double, double, double, float, float)
	 *          setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(),
	 *          loc.getPitch())}
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public final void setLocation(Location loc) {
		this.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
	}

	/**
	 * Move or teleport an entity to the new location, depending on distance<br>
	 * 
	 * @apiNote Calls {@link #setLocation(double, double, double, float, float)
	 *          setLocation(x, y, z, 0, 0)}
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public final void setLocation(double x, double y, double z) {
		this.setLocation(x, y, z, 0, 0);
	}

	/**
	 * Move or teleport an entity to the new location, depending on distance
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param yaw   degrees such as {@link Location#getYaw()}
	 * @param pitch degrees such as {@link Location#getPitch()}
	 */
	public final void setLocation(double x, double y, double z, float yaw, float pitch) {
		this.assertNotDead();

		this.yaw = yaw;
		this.pitch = pitch;

		if (new Location(this.world, x, y, z).distance(new Location(this.world, this.x, this.y, this.z)) > 8) {
			this.sendTeleportPacket(x, y, z);
		} else {
			this.sendMovePacket(x, y, z, true);
		}

		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Only move an entity without any additional checks. <b>Distance must be <8
	 * blocks.</b><br>
	 * Typically used for path finding<br>
	 * If you can't guarantee distance is <8, use
	 * {@link #setLocation(double, double, double, float, float)}
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @param yaw
	 * @param pitch
	 */
	public final void move(double x, double y, double z, float yaw, float pitch) {
		this.assertNotDead();

		this.yaw = yaw;
		this.pitch = pitch;

		this.sendMovePacket(x, y, z, true);

		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	/**
	 * Only move an entity without any additional checks. <b>Distance must be <8
	 * blocks.</b><br>
	 * Typically used for path finding<br>
	 * If you can't guarantee distance is <8, use
	 * {@link #setLocation(double, double, double, float, float)}
	 * 
	 * @param loc
	 */
	public final void move(Location loc) {
		this.move(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
	}

	/**
	 * Allow the player to see this entity
	 * 
	 * @param player
	 */
	public final void show(Player player) {
		this.assertNotDead();
		this.visibilityHandler.setVisible(player);
	}

	/**
	 * Do not allow the player to see this entity
	 * 
	 * @param player
	 */
	public final void hide(Player player) {
		this.assertNotDead();
		this.visibilityHandler.setNotVisible(player);
	}
	
	/**
	 * @return this entities unique id
	 */
	protected final int getEntityId() {
		return this.entityId;
	}

	/**
	 * @return the entity type protocol id
	 */
	protected final int getProtocolId() {
		return this.protocolID;
	}
	
	/**
	 * @return the visibilityHandler
	 */
	protected final VisibilityHandler getVisibilityHandler() {
		this.assertNotDead();
		return this.visibilityHandler;
	}

	/**
	 * @return the dataWatcher
	 */
	protected final WrappedDataWatcher getDataWatcher() {
		this.assertNotDead();
		return this.dataWatcher;
	}

	/**
	 * Sends a Entity Metadata update packet to players this entity is visible to
	 */
	protected final void sendMetaUpdate() {
		this.assertNotDead();
		for (Player p : this.visibilityHandler.renderedTo()) {
			this.sendMetaUpdatePacket(p);
		}
	}
	
	private final void sendMovePacket(double newX, double newY, double newZ, boolean onGround) {
		this.assertNotDead();
		if (this.visibilityHandler.renderedTo().isEmpty()) return;

		PacketContainer movePacket = new PacketContainer(PacketType.Play.Server.REL_ENTITY_MOVE_LOOK);

		short dx = (short) ((newX * 32 - this.x * 32) * 128);
		short dy = (short) ((newY * 32 - this.y * 32) * 128);
		short dz = (short) ((newZ * 32 - this.z * 32) * 128);

		movePacket.getIntegers()
			.write(0, this.getEntityId());
		movePacket.getShorts()
			.write(0, dx)
			.write(1, dy)
			.write(2, dz);
		movePacket.getBytes()
			.write(0, AngleUtil.fromDegrees(this.yaw))
			.write(1, AngleUtil.fromDegrees(this.pitch));
		movePacket.getBooleans()
			.write(0, onGround);

		try {
			for (Player p : this.visibilityHandler.renderedTo()) {
				protocol.sendServerPacket(p, movePacket);
			}
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	private final void sendTeleportPacket(double newX, double newY, double newZ) {
		this.assertNotDead();
		if (this.visibilityHandler.renderedTo().isEmpty()) return;

		PacketContainer teleportPacket = new PacketContainer(PacketType.Play.Server.ENTITY_TELEPORT);

		teleportPacket.getIntegers()
			.write(0, this.getEntityId());
		teleportPacket.getDoubles()
			.write(0, newX)
			.write(1, newY)
			.write(2, newZ);
		teleportPacket.getBytes()
			.write(0, AngleUtil.fromDegrees(this.yaw))
			.write(1, AngleUtil.fromDegrees(this.pitch));

		try {
			for (Player p : this.visibilityHandler.renderedTo()) {
				protocol.sendServerPacket(p, teleportPacket);
			}
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Send the update packet to a single player
	 * 
	 * @param player
	 */
	private final void sendMetaUpdatePacket(Player player) {
		this.assertNotDead();
		PacketContainer metaPacket = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);

		metaPacket.getIntegers()
			.write(0, this.getEntityId());
		metaPacket.getWatchableCollectionModifier()
			.write(0, this.dataWatcher.getWatchableObjects());

		try {
			protocol.sendServerPacket(player, metaPacket);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @throws IllegalStateException If entity is dead
	 */
	protected final void assertNotDead() {
		if (this.isDead.get())
			throw new IllegalStateException(
					"FakeEntity: Cannot Perform Set / Update Operations On A Destroyed Entity");
	}
	
	protected final static class EntityMetaByte {
		/** Where in the DataWrapper the byte of booleans is located **/
		public static int byteIndex() {
			return 0;
		}

		/** @return Where within the Byte the invisible bit is located. */
		static int invisibleIndex() {
			switch (MCVersion.CUR_VERSION()) {
			case V1_10:
			case V1_11:
			case V1_12:
				return 4;
			case V1_13:
			case V1_14:
			case V1_15:
			case V1_16:
			default:
				return 5;
			}
		}
		
		static int glowingIndex() {
			switch (MCVersion.CUR_VERSION()) {
			case V1_10:
			case V1_11:
			case V1_12:
				return 5;
			case V1_13:
			case V1_14:
			case V1_15:
			case V1_16:
			default:
				return 6;
			}
		}
	}

	protected final static class EntityMetaData {
		/** @return Index inside DataWatcher the custom name is located. */
		static int customNameIndex() {
			switch (MCVersion.CUR_VERSION()) {
			case V1_10:
			case V1_11:
			case V1_12:
			case V1_13:
			case V1_14:
			case V1_15:
			case V1_16:
			default:
				return 2;
			}
		}

		/** @return Index inside DataWatcher the custom name visible is located. */
		static int nameVisible() {
			switch (MCVersion.CUR_VERSION()) {
			case V1_10:
			case V1_11:
			case V1_12:
			case V1_13:
			case V1_14:
			case V1_15:
			case V1_16:
			default:
				return 3;
			}
		}

		/** @return Index inside DataWatcher the invisibility is located. */
		static int gravityIndex() {
			switch (MCVersion.CUR_VERSION()) {
			case V1_10:
			case V1_11:
			case V1_12:
			case V1_13:
			case V1_14:
			case V1_15:
			case V1_16:
			default:
				return 5;
			}
		}
		
		static int poseIndex() {
			switch (MCVersion.CUR_VERSION()) {
			case V1_10:
			case V1_11:
			case V1_12:
			case V1_13:
			case V1_14:
			case V1_15:
			case V1_16:
			default:
				return 6;
			}
		}
	}
}