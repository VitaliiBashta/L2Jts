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
public class ai_drakos_warrior extends detect_party_warrior {

    public ai_drakos_warrior(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
        if (Rnd.get(100) < 1) {
            final int i1 = (2 + Rnd.get(3));
            getActor().altOnMagicUseTimer(getActor(), SkillTable.getInstance().getSkillEntry(6858, 1));
            for (int i0 = 0; i0 < i1; i0++) {
                int drakos_assasin = 22823;
                final NpcInstance npc = NpcUtils.spawnSingleStablePoint(drakos_assasin, getActor().getX() + Rnd.get(200), getActor().getY() + Rnd.get(200), getActor().getZ(), 350);
                npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, 2);
            }
        }
        super.onEvtAttacked(attacker, skill, damage);
    }
}