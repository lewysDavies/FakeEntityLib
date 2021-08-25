package uk.lewdev.entitylib.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import uk.lewdev.entitylib.entity.protocol.FakeEntity;

/**
 * @author Lewys Davies (Lew_)
 */
public class PlayerInteractFakeEntity extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Player player;
    private final FakeEntity entity;

    public PlayerInteractFakeEntity(Player player, FakeEntity entity) {
        this.player = player;
        this.entity = entity;
    }

    public Player getPlayer() {
        return this.player;
    }

    public FakeEntity getFakeEntity() {
        return this.entity;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
