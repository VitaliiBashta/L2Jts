package org.mmocore.gameserver.model.entity.events.actions;

import org.mmocore.gameserver.model.entity.events.Event;
import org.mmocore.gameserver.model.entity.events.EventAction;

/**
 * @author VISTALL
 * @date 15:44/28.04.2011
 */
public class ActiveDeactiveAction implements EventAction {
    private final boolean _active;
    private final String _name;

    public ActiveDeactiveAction(final boolean active, final String name) {
        _active = active;
        _name = name;
    }

    @Override
    public void call(final Event event) {
        event.zoneAction(_name, _active);
    }
}
