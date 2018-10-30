package org.mmocore.gameserver.scripts.ai.pts.dragon_valley;

import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * @author KilRoy
 * TODO[K] - хз как у нас работают рефлект скилы, но данный моб, кидает их на себя и аттакера, болванку сделаю, остальное на ТОДО
 * Скилл рефлекта кладет на себя с шансом в 5%, при наложении рефлекта, включается таймер(15000мс) на восстановление i_ai3 в 0, и так по кругу.
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class ai_emerald_horn extends detect_party_wizard {
    private int i_ai2;
    private int i_ai3;

    public ai_emerald_horn(NpcInstance actor) {
        super(actor);
        i_ai2 = 0;
        i_ai3 = 1;
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
        if (/*getActor().getEffectList().getEffectsBySkillId(6823) != null && */i_ai3 == 1) //TODO[K] - тут проверка еще и на имеющийся на акторе скилл 6823 s_emerald_horn_reflective_attack1
        {
            i_ai2 = damage;
        }
        if (i_ai2 > 5000) {
            getActor().altOnMagicUseTimer(getActor().getAggroList().getRandomHated(), SkillTable.getInstance().getSkillEntry(6825, 2));
            i_ai2 = 0;
            i_ai3 = 0;
        }
        if (i_ai2 > 10000) {
            getActor().altOnMagicUseTimer(getActor().getAggroList().getRandomHated(), SkillTable.getInstance().getSkillEntry(6825, 1));
            i_ai2 = 0;
            i_ai3 = 0;
        }
        super.onEvtAttacked(attacker, skill, damage);
    }
}