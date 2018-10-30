package org.mmocore.gameserver.model.entity.events.objects;

import org.mmocore.gameserver.manager.ReflectionManager;
import org.mmocore.gameserver.model.entity.Reflection;
import org.mmocore.gameserver.model.entity.events.Event;
import org.mmocore.gameserver.model.instances.DoorInstance;

/**
 * @author VISTALL
 * @date 17:29/10.12.2010
 */
public class DoorObject implements SpawnableObject, InitableObject {
    private final int _id;
    private DoorInstance _door;

    private boolean _weak;

    public DoorObject(final int id) {
        _id = id;
    }

    @Override
    public void initObject(final Event e) {
        _door = e.getReflection().getDoor(_id);
    }

    @Override
    public void spawnObject(final Event event) {
        refreshObject(event);
    }

    @Override
    public void despawnObject(final Event event) {
        final Reflection ref = event.getReflection();
        if (ref == ReflectionManager.DEFAULT) {
            refreshObject(event);
        } else {
            //TODO [VISTALL] удалить двери
        }
    }

    @Override
    public void respawnObject(Event event) {

    }

    @Override
    public void refreshObject(final Event event) {
        if (!event.isInProgress()) {
            _door.removeEvent(event);
        } else {
            _door.addEvent(event);
        }

        if (_door.getCurrentHp() <= 0) {
            _door.decayMe();
            _door.spawnMe();
        }

        _door.setCurrentHp(_door.getMaxHp() * (isWeak() ? 0.5 : 1.), true);
        close(event);
    }

    public int getUId() {
        return _door.getDoorId();
    }

    public int getUpgradeValue() {
        return _door.getUpgradeHp();
    }

    public void setUpgradeValue(final Event event, final int val) {
        _door.setUpgradeHp(val);
        refreshObject(event);
    }

    public void open(final Event e) {
        _door.openMe(null, !e.isInProgress());
    }

    public void close(final Event e) {
        _door.closeMe(null, !e.isInProgress());
    }

    public DoorInstance getDoor() {
        return _door;
    }

    public boolean isWeak() {
        return _weak;
    }

    public void setWeak(final boolean weak) {
        _weak = weak;
    }
}
