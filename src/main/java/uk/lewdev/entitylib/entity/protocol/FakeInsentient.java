package uk.lewdev.entitylib.entity.protocol;

import java.util.UUID;

import org.bukkit.World;
import org.bukkit.entity.EntityType;

import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;

import uk.lewdev.entitylib.utils.MCVersion;
import uk.lewdev.entitylib.utils.MaskUtil;

/**
 * @author Lewys Davies (Lew_)
 */
public abstract class FakeInsentient extends FakeLivingEntity {
	
	private WrappedDataWatcherObject insentientByteWatcher = new WrappedDataWatcherObject(InsentientMetaData.byteIndex(), 
			Registry.get(Byte.class));
	
	private byte insentientByte = (byte) 0;
	private boolean leftHanded = false;
	private boolean ai = true;

	protected FakeInsentient(EntityType type, UUID uuid, World world, double x, double y, double z, float yaw, float headPitch, float headYaw) {
		super(type, uuid, world, x, y, z, yaw, headPitch, headYaw);
		super.getDataWatcher().setObject(this.insentientByteWatcher, this.insentientByte);
	}
	
	protected FakeInsentient(EntityType type, World world, double x, double y, double z, float yaw, float headPitch) {
		this(type, UUID.randomUUID(), world, x, y, z, yaw, headPitch, 0);
	}
	
	protected FakeInsentient(EntityType type, UUID uuid, World world, double x, double y, double z) {
		this(type, uuid, world, x, y, z, 0, 0, 0);
	}
	
	protected FakeInsentient(EntityType type, World world, double x, double y, double z) {
		this(type, UUID.randomUUID(), world, x, y, z, 0, 0, 0);
	}
	
	public final boolean hasAi() {
        return this.ai;
    }
	
	public void setAi(boolean ai) {
		super.assertNotDead();
		
		this.ai = ai;
		this.insentientByte =  MaskUtil.setBit(this.insentientByte, InsentientMetaData.aiBit(), this.ai);
		
		super.getDataWatcher().setObject(this.insentientByteWatcher, this.insentientByte);
		super.sendMetaUpdate();
	}
	
	public boolean isLeftHanded() {
        return this.leftHanded;
    }
	
	public void setLeftHanded(boolean leftHanded) {
		super.assertNotDead();
		
		this.leftHanded = leftHanded;
		this.insentientByte =  MaskUtil.setBit(this.insentientByte, InsentientMetaData.leftHandBit(), this.leftHanded);
		
		super.getDataWatcher().setObject(this.insentientByteWatcher, this.insentientByte);
		super.sendMetaUpdate();
	}
	
	private enum InsentientMetaData {
		MC1_15(14, 0, 1);
		
		private int insentientByteIndex, aiIndex, leftHandIndex;
		
		private InsentientMetaData(int insentientByteIndex, int aiIndex, int leftHandIndex) {
			this.insentientByteIndex = insentientByteIndex;
		}
		
		public static InsentientMetaData curVer() {
			switch (MCVersion.CUR_VERSION()) {
			default:
				return MC1_15;
			}
		}
		
		public static int byteIndex() {
			return curVer().insentientByteIndex;
		}
		
		public static int aiBit() {
			return curVer().aiIndex;
		}
		
		public static int leftHandBit() {
			return curVer().leftHandIndex;
		}
	}
}
