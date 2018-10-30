package org.mmocore.gameserver.model.entity.events.objects;

import org.mmocore.gameserver.model.entity.events.Event;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.NpcUtils;

/**
 * @author VISTALL
 * @date 16:32/14.07.2011
 */
public class SpawnSimpleObject implements SpawnableObject {
    private final int _npcId;
    private final Location _loc;

    private NpcInstance _npc;

    public SpawnSimpleObject(final int npcId, final Location loc) {
        _npcId = npcId;
        _loc = loc;
    }

    @Override
    public void spawnObject(final Event event) {
        _npc = NpcUtils.spawnSingle(_npcId, _loc, event.getReflection());
        _npc.addEvent(event);
    }

    @Override
    public void despawnObject(final Event event) {
        _npc.removeEvent(event);
        _npc.deleteMe();
    }

    @Override
    public void respawnObject(Event event) {
        if (_npc != null && !_npc.isVisible()) {
            _npc.setCurrentHpMp(_npc.getMaxHp(), _npc.getMaxMp(), true);
            _npc.setHeading(_loc.h);
            _npc.setReflection(event.getReflection());
            _npc.spawnMe(_npc.getSpawnedLoc());
        }
    }

    @Override
    public void refreshObject(final Event event) {

    }

    public int getNpcId() {
        return _npcId;
    }

    public Location getLoc() {
        return _loc;
    }

    public NpcInstance getNpc() {
        return _npc;
    }

}
