package org.mmocore.gameserver.scripts.ai.pts.dragon_valley;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.utils.NpcUtils;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * @author KilRoy
 * TODO[K] - скилы статы и АИ мобов из данного АИ
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class ai_shadow_summoner extends detect_party_warrior {
    private static final int feast_boomer = 25730;
    private static final int feast_feeder = 25731;
    private static final int SummonTimer = 2010506;
    private static final int limitTimer = 2010508;
    private static final int delayTimer = 2010509;
    private int i_ai2;
    private int i_ai3;

    public ai_shadow_summoner(NpcInstance actor) {
        super(actor);
        i_ai2 = 0;
        i_ai3 = 0;
    }

    @Override
    protected void onEvtTimerFiredEx(int timer_id, Object arg1, Object arg2) {
        final NpcInstance actor = getActor();
        if (actor == null || actor.isDead()) {
            return;
        }

        if (timer_id == limitTimer) {
            i_ai3 = 1;
        }
        if (timer_id == SummonTimer && i_ai3 == 0 && !getActor().isDead()) {
            AddTimerEx(SummonTimer, 30 * 1000);
            AddTimerEx(limitTimer, 5 * 1000);
        }
        if (timer_id == delayTimer && !getActor().isDead()) {
            if (getActor().getTarget() != null) {
                if (Rnd.get(2) < 1) {
                    final NpcInstance npc1 = NpcUtils.spawnSingleStablePoint(feast_boomer, getActor().getX() + 150, getActor().getY() + 150, getActor().getZ(), 60000L);
                    npc1.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, getActor().getTarget(), 2);
                } else {
                    final NpcInstance npc2 = NpcUtils.spawnSingleStablePoint(feast_feeder, getActor().getX() + 150, getActor().getY() + 150, getActor().getZ(), 60000L);
                    npc2.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, getActor().getTarget(), 2);
                }
            }
        }
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
        if (getActor().getCurrentHpPercents() < 25 && i_ai2 == 0) {
            i_ai2 = 1;
            AddTimerEx(SummonTimer, 1000L);
            AddTimerEx(limitTimer, 10 * 60 * 1000);
            getActor().altOnMagicUseTimer(getActor(), SkillTable.getInstance().getSkillEntry(6842, 1));
        }
        super.onEvtAttacked(attacker, skill, damage);
    }
}