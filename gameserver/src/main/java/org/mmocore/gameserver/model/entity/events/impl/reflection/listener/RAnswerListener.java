package org.mmocore.gameserver.model.entity.events.impl.reflection.listener;

import org.mmocore.gameserver.listener.actor.player.OnAnswerListener;
import org.mmocore.gameserver.model.entity.events.impl.ReflectionEvent;
import org.mmocore.gameserver.model.entity.events.impl.reflection.REventState;
import org.mmocore.gameserver.model.entity.events.listener.AbstractEventListener;
import org.mmocore.gameserver.object.Player;

/**
 * @author Mangol
 * @since 02.05.2016
 */
public class RAnswerListener<T extends ReflectionEvent> extends AbstractEventListener<T> implements OnAnswerListener {
    private final long time;

    public RAnswerListener(final T event, final Player player, final long time) {
        super(event, player);
        this.time = time > 0 ? System.currentTimeMillis() + time : 0;
    }

    @Override
    public void sayYes() {
        if (getPlayer() == null) {
            return;
        }
        if (getPlayer().getEvent(getEvent().getClass()) != null) {
            getPlayer().sendMessage(ALREADY_REG);
        } else if (getEvent().getEventState() == REventState.CLOSE_REGISTRATION) {
            getPlayer().sendMessage(CLOSE_REG);
        } else if (getEvent().getEventState() == REventState.REGISTRATION) {
            getEvent().addPlayer(getPlayer());
            getPlayer().sendMessage(OK_REG);
        }
    }

    @Override
    public void sayNo() {

    }

    @Override
    public long expireTime() {
        return time;
    }
}
