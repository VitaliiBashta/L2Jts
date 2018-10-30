package org.mmocore.gameserver.model.entity.events.actions;

import org.mmocore.gameserver.model.entity.events.Event;
import org.mmocore.gameserver.model.entity.events.EventAction;

/**
 * Created by [STIGMATED] : 08.09.12 : 11:46
 */
public class AnnounceFromHolderAction implements EventAction {
    private final String _string;

    public AnnounceFromHolderAction(final String string) {
        _string = string;
    }

    @Override
    public void call(final Event event) {
        event.announceFromHolder(_string);
    }
}