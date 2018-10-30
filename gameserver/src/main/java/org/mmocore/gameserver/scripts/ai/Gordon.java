package org.mmocore.gameserver.scripts.ai;

import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;

public class Gordon extends Fighter {
    public Gordon(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean isGlobalAI() {
        return true;
    }

    @Override
    public boolean checkAggression(Creature target) {
        // Агрится только на носителей проклятого оружия
        if (!target.isCursedWeaponEquipped()) {
            return false;
        }
        return super.checkAggression(target);
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }
}