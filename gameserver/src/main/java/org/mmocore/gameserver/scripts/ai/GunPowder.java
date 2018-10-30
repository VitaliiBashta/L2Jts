package org.mmocore.gameserver.scripts.ai;

import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;

/**
 * @author Mangol
 * @version Freya
 */
public class GunPowder extends DefaultAI {

    public GunPowder(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
        NpcInstance actor = getActor();
        if (actor != null && !actor.isDead()) {
            // skill use
            int s_pailaka_boomup1 = 5714;
            actor.doCast(SkillTable.getInstance().getSkillEntry(s_pailaka_boomup1, 1), actor, true);
            ThreadPoolManager.getInstance().schedule(new Runnable() {
                @Override
                public void run() {
                    actor.deleteMe();
                }
            }, 2000L);
        }
        super.onEvtAttacked(attacker, skill, damage);
    }
}