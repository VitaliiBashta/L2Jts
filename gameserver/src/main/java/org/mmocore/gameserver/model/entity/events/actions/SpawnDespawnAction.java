package org.mmocore.gameserver.model.entity.events.actions;

import org.mmocore.gameserver.model.entity.events.Event;
import org.mmocore.gameserver.model.entity.events.EventAction;

/**
 * @author VISTALL
 * @date 17:05/10.12.2010
 */
public class SpawnDespawnAction implements EventAction {
    private final boolean _spawn;
    private final String _name;

    public SpawnDespawnAction(final String name, final boolean spawn) {
        _spawn = spawn;
        _name = name;
    }

    @Override
    public void call(final Event event) {
        event.spawnAction(_name, _spawn);
    }
}
