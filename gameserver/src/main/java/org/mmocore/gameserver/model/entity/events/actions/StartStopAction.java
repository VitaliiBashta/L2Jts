package org.mmocore.gameserver.model.entity.events.actions;

import org.mmocore.gameserver.model.entity.events.Event;
import org.mmocore.gameserver.model.entity.events.EventAction;

/**
 * @author VISTALL
 * @date 16:29/10.12.2010
 */
public class StartStopAction implements EventAction {
    public static final String EVENT = "event";

    private final String _name;
    private final boolean _start;

    public StartStopAction(final String name, final boolean start) {
        _name = name;
        _start = start;
    }

    @Override
    public void call(final Event event) {
        event.action(_name, _start);
    }
}
