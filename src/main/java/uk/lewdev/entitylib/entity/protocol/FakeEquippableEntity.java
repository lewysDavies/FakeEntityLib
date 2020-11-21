package uk.lewdev.entitylib.entity.protocol;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;

import uk.lewdev.entitylib.entity.protocol.wrappers.WrapperPlayServerEntityEquipment;

/**
 * A {@link FakeLivingEntity} that could have armour
 * 
 * @author Lewys Davies (Lew_)
 */
public class FakeEquippableEntity extends FakeLivingEntity {

    private final Map<ItemSlot, ItemStack> equipment = new HashMap<>();

    protected FakeEquippableEntity(EntityType type, UUID uuid, World world, double x, double y, double z, float yaw,
            float headPitch, float headYaw) {
        super(type, uuid, world, x, y, z, yaw, headPitch);
    }

    protected FakeEquippableEntity(EntityType type, UUID uuid, World world, double x, double y, double z, float yaw,
            float headPitch) {
        super(type, uuid, world, x, y, z, yaw, headPitch, 0);
    }

    protected FakeEquippableEntity(EntityType type, UUID uuid, World world, double x, double y, double z) {
        super(type, uuid, world, x, y, z, 0, 0, 0);
    }

    @Override
    protected void sendSpawnPacket(Player player) {
        super.sendSpawnPacket(player);

        for (Entry<ItemSlot, ItemStack> slot : equipment.entrySet()) {
            WrapperPlayServerEntityEquipment packet = new WrapperPlayServerEntityEquipment();
            packet.setEntityID(getEntityId());
            packet.setSlot(slot.getKey());
            packet.setItem(slot.getValue());
            
            packet.sendPacket(player);
        }
    }

    public void setItem(ItemSlot slot, ItemStack item) {
        this.assertNotDead();
        
        this.equipment.put(slot, item);
        
        WrapperPlayServerEntityEquipment packet = new WrapperPlayServerEntityEquipment();
        packet.setEntityID(getEntityId());
        packet.setSlot(slot);
        packet.setItem(item);

        for (Player player : super.getVisibilityHandler().renderedTo()) {
            packet.sendPacket(player);
        }
    }
    
    public ItemStack getItem(ItemSlot slot) {
        return this.equipment.get(slot);
    }
}
