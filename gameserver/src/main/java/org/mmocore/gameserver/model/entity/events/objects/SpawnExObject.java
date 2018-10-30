package org.mmocore.gameserver.model.entity.events.objects;

import org.mmocore.gameserver.manager.SpawnManager;
import org.mmocore.gameserver.model.Spawner;
import org.mmocore.gameserver.model.entity.events.Event;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author VISTALL
 * @date 17:33/10.12.2010
 */
public class SpawnExObject implements SpawnableObject {
    private static final Logger _log = LoggerFactory.getLogger(SpawnExObject.class);

    private final List<Spawner> _spawns;
    private final String _name;
    private boolean _spawned = false;

    public SpawnExObject(final String name) {
        _name = name;
        _spawns = SpawnManager.getInstance().getSpawners(_name);
        if (_spawns.isEmpty()) {
            _log.warn("SpawnExObject: not found spawn group: " + name);
        }
    }

    @Override
    public void spawnObject(final Event event) {
        if (_spawned) {
            _log.warn("SpawnExObject: can't spawn twice: " + _name + "; event: " + event, new Exception());
        } else {
            for (final Spawner spawn : _spawns) {
                if (event.isInProgress()) {
                    spawn.addEvent(event);
                } else {
                    spawn.removeEvent(event);
                }

                spawn.setReflection(event.getReflection());
                spawn.init();
            }
            _spawned = true;
        }
    }

    @Override
    public void respawnObject(Event event) {
        if (!_spawned) {
            _log.warn("SpawnExObject: can't respawn, not spawned: " + _name + "; event: " + event, new Exception());
        } else {
            _spawns.forEach(Spawner::init);
        }
    }

    @Override
    public void despawnObject(final Event event) {
        if (!_spawned) {
            return;
        }
        _spawned = false;
        for (final Spawner spawn : _spawns) {
            spawn.removeEvent(event);
            spawn.deleteAll();
        }
    }

    @Override
    public void refreshObject(final Event event) {
        for (final NpcInstance npc : getAllSpawned()) {
            if (event.isInProgress()) {
                npc.addEvent(event);
            } else {
                npc.removeEvent(event);
            }
        }
    }

    public List<Spawner> getSpawns() {
        return _spawns;
    }

    public List<NpcInstance> getAllSpawned() {
        final List<NpcInstance> npcs = new ArrayList<>();
        for (final Spawner spawn : _spawns) {
            npcs.addAll(spawn.getAllSpawned());
        }
        return npcs.isEmpty() ? Collections.<NpcInstance>emptyList() : npcs;
    }

    public NpcInstance getFirstSpawned() {
        final List<NpcInstance> npcs = getAllSpawned();
        return npcs.isEmpty() ? null : npcs.get(0);
    }

    public boolean isSpawned() {
        return _spawned;
    }
}