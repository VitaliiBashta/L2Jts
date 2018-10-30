package org.mmocore.gameserver.model.entity.events.actions;

import org.mmocore.gameserver.model.entity.events.Event;
import org.mmocore.gameserver.model.entity.events.EventAction;

/**
 * @author VISTALL
 * @date 23:45/09.03.2011
 */
public class RefreshAction implements EventAction {
    private final String _name;

    public RefreshAction(final String name) {
        _name = name;
    }

    @Override
    public void call(final Event event) {
        event.refreshAction(_name);
    }
}
