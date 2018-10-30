package org.mmocore.gameserver.model.instances;

import org.mmocore.gameserver.manager.RaidBossSpawnManager;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

public class MonsterWithLongRespawnInstance extends MonsterInstance {
    public MonsterWithLongRespawnInstance(final int objectId, final NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    protected void onDecay() {
        super.onDecay();
        RaidBossSpawnManager.getInstance().onBossDespawned(this);
    }

    @Override
    protected void onSpawn() {
        super.onSpawn();
        RaidBossSpawnManager.getInstance().onBossSpawned(this);
    }
}
