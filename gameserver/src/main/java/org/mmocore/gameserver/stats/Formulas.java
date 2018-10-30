package org.mmocore.gameserver.stats;

import org.jts.dataparser.data.holder.pcparameter.common.LevelParameter;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.configuration.config.FormulasConfig;
import org.mmocore.gameserver.configuration.config.OtherConfig;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.configuration.config.custom.CustomConfig;
import org.mmocore.gameserver.listeners.impl.OnCriticalHit;
import org.mmocore.gameserver.listeners.impl.OnReceiveCriticalHit;
import org.mmocore.gameserver.manager.GameTimeManager;
import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.model.Skill.SkillType;
import org.mmocore.gameserver.model.base.BaseStats;
import org.mmocore.gameserver.model.base.Element;
import org.mmocore.gameserver.model.base.SkillTrait;
import org.mmocore.gameserver.model.instances.ReflectionBossInstance;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.skills.EffectType;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.skills.effects.EffectTemplate;
import org.mmocore.gameserver.stats.funcs.FuncTemplate;
import org.mmocore.gameserver.templates.item.WeaponTemplate;
import org.mmocore.gameserver.utils.PositionUtils;

import static org.mmocore.gameserver.model.Skill.SkillMagicType;

public class Formulas {
    private static final boolean[] DEBUG_DISABLED = {false, false, false};
    //private static final Logger LOGGER = LoggerFactory.getLogger(Formulas.class);

    /**
     * Calculate the HP regen rate (base + modifiers).<BR><BR>
     */
    public static double calcHpRegen(final Creature cha) {
        double init;
        if (cha.isPlayer())
            init = cha.getPlayer().getPlayerTemplateComponent().getBaseRegen(LevelParameter.hp);
        else
            init = cha.getTemplate().getBaseHpReg();

        if (cha.isPlayable()) {
            init *= BaseStats.CON.calcBonus(cha);
            if (cha.isSummon()) {
                init *= 2;
            }
        }

        return cha.calcStat(Stats.REGENERATE_HP_RATE, init, null, null);
    }

    /**
     * Calculate the MP regen rate (base + modifiers).<BR><BR>
     */
    public static double calcMpRegen(final Creature cha) {
        double init;
        if (cha.isPlayer())
            init = cha.getPlayer().getPlayerTemplateComponent().getBaseRegen(LevelParameter.mp);
        else
            init = cha.getTemplate().getBaseMpReg();

        if (cha.isPlayable()) {
            init *= BaseStats.MEN.calcBonus(cha);
            if (cha.isSummon()) {
                init *= 2;
            }
        }

        return cha.calcStat(Stats.REGENERATE_MP_RATE, init, null, null);
    }

    /**
     * Calculate the CP regen rate (base + modifiers).<BR><BR>
     */
    public static double calcCpRegen(final Creature cha) {
        double init = 0.0;
        if (cha.isPlayer())
            init = cha.getPlayer().getPlayerTemplateComponent().getBaseRegen(LevelParameter.cp) * BaseStats.CON.calcBonus(cha);
        return cha.calcStat(Stats.REGENERATE_CP_RATE, init);
        //return cha.calcStat(Stats.REGENERATE_CP_RATE, init, null, null);
    }

