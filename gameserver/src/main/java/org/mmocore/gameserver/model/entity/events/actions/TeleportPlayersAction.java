package org.mmocore.gameserver.model.entity.events.actions;

import org.mmocore.gameserver.model.entity.events.Event;
import org.mmocore.gameserver.model.entity.events.EventAction;

/**
 * @author VISTALL
 * @date 0:47/18.05.2011
 */
public class TeleportPlayersAction implements EventAction {
    private final String _name;

    public TeleportPlayersAction(final String name) {
        _name = name;
    }

    @Override
    public void call(final Event event) {
        event.teleportPlayers(_name, null);
    }
}
