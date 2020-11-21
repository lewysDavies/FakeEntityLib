package uk.lewdev.entitylib.entity;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;

import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;
import com.comphenix.protocol.wrappers.WrappedVillagerData;
import com.comphenix.protocol.wrappers.WrappedVillagerData.Profession;
import com.comphenix.protocol.wrappers.WrappedVillagerData.Type;

import uk.lewdev.entitylib.entity.protocol.FakeInsentient;
import uk.lewdev.entitylib.utils.MCVersion;

/**
 * @since 1.15+
 * @author Lewys Davies (Lew_)
 */
public class FakeVillager extends FakeInsentient {

	private WrappedDataWatcherObject villagerDataWatcher = new WrappedDataWatcherObject(VillagerMetaData.villagerDataIndex(), 
			Registry.get(WrappedVillagerData.getNmsClass()));
	
	private Type type = Type.PLAINS;
	private Profession profession = Profession.NONE;
	
	public FakeVillager(World world, double x, double y, double z, float yaw, float headPitch, float headYaw) {
		super(EntityType.VILLAGER, UUID.randomUUID(), world, x, y, z, yaw, headPitch, headYaw);
		this.initDataWatcherObjs();
	}
	
	public FakeVillager(World world, double x, double y, double z, float yaw, float headPitch) {
		this(world, x, y, z, yaw, headPitch, 0);
	}
	
	public FakeVillager(Location loc, float headYaw) {
		this(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch(), headYaw);
	}

	private final void initDataWatcherObjs() {
		WrappedVillagerData vd = WrappedVillagerData.fromValues(this.type, this.profession, 1);
		super.getDataWatcher().setObject(this.villagerDataWatcher, vd.getHandle());
	}
	
	public final void setVillagerData(Type type, Profession profession) {
		super.assertNotDead();
		
		this.type = type;
		this.profession = profession;
		
		WrappedVillagerData vd = WrappedVillagerData.fromValues(type, profession, 2);
		super.getDataWatcher().setObject(this.villagerDataWatcher, vd.getHandle());
		super.sendMetaUpdate();
	}
	
	private enum VillagerMetaData {
		MC1_15(17);
		
		private int villagerData;
		
		private VillagerMetaData(int villagerData) {
			this.villagerData = villagerData;
		}
		
		public static VillagerMetaData curVer() {
			switch (MCVersion.CUR_VERSION()) {
			default:
				return MC1_15;
			}
		}
		
		public static int villagerDataIndex() {
			return curVer().villagerData;
		}
	}
}
