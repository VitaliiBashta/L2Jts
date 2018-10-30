package org.mmocore.gameserver.skills.skillclasses;

import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.templates.StatsSet;

import java.util.List;


public class VitalityHeal extends Skill {
    public VitalityHeal(final StatsSet set) {
        super(set);
    }

    @Override
    public void useSkill(final SkillEntry skillEntry, final Creature activeChar, final List<Creature> targets) {
        final int fullPoints = AllSettingsConfig.VITALITY_LEVELS[4];
        final double percent = _power;

        for (final Creature target : targets) {
            if (target.isPlayer()) {
                final Player player = target.getPlayer();
                final double points = fullPoints / 100.0d * percent;
                player.getVitalityComponent().addVitality(points);
            }
            getEffects(skillEntry, activeChar, target, getActivateRate() > 0, false);
        }

        if (isSSPossible()) {
            activeChar.unChargeShots(isMagic());
        }
    }
}