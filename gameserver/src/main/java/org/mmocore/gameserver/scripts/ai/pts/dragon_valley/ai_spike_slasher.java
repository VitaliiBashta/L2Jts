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
public class ai_spike_slasher extends detect_party_warrior {
    private static final int gem_dragon = 25733;
    private int i_ai2;
    private int i_ai3;

    public ai_spike_slasher(NpcInstance actor) {
        super(actor);
        i_ai2 = 0;
        i_ai3 = 0;
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
        if (getActor().getCurrentHpPercents() < 60 && i_ai2 == 0) {
            final Creature rndTarget = getActor().getAggroList().getRandomHated();
            getActor().altOnMagicUseTimer(rndTarget, SkillTable.getInstance().getSkillEntry(6841, 1));
            final int i1 = 3 + Rnd.get(3);
            for (int i0 = 0; i0 < i1; i0++) {
                final NpcInstance npcs = NpcUtils.spawnSingleStablePoint(gem_dragon, getActor().getX() + Rnd.get(200), getActor().getY() + Rnd.get(200), getActor().getZ(), 60000L);
                npcs.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, rndTarget, 2);
            }
            i_ai2 = 1;
        }
        if (getActor().getCurrentHpPercents() < 20 && i_ai3 == 0) {
            final Creature rndTarget = getActor().getAggroList().getRandomHated();
            getActor().altOnMagicUseTimer(rndTarget, SkillTable.getInstance().getSkillEntry(6841, 1));
            final int i1 = 3 + Rnd.get(3);
            for (int i0 = 0; i0 < i1; i0++) {
                final NpcInstance npcs = NpcUtils.spawnSingleStablePoint(gem_dragon, getActor().getX() + Rnd.get(200), getActor().getY() + Rnd.get(200), getActor().getZ(), 60000L);
                npcs.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, rndTarget, 2);
            }
            i_ai3 = 1;
        }
        super.onEvtAttacked(attacker, skill, damage);
    }
}