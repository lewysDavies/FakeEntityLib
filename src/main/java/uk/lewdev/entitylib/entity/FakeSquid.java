package uk.lewdev.entitylib.entity;

import java.util.UUID;

import org.bukkit.World;
import org.bukkit.entity.EntityType;

import uk.lewdev.entitylib.entity.protocol.FakeLivingEntity;

/**
 * @author Lewys Davies (Lew_)
 */
public class FakeSquid extends FakeLivingEntity {

    public FakeSquid(World world, double x, double y, double z) {
        super(EntityType.SQUID, UUID.randomUUID(), world, x, y, z);
    }
}
