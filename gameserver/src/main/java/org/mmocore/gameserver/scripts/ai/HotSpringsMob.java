package org.mmocore.gameserver.scripts.ai;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.Mystic;
import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;

import java.util.List;

/**
 * AI for:
 * Hot Springs Atrox (id 21321)
 * Hot Springs Atroxspawn (id 21317)
 * Hot Springs Bandersnatch (id 21322)
 * Hot Springs Bandersnatchling (id 21314)
 * Hot Springs Flava (id 21316)
 * Hot Springs Nepenthes (id 21319)
 *
 * @author Diamond
 */
public class HotSpringsMob extends Mystic {
    private static final int DeBuffs[] = {4554, 4552};

    public HotSpringsMob(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
        NpcInstance actor = getActor();
        if (attacker != null && Rnd.chance(5)) {
            int DeBuff = DeBuffs[Rnd.get(DeBuffs.length)];
            List<Effect> effect = attacker.getEffectList().getEffectsBySkillId(DeBuff);
            if (effect != null) {
                int level = effect.get(0).getSkill().getLevel();
                if (level < 10) {
                    effect.get(0).exit();
                    SkillEntry skills = SkillTable.getInstance().getSkillEntry(DeBuff, level + 1);
                    skills.getEffects(actor, attacker, false, false);
                }
            } else {
                SkillEntry skills = SkillTable.getInstance().getSkillEntry(DeBuff, 1);
                if (skills != null) {
                    skills.getEffects(actor, attacker, false, false);
                } else {
                    System.out.println("Skill " + DeBuff + " is null, fix it.");
                }
            }
        }
        super.onEvtAttacked(attacker, skill, damage);
    }
}