package org.mmocore.gameserver.model.instances;

import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

public class ReflectionBossInstance extends RaidBossInstance {
    private static final int COLLAPSE_AFTER_DEATH_TIME = 5; // 5 мин

    public ReflectionBossInstance(final int objectId, final NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    protected int getMinChannelSizeForLock() {
        return 0;
    }

    @Override
    protected void onDeath(final Creature killer) {
        if (hasPrivates()) {
            getPrivatesList().unspawnPrivates();
        }
        super.onDeath(killer);
        clearReflection();
    }

    /**
     * Удаляет все спауны из рефлекшена и запускает 5ти минутный коллапс-таймер.
     */
    protected void clearReflection() {
        getReflection().clearReflection(COLLAPSE_AFTER_DEATH_TIME, true);
    }
}