    /**
     * Для простых ударов
     * patk = patk
     * При крите простым ударом:
     * patk = patk * (1 + crit_damage_rcpt) * crit_damage_mod + crit_damage_static
     * Для blow скиллов
     * TODO
     * Для скилловых критов, повреждения просто удваиваются, бафы не влияют (кроме blow, для них выше)
     * patk = (1 + crit_damage_rcpt) * (patk + skill_power)
     * Для обычных атак
     * damage = patk * ss_bonus * 70 / pdef
     */
    public static AttackInfo calcPhysDam(final Creature attacker, final Creature target, final SkillEntry skill, final boolean dual, final boolean blow, final boolean ss, final boolean onCrit) {
        final AttackInfo info = new AttackInfo();

        info.damage = attacker.getPAtk(target);
        info.defence = target.getPDef(attacker);
        info.crit_static = attacker.calcStat(Stats.CRITICAL_DAMAGE_STATIC, target, skill);
        info.death_rcpt = 0.01 * target.calcStat(Stats.DEATH_VULNERABILITY, attacker, skill);
        info.lethal1 = skill == null ? 0 : skill.getTemplate().getLethal1() * info.death_rcpt;
        info.lethal2 = skill == null ? 0 : skill.getTemplate().getLethal2() * info.death_rcpt;
        info.crit = Rnd.chance(calcCrit(attacker, target, skill, blow));
        info.shld = (skill == null || !skill.getTemplate().getShieldIgnore()) && calcShldUse(attacker, target);
        info.lethal = false;
        info.miss = false;
        final boolean isPvP = attacker.isPlayable() && target.isPlayable();

        if (info.shld) {
            info.defence += target.getShldDef();
        }

        info.defence = Math.max(info.defence, 1);

        if (skill != null) {
            if (!blow && !target.isLethalImmune()) // считаем леталы для не blow скиллов
            {
                if (Rnd.chance(info.lethal1)) {
                    if (target.isPlayer()) {
                        info.lethal = true;
                        info.lethal_dmg = target.getCurrentCp();
                        target.sendPacket(SystemMsg.YOUR_CP_WAS_DRAINED_BECAUSE_YOU_WERE_HIT_WITH_A_CP_SIPHON_SKILL);
                    } else {
                        info.lethal_dmg = target.getCurrentHp() / 2;
                    }
                    attacker.sendPacket(SystemMsg.CP_SIPHON);
                } else if (Rnd.chance(info.lethal2)) {
                    if (target.isPlayer()) {
                        info.lethal = true;
                        info.lethal_dmg = target.getCurrentHp() + target.getCurrentCp() -
                                1.1; // Oly\Duel хак установки не точно 1 ХП, а чуть больше для предотвращения псевдосмерти
                        target.sendPacket(SystemMsg.LETHAL_STRIKE);
                    } else {
                        info.lethal_dmg = target.getCurrentHp() - 1;
                    }
                    attacker.sendPacket(SystemMsg.YOUR_LETHAL_STRIKE_WAS_SUCCESSFUL);
                }
            }

            // если скилл не имеет своей силы дальше идти бесполезно, можно сразу вернуть дамаг от летала
            if (skill.getTemplate().getPower(target) == 0) {
                info.damage = 0; // обычного дамага в этом случае не наносится
                return info;
            }

            if (blow && !skill.getTemplate().isBehind() && ss) // Для обычных blow не влияет на power
            {
                info.damage *= 2.04;
            }

            // Для зарядок влияет на суммарный бонус
            if (skill.getTemplate().isChargeBoost()) {
                info.damage = attacker.calcStat(Stats.SKILL_POWER, info.damage + skill.getTemplate().getPower(target), null, null);
            } else {
                info.damage += attacker.calcStat(Stats.SKILL_POWER, skill.getTemplate().getPower(target), null, null);
            }

            if (blow && skill.getTemplate().isBehind() && ss) // Для backstab влияет на power, но меньше множитель
            {
                info.damage *= 1.5;
            }

            //Заряжаемые скилы имеют постоянный урон
            if (!skill.getTemplate().isChargeBoost()) {
                info.damage *= 1 + (Rnd.get() * attacker.getRandomDamage() * 2 - attacker.getRandomDamage()) / 100;
            }

            if (blow) {
                // Откат 376 ревы
                info.damage *= 0.01 * attacker.calcStat(Stats.CRITICAL_DAMAGE, target, skill);
                //info.damage *= 0.5 + attacker.calcStat(Stats.CRITICAL_DAMAGE, 0.5, target, skill);
                info.damage = target.calcStat(Stats.CRIT_DAMAGE_RECEPTIVE, info.damage, attacker, skill);
                info.damage += 6.1 * info.crit_static;
            }

            if (skill.getTemplate().isChargeBoost()) {
                info.damage *= 0.8 + 0.2 * attacker.getIncreasedForce();
            } else if (skill.getTemplate().isSoulBoost()) {
                info.damage *= 1.0 + 0.06 * Math.min(attacker.getConsumedSouls(), 5);
            }

            // Gracia Physical Skill Damage Bonus
            info.damage *= 1.10113;

            if (info.crit) {
                info.damage *= 2.;
            }
        } else {
            info.damage *= 1 + (Rnd.get() * attacker.getRandomDamage() * 2 - attacker.getRandomDamage()) / 100;

            if (dual) {
                info.damage /= 2.;
            }

            if (info.crit) {
                info.damage *= 0.01 * attacker.calcStat(Stats.CRITICAL_DAMAGE, target, null);
                info.damage = 2 * target.calcStat(Stats.CRIT_DAMAGE_RECEPTIVE, info.damage, attacker, null);
                info.damage += info.crit_static;
            }
        }

        if (info.crit) {
            // шанс абсорбации души (без анимации) при крите, если Soul Mastery 4го уровня или более
            int chance = attacker.getSkillLevel(Skill.SKILL_SOUL_MASTERY);
            if (chance > 0) {
                if (chance >= 21) {
                    chance = 30;
                } else if (chance >= 15) {
                    chance = 25;
                } else if (chance >= 9) {
                    chance = 20;
                } else if (chance >= 4) {
                    chance = 15;
                }
                if (Rnd.chance(chance)) {
                    attacker.setConsumedSouls(attacker.getConsumedSouls() + 1, null);
                }
            }
        }

        // у зарядок нет бонусов от положения цели
        if (skill == null || !skill.getTemplate().isChargeBoost()) {
            switch (PositionUtils.getDirectionTo(target, attacker)) {
                case BEHIND:
                    info.damage *= 1.2;
                    break;
                case SIDE:
                    info.damage *= 1.1;
                    break;
            }
        }

        if (ss && !blow) {
            info.damage *= 2.0;
        }

        info.damage *= 70. / info.defence;
        info.damage = attacker.calcStat(Stats.PHYSICAL_DAMAGE, info.damage, target, skill);

        if (info.shld && Rnd.chance(5)) {
            info.damage = 1;
        }

        if (isPvP) {
            if (skill == null) {
                info.damage += info.damage * (attacker.calcStat(Stats.PVP_PHYS_DMG_BONUS, 1, null, null) - target.calcStat(Stats.PVP_PHYS_DEFENCE_BONUS, 1, null, null));
            } else {
                info.damage += info.damage * (attacker.calcStat(Stats.PVP_PHYS_SKILL_DMG_BONUS, 1, null, null) - target
                        .calcStat(Stats.PVP_PHYS_SKILL_DEFENCE_BONUS, 1, null, null));
            }
        }

        // Тут проверяем только если skill != null, т.к. L2Character.onHitTimer не обсчитывает дамаг.
        if (skill != null) {
            if (info.shld) {
                if (info.damage == 1) {
                    target.sendPacket(SystemMsg.YOUR_EXCELLENT_SHIELD_DEFENSE_WAS_A_SUCCESS);
                } else {
                    target.sendPacket(SystemMsg.YOUR_SHIELD_DEFENSE_HAS_SUCCEEDED);
                }
            }

            // Уворот от физ скилов уводит атаку в 0
            if (info.damage > 1 && !skill.getTemplate().hasNotSelfEffects() && Rnd.chance(target.calcStat(Stats.PSKILL_EVASION, 0, attacker, skill))) {
                attacker.sendPacket(new SystemMessage(SystemMsg.C1_DODGES_THE_ATTACK).addName(target));
                target.sendPacket(new SystemMessage(SystemMsg.YOU_HAVE_AVOIDED_C1S_ATTACK).addName(attacker));
                info.damage = 0;
            }

            if (info.damage > 1 && skill.getTemplate().isDeathlink()) {
                info.damage *= 1.8 * (1.0 - attacker.getCurrentHpRatio());
            }

            if (onCrit && !calcBlow(attacker, target, skill)) {
                info.miss = true;
                info.damage = 0;
                attacker.sendPacket(new SystemMessage(SystemMsg.C1S_ATTACK_WENT_ASTRAY).addName(attacker));
            }

            if (blow) {
                if (Rnd.chance(info.lethal1)) {
                    if (target.isPlayer()) {
                        info.lethal = true;
                        info.lethal_dmg = target.getCurrentCp();
                        target.sendPacket(SystemMsg.YOUR_CP_WAS_DRAINED_BECAUSE_YOU_WERE_HIT_WITH_A_CP_SIPHON_SKILL);
                    } else if (target.isLethalImmune()) {
                        info.damage *= 2;
                    } else {
                        info.lethal_dmg = target.getCurrentHp() / 2;
                    }
                    attacker.sendPacket(SystemMsg.CP_SIPHON);
                } else if (Rnd.chance(info.lethal2)) {
                    if (target.isPlayer()) {
                        info.lethal = true;
                        info.lethal_dmg = target.getCurrentHp() + target.getCurrentCp() - 1.1;
                        target.sendPacket(SystemMsg.LETHAL_STRIKE);
                    } else if (target.isLethalImmune()) {
                        info.damage *= 3;
                    } else {
                        info.lethal_dmg = target.getCurrentHp() - 1;
                    }
                    attacker.sendPacket(SystemMsg.YOUR_LETHAL_STRIKE_WAS_SUCCESSFUL);
                }
            }

            if (info.damage > 0) {
                attacker.displayGiveDamageMessage(target, (int) info.damage, info.crit || blow, false, false, false);
            }

            if (target.isStunned() && calcStunBreak(attacker, target, info.crit)) {
                target.getEffectList().stopEffects(EffectType.Stun);
            }

            if (calcCastBreak(attacker, target, info.damage)) {
                target.abortCast(false, true);
            }
        }

        if (info.crit) {
            attacker.listeners().onAction(OnCriticalHit.class, f -> f.onAction(attacker, target));
            target.listeners().onAction(OnReceiveCriticalHit.class, f -> f.onAction(attacker, target));
        }
        return info;
    }

