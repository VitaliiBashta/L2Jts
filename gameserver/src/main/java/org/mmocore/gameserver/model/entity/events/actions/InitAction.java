package org.mmocore.gameserver.model.entity.events.actions;

import org.mmocore.gameserver.model.entity.events.Event;
import org.mmocore.gameserver.model.entity.events.EventAction;

/**
 * @author VISTALL
 * @date 11:41/30.06.2011
 */
public class InitAction implements EventAction {
    private final String _name;

    public InitAction(final String name) {
        _name = name;
    }

    @Override
    public void call(final Event event) {
        event.initAction(_name);
    }
}
