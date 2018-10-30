package org.mmocore.gameserver.model.entity.events.actions;

import org.mmocore.gameserver.model.entity.events.Event;
import org.mmocore.gameserver.model.entity.events.EventAction;

/**
 * @author VISTALL
 * @date 17:07/09.03.2011
 */
public class OpenCloseAction implements EventAction {
    private final boolean _open;
    private final String _name;

    public OpenCloseAction(final boolean open, final String name) {
        _open = open;
        _name = name;
    }

    @Override
    public void call(final Event event) {
        event.doorAction(_name, _open);
    }
}
