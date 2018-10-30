package org.mmocore.gameserver.model.entity.events.impl.reflection.raid;

import org.mmocore.gameserver.model.instances.MonsterInstance;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

/**
 * @author Mangol
 * @since 08.10.2016
 */
public class RRaidBossInstance extends MonsterInstance {
    public RRaidBossInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public boolean isFearImmune() {
        return true;
    }

    @Override
    public boolean isParalyzeImmune() {
        return true;
    }

    @Override
    public boolean isLethalImmune() {
        return true;
    }

    @Override
    public boolean hasRandomWalk() {
        return false;
    }

    @Override
    public boolean canChampion() {
        return false;
    }

    @Override
    public boolean isIgnoreDrop() {
        return true;
    }

    @Override
    public boolean isIgnoreCrp() {
        return true;
    }

    @Override
    public long getExpReward() {
        return 0;
    }

    @Override
    public long getSpReward() {
        return 0;
    }
}
