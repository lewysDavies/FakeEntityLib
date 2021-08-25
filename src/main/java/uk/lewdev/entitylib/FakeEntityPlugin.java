package uk.lewdev.entitylib;

import com.comphenix.protocol.ProtocolLibrary;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import uk.lewdev.entitylib.entity.protocol.EntityAsyncRenderTicker;
import uk.lewdev.entitylib.entity.protocol.FakeEntity;
import uk.lewdev.entitylib.event.ProtocolLibListeners;
import uk.lewdev.entitylib.utils.EntityIdProvider;
import uk.lewdev.entitylib.utils.MCVersion;

/**
 * @author Lewys Davies (Lew_)
 */
public class FakeEntityPlugin extends JavaPlugin implements FakeEntityAPI {

    static FakeEntityPlugin instance;
    private EntityIdProvider entityIdProvider;

    @Override
    public void onEnable() {
        instance = this;

        this.entityIdProvider = MCVersion.getCurrentVersion().getProvider();

        new EntityAsyncRenderTicker().runTaskTimerAsynchronously(this, 10, 10);
        new ProtocolLibListeners(this);
    }

    @Override
    public void onDisable() {
        ProtocolLibrary.getProtocolManager().removePacketListeners(this);
        FakeEntity.ALL_ALIVE_INSTANCES.values().forEach(FakeEntity::destroy);
    }

    @NotNull
    public EntityIdProvider getEntityIdProvider() {
        return this.entityIdProvider;
    }
}