package org.mmocore.gameserver.skills.skillclasses;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.configuration.config.FormulasConfig;
import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.stats.Stats;
import org.mmocore.gameserver.templates.StatsSet;

import java.util.List;

public class Continuous extends Skill {
    private final int _lethal1;
    private final int _lethal2;

    public Continuous(final StatsSet set) {
        super(set);
        _lethal1 = set.getInteger("lethal1", 0);
        _lethal2 = set.getInteger("lethal2", 0);
    }

    @Override
    public void useSkill(final SkillEntry skillEntry, final Creature activeChar, final List<Creature> targets) {
        Creature realTarget;
        boolean reflected;

        for (final Creature target : targets) {
            if (target != null) {
                // Player holding a cursed weapon can't be buffed and can't buff
                if (getSkillType() == SkillType.BUFF && !target.equals(activeChar)) {
                    if (target.isCursedWeaponEquipped() || activeChar.isCursedWeaponEquipped()) {
                        continue;
                    }
                }

                final int diffLevel = target.getLevel() - skillEntry.getTemplate().getMagicLevel();

                if (diffLevel < FormulasConfig.ALT_LETHAL_DIFF_LEVEL) {
                    final double mult = 0.01 * target.calcStat(Stats.DEATH_VULNERABILITY, activeChar, skillEntry);
                    double mod = 1.0;

                    if (FormulasConfig.ALT_LETHAL_PENALTY) {
                        switch (diffLevel) {
                            case 1:
                                mod = 0.9;
                                break;
                            case 2:
                                mod = 0.8;
                                break;
                            case 3:
                                mod = 0.7;
                                break;
                            case 4:
                                mod = 0.6;
                                break;
                            case 5:
                                mod = 0.5;
                                break;
                            case 6:
                                mod = 0.4;
                                break;
                            default:
                                break;
                        }
                    }
                    final double lethal1 = _lethal1 * mult * mod;
                    final double lethal2 = _lethal2 * mult * mod;

                    if (lethal1 > 0 && Rnd.chance(lethal1)) {
                        if (target.isPlayer()) {
                            target.reduceCurrentHp(target.getCurrentCp(), activeChar, skillEntry, true, true, false, true, false, false, true);
                            target.sendPacket(SystemMsg.LETHAL_STRIKE);
                            activeChar.sendPacket(SystemMsg.YOUR_LETHAL_STRIKE_WAS_SUCCESSFUL);
                        } else if (target.isNpc() && !target.isLethalImmune()) {
                            target.reduceCurrentHp(target.getCurrentHp() / 2, activeChar, skillEntry, true, true, false, true, false, false, true);
                            activeChar.sendPacket(SystemMsg.YOUR_LETHAL_STRIKE_WAS_SUCCESSFUL);
                        }
                    } else if (lethal2 > 0 && Rnd.chance(lethal2)) {
                        if (target.isPlayer()) {
                            target.reduceCurrentHp(target.getCurrentHp() + target.getCurrentCp() - 1, activeChar, skillEntry, true, true, false, true, false, false, true);
                            target.sendPacket(SystemMsg.LETHAL_STRIKE);
                            activeChar.sendPacket(SystemMsg.YOUR_LETHAL_STRIKE_WAS_SUCCESSFUL);
                        } else if (target.isNpc() && !target.isLethalImmune()) {
                            target.reduceCurrentHp(target.getCurrentHp() - 1, activeChar, skillEntry, true, true, false, true, false, false, true);
                            activeChar.sendPacket(SystemMsg.YOUR_LETHAL_STRIKE_WAS_SUCCESSFUL);
                        }
                    }
                }

                reflected = target.checkReflectSkill(activeChar, skillEntry);
                realTarget = reflected ? activeChar : target;

                getEffects(skillEntry, activeChar, realTarget, getActivateRate() > 0, false, reflected);
            }
        }

        if (isSSPossible()) {
            if (!(AllSettingsConfig.SAVING_SPS && _skillType == SkillType.BUFF)) {
                activeChar.unChargeShots(isMagic());
            }
        }
    }
}