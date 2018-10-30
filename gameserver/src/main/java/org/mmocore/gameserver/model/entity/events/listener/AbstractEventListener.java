package org.mmocore.gameserver.model.entity.events.listener;

import org.mmocore.gameserver.model.entity.events.Event;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.object.Player;

/**
 * @author Mangol
 * @since 02.05.2016
 */
public abstract class AbstractEventListener<T extends Event> {
    protected static final CustomMessage ALREADY_REG = new CustomMessage("event.r.already");
    protected static final CustomMessage CLOSE_REG = new CustomMessage("event.r.close");
    protected static final CustomMessage OK_REG = new CustomMessage("event.r.ok");

    private Player player;
    private T event;

    public AbstractEventListener(final T event) {
        this.event = event;
    }

    public AbstractEventListener(final T event, final Player player) {
        this.event = event;
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public T getEvent() {
        return event;
    }
}
