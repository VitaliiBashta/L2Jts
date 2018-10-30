package org.mmocore.gameserver.scripts.ai.pts.the_enchanted_valley;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;

/**
 * @author KilRoy
 * @author Mangol
 */
public class tree_q0421 extends Fighter {
    private static final int guardian_of_tree = 27189;

    public tree_q0421(final NpcInstance actor) {
        super(actor);
        actor.block();
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
        final NpcInstance actor = getActor();
        if (actor != null && attacker != null) {
            if (attacker.isPlayer()) {
                if (actor.getCurrentHpPercents() <= 67 && Rnd.chance(29)) {
                    actor.doCast(SkillTable.getInstance().getSkillEntry(4243, 1), attacker, true);
                }
            } else if (attacker.isPet()) {
                super.onEvtAttacked(attacker, skill, damage);
            }
        }
        super.onEvtAttacked(attacker, skill, damage);
    }

    @Override
    protected void onEvtDead(final Creature killer) {
        final NpcInstance npc = getActor();
        if (npc.getDistance(killer) <= 1500) {
            npc.getPrivatesList().createOnePrivateEx(guardian_of_tree, npc.getX(), npc.getY(), npc.getZ(), 0, killer, 1, -1, false);
            for (int i = 1; i <= 19; i++) {
                npc.getPrivatesList().createOnePrivateEx(guardian_of_tree, npc.getX(), npc.getY(), npc.getZ(), 0, killer, 1, 0, false);
            }
        }
        super.onEvtDead(killer);
    }

    @Override
    protected void onEvtAggression(final Creature attacker, final int aggro) {
    }

    @Override
    protected boolean randomAnimation() {
        return false;
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }
}