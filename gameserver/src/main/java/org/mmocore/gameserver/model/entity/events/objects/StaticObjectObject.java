package org.mmocore.gameserver.model.entity.events.objects;

import org.mmocore.gameserver.data.xml.holder.StaticObjectHolder;
import org.mmocore.gameserver.model.entity.events.Event;
import org.mmocore.gameserver.model.instances.StaticObjectInstance;

/**
 * @author VISTALL
 * @date 22:50/09.03.2011
 */
public class StaticObjectObject implements SpawnableObject {
    private final int _uid;
    private StaticObjectInstance _instance;

    public StaticObjectObject(final int id) {
        _uid = id;
    }

    @Override
    public void spawnObject(final Event event) {
        _instance = StaticObjectHolder.getInstance().getObject(_uid);
    }

    @Override
    public void despawnObject(final Event event) {
        //
    }

    @Override
    public void respawnObject(Event event) {

    }

    @Override
    public void refreshObject(final Event event) {
        if (!event.isInProgress()) {
            _instance.removeEvent(event);
        } else {
            _instance.addEvent(event);
        }
    }

    public void setMeshIndex(final int id) {
        _instance.setMeshIndex(id);
        _instance.broadcastInfo(false);
    }

    public int getUId() {
        return _uid;
    }
}
