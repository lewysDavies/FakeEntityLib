package uk.lewdev.entitylib.events;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import uk.lewdev.entitylib.FakeEntityPlugin;
import uk.lewdev.entitylib.entity.protocol.FakeEntity;

/**
 * @author Lewys Davies (Lew_)
 */
public class ProtocolLibListeners {

    private static boolean hasInit = false;

    public ProtocolLibListeners() {
        if (hasInit) {
            return;
        }

        hasInit = true;
        this.entityInteractListener();
    }

    private void entityInteractListener() {
        ProtocolLibrary.getProtocolManager().addPacketListener(
          new PacketAdapter(FakeEntityPlugin.instance, ListenerPriority.NORMAL, PacketType.Play.Client.USE_ENTITY, PacketType.Play.Client.ENTITY_ACTION) {
              @Override
              public void onPacketReceiving(PacketEvent event) {
                  int entityId = event.getPacket().getIntegers().getValues().get(0);
                  Player player = event.getPlayer();
                  FakeEntity entity = FakeEntity.ALL_ALIVE_INSTANCES.get(entityId);

                  if (entity != null) {
                      Bukkit.getPluginManager().callEvent(new PlayerInteractFakeEntity(player, entity));
                  }
              }
          });
    }
}
