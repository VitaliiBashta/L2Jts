package org.mmocore.gameserver.scripts.ai.pts.the_enchanted_valley;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.ai.Fighter;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.utils.ChatUtils;

/**
 * @author Mangol
 */
public class guardian_of_tree extends Fighter {
    public guardian_of_tree(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtTimerFiredEx(final int timer_id, final Object arg1, final Object arg2) {
        final NpcInstance actor = getActor();
        if (actor == null) {
            return;
        }
        if (timer_id == 42101) {
            actor.deleteMe();
        } else {
            super.onEvtTimerFiredEx(timer_id, arg1, arg2);
        }
    }

    @Override
    protected void onEvtSpawn() {
        super.onEvtSpawn();
        final NpcInstance actor = getActor();
        int i0 = actor.getParam2();
        final int i1 = actor.getParam3();
        final Creature arg = actor.getParam4();
        if (i0 > 0) {
            AddTimerEx(42101, 1000 * 300);
            if (arg != null && actor.getDistance(arg) <= 1500) {
                if (i1 == -1) {
                    actor.doCast(SkillTable.getInstance().getSkillEntry(4243, 1), arg, true);
                }
                actor.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, arg, 2000);
            }
        } else {
            i0 = Rnd.get(3);
            if (i0 == 0) {
                ChatUtils.say(actor, NpcString.valueOf(42118));
            } else if (i0 == 1) {
                ChatUtils.say(actor, NpcString.valueOf(42119));
            } else {
                ChatUtils.say(actor, NpcString.valueOf(42120));
            }
        }
    }
}