    public static AttackInfo calcMagicDam(final Creature attacker, final Creature target, final SkillEntry skill, final int sps) {
        final boolean isPvP = attacker.isPlayable() && target.isPlayable();
        // Параметр ShieldIgnore для магических скиллов инвертирован
        final boolean shield = skill.getTemplate().getShieldIgnore() && calcShldUse(attacker, target);

        double mAtk = attacker.getMAtk(target, skill);

        if (sps == 2) {
            mAtk *= 4;
        } else if (sps == 1) {
            mAtk *= 2;
        }

        double mdef = target.getMDef(null, skill);

        if (shield) {
            mdef += target.getShldDef();
        }
        if (mdef == 0) {
            mdef = 1;
        }

        double power = skill.getTemplate().getPower(target);
        double lethalDamage = 0;
        final int diffLevel = target.getLevel() - skill.getTemplate().getMagicLevel();
        double mod = 1.0;

        if (diffLevel < FormulasConfig.ALT_LETHAL_DIFF_LEVEL) {
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

            if (Rnd.chance(skill.getTemplate().getLethal1() * mod)) {
                if (target.isPlayer()) {
                    lethalDamage = target.getCurrentCp();
                    target.sendPacket(SystemMsg.YOUR_CP_WAS_DRAINED_BECAUSE_YOU_WERE_HIT_WITH_A_CP_SIPHON_SKILL);
                } else if (!target.isLethalImmune()) {
                    lethalDamage = target.getCurrentHp() / 2;
                } else {
                    power *= 2;
                }
                attacker.sendPacket(SystemMsg.CP_SIPHON);
            } else if (Rnd.chance(skill.getTemplate().getLethal2() * mod)) {
                if (target.isPlayer()) {
                    lethalDamage = target.getCurrentHp() + target.getCurrentCp() - 1.1;
                    target.sendPacket(SystemMsg.LETHAL_STRIKE);
                } else if (!target.isLethalImmune()) {
                    lethalDamage = target.getCurrentHp() - 1;
                } else {
                    power *= 3;
                }
                attacker.sendPacket(SystemMsg.YOUR_LETHAL_STRIKE_WAS_SUCCESSFUL);
            }
        }

        if (power == 0) {
            if (lethalDamage > 0) {
                attacker.displayGiveDamageMessage(target, (int) lethalDamage, false, false, false, false);
            }
            return new AttackInfo(lethalDamage);
        }

        if (skill.getTemplate().isSoulBoost()) {
            power *= 1.0 + 0.06 * Math.min(attacker.getConsumedSouls(), 5);
        }

        double damage = 91 * power * Math.sqrt(mAtk) / mdef;

        damage *= 1 + (Rnd.get() * attacker.getRandomDamage() * 2 - attacker.getRandomDamage()) / 100;

        final boolean crit = calcMCrit(attacker.getMagicCriticalRate(target, skill));

        if (crit) {
            damage *= attacker.calcStat(Stats.MCRITICAL_DAMAGE, attacker.isPlayable() && target.isPlayable() ? 2.5 : 3., target, skill);
        }

        damage = attacker.calcStat(Stats.MAGIC_DAMAGE, damage, target, skill);

        if (shield) {
            if (Rnd.chance(5)) {
                damage = 0;
                target.sendPacket(SystemMsg.YOUR_EXCELLENT_SHIELD_DEFENSE_WAS_A_SUCCESS);
                attacker.sendPacket(new SystemMessage(SystemMsg.C1_RESISTED_C2S_MAGIC).addName(target).addName(attacker));
            } else {
                target.sendPacket(SystemMsg.YOUR_SHIELD_DEFENSE_HAS_SUCCEEDED);
                attacker.sendPacket(SystemMsg.YOUR_OPPONENT_HAS_RESISTANCE_TO_MAGIC_THE_DAMAGE_WAS_DECREASED);
            }
        }

        final int levelDiff = target.getLevel() - attacker.getLevel(); // C Gracia Epilogue уровень маг. атак считается только по уроню атакующего

        if (damage > 1 && skill.getTemplate().isDeathlink()) {
            damage *= 1.8 * (1.0 - attacker.getCurrentHpRatio());
        }

        if (damage > 1 && skill.getTemplate().isBasedOnTargetDebuff()) {
            damage *= 1 + 0.05 * target.getEffectList().getAllEffects().size();
        }

        if (skill.getSkillType() == SkillType.MANADAM) {
            damage = Math.max(1, damage / 4.);
        }

        if (isPvP && damage > 1) {
            damage += damage * (attacker.calcStat(Stats.PVP_MAGIC_SKILL_DMG_BONUS, 1, null, null) - target.calcStat(Stats.PVP_MAGIC_SKILL_DEFENCE_BONUS, 1, null, null));
        }

        final double magic_rcpt = target.calcStat(Stats.MAGIC_RESIST, attacker, skill) - attacker.calcStat(Stats.MAGIC_POWER, target, skill);
        final double failChance = 4. * Math.max(1., levelDiff) * (1. + magic_rcpt / 100.);
        if (Rnd.chance(failChance)) {
            if (levelDiff > 9) {
                damage = 0;
                final SystemMessage msg = new SystemMessage(SystemMsg.C1_RESISTED_C2S_MAGIC).addName(target).addName(attacker);
                attacker.sendPacket(msg);
                target.sendPacket(msg);
            } else {
                damage /= 2;
                final SystemMessage msg = new SystemMessage(SystemMsg.DAMAGE_IS_DECREASED_BECAUSE_C1_RESISTED_C2S_MAGIC).addName(target).addName(
                        attacker);
                attacker.sendPacket(msg);
                target.sendPacket(msg);
            }
        }

        damage += lethalDamage;

        if (damage > 0) {
            attacker.displayGiveDamageMessage(target, (int) damage, crit, false, false, true);
        }

        if (calcCastBreak(attacker, target, damage)) {
            target.abortCast(false, true);
        }

        return new AttackInfo(damage, lethalDamage);
    }

    public static boolean calcStunBreak(final Creature activeChar, final Creature target, final boolean crit) {
        double chance = crit ? 25.0 : 10.0 - (activeChar.calcStat(Stats.STUN_POWER, 0., activeChar, null) - target.calcStat(Stats.STUN_RESIST, 0.,
                target, null)) * 0.5;
        chance = Math.max(chance, crit ? 30.0 : 5.0);
        chance = Math.max(1.0, chance);
        return Rnd.chance(chance);
    }

    /**
     * Returns true in case of fatal blow success
     */
    public static boolean calcBlow(final Creature activeChar, final Creature target, final SkillEntry skill) {
        final WeaponTemplate weapon = activeChar.getActiveWeaponItem();

        final double base_weapon_crit = weapon == null ? 4. : weapon.getCritical();
        final double crit_height_bonus = 0.008 * Math.min(25, Math.max(-25, target.getZ() - activeChar.getZ())) + 1.1;
        final double buffs_mult = activeChar.calcStat(Stats.FATALBLOW_RATE, target, skill);
        final double skill_mod = skill.getTemplate().isBehind() ? 5 : 4; // CT 2.3 blowrate increase

        double chance = base_weapon_crit * buffs_mult * crit_height_bonus * skill_mod;

        if (!target.isInCombat()) {
            chance *= 1.1;
        }

        switch (PositionUtils.getDirectionTo(target, activeChar)) {
            case BEHIND:
                chance *= 1.3;
                break;
            case SIDE:
                chance *= 1.1;
                break;
            case FRONT:
                if (skill.getTemplate().isBehind()) {
                    chance = 3.0;
                }
                break;
        }
        chance = Math.min(skill.getTemplate().isBehind() ? 100 : 80, chance);
        return Rnd.chance(chance);
    }

    /**
     * Возвращает шанс крита в процентах
     */
    public static double calcCrit(final Creature attacker, final Creature target, final SkillEntry skill, final boolean blow) {
        if (attacker.isPlayer() && attacker.getActiveWeaponItem() == null) {
            return 0;
        }
        if (skill != null) {
            return skill.getTemplate().getCriticalRate() * (blow ? BaseStats.DEX.calcBonus(attacker) : BaseStats.STR.calcBonus(attacker)) * 0.01 *
                    attacker.calcStat(Stats.SKILL_CRIT_CHANCE_MOD, target, skill);
        }

        double rate = attacker.getCriticalHit(target, null) * 0.01 * target.calcStat(Stats.CRIT_CHANCE_RECEPTIVE, attacker, null);

        switch (PositionUtils.getDirectionTo(target, attacker)) {
            case BEHIND:
                rate *= 1.4;
                break;
            case SIDE:
                rate *= 1.2;
                break;
        }

        return rate / 10;
    }

    public static boolean calcMCrit(final double mRate) {
        // floating point random gives more accuracy calculation, because argument also floating point
        return Rnd.get() * 100 <= Math.min(FormulasConfig.LIM_MCRIT, mRate);
    }

    /**
     * @param attacker - тот кто атакует
     * @param target   - тот кто кастует
     * @param damage   - дамаг который прилетел
     * @return - Возращает шанс сбития каста (офлайк формула сбития каста (c) PaInKiLlEr)
     */
    public static boolean calcCastBreak(final Creature attacker, final Creature target, final double damage) {
        // формула не действует на рейдов или на цель которая не кастует скил
        if (target == null || target.isInvul() || target.isRaid() || !target.isCastingNow())
            return false;

        final SkillEntry skill = target.getCastingSkill();

        if (skill == null)
            return false;

        // TODO: убрать куда-то
        if (skill.getSkillType() == SkillType.TAKECASTLE || skill.getSkillType() == SkillType.TAKEFORTRESS
                || skill.getSkillType() == SkillType.TAKEFLAG)
            return false;

        // Сбиты могут быть только те скиллы, у которых параметр is_magic не равен 0
        if (skill.getTemplate().getMagicType() == SkillMagicType.PHYSIC)
            return false;

        final double reduce_interrupt_multiplier = target.calcStat(Stats.CAST_INTERRUPT, 1, null, null);
        final double reduce_interrupt_adder = target.calcStat(Stats.CAST_INTERRUPT, 0, null, null);
        final int base_interrupt_factor = (int) (100 * Math.floor(damage * reduce_interrupt_multiplier + reduce_interrupt_adder) / target.getMaxHp());
        // floor - округлить вниз от того, что стоит в скобках.
        // damage - дамаг, который прилетел кастеру.
        // getMaxHp() - максимальное хп кастера.
        // reduce_interrupt_adder - сумма статы Stats.CAST_INTERRUPT
        // reduce_interrupt_multiplier - множитель статы Stats.CAST_INTERRUPT

        // Теперь посчитаем level_factor
        final double level_factor = target.getLevel() + 0.125 * target.getMEN() - attacker.getLevel();
        double final_interrupt_chance = 0;
        if (level_factor >= base_interrupt_factor)
            final_interrupt_chance = 5;

        if (level_factor < base_interrupt_factor)
            final_interrupt_chance = Math.min(Math.floor((2 * base_interrupt_factor)), 98);

        return Rnd.chance(final_interrupt_chance);
    }

