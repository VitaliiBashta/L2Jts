package org.mmocore.gameserver.scripts.ai.pts.dragon_valley;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * @author KilRoy
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class ai_dust_rider extends detect_party_warrior {
    private int i_ai2;

    public ai_dust_rider(NpcInstance actor) {
        super(actor);
        i_ai2 = 0;
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
        if (getActor().getCurrentHpPercents() < 30 && i_ai2 == 0) {
            getActor().altOnMagicUseTimer(getActor(), SkillTable.getInstance().getSkillEntry(6914, 3));
            i_ai2 = 1;
        }
        super.onEvtAttacked(attacker, skill, damage);
    }
}