package org.mmocore.gameserver.model.entity.events.actions;

import org.mmocore.gameserver.model.entity.events.Event;
import org.mmocore.gameserver.model.entity.events.EventAction;

/**
 * @author VISTALL
 * @date 11:12/11.03.2011
 */
public class AnnounceAction implements EventAction {
    private final int _id;

    public AnnounceAction(final int id) {
        _id = id;
    }

    @Override
    public void call(final Event event) {
        event.announce(_id);
    }
}