    /**
     * Calculate delay (in milliseconds) before next ATTACK
     */
    public static int calcPAtkSpd(final double rate) {
        return (int) (500000 / rate); // в миллисекундах поэтому 500*1000
    }

    /**
     * @param attacker  - тот кто кастует
     * @param skill     - скил который кастуется
     * @param skillTime - время каста скила
     * @return - возращает новое время каста взависимости от формулы
     */
    public static int calcMAtkSpd(final Creature attacker, final Skill skill, final double skillTime) {
        switch (skill.getMagicType()) {
            case SPECIAL:
                return (int) skillTime;
            case PHYSIC:
            case MUSIC:
                return (int) (skillTime * 333 / Math.max(attacker.getPAtkSpd(), 1));
            case MAGIC:
                return (int) (skillTime * (attacker.isPlayer() ? attacker.getPlayer().getPlayerTemplateComponent().getBaseAttackSpeed() : attacker.getTemplate().getBaseMAtkSpd()) / Math.max(attacker.getMAtkSpd(), 1));
        }
        return (int) skillTime;
    }

    /**
     * Calculate reuse delay (in milliseconds) for skills
     */
    public static long calcSkillReuseDelay(final Creature actor, final SkillEntry skill) {
        long reuseDelay = skill.getTemplate().getReuseDelay();
        if (actor.isMonster())
            reuseDelay = skill.getTemplate().getReuseForMonsters();
        if (skill.getTemplate().isReuseDelayPermanent() || skill.getTemplate().isHandler() || skill.getTemplate().isItemSkill())
            return reuseDelay;

        // Если прошел шанс использовать скил без отката, тогда возращаем откат 0 секунд
        if (actor.getSkillMastery(skill.getTemplate()).hasZeroReuse())
            return 0;

        switch (skill.getTemplate().getMagicType()) {
            case PHYSIC:
                return (long) actor.calcStat(Stats.PHYSIC_REUSE_RATE, reuseDelay, null, skill);
            case MAGIC:
                return (long) actor.calcStat(Stats.MAGIC_REUSE_RATE, reuseDelay, null, skill);
            case SPECIAL:
                return reuseDelay;
            case MUSIC:
                return (long) actor.calcStat(Stats.MUSIC_REUSE_RATE, reuseDelay, null, skill);
        }

        return reuseDelay;
    }

    /**
     * - Формула попасть/промохнуться по цели
     *
     * @param attacker
     * @param target
     * @return
     */
    public static boolean calcHitMiss(final Creature attacker, final Creature target) {
        int chance = (80 + (2 * (attacker.getAccuracy() - target.getEvasionRate(attacker)))) * 10;
        double mod = 100;
        final PositionUtils.TargetDirection targetDirection = PositionUtils.getDirectionTo(attacker, target);
        if ((attacker.getZ() - target.getZ()) > 50) {
            mod += 3;
        } else if ((attacker.getZ() - target.getZ()) < -50) {
            mod += -3;
        }
        if (GameTimeManager.getInstance().isNowNight()) {
            mod += -10;
        }
        if (targetDirection == PositionUtils.TargetDirection.BEHIND) {
            mod += 10;
        } else if (targetDirection == PositionUtils.TargetDirection.FRONT) {
            mod += 0;
        } else {
            mod += 5;
        }
        final double modFinal = Math.max(mod / 100, 0);
        chance += modFinal;
        chance = Math.max(chance, 200);
        chance = Math.min(chance, 980);
        return chance < Rnd.get(1000);
    }

    /**
     * Returns true if shield defence successfull
     */
    public static boolean calcShldUse(final Creature attacker, final Creature target) {
        final WeaponTemplate template = target.getSecondaryWeaponItem();
        if (template == null || template.getItemType() != WeaponTemplate.WeaponType.NONE) {
            return false;
        }
        final int angle = (int) target.calcStat(Stats.SHIELD_ANGLE, attacker, null);
        if (!PositionUtils.isFacing(target, attacker, angle)) {
            return false;
        }
        return Rnd.chance((int) target.calcStat(Stats.SHIELD_RATE, attacker, null));
    }

    public static boolean[] isDebugEnabled(final Creature caster, final Creature target) {
        if (AllSettingsConfig.ALT_DEBUG_ENABLED) {
            // Включена ли отладка на кастере
            final boolean debugCaster = caster.getPlayer() != null && caster.getPlayer().isDebug();
            // Включена ли отладка на таргете
            final boolean debugTarget = target.getPlayer() != null && target.getPlayer().isDebug();
            // Разрешена ли отладка в PvP
            if (AllSettingsConfig.ALT_DEBUG_PVP_ENABLED && (debugCaster && debugTarget) &&
                    (!AllSettingsConfig.ALT_DEBUG_PVP_DUEL_ONLY || (caster.getPlayer().isInDuel() && target.getPlayer().isInDuel()))) {
                return new boolean[]{true, debugCaster, debugTarget};
            }
            // Включаем отладку в PvE если разрешено
            if (AllSettingsConfig.ALT_DEBUG_PVE_ENABLED && ((debugCaster && target.isMonster()) || (debugTarget && caster.isMonster()))) {
                return new boolean[]{true, debugCaster, debugTarget};
            }
        }

        return DEBUG_DISABLED;
    }

    public static double calcParamOnStat(final Creature target, final Stats stat) // Рассчитываем любой параметр базируясь на переданном стате
    {
        double _res = 1.;

        for (final SkillEntry skill : target.getAllSkills()) // Проверяем в пассивках
        {
            if (skill != null && skill.getTemplate().isPassive() && skill.getTemplate().getSkillType() == SkillType.BUFF) {
                for (final FuncTemplate ft : skill.getTemplate()._funcTemplates) {
                    if (ft._stat == stat && "Add".equals(ft._operate)) {
                        if (ft._value < 100) {
                            _res *= ft._stat == Stats.DEBUFF_RESIST ? 1 - ft._value / 100 : 1 + ft._value / 100;
                        } else {
                            return Double.NEGATIVE_INFINITY; // Полный иммунитет
                        }
                    } else if (ft._stat == stat && "Sub".equals(ft._operate)) {
                        if (ft._value < 100) {
                            _res *= 1 + ft._value / 100;
                        } else {
                            return Double.POSITIVE_INFINITY; // Полное отсутствие резиста
                        }
                    }
                }
            }
        }

        for (final Effect eff : target.getEffectList().getAllEffects()) // Проверяем в наложенных эффектах
        {
            for (final FuncTemplate ft : eff.getTemplate()._funcTemplates) {
                if (ft._stat == stat && "Add".equals(ft._operate)) {
                    if (ft._value < 100) {
                        _res *= ft._stat == Stats.DEBUFF_RESIST ? 1 - ft._value / 100 : 1 + ft._value / 100;
                    } else {
                        return Double.NEGATIVE_INFINITY;
                    }
                } else if (ft._stat == stat && "Sub".equals(ft._operate)) {
                    if (ft._value < 100) {
                        _res *= 1 + ft._value / 100;
                    } else
                        return Double.POSITIVE_INFINITY;
                }
            }
        }

        return _res;
    }

    public static boolean calcSuccessEffect(final Creature player, final Creature target, final SkillEntry skill, final int activateRate) {
        return calcSuccessEffect(player, target, skill, activateRate, null, player.getChargedSpiritShot());
    }

