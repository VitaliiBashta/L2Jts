package org.mmocore.gameserver.skills.skillclasses;

import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.templates.StatsSet;

import java.util.List;


public class DeathPenalty extends Skill {
    public DeathPenalty(final StatsSet set) {
        super(set);
    }

    @Override
    public boolean checkCondition(final SkillEntry skillEntry, final Creature activeChar, final Creature target, final boolean forceUse, final boolean dontMove,
                                  final boolean first) {
        // Chaotic characters can't use scrolls of recovery
        if (activeChar.getKarma() > 0 && !AllSettingsConfig.ALT_DEATH_PENALTY_C5_CHAOTIC_RECOVERY) {
            activeChar.sendActionFailed();
            return false;
        }

        return super.checkCondition(skillEntry, activeChar, target, forceUse, dontMove, first);
    }

    @Override
    public void useSkill(final SkillEntry skillEntry, final Creature activeChar, final List<Creature> targets) {
        for (final Creature target : targets) {
            if (target != null) {
                if (!target.isPlayer()) {
                    continue;
                }
                ((Player) target).getDeathPenalty().reduceLevel();
            }
        }
    }
}