package org.mmocore.gameserver.skills.skillclasses;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.stats.Formulas;
import org.mmocore.gameserver.stats.Stats;
import org.mmocore.gameserver.templates.StatsSet;

import java.util.List;


public class ManaDam extends Skill {
    public ManaDam(final StatsSet set) {
        super(set);
    }

    @Override
    public void useSkill(final SkillEntry skillEntry, final Creature activeChar, final List<Creature> targets) {
        int sps = 0;
        if (isSSPossible()) {
            sps = activeChar.getChargedSpiritShot();
        }

        for (final Creature target : targets) {
            if (target != null) {
                if (target.isDead()) {
                    continue;
                }

                double mAtk = activeChar.getMAtk(target, skillEntry);
                if (sps == 2) {
                    mAtk *= 4;
                } else if (sps == 1) {
                    mAtk *= 2;
                }

                double mDef = target.getMDef(activeChar, skillEntry);
                if (mDef < 1.) {
                    mDef = 1.;
                }

                double damage = Math.sqrt(mAtk) * this.getPower() / mDef / 111 * target.getMaxMp();
                damage *= 1 + (Rnd.get() * activeChar.getRandomDamage() * 2 - activeChar.getRandomDamage()) / 100;

                final boolean crit = Formulas.calcMCrit(activeChar.getMagicCriticalRate(target, skillEntry));
                if (crit) {
                    activeChar.sendPacket(SystemMsg.MAGIC_CRITICAL_HIT);
                    damage *= activeChar.calcStat(Stats.MCRITICAL_DAMAGE, activeChar.isPlayable() && target.isPlayable() ? 2.5 : 3., target, skillEntry);
                }

                if (activeChar.isPlayer() && target.isPlayer() && damage > 1) {
                    damage += damage * (activeChar.calcStat(Stats.PVP_MAGIC_SKILL_DMG_BONUS, 1, null, null) - target.calcStat(Stats.PVP_MAGIC_SKILL_DEFENCE_BONUS, 1, null, null));
                }

                final int levelDiff = target.getLevel() - activeChar.getLevel();
                final double magic_rcpt = target.calcStat(Stats.MAGIC_RESIST, activeChar, skillEntry) - activeChar.calcStat(Stats.MAGIC_POWER, target, skillEntry);
                final double failChance = 4. * Math.max(1., levelDiff) * (1. + magic_rcpt / 100.);
                if (Rnd.chance(failChance)) {
                    if (levelDiff > 9) {
                        damage = 0;
                        final SystemMessage msg = new SystemMessage(SystemMsg.C1_RESISTED_C2S_MAGIC).addName(target).addName(activeChar);
                        activeChar.sendPacket(msg);
                        target.sendPacket(msg);
                    } else {
                        damage /= 2;
                        final SystemMessage msg = new SystemMessage(SystemMsg.DAMAGE_IS_DECREASED_BECAUSE_C1_RESISTED_C2S_MAGIC).addName(target).addName(activeChar);
                        activeChar.sendPacket(msg);
                        target.sendPacket(msg);
                    }
                }
                target.reduceCurrentMp(damage, activeChar);


                getEffects(skillEntry, activeChar, target, getActivateRate() > 0, false);
            }
        }

        if (isSSPossible()) {
            activeChar.unChargeShots(isMagic());
        }
    }
}
