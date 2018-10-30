package org.mmocore.gameserver.model.entity.events.impl.reflection.listener;

import org.mmocore.gameserver.listener.actor.OnReviveListener;
import org.mmocore.gameserver.model.entity.events.impl.ReflectionEvent;
import org.mmocore.gameserver.model.entity.events.listener.AbstractEventListener;
import org.mmocore.gameserver.object.Creature;

import java.util.concurrent.ScheduledFuture;

/**
 * @author Mangol
 * @since 06.05.2016
 */
public class RReviveListener<T extends ReflectionEvent> extends AbstractEventListener<T> implements OnReviveListener {
    public RReviveListener(T event) {
        super(event);
    }

    @Override
    public void onRevive(Creature actor) {
        if (actor == null || !actor.isPlayer()) {
            return;
        }
        final ScheduledFuture<?> oldSchedule = getEvent().getDeathMap().remove(actor.getObjectId());
        if (oldSchedule != null) {
            oldSchedule.cancel(true);
        }
    }
}
