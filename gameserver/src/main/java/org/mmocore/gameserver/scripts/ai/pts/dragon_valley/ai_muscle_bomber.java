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
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class ai_muscle_bomber extends detect_party_warrior {
    private static final int drakos_assasin = 22823;
    private static final int summonTimer = 23210001;
    private static final int limitTimer = 23210002;
    private int i_ai2;
    private int i_ai3;
    private int i_ai4;
    private int i_ai5;

    public ai_muscle_bomber(NpcInstance actor) {
        super(actor);
        i_ai2 = 0;
        i_ai3 = 0;
        i_ai5 = 0;
        i_ai4 = 0;
    }

    @Override
    protected void onEvtTimerFiredEx(int timer_id, Object arg1, Object arg2) {
        final NpcInstance actor = getActor();
        if (actor == null || actor.isDead()) {
            return;
        }

        if (timer_id == limitTimer) {
            i_ai5 = 1;
        } else if (timer_id == summonTimer && i_ai5 == 0 && !getActor().isDead()) {
            if (getActor().getTarget() != null) {
                final NpcInstance npc1 = NpcUtils.spawnSingleStablePoint(drakos_assasin, getActor().getX() + Rnd.get(100), getActor().getY() + Rnd.get(100), getActor().getZ(), 175);
                final NpcInstance npc2 = NpcUtils.spawnSingleStablePoint(drakos_assasin, getActor().getX() + Rnd.get(100), getActor().getY() + Rnd.get(100), getActor().getZ(), 175);
                npc1.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, getActor().getAggroList().getRandomHated(), 2);
                npc2.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, getActor().getAggroList().getRandomHated(), 2);
            }
            AddTimerEx(summonTimer, 60 * 1000);
        }
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
        if (getActor().getCurrentHpPercents() < 80 && i_ai2 == 0) {
            i_ai2 = 1;
            getActor().altOnMagicUseTimer(getActor(), SkillTable.getInstance().getSkillEntry(6842, 1));
        }
        if (getActor().getCurrentHpPercents() < 50 && i_ai3 == 0) {
            i_ai3 = 1;
            AddTimerEx(summonTimer, 60 * 1000);
            AddTimerEx(limitTimer, 300 * 1000);
            getActor().altOnMagicUseTimer(getActor(), SkillTable.getInstance().getSkillEntry(6842, 2));
        }
        if (getActor().getCurrentHpPercents() < 10 && i_ai4 == 0) {
            i_ai4 = 1;
            getActor().altOnMagicUseTimer(getActor(), SkillTable.getInstance().getSkillEntry(6843, 1));
        }
        super.onEvtAttacked(attacker, skill, damage);
    }
}