package org.mmocore.gameserver.skills.skillclasses;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.configuration.config.FormulasConfig;
import org.mmocore.gameserver.configuration.config.SpoilConfig;
import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.model.instances.MonsterInstance;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.stats.Formulas;
import org.mmocore.gameserver.stats.Formulas.AttackInfo;
import org.mmocore.gameserver.templates.StatsSet;

import java.util.List;


public class Spoil extends Skill {
    public Spoil(final StatsSet set) {
        super(set);
    }

    @Override
    public void useSkill(final SkillEntry skillEntry, final Creature activeChar, final List<Creature> targets) {
        if (!activeChar.isPlayer())
            return;

        final int ss = isSSPossible() ? (isMagic() ? activeChar.getChargedSpiritShot() : activeChar.getChargedSoulShot() ? 2 : 0) : 0;
        if (ss > 0 && getPower() > 0) {
            activeChar.unChargeShots(false);
        }

        for (final Creature target : targets) {
            if (target != null && !target.isDead()) {
                if (target.isMonster()) {
                    if (((MonsterInstance) target).isSpoiled()) {
                        activeChar.sendPacket(SystemMsg.IT_HAS_ALREADY_BEEN_SPOILED);
                    } else {
                        final MonsterInstance monster = (MonsterInstance) target;
                        final boolean success;
                        if (!SpoilConfig.ALT_SPOIL_FORMULA) {
                            final int monsterLevel = monster.getLevel();
                            final int modifier = Math.abs(monsterLevel - activeChar.getLevel());
                            double rateOfSpoil = SpoilConfig.BASE_SPOIL_RATE;

                            if (modifier > 8) {
                                rateOfSpoil -= rateOfSpoil * (modifier - 8) * 9 / 100;
                            }

                            rateOfSpoil = rateOfSpoil * getMagicLevel() / monsterLevel;

                            if (rateOfSpoil < SpoilConfig.MINIMUM_SPOIL_RATE) {
                                rateOfSpoil = SpoilConfig.MINIMUM_SPOIL_RATE;
                            } else if (rateOfSpoil > 99.) {
                                rateOfSpoil = 99.;
                            }

                            if (((Player) activeChar).isGM()) {
                                activeChar.sendMessage(new CustomMessage("org.mmocore.gameserver.skills.skillclasses.Spoil.Chance").addNumber(
                                        (long) rateOfSpoil));
                            }
                            success = Rnd.chance(rateOfSpoil);
                        } else {
                            if (FormulasConfig.ALT_DEBUFF_CALCULATE) {
                                success = Formulas.calcSuccessEffect(activeChar, target, skillEntry, getActivateRate());
                            } else {
                                success = Formulas.calcSkillSuccess(activeChar, target, skillEntry, getActivateRate());
                            }
                        }

                        if (success && monster.setSpoiled((Player) activeChar)) {
                            activeChar.sendPacket(SystemMsg.THE_SPOIL_CONDITION_HAS_BEEN_ACTIVATED);
                        } else {
                            activeChar.sendPacket(new SystemMessage(SystemMsg.S1_HAS_FAILED).addSkillName(_id, getDisplayLevel()));
                        }
                    }
                }

                if (getPower() > 0) {
                    final AttackInfo info;
                    if (isMagic()) {
                        info = Formulas.calcMagicDam(activeChar, target, skillEntry, ss);
                    } else {
                        info = Formulas.calcPhysDam(activeChar, target, skillEntry, false, false, ss > 0, false);
                    }

                    if (info.lethal_dmg > 0)
                        target.reduceCurrentHp(info.lethal_dmg, activeChar, skillEntry, true, true, false, false, false, false, false, true);

                    target.reduceCurrentHp(info.damage, activeChar, skillEntry, true, true, false, true, false, false, true);

                    target.doCounterAttack(skillEntry, activeChar, false);
                }

                getEffects(skillEntry, activeChar, target, false, false);

                target.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, activeChar, Math.max(_effectPoint, 1));
            }
        }
    }
}