package org.mmocore.gameserver.model.entity.events.actions;

import org.mmocore.gameserver.model.entity.events.Event;
import org.mmocore.gameserver.model.entity.events.EventAction;

import java.util.Collections;
import java.util.List;

/**
 * @author VISTALL
 * @date 8:38/05.03.2011
 */
public class IfElseAction implements EventAction {
    private final String _name;
    private final boolean _reverse;
    private List<EventAction> _ifList = Collections.emptyList();
    private List<EventAction> _elseList = Collections.emptyList();

    public IfElseAction(final String name, final boolean reverse) {
        _name = name;
        _reverse = reverse;
    }

    @Override
    public void call(final Event event) {
        final List<EventAction> list = (_reverse ? !event.ifVar(_name) : event.ifVar(_name)) ? _ifList : _elseList;
        for (final EventAction action : list) {
            action.call(event);
        }
    }

    public void setIfList(final List<EventAction> ifList) {
        _ifList = ifList;
    }

    public void setElseList(final List<EventAction> elseList) {
        _elseList = elseList;
    }
}
