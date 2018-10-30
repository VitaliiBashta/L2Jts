package org.mmocore.gameserver.object.components.creatures.listeners;

import org.mmocore.gameserver.listener.actor.npc.OnDecayListener;
import org.mmocore.gameserver.listener.actor.npc.OnSpawnListener;
import org.mmocore.gameserver.model.instances.NpcInstance;


/**
 * @author G1ta0
 */
public class NpcListenerList extends CharListenerList {
    public NpcListenerList(final NpcInstance actor) {
        super(actor);
    }

    @Override
    public NpcInstance getActor() {
        return (NpcInstance) actor;
    }

    public void onSpawn() {
        if (!global.getListeners().isEmpty()) {
            global.getListeners().stream().filter(OnSpawnListener.class::isInstance).forEach(listener -> {
                ((OnSpawnListener) listener).onSpawn(getActor());
            });
        }

        if (!getListeners().isEmpty()) {
            getListeners().stream().filter(OnSpawnListener.class::isInstance).forEach(listener -> {
                ((OnSpawnListener) listener).onSpawn(getActor());
            });
        }
    }

    public void onDecay() {
        if (!global.getListeners().isEmpty()) {
            global.getListeners().stream().filter(OnDecayListener.class::isInstance).forEach(listener -> {
                ((OnDecayListener) listener).onDecay(getActor());
            });
        }

        if (!getListeners().isEmpty()) {
            getListeners().stream().filter(OnDecayListener.class::isInstance).forEach(listener -> {
                ((OnDecayListener) listener).onDecay(getActor());
            });
        }
    }
}
