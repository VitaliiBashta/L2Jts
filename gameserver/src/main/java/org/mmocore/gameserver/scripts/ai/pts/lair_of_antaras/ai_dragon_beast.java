package org.mmocore.gameserver.scripts.ai.pts.lair_of_antaras;

import org.mmocore.gameserver.ai.CtrlIntention;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.utils.NpcUtils;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * @author KilRoy
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class ai_dragon_beast extends ai_antaras_cave_raid_basic {
    private static final int SPLIT_TIME = 4001;
    private int i_ai0;
    private int i_ai6;

    public ai_dragon_beast(NpcInstance actor) {
        super(actor);
        i_ai0 = 0;
        i_ai6 = 0;
    }

    @Override
    protected void onEvtTimerFiredEx(int timer_id, Object arg1, Object arg2) {
        if (getActor() == null || getActor().isDead()) {
            return;
        }

        if (timer_id == SPLIT_TIME) {
            if (getActor().getCurrentHpPercents() < 60) {
                i_ai6 = 1;
                for (int i = 0; i < 2; i++) {
                    final NpcInstance npc = NpcUtils.spawnSingle(25732, getActor().getLoc(), 10800000L);
                    npc.setCurrentHpMp(npc.getCurrentHp() * 0.20, npc.getCurrentMp());
                    if (getActor().getAggroList() != null && !getActor().getAggroList().isEmpty()) {
                        final Creature agrTarget = getActor().getAggroList().getRandomHated();
                        if (agrTarget != null) {
                            npc.getAggroList().addDamageHate(agrTarget, 0, 1);
                            npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, agrTarget);
                        }
                    }
                }
                getActor().deleteMe();
            } else if (i_ai6 == 0) {
                AddTimerEx(SPLIT_TIME, (10 * 1000));
            }
        }
        super.onEvtTimerFiredEx(timer_id, arg1, arg2);
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
        if (i_ai0 == 0) {
            i_ai0 = 1;
            AddTimerEx(SPLIT_TIME, (10 * 1000));
        }
        super.onEvtAttacked(attacker, skill, damage);
    }
}