    public static boolean calcSuccessEffect(final Creature player, final Creature target, final SkillEntry skillEntry,
                                            final double initialActivateRate, final EffectTemplate et, final int spiritshot) {
        final SkillType _skillType = skillEntry.getSkillType();
        double activateRate = initialActivateRate;

        if (activateRate == -1) {
            return true;
        }

        if (_skillType == SkillType.BUFF || (et != null && et._effectType == EffectType.Buff)) {
            return true;
        }

        if (et != null && et._effectType == EffectType.Cancel) {
            return true;
        }

        if (activateRate > 100) // Заглушка
        {
            activateRate = 80;
        }

        final double _baseChance = activateRate;
        final Skill skill = skillEntry.getTemplate();

        final boolean[] debug = isDebugEnabled(player, target);
        final boolean debugGlobal = debug[0];
        final boolean debugCaster = debug[1];
        final boolean debugTarget = debug[2];
        final StringBuilder stat = new StringBuilder(100);

        // Шаг 1
        // chance = base_chance + 30 - stat (количество) + min((magic_level - level_цели + 3), 0) * level_bonus

        int _statMod = 0;


        if (skill.getSaveVs() != null) // Если нужен базовый параметр
        {
            switch (skill.getSaveVs()) {
                case STR:
                    _statMod = target.isPlayer() ? target.getPlayer().getPlayerTemplateComponent().getParameter().getBaseSTR() : target.getBaseTemplate().getBaseAttr().getSTR();
                    break;
                case CON:
                    _statMod = target.isPlayer() ? target.getPlayer().getPlayerTemplateComponent().getParameter().getBaseCON() : target.getBaseTemplate().getBaseAttr().getCON();
                    break;
                case DEX:
                    _statMod = target.isPlayer() ? target.getPlayer().getPlayerTemplateComponent().getParameter().getBaseDEX() : target.getBaseTemplate().getBaseAttr().getDEX();
                    break;
                case INT:
                    _statMod = target.isPlayer() ? target.getPlayer().getPlayerTemplateComponent().getParameter().getBaseINT() : target.getBaseTemplate().getBaseAttr().getINT();
                    break;
                case MEN:
                    _statMod = target.isPlayer() ? target.getPlayer().getPlayerTemplateComponent().getParameter().getBaseMEN() : target.getBaseTemplate().getBaseAttr().getMEN();
                    break;
                case WIT:
                    _statMod = target.isPlayer() ? target.getPlayer().getPlayerTemplateComponent().getParameter().getBaseWIT() : target.getBaseTemplate().getBaseAttr().getWIT();
                    break;
                default:
                    break;
            }
        } else // иначе выбираем текущий
        {
            if (_skillType != null) {
                _statMod = calcSkillStatModifier(_skillType, (et == null ? null : et._effectType), target);
            }
        }

        final int _magicLevel = skill.getMagicLevel() > 0 ? skill.getMagicLevel() : player.getLevel();

        int _levelBonus = skill.getLevelModifier();
        if (_levelBonus == 0) {
            _levelBonus = 2;
        }

        // Результат после 1-го шага
        activateRate = activateRate + 30 - _statMod + (Math.min(_magicLevel - target.getLevel() + 3, 0)) * _levelBonus;

        if (debugGlobal) {
            stat.append("Skill: ");
            stat.append(skill.getName());
            if (et != null) {
                stat.append(" Effect: ");
                stat.append(et._effectType.name());
            }
            stat.append(" AR: ");
            stat.append(String.format("%2.1f", _baseChance));
            stat.append(" STAT:");
            stat.append(_statMod);
            stat.append(" ML:");
            stat.append(_magicLevel);
            stat.append(" LB:");
            stat.append(_levelBonus);
            stat.append(" ChST1: ");
            stat.append(String.format("%2.2f", activateRate));
        }

        // Для Aura Flash не должно быть никаких расчетов.
        if (et != null && et._effectType == EffectType.RemoveTarget && skill.getId() == 1417) {
            if (debugGlobal) {
                stat.append(" | ");
                stat.append(" FinalChance: ");
                stat.append(String.format("%2.2f", activateRate));
                if (debugCaster)
                    player.sendMessage(stat.toString());
                if (debugTarget)
                    target.sendMessage(stat.toString());
            }
            return Rnd.chance(activateRate);
        }

        // Шаг 2
        // резисты - перемножаются все резисты на цели + бонусы на атакующем и умножаются на chance.

        double _vulnerability = 1.;
        double _proficiency = 1.;

        // Если в скиле задан параметр TRAIT, то резист считаем исходя из него, иначе по типу скила/эффекта

        final SkillTrait trait = skill.getTraitType();
        if (trait != null) {
            switch (trait) {
                case BLEED:
                    _vulnerability = calcParamOnStat(target, Stats.BLEED_RESIST);
                    _proficiency = calcParamOnStat(player, Stats.BLEED_POWER);
                    break;
                case BOSS:
                case DEATH:
                case DERANGEMENT:
                    _vulnerability = calcParamOnStat(target, Stats.MENTAL_RESIST);
                    _proficiency = calcParamOnStat(player, Stats.MENTAL_POWER);
                    break;
                case ETC:
                case GUST:
                case HOLD:
                    _vulnerability = calcParamOnStat(target, Stats.ROOT_RESIST);
                    _proficiency = calcParamOnStat(player, Stats.ROOT_POWER);
                    break;
                case PARALYZE:
                    _vulnerability = calcParamOnStat(target, Stats.PARALYZE_RESIST);
                    _proficiency = calcParamOnStat(player, Stats.PARALYZE_POWER);
                    break;
                case PHYSICAL_BLOCKADE:
                case POISON:
                    _vulnerability = calcParamOnStat(target, Stats.POISON_RESIST);
                    _proficiency = calcParamOnStat(player, Stats.POISON_POWER);
                    break;
                case SHOCK:
                    _vulnerability = calcParamOnStat(target, Stats.STUN_RESIST);
                    _proficiency = calcParamOnStat(player, Stats.STUN_POWER);
                    break;
                case SLEEP:
                    _vulnerability = calcParamOnStat(target, Stats.SLEEP_RESIST);
                    _proficiency = calcParamOnStat(player, Stats.SLEEP_POWER);
                    break;
                default:
                    break;
            }

        } else if (skill.getSkillType() != SkillType.DEBUFF) {
            if (!skill.isIgnoreResists()) {
                _vulnerability = calcSkillTypeVulnerability(_vulnerability, target, skill.getSkillType(), (et == null ? null : et._effectType));
            }

            _proficiency = calcSkillTypeProficiency(1, player, skill.getSkillType(), (et == null ? null : et._effectType));
        }

        if (skill.isCubicSkill()) {
            _proficiency = 1.; // костыль для кубиков
        }

        if (_vulnerability == Double.NEGATIVE_INFINITY || _proficiency == Double.POSITIVE_INFINITY) // Иммунитет
        {
            return false;
        }

        if (_vulnerability == Double.POSITIVE_INFINITY || _proficiency == Double.NEGATIVE_INFINITY) // Полное прохождение
        {
            return true;
        }

        double resist = 1 - _vulnerability + _proficiency;
        resist = Math.max(resist, 0.05);

        activateRate *= resist;

        if (debugGlobal) {
            stat.append(" | ");
            stat.append("Vuln:");
            stat.append(String.format("%1.2f", _vulnerability));
            stat.append(" Prof:");
            stat.append(String.format("%1.2f", _proficiency));
            stat.append(" Res:");
            stat.append(String.format("%1.2f", resist));
            stat.append(" ChST2: ");
            stat.append(String.format("%2.2f", activateRate));
        }
        // Шаг 3
        // шанс умножается на защиту к дебаффам

        // XXX это костыль для FS#278 - Erase, привязка к резисту к дебаффам
        if (skill == null || skill.getId() != 1395) {
            final double debuffVuln = calcParamOnStat(target, Stats.DEBUFF_RESIST);

            if (debuffVuln == Double.NEGATIVE_INFINITY) // Иммунитет
            {
                return false;
            }

            if (debuffVuln == Double.POSITIVE_INFINITY) // Полное прохождение
            {
                return true;
            }

            activateRate *= debuffVuln;

            if (debugGlobal) {
                stat.append(" | ");
                stat.append("Debuff vuln: ");
                stat.append(String.format("%1.2f", debuffVuln));
                stat.append(" ChST3: ");
                stat.append(String.format("%2.2f", activateRate));
            }
        }

        // Шаг 4
        // Если скилл элементальный - то шанс умножается на бонус дамага атрибута по цели.

        double elementMod = 1;
        final Element element = skill.getElement();
        if (element != Element.NONE) {
            elementMod = skill.getElementPower();
            final Element attackElement = getAttackElement(player, target);
            if (attackElement == element) {
                elementMod += player.calcStat(element.getAttack(), 0);
            }
            elementMod -= target.calcStat(element.getDefence(), 0);
            // Выравниваем показатели если зашкалили
            elementMod = Math.max(elementMod, 0);
            // Считаем модификатор
            if (elementMod <= 45) {
                elementMod = 1 + elementMod / 225;
            } else if (elementMod > 45 && elementMod < 150) {
                elementMod = 1.2;
            } else if (elementMod >= 150 && elementMod < 300) {
                elementMod = 1.4;
            } else {
                elementMod = 1.7;
            }
        }

        activateRate *= elementMod; // Вносим поправку на элемент

        if (debugGlobal) {
            stat.append(" | ");
            stat.append(" ELMod:");
            stat.append(String.format("%1.2f", elementMod));
            stat.append(" ChST4: ");
            stat.append(String.format("%2.2f", activateRate));
        }

        // Шаг 5
        // если скилл магический - то шанс умножается на sqrt(m.atk)/m.def*11, м.атк для блесс сосок в 4 раза выше, для обычных в 2 раза выше

        if (skill.isMagic()) {
            int ssMod = 1;

            if (skill.isSSPossible()) {
                if (spiritshot == 1) {
                    ssMod = 2;
                } else if (spiritshot == 2) {
                    ssMod = 4;
                }
            }

            activateRate *= Math.sqrt(player.getMAtk(target, skillEntry) * ssMod) / target.getMDef(player, skillEntry) * 11;

            if (debugGlobal) {
                stat.append(" | ");
                stat.append("MAtk:");
                stat.append(player.getMAtk(target, skillEntry));
                stat.append(" SS:");
                stat.append(ssMod);
                stat.append(" MDef:");
                stat.append(target.getMDef(player, skillEntry));
                stat.append(" ChST5: ");
                stat.append(String.format("%2.2f", activateRate));
            }
        }

        // Обрезаем шансы (не менее 10% и не более 90%) если цель не босс и не рейдбосс
        if (!(target.isRaid() || target.isBoss() || target.isMinion())) {
            if (skill.getSkillType() != SkillType.DESTROY_SUMMON) // Для скилов DESTROY_SUMMON минимальное ограничение не ставится
            {
                activateRate = Math.max(activateRate, 10);
            }
            activateRate = Math.min(activateRate, 90);
        }

        if (debugGlobal) {
            stat.append(" | ");
            stat.append(" FinalChance: ");
            stat.append(String.format("%2.2f", activateRate));
        }

        if (debugGlobal) {
            if (debugCaster) {
                player.getPlayer().sendMessage(stat.toString());
            }
            if (debugTarget) {
                target.getPlayer().sendMessage(stat.toString());
            }
        }

        if (player.isPlayer() && (AllSettingsConfig.showSkillChances || player.getPlayer().isGM()))
            player.getPlayer().sendMessage(skill.getName() + " chance: " + activateRate + "%");
        return Rnd.chance((int) activateRate);
    }

