package uk.lewdev.entitylib.entity;

import java.util.UUID;

import org.bukkit.World;
import org.bukkit.entity.EntityType;

import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;

import uk.lewdev.entitylib.entity.protocol.FakeLivingEntity;

/**
 * @author Lewys Davies (Lew_)
 */
public class FakeGuardian extends FakeLivingEntity {
    
    private WrappedDataWatcherObject guardianTarget = new WrappedDataWatcherObject(16, 
            Registry.get(Integer.class));
    
    private int entityTargetID = 0;

    protected FakeGuardian(World world, double x, double y, double z) {
        super(EntityType.GUARDIAN, UUID.randomUUID(), world, x, y, z);
        super.getDataWatcher().setObject(this.guardianTarget, this.entityTargetID);
    }
    
    public void setTarget(int entityID) {
        super.assertNotDead();
        
        this.entityTargetID = entityID;
        super.getDataWatcher().setObject(this.guardianTarget, this.entityTargetID);
        
        super.sendMetaUpdate();
    }
    
    public void clearTarget() {
        this.setTarget(0);
    }
    
    public int getTarget() {
        return this.entityTargetID;
    }
}
