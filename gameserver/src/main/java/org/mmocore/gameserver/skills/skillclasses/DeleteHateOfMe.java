package org.mmocore.gameserver.skills.skillclasses;

import org.mmocore.gameserver.ai.CtrlIntention;
import org.mmocore.gameserver.configuration.config.FormulasConfig;
import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.stats.Formulas;
import org.mmocore.gameserver.templates.StatsSet;

import java.util.List;


public class DeleteHateOfMe extends Skill {
    public DeleteHateOfMe(final StatsSet set) {
        super(set);
    }

    @Override
    public void useSkill(final SkillEntry skillEntry, final Creature activeChar, final List<Creature> targets) {
        for (final Creature target : targets) {
            if (target != null) {
                if (activeChar.isPlayer() && ((Player) activeChar).isGM()) {
                    activeChar.sendMessage(new CustomMessage("org.mmocore.gameserver.skills.Formulas.Chance").addString(getName()).addNumber(getActivateRate()));
                }

                final boolean success;
                if (FormulasConfig.ALT_DEBUFF_CALCULATE) {
                    success = Formulas.calcSuccessEffect(activeChar, target, skillEntry, getActivateRate());
                } else {
                    success = Formulas.calcSkillSuccess(activeChar, target, skillEntry, getActivateRate());
                }

                if (target.isNpc() && success) {
                    final NpcInstance npc = (NpcInstance) target;
                    npc.getAggroList().remove(activeChar, true);
                    npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
                }
                getEffects(skillEntry, activeChar, target, true, false);
            }
        }
    }
}