    public static double calcSkillTypeProficiency(double multiplier, final Creature target, final SkillType skillType, final EffectType effType) {
        if ((skillType != null && skillType == SkillType.BLEED) || (effType != null && effType == EffectType.Bleed)) {
            multiplier = calcParamOnStat(target, Stats.BLEED_POWER);
        } else if ((skillType != null && skillType == SkillType.POISON) ||
                (effType != null && (effType == EffectType.Poison || effType == EffectType.PoisonLethal))) {
            multiplier = calcParamOnStat(target, Stats.POISON_POWER);
        } else if ((skillType != null && skillType == SkillType.STUN) || (effType != null && effType == EffectType.Stun)) {
            multiplier = calcParamOnStat(target, Stats.STUN_POWER);
        } else if ((skillType != null && skillType == SkillType.PARALYZE) ||
                (effType != null && (effType == EffectType.Paralyze || effType == EffectType.Petrification))) {
            multiplier = calcParamOnStat(target, Stats.PARALYZE_POWER);
        } else if ((skillType != null && skillType == SkillType.ROOT) || (effType != null && effType == EffectType.Root)) {
            multiplier = calcParamOnStat(target, Stats.ROOT_POWER);
        } else if ((skillType != null && skillType == SkillType.SLEEP) || (effType != null && effType == EffectType.Sleep)) {
            multiplier = calcParamOnStat(target, Stats.SLEEP_POWER);
        } else if ((skillType != null && (skillType == SkillType.MUTE || skillType == SkillType.DESTROY_SUMMON)) || (effType != null &&
                (effType == EffectType.Betray ||
                        effType == EffectType.Bluff ||
                        effType ==
                                EffectType.DestroySummon ||
                        effType == EffectType.Discord
                        ||
                        effType == EffectType.Enervation ||
                        effType == EffectType.Fear ||
                        effType == EffectType.Mute ||
                        effType == EffectType.MuteAll ||
                        effType == EffectType.MuteAttack ||
                        effType ==
                                EffectType.MutePhisycal))) {
            multiplier = calcParamOnStat(target, Stats.MENTAL_POWER);
        } else if (skillType != null && skillType == SkillType.DEBUFF) {
            multiplier = calcParamOnStat(target, Stats.DEBUFF_POWER);
        } else if ((skillType != null && skillType == SkillType.STEAL_BUFF) || (effType != null && effType == EffectType.Cancel)) {
            multiplier = calcParamOnStat(target, Stats.CANCEL_POWER);
        } else if (skillType != null && skillType == SkillType.MANADAM) {
            multiplier = calcParamOnStat(target, Stats.MAGIC_POWER);
        }

        return multiplier;
    }

    public static double calcSkillTypeVulnerability(double multiplier, final Creature target, final SkillType skillType, final EffectType effType) {
        if (skillType != null || effType != null) {
            if ((skillType != null && skillType == SkillType.BLEED) || (effType != null && effType == EffectType.Bleed)) {
                multiplier = calcParamOnStat(target, Stats.BLEED_RESIST);
            } else if ((skillType != null && skillType == SkillType.POISON) ||
                    (effType != null && (effType == EffectType.Poison || effType == EffectType.PoisonLethal))) {
                multiplier = calcParamOnStat(target, Stats.POISON_RESIST);
            } else if ((skillType != null && skillType == SkillType.STUN) || (effType != null && effType == EffectType.Stun)) {
                multiplier = calcParamOnStat(target, Stats.STUN_RESIST);
            } else if ((skillType != null && skillType == SkillType.PARALYZE) ||
                    (effType != null && (effType == EffectType.Paralyze || effType == EffectType.Petrification))) {
                multiplier = calcParamOnStat(target, Stats.PARALYZE_RESIST);
            } else if ((skillType != null && skillType == SkillType.ROOT) || (effType != null && effType == EffectType.Root)) {
                multiplier = calcParamOnStat(target, Stats.ROOT_RESIST);
            } else if ((skillType != null && skillType == SkillType.SLEEP) || (effType != null && effType == EffectType.Sleep)) {
                multiplier = calcParamOnStat(target, Stats.SLEEP_RESIST);
            } else if ((skillType != null && (skillType == SkillType.MUTE || skillType == SkillType.DESTROY_SUMMON)) || (effType != null &&
                    (effType == EffectType.Betray ||
                            effType == EffectType.Bluff ||
                            effType ==
                                    EffectType.DestroySummon ||
                            effType == EffectType.Discord
                            || effType ==
                            EffectType.Enervation ||
                            effType == EffectType.Fear ||
                            effType == EffectType.Mute ||
                            effType ==
                                    EffectType.MuteAll ||
                            effType ==
                                    EffectType.MuteAttack ||
                            effType ==
                                    EffectType.MutePhisycal))) {
                multiplier = calcParamOnStat(target, Stats.MENTAL_RESIST);
            } else if (skillType != null && skillType == SkillType.DEBUFF) {
                multiplier = calcParamOnStat(target, Stats.DEBUFF_RESIST);
            } else if ((skillType != null && skillType == SkillType.STEAL_BUFF) || (effType != null && effType == EffectType.Cancel)) {
                multiplier = calcParamOnStat(target, Stats.CANCEL_RESIST);
            } else if (skillType != null && skillType == SkillType.MANADAM) {
                multiplier = calcParamOnStat(target, Stats.MAGIC_RESIST);
            }
        }

        return multiplier;
    }

