package org.mmocore.gameserver.skills.skillclasses;

import org.mmocore.gameserver.configuration.config.FormulasConfig;
import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Servitor;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.stats.Formulas;
import org.mmocore.gameserver.templates.StatsSet;

import java.util.List;

public class DestroySummon extends Skill {
    public DestroySummon(final StatsSet set) {
        super(set);
    }

    @Override
    public void useSkill(final SkillEntry skillEntry, final Creature activeChar, final List<Creature> targets) {
        boolean success;
        for (final Creature target : targets) {
            if (target != null) {
                if (FormulasConfig.ALT_DEBUFF_CALCULATE) {
                    success = Formulas.calcSuccessEffect(activeChar, target, skillEntry, getActivateRate());
                } else {
                    success = Formulas.calcSkillSuccess(activeChar, target, skillEntry, getActivateRate());
                }

                if (getActivateRate() > 0 && !success) {
                    activeChar.sendPacket(new SystemMessage(SystemMsg.C1_HAS_RESISTED_YOUR_S2).addString(target.getName()).addSkillName(getId(), getLevel()));
                    continue;
                }

                if (target.isSummon()) {
                    ((Servitor) target).saveEffects();
                    ((Servitor) target).unSummon(false, false);
                    getEffects(skillEntry, activeChar, target, getActivateRate() > 0, false);
                }
            }
        }

        if (isSSPossible()) {
            activeChar.unChargeShots(isMagic());
        }
    }
}