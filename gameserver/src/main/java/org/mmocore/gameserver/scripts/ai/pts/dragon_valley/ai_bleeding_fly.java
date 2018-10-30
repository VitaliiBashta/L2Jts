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
 * TODO[K] - скилы и АИ моба из АИ
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class ai_bleeding_fly extends detect_party_wizard {
    private static final int big_bloody_leech = 25734;
    private int i_ai2;
    private int i_ai3;
    private int i_ai5;
    private int i_ai6;

    public ai_bleeding_fly(NpcInstance actor) {
        super(actor);
        i_ai2 = 0;
        i_ai3 = 0;
        i_ai5 = 5;
        i_ai6 = 10;
    }

    @Override
    protected void onEvtTimerFiredEx(int timer_id, Object arg1, Object arg2) {
        final NpcInstance actor = getActor();
        if (actor == null || actor.isDead()) {
            return;
        }

        if (timer_id == 20100503) {
            if (i_ai5 > 0 && !getActor().isDead()) {
                i_ai5--;
                getActor().altOnMagicUseTimer(getActor(), SkillTable.getInstance().getSkillEntry(6832, 1));
                final NpcInstance npc1 = NpcUtils.spawnSingleStablePoint(big_bloody_leech, getActor().getX() + Rnd.get(150), getActor().getY() + Rnd.get(150), getActor().getZ(), 60000L);
                npc1.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, getActor().getAggroList().getRandomHated(), 2);
                final NpcInstance npc2 = NpcUtils.spawnSingleStablePoint(big_bloody_leech, getActor().getX() + Rnd.get(150), getActor().getY() + Rnd.get(150), getActor().getZ(), 60000L);
                npc2.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, getActor().getAggroList().getRandomHated(), 2);
                if (i_ai2 == 1) {
                    AddTimerEx(20100503, 140 * 1000);
                }
            }
        } else if (timer_id == 20100504) {
            if (i_ai6 > 0 && !getActor().isDead()) {
                i_ai6--;
                getActor().altOnMagicUseTimer(getActor(), SkillTable.getInstance().getSkillEntry(6832, 1));
                getActor().altOnMagicUseTimer(getActor(), SkillTable.getInstance().getSkillEntry(6915, 3));
                final NpcInstance npc1 = NpcUtils.spawnSingleStablePoint(big_bloody_leech, getActor().getX() + Rnd.get(150), getActor().getY() + Rnd.get(150), getActor().getZ(), 60000L);
                npc1.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, getActor().getAggroList().getRandomHated(), 2);
                final NpcInstance npc2 = NpcUtils.spawnSingleStablePoint(big_bloody_leech, getActor().getX() + Rnd.get(150), getActor().getY() + Rnd.get(150), getActor().getZ(), 60000L);
                npc2.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, getActor().getAggroList().getRandomHated(), 2);
                if (i_ai3 == 1) {
                    AddTimerEx(20100504, 80 * 1000);
                }
            }
        }
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
        if (getActor().getCurrentHpPercents() < 50 && i_ai2 == 0) {
            i_ai2 = 1;
            AddTimerEx(20100503, 1000);
        }
        if (getActor().getCurrentHpPercents() < 25 && i_ai3 == 0) {
            i_ai2 = 0;
            i_ai3 = 1;
            AddTimerEx(20100504, 1000);
        }
        super.onEvtAttacked(attacker, skill, damage);
    }
}