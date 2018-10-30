package org.mmocore.gameserver.scripts.ai;

import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.world.World;

import java.util.List;

/**
 * @author Magister
 * @date 22/02/2015
 * @npc 18437
 * @memo Используется для баффа в инстанс, для квеста _179_IntoTheLargeCavern.
 */
public class NornilsTrapBuff extends DefaultAI {
    public NornilsTrapBuff(NpcInstance actor) {
        super(actor);
        AI_TASK_ACTIVE_DELAY = 3000;
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
    }

    @Override
    protected void onEvtThink() {
        NpcInstance actor = getActor();
        List<Creature> around = World.getAroundCharacters(actor, 200, 300);
        if (around.isEmpty()) {
            return;
        }
        for (Creature cha : around) {
            if (cha.isPlayer() && !cha.isDead()) {
                SkillTable.getInstance().getSkillEntry(4322, 1).getEffects(cha, cha, false, false);
                SkillTable.getInstance().getSkillEntry(4324, 1).getEffects(cha, cha, false, false);
                SkillTable.getInstance().getSkillEntry(4327, 1).getEffects(cha, cha, false, false);
                SkillTable.getInstance().getSkillEntry(4329, 1).getEffects(cha, cha, false, false);
            }
        }
    }
}
