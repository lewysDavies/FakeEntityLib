package uk.lewdev.entitylib.entity.protocol;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import uk.lewdev.entitylib.utils.AngleUtil;

public abstract class FakeLivingEntity extends FakeEntity {
	
	private float headYaw = 0f;
	//TODO private byte livingByte = (byte) 0;
	
	// Note: Pitch is used for HeadPitch in living entities
	protected FakeLivingEntity(EntityType type, UUID uuid, World world, double x, double y, double z, float yaw, float headPitch, float headYaw) {
		super(type, uuid, world, x, y, z, yaw, headPitch);
		this.headYaw = headYaw;
	}
	// Note: Pitch is used for HeadPitch in living entities
	protected FakeLivingEntity(EntityType type, UUID uuid, World world, double x, double y, double z, float yaw, float headPitch) {
		this(type, uuid, world, x, y, z, yaw, headPitch, 0);
	}
	
	protected FakeLivingEntity(EntityType type, UUID uuid, World world, double x, double y, double z) {
		this(type, uuid, world, x, y, z, 0, 0, 0);
	}
	
	@Override
	protected void sendSpawnPacket(Player player) {
		super.assertNotDead();
		
		PacketContainer spawnPacket = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY_LIVING);
		
		spawnPacket.getIntegers()
			.write(0, super.getEntityId())
			.write(1, super.getProtocolId());
		spawnPacket.getUUIDs()
			.write(0, super.getUUID());
		spawnPacket.getDoubles()
			.write(0, super.getX())
			.write(1, super.getY())
			.write(2, super.getZ());
		spawnPacket.getBytes()
			.write(0, AngleUtil.fromDegrees(super.getYaw()))
			.write(1, AngleUtil.fromDegrees(super.getPitch()))
			.write(2, AngleUtil.fromDegrees(super.getPitch()));
		
		// Spawn
		try {
			protocol.sendServerPacket(player, spawnPacket);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
		// Set head yaw to current
		this.sendHeadYawPacket(player);
		super.sendMetaUpdate();
	}
	
	public final float getHeadPitch() {
		return super.getPitch();
	}
	
	public final float getHeadYaw() {
		return this.headYaw;
	}
	
	public final void setHeadPitch(float pitch) {
		super.setPitch(pitch);
	}
	
	/**
	 * Set the head yaw and updates all visible players
	 * @param yaw
	 */
	public final void setHeadYaw(float yaw) {
		super.assertNotDead();
		
		this.headYaw = yaw;
		super.getVisibilityHandler().renderedTo().forEach(player -> this.sendHeadYawPacket(player));
	}
	
	/**
	 * Send head rotation packet to player
	 * @param player
	 */
	protected final void sendHeadYawPacket(Player player) {
		super.assertNotDead();
		
		PacketContainer lookPacket = new PacketContainer(PacketType.Play.Server.ENTITY_HEAD_ROTATION);
		
		lookPacket.getIntegers()
			.write(0, super.getEntityId());
		lookPacket.getBytes()
			.write(0, AngleUtil.fromDegrees(this.headYaw));
		
		try {
			protocol.sendServerPacket(player, lookPacket);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
}