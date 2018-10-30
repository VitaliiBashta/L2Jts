package org.mmocore.gameserver.scripts.ai.pts.dragon_valley;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.ai.Mystic;
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
public class ai_valley_necro extends Mystic {
    private static final int maluk_summon_boomer = 22818;
    private static final int maluk_summon_zombie = 22819;

    public ai_valley_necro(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
        if (getActor().getCurrentHpPercents() < 60) {
            if (Rnd.get(10) < 1) {
                getActor().altOnMagicUseTimer(getActor(), SkillTable.getInstance().getSkillEntry(6848, 1));
                if (attacker != null) {
                    if (Rnd.get(2) < 1) {
                        final NpcInstance npc = NpcUtils.spawnSingleStablePoint(maluk_summon_boomer, getActor().getX(), getActor().getY(), getActor().getZ());
                        npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, 2);
                    } else {
                        final NpcInstance npc = NpcUtils.spawnSingleStablePoint(maluk_summon_zombie, getActor().getX(), getActor().getY(), getActor().getZ());
                        npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, 2);
                    }
                }
            }
        }
        super.onEvtAttacked(attacker, skill, damage);
    }
}