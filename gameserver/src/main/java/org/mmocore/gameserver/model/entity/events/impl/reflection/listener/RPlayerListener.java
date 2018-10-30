package org.mmocore.gameserver.model.entity.events.impl.reflection.listener;

import org.mmocore.gameserver.listener.actor.player.OnPlayerExitListener;
import org.mmocore.gameserver.listener.actor.player.OnTeleportListener;
import org.mmocore.gameserver.model.entity.Reflection;
import org.mmocore.gameserver.model.entity.events.impl.ReflectionEvent;
import org.mmocore.gameserver.model.entity.events.impl.reflection.REventState;
import org.mmocore.gameserver.model.entity.events.listener.AbstractEventListener;
import org.mmocore.gameserver.object.Player;

/**
 * @author Mangol
 * @since 03.05.2016
 */
public class RPlayerListener<T extends ReflectionEvent> extends AbstractEventListener<T> implements OnPlayerExitListener, OnTeleportListener {
    public RPlayerListener(T event) {
        super(event);
    }

    @Override
    public void onPlayerExit(final Player player) {
        if (player == null) {
            return;
        }
        getEvent().exitPlayer(player);
    }

    @Override
    public void onTeleport(Player player, int x, int y, int z, Reflection reflection) {
        if (player == null)
            return;
        if (getEvent().getEventState() != REventState.NONE && reflection != getEvent().getReflection())
            getEvent().exitPlayer(player);
    }
}
