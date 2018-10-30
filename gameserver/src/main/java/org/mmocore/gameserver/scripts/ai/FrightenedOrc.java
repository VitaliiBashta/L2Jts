package org.mmocore.gameserver.scripts.ai;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;

public class FrightenedOrc extends Fighter {
    private boolean _sayOnAttack;

    public FrightenedOrc(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtSpawn() {
        _sayOnAttack = true;
        super.onEvtSpawn();
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
        NpcInstance actor = getActor();
        if (attacker != null && Rnd.chance(10) && _sayOnAttack) {
            Functions.npcSay(actor, "Don't kill me! If you show mercy I will pay you 10000 adena!");
            _sayOnAttack = false;
        }

        super.onEvtAttacked(attacker, skill, damage);
    }

}