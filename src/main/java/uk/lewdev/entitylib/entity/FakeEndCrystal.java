package uk.lewdev.entitylib.entity;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;

import uk.lewdev.entitylib.entity.protocol.FakeEntity;

/**
 * @author Lewys Davies (Lew_)
 */
public class FakeEndCrystal extends FakeEntity {
    
    private WrappedDataWatcherObject beamTarget = new WrappedDataWatcherObject(7, 
            Registry.getBlockPositionSerializer(true));
    
    private WrappedDataWatcherObject showBottom = new WrappedDataWatcherObject(8, 
            Registry.get(Boolean.class));

    /**
     * @param world
     * @param x
     * @param y
     * @param z
     */
    public FakeEndCrystal(World world, double x, double y, double z) {
        super(EntityType.ENDER_CRYSTAL, UUID.randomUUID(), world, x, y, z, 0, 0);
        this.initDataWatcherObjs();
    }
    
    protected void initDataWatcherObjs() {
        super.getDataWatcher().setObject(this.beamTarget, Optional.empty());
        super.getDataWatcher().setObject(this.showBottom, true);
    } 

    @Override
    protected void sendSpawnPacket(Player player) {
        super.assertNotDead();
        
        PacketContainer spawnPacket = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY);
        
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
            .write(0, (byte) 0)
            .write(1, (byte) 0)
            .write(2, (byte) 0);
        
        try {
            protocol.sendServerPacket(player, spawnPacket);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        
        super.sendMetaUpdate();
    }
    
    /**
     * @param loc (nullable)
     */
    public void setBeamTarget(Location loc) {
        if(loc != null) {
            super.getDataWatcher().setObject(this.beamTarget, Optional.of(new BlockPosition((int) loc.getX(), (int) loc.getY(), (int) loc.getZ())));
        } else {
            super.getDataWatcher().setObject(this.beamTarget, Optional.empty());
        }
        
        super.sendMetaUpdate();
    }
    
    /**
     * @param position (nullable)
     */
    public void setBeamTarget(BlockPosition position) {
        super.getDataWatcher().setObject(this.beamTarget, Optional.ofNullable(position));
        super.sendMetaUpdate();
    }
    
    public void setShowBottom(boolean showBottom) {
        super.getDataWatcher().setObject(this.showBottom, showBottom);
        super.sendMetaUpdate();
    }
}
