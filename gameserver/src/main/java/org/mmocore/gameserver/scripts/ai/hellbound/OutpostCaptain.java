package org.mmocore.gameserver.scripts.ai.hellbound;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.manager.HellboundManager;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.utils.ReflectionUtils;

public class OutpostCaptain extends Fighter {
    private static final int iron_tower_out_door = 20250001;

    public OutpostCaptain(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
        if (attacker == null || attacker.getPlayer() == null) {
            return;
        }

        if (Rnd.chance(1)) {
            attacker.teleToLocation(getActor().getLoc());
        }
    }

    @Override
    protected void onEvtDead(final Creature killer) {
        final int i0 = HellboundManager.getHellboundLevel();
        if (i0 == 8) {
            ReflectionUtils.getDoor(iron_tower_out_door).openMe(killer.getPlayer(), false);
            getActor().getSpawn().stopRespawn();
        }
        super.onEvtDead(killer);
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }

}