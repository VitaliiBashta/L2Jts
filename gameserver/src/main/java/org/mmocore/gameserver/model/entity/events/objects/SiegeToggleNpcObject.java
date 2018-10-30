package org.mmocore.gameserver.model.entity.events.objects;


import org.mmocore.gameserver.data.xml.holder.NpcHolder;
import org.mmocore.gameserver.model.entity.events.Event;
import org.mmocore.gameserver.model.instances.residences.SiegeToggleNpcInstance;
import org.mmocore.gameserver.utils.Location;

import java.util.Set;

/**
 * @author VISTALL
 * @date 17:55/12.03.2011
 */
public class SiegeToggleNpcObject implements SpawnableObject {
    private final SiegeToggleNpcInstance _toggleNpc;
    private final Location _location;

    public SiegeToggleNpcObject(final int id, final int fakeNpcId, final Location loc, final int hp, final Set<String> set) {
        _location = loc;

        _toggleNpc = (SiegeToggleNpcInstance) NpcHolder.getInstance().getTemplate(id).getNewInstance();

        _toggleNpc.initFake(fakeNpcId);
        _toggleNpc.setMaxHp(hp);
        _toggleNpc.setZoneList(set);
    }

    @Override
    public void spawnObject(final Event event) {
        _toggleNpc.decayFake();

        if (event.isInProgress()) {
            _toggleNpc.addEvent(event);
        } else {
            _toggleNpc.removeEvent(event);
        }

        _toggleNpc.setCurrentHp(_toggleNpc.getMaxHp(), true);
        _toggleNpc.spawnMe(_location);
    }

    @Override
    public void despawnObject(final Event event) {
        _toggleNpc.removeEvent(event);
        _toggleNpc.decayFake();
        _toggleNpc.decayMe();
    }

    @Override
    public void respawnObject(Event event) {

    }

    @Override
    public void refreshObject(final Event event) {

    }

    public SiegeToggleNpcInstance getToggleNpc() {
        return _toggleNpc;
    }

    public boolean isAlive() {
        return _toggleNpc.isVisible();
    }
}
