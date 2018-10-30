package org.mmocore.gameserver.scripts.ai.primeval_isle;

import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;

/**
 * @author VISTALL
 * @date 4:57/16.06.2011
 */
public class SprigantPoison extends DefaultAI {
    private static final int TICK_IN_MILISECONDS = 15000;
    private final SkillEntry SKILL = SkillTable.getInstance().getSkillEntry(5086, 1);
    private long _waitTime;

    public SprigantPoison(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected boolean thinkActive() {
        if (System.currentTimeMillis() > _waitTime) {
            NpcInstance actor = getActor();

            actor.doCast(SKILL, actor, false);

            _waitTime = System.currentTimeMillis() + TICK_IN_MILISECONDS;
            return true;
        }

        return false;
    }
}
