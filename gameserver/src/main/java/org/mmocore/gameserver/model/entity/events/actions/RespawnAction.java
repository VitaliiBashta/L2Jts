package org.mmocore.gameserver.model.entity.events.actions;

import org.mmocore.gameserver.model.entity.events.Event;
import org.mmocore.gameserver.model.entity.events.EventAction;

public class RespawnAction implements EventAction {
    private final String name;

    public RespawnAction(String name) {
        this.name = name;
    }

    @Override
    public void call(Event event) {
        event.respawnAction(name);
    }
}