package org.mmocore.gameserver.scripts.ai.pts.dragon_valley;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.CtrlIntention;
import org.mmocore.gameserver.model.Skill.SkillType;
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
public class ai_blackdagger_wing extends detect_party_wizard {
    public ai_blackdagger_wing(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean checkAggression(Creature target) {
        final NpcInstance actor = getActor();
        if (target.isPlayable() && !target.isDead() && !target.isInvisible()) {
            actor.getAggroList().addDamageHate(target, 0, 1);
            setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
            if (Rnd.get(5) < 1) {
                actor.altOnMagicUseTimer(target, SkillTable.getInstance().getSkillEntry(6834, 1));
            }
        }
        return true;
    }

    @Override
    protected void onEvtSeeSpell(SkillEntry skill, Creature caster) {
        final NpcInstance actor = getActor();
        if (actor.isDead() || skill == null || caster == null) {
            return;
        }

        if (skill.getSkillType() == SkillType.DEBUFF) {
            if (Rnd.get(5) < 1) {
                actor.altOnMagicUseTimer(caster, SkillTable.getInstance().getSkillEntry(6834, 1));
            }
        }
    }
}