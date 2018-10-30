package org.mmocore.gameserver.model.entity.events;

import org.mmocore.commons.threading.RunnableImpl;

/**
 * @author VISTALL
 * @date 18:02/10.12.2010
 */
public class EventTimeTask extends RunnableImpl {
    private final Event _event;
    private final int _time;

    public EventTimeTask(final Event event, final int time) {
        _event = event;
        _time = time;
    }

    @Override
    public void runImpl() throws Exception {
        _event.timeActions(_time);
    }
}