    public static int calcSkillStatModifier(final SkillType skType, final EffectType effType, final Creature target) {
        int _stat = 30;

        if ((skType != null && (skType == SkillType.BLEED || skType == SkillType.POISON || skType == SkillType.STUN))
                || (effType != null &&
                (effType == EffectType.Bleed || effType == EffectType.Poison || effType == EffectType.PoisonLethal || effType == EffectType.Stun))) {
            _stat = target.getCON();
        } else if ((skType != null && (skType == SkillType.MUTE || skType == SkillType.DEBUFF || skType == SkillType.SLEEP
                || skType == SkillType.ROOT || skType == SkillType.PARALYZE || skType == SkillType.DESTROY_SUMMON)) ||
                (effType != null && (effType == EffectType.Root || effType == EffectType.Paralyze || effType == EffectType.Petrification
                        || effType == EffectType.Sleep || effType == EffectType.Betray || effType == EffectType.Bluff ||
                        effType == EffectType.DestroySummon
                        || effType == EffectType.Discord || effType == EffectType.Enervation || effType == EffectType.Fear ||
                        effType == EffectType.Mute
                        || effType == EffectType.MuteAll || effType == EffectType.MuteAttack || effType == EffectType.MutePhisycal))) {
            _stat = target.getMEN();
        }

        _stat = Math.max(_stat, 0);

        return _stat;
    }

    public static boolean calcSkillSuccess(final Creature creature, final Creature target, final SkillEntry skillEntry,
                                           final double initialActivateRate, final EffectTemplate et, final int spiritshot) {
        if (initialActivateRate == -1) {
            return true;
        }

        double activateRate = initialActivateRate;

        activateRate = Math.max(Math.min(activateRate, 150), 1); // На всякий случай
        final double base = activateRate; // Запоминаем базовый шанс (нужен позже)

        if (!skillEntry.getTemplate().isOffensive()) {
            return Rnd.chance(activateRate);
        }

        final boolean[] debug = isDebugEnabled(creature, target);
        final boolean debugGlobal = debug[0];
        final boolean debugCaster = debug[1];
        final boolean debugTarget = debug[2];

        double statMod = 1.;
        if (skillEntry.getTemplate().getSaveVs() != null) {
            statMod = skillEntry.getTemplate().getSaveVs().calcChanceMod(target);
            activateRate *= statMod; // Бонус от MEN/CON/etc
        }

        activateRate = Math.max(activateRate, 1);

        double mAtkMod = 1.;
        int ssMod = 0;
        if (skillEntry.getTemplate().isMagic()) // Этот блок только для магических скиллов
        {
            final int mdef = Math.max(1, target.getMDef(target, skillEntry)); // Вычисляем mDef цели
            double matk = creature.getMAtk(target, skillEntry);

            if (skillEntry.getTemplate().isSSPossible()) {
                switch (spiritshot) {
                    case ItemInstance.CHARGED_BLESSED_SPIRITSHOT:
                        ssMod = 4;
                        break;
                    case ItemInstance.CHARGED_SPIRITSHOT:
                        ssMod = 2;
                        break;
                    default:
                        ssMod = 1;
                }
                matk *= ssMod;
            }

            mAtkMod = FormulasConfig.SKILLS_CHANCE_MOD * Math.pow(matk, FormulasConfig.SKILLS_CHANCE_POW) / mdef;

			/*
			if (mAtkMod < 0.7)
				mAtkMod = 0.7;
			else if (mAtkMod > 1.4)
				mAtkMod = 1.4;
			*/

            activateRate *= mAtkMod;
            activateRate = Math.max(activateRate, 1);
        }

        double lvlDependMod = skillEntry.getTemplate().getLevelModifier();
        if (lvlDependMod != 0) {
            final int attackLevel = skillEntry.getTemplate().getMagicLevel() > 0 ? skillEntry.getTemplate().getMagicLevel() : creature.getLevel();
			/*final int delta = attackLevel - target.getLevel();
			lvlDependMod = delta / 5;
			lvlDependMod = lvlDependMod * 5;
			if (lvlDependMod != delta)
				lvlDependMod = delta < 0 ? lvlDependMod - 5 : lvlDependMod + 5;

			activateRate += lvlDependMod;*/
            lvlDependMod = 1. + (attackLevel - target.getLevel()) * 0.03 * lvlDependMod;
            if (lvlDependMod < 0) {
                lvlDependMod = 0;
            } else if (lvlDependMod > 2) {
                lvlDependMod = 2;
            }

            activateRate *= lvlDependMod;
        }

        double vulnMod = 0;
        double profMod = 0;
        double resMod = 1.;
        double debuffMod = 1.;
        if (!skillEntry.getTemplate().isIgnoreResists()) {
            debuffMod = 1. - target.calcStat(Stats.DEBUFF_RESIST, creature, skillEntry) / 120.;

            if (debuffMod != 1) // Внимание, знак был изменен на противоположный !
            {
                if (debuffMod == Double.NEGATIVE_INFINITY) {
                    if (debugGlobal) {
                        if (debugCaster) {
                            creature.getPlayer().sendMessage("Full debuff immunity");
                        }
                        if (debugTarget) {
                            target.getPlayer().sendMessage("Full debuff immunity");
                        }
                    }
                    return false;
                }
                if (debuffMod == Double.POSITIVE_INFINITY) {
                    if (debugGlobal) {
                        if (debugCaster) {
                            creature.getPlayer().sendMessage("Full debuff vulnerability");
                        }
                        if (debugTarget) {
                            target.getPlayer().sendMessage("Full debuff vulnerability");
                        }
                    }
                    return true;
                }

                debuffMod = Math.max(debuffMod, 0);
                activateRate *= debuffMod;
            }

            final SkillTrait trait = skillEntry.getTemplate().getTraitType();
            if (trait != null) {
                vulnMod = trait.calcVuln(creature, target, skillEntry);
                profMod = trait.calcProf(creature, target, skillEntry);

                final double maxResist = 90 + profMod * 0.85;
                resMod = (maxResist - vulnMod) / 60.;
            }

            if (resMod != 1) // Внимание, знак был изменен на противоположный !
            {
                if (resMod == Double.NEGATIVE_INFINITY) {
                    if (debugGlobal) {
                        if (debugCaster) {
                            creature.getPlayer().sendMessage("Full immunity");
                        }
                        if (debugTarget) {
                            target.getPlayer().sendMessage("Full immunity");
                        }
                    }
                    return false;
                }
                if (resMod == Double.POSITIVE_INFINITY) {
                    if (debugGlobal) {
                        if (debugCaster) {
                            creature.getPlayer().sendMessage("Full vulnerability");
                        }
                        if (debugTarget) {
                            target.getPlayer().sendMessage("Full vulnerability");
                        }
                    }
                    return true;
                }

                resMod = Math.max(resMod, 0);
                activateRate *= resMod;
            }
        }

        double elementMod = 0;
        final Element element = skillEntry.getTemplate().getElement();
        if (element != Element.NONE) {
            elementMod = skillEntry.getTemplate().getElementPower();
            final Element attackElement = getAttackElement(creature, target);
            if (attackElement == element) {
                elementMod += creature.calcStat(element.getAttack(), 0);
            }

            elementMod -= target.calcStat(element.getDefence(), 0);
			/*if (elementMod < 0)
				elementMod = 0;
			else
				elementMod = Math.round(elementMod / 10);*/
            elementMod = Math.round(elementMod / 10);

            activateRate += elementMod;
        }

        //if(skillEntry.isSoulBoost()) // Бонус от душ камаелей
        //	activateRate *= 0.85 + 0.06 * Math.min(character.getConsumedSouls(), 5);

        activateRate = Math.max(activateRate, Math.min(base,
                FormulasConfig.SKILLS_CHANCE_MIN)); // Если базовый шанс более FormulasConfig.SKILLS_CHANCE_MIN, то при небольшой разнице в уровнях, делаем кап снизу.
        activateRate = Math.max(Math.min(activateRate, FormulasConfig.SKILLS_CHANCE_CAP), 1); // Применяем кап
        if (creature.isPlayer() && (AllSettingsConfig.showSkillChances || creature.getPlayer().isGM()))
            creature.getPlayer().sendMessage(skillEntry.getName() + " chance: " + (int) activateRate + "%");

        final boolean result = Rnd.chance((int) activateRate);

        if (debugGlobal) {
            final StringBuilder stat = new StringBuilder(100);
            stat.append(skillEntry.getId());
            stat.append('/');
            stat.append(skillEntry.getDisplayLevel());
            stat.append(' ');
            if (et == null) {
                stat.append(skillEntry.getTemplate().getName());
            } else {
                stat.append(et._effectType.name());
            }
            stat.append(" AR:");
            stat.append((int) base);
            stat.append(' ');
            if (skillEntry.getTemplate().getSaveVs() != null) {
                stat.append(skillEntry.getTemplate().getSaveVs().name());
                stat.append(':');
                stat.append(String.format("%1.1f", statMod));
            }
            if (skillEntry.getTemplate().isMagic()) {
                stat.append(' ');
                stat.append(" mAtk:");
                stat.append(String.format("%1.1f", mAtkMod));
                stat.append(" SS:");
                stat.append(ssMod);
            }
            if (skillEntry.getTemplate().getTraitType() != null) {
                stat.append(' ');
                stat.append(skillEntry.getTemplate().getTraitType().name());
            }
            stat.append(' ');
            stat.append(String.format("%1.1f", resMod));
            stat.append('(');
            stat.append(String.format("%1.1f", profMod));
            stat.append('/');
            stat.append(String.format("%1.1f", vulnMod));
            if (debuffMod != 0) {
                stat.append('+');
                stat.append(String.format("%1.1f", debuffMod));
            }
            stat.append(") lvl:");
            stat.append(String.format("%1.1f", lvlDependMod));
            stat.append(" elem:");
            stat.append((int) elementMod);
            stat.append(" Chance:");
            stat.append(String.format("%1.1f", activateRate));
            if (!result) {
                stat.append(" failed");
            }

            // отсылаем отладочные сообщения
            if (debugCaster) {
                creature.getPlayer().sendMessage(stat.toString());
            }
            if (debugTarget) {
                target.getPlayer().sendMessage(stat.toString());
            }
        }
        return result;
    }

