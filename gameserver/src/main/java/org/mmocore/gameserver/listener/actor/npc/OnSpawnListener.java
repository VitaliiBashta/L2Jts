package org.mmocore.gameserver.listener.actor.npc;

import org.mmocore.gameserver.listener.NpcListener;
import org.mmocore.gameserver.model.instances.NpcInstance;

@FunctionalInterface
public interface OnSpawnListener extends NpcListener {
    void onSpawn(NpcInstance actor);
}
