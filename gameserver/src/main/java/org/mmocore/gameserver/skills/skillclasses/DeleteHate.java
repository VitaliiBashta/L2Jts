package org.mmocore.gameserver.skills.skillclasses;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.CtrlIntention;
import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.templates.StatsSet;

import java.util.List;


public class DeleteHate extends Skill {
    public DeleteHate(final StatsSet set) {
        super(set);
    }

    @Override
    public void useSkill(final SkillEntry skillEntry, final Creature activeChar, final List<Creature> targets) {
        for (final Creature target : targets) {
            if (target != null) {

                if (target.isRaid()) {
                    continue;
                }

                if (getActivateRate() > 0) {
                    if (activeChar.isPlayer() && ((Player) activeChar).isGM()) {
                        activeChar.sendMessage(new CustomMessage("org.mmocore.gameserver.skills.Formulas.Chance").addString(getName()).addNumber(getActivateRate()));
                    }

                    if (!Rnd.chance(getActivateRate())) {
                        return;
                    }
                }

                if (target.isNpc()) {
                    final NpcInstance npc = (NpcInstance) target;
                    npc.getAggroList().clear(false);
                    npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
                }

                getEffects(skillEntry, activeChar, target, false, false);
            }
        }
    }
}