    public static boolean calcSkillSuccess(final Creature creature, final Creature target, final SkillEntry skill, final int activateRate) {
        return calcSkillSuccess(creature, creature, skill, activateRate, null, creature.getChargedSpiritShot());
    }

    public static void calcSkillMastery(final SkillEntry skill, final Creature activeChar) {
        if (skill.getTemplate().isHandler()) {
            return;
        }

        //Skill id 330 for fighters, 331 for mages
        //Actually only GM can have 2 skill masteries, so let's make them more lucky ^^
        if ((activeChar.getSkillLevel(331) > 0 && activeChar.calcStat(Stats.SKILL_MASTERY, activeChar.getINT(), null, skill) >= Rnd.get(5000)) ||
                (activeChar.getSkillLevel(330) > 0 && activeChar.calcStat(Stats.SKILL_MASTERY, activeChar.getSTR(), null, skill) >= Rnd.get(5000))) {
            activeChar.setSkillMastery(skill.getTemplate());
        }
    }

    public static double calcDamageResists(final SkillEntry skill, final Creature attacker, final Creature defender, double value) {
        if (attacker == defender) // это дамаг от местности вроде ожога в лаве, наносится от своего имени
        {
            return value; // TODO: по хорошему надо учитывать защиту, но поскольку эти скиллы немагические то надо делать отдельный механизм
        }

        if (attacker.isBoss()) {
            value *= ServerConfig.RATE_EPIC_ATTACK;
        } else if (attacker.isRaid() || attacker instanceof ReflectionBossInstance) {
            value *= ServerConfig.RATE_RAID_ATTACK;
        }

        if (defender.isBoss()) {
            value /= ServerConfig.RATE_EPIC_DEFENSE;
        } else if (defender.isRaid() || defender instanceof ReflectionBossInstance) {
            value /= ServerConfig.RATE_RAID_DEFENSE;
        }

        final Player pAttacker = attacker.getPlayer();

        // если уровень игрока ниже чем на 2 и более уровней моба 78+, то его урон по мобу снижается
        final int diff = defender.getLevel() - (pAttacker != null ? pAttacker.getLevel() : attacker.getLevel());
        if (attacker.isPlayable() && defender.isMonster() && defender.getLevel() >= 78 && diff > 2) {
            value *= .7 / Math.pow(diff - 2, .25);
        }

        Element element = Element.NONE;
        double power = 0.;

        // использует элемент умения
        if (skill != null) {
            element = skill.getTemplate().getElement();
            power = skill.getTemplate().getElementPower();
        }
        // используем максимально эффективный элемент
        else {
            element = getAttackElement(attacker, defender);
        }

        if (element == Element.NONE) {
            return value;
        }

        if (pAttacker != null && pAttacker.isGM() && OtherConfig.DEBUG) {
            pAttacker.sendMessage("Element: " + element.name());
            pAttacker.sendMessage("Attack: " + attacker.calcStat(element.getAttack(), power));
            pAttacker.sendMessage("Defence: " + defender.calcStat(element.getDefence(), 0.));
            pAttacker.sendMessage("Modifier: " + getElementMod(defender.calcStat(element.getDefence(), 0.), attacker.calcStat(element.getAttack(),
                    power)));
        }

        return value * getElementMod(defender.calcStat(element.getDefence(), 0.), attacker.calcStat(element.getAttack(), power));
    }

    public static int calculateKarmaLost(final Player player, long exp) {
        final double bonus = player.getPcKarmaBonus();
        if (exp > 0) {
            double rateXP = ServerConfig.RATE_XP;
            if (CustomConfig.subscriptionAllow && !player.isGM() && !player.getCustomPlayerComponent().isSubscriptionActive()) {
                rateXP = Math.max(1.0, ServerConfig.RATE_XP / CustomConfig.cutRateXP);
            }
            exp /= rateXP;
        }
        return (int) ((Math.abs(exp) / bonus) / 30);
    }

    /**
     * Возвращает множитель для атаки из значений атакующего и защитного элемента.
     * <br /><br />
     * Диапазон от 1.0 до 1.7 (Freya)
     * <br /><br />
     *
     * @param defense значение защиты
     * @param attack  значение атаки
     * @return множитель
     */
    private static double getElementMod(final double defense, final double attack) {
        final double diff = attack - defense;
        if (diff <= 0) {
            return 1.0;
        } else if (diff < 50) {
            return 1.0 + diff * 0.003948;
        } else if (diff < 150) {
            return 1.2;
        } else if (diff < 300) {
            return 1.4;
        } else {
            return 1.7;
        }
    }

    /**
     * Возвращает максимально эффективный атрибут, при атаке цели
     *
     * @param attacker
     * @param target
     * @return
     */
    public static Element getAttackElement(final Creature attacker, final Creature target) {
        double val, max = Double.MIN_VALUE;
        Element result = Element.NONE;
        for (final Element e : Element.VALUES) {
            val = attacker.calcStat(e.getAttack(), 0., null, null);
            if (val <= 0.) {
                continue;
            }

            if (target != null) {
                val -= target.calcStat(e.getDefence(), 0., null, null);
            }

            if (val > max) {
                result = e;
                max = val;
            }
        }

        return result;
    }

    public static class AttackInfo {
        public double damage = 0;
        public double defence = 0;
        public double crit_static = 0;
        public double death_rcpt = 0;
        public double lethal1 = 0;
        public double lethal2 = 0;
        public double lethal_dmg = 0;
        public boolean crit = false;
        public boolean shld = false;
        public boolean lethal = false;
        public boolean miss = false;

        public AttackInfo() {
        }

        public AttackInfo(final double lethal_dmg) {
            this.lethal_dmg = lethal_dmg;
        }

        public AttackInfo(final double damage, final double lethal_dmg) {
            this.damage = damage;
            this.lethal_dmg = lethal_dmg;
        }

        public AttackInfo(final double damage, final double defence, final double crit_static, final double death_rcpt, final double lethal1, final double lethal2, final double lethal_dmg, final boolean crit, final boolean shld, final boolean lethal, final boolean miss) {
            this.damage = damage;
            this.defence = defence;
            this.crit_static = crit_static;
            this.death_rcpt = death_rcpt;
            this.lethal1 = lethal1;
            this.lethal2 = lethal2;
            this.lethal_dmg = lethal_dmg;
            this.crit = crit;
            this.shld = shld;
            this.lethal = lethal;
            this.miss = miss;
        }
    }
}
