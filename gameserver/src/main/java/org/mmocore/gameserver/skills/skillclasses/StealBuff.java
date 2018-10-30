package org.mmocore.gameserver.skills.skillclasses;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.EffectType;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.skills.effects.EffectTemplate;
import org.mmocore.gameserver.templates.StatsSet;
import org.mmocore.gameserver.utils.EffectsComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author pchayka
 */

public class StealBuff extends Skill {
    private final int _stealCount;

    public StealBuff(final StatsSet set) {
        super(set);
        _stealCount = set.getInteger("stealCount", 1);
    }

    private static boolean canSteal(final Effect e) {
        return e != null && e.isInUse() && e.isCancelable() && !e.getSkill().getTemplate().isToggle() && !e.getSkill().getTemplate().isPassive() && !e.getSkill().getTemplate()
                .isOffensive() && e.getEffectType() != EffectType.p_recovery_vp && !e.getTemplate()._applyOnCaster;
    }

    /*	private static boolean calcStealChance(Creature effected, Creature effector)
        {
            double cancel_res_multiplier = effected.calcStat(Stats.CANCEL_RESIST, 1, null, null);
            int dml = effector.getLevel() - effected.getLevel();   // to check: magicLevel or player level? Since it's magic skill setting player level as default
            double prelimChance = (dml + 50) * (1 - cancel_res_multiplier * .01);   // 50 is random reasonable constant which gives ~50% chance of steal success while else is equal
            return Rnd.chance(prelimChance);
        }
    */
    private static Effect cloneEffect(final Creature cha, final Effect eff) {
        final SkillEntry skill = eff.getSkill();

        for (final EffectTemplate et : skill.getTemplate().getEffectTemplates()) {
            final Effect effect = et.getEffect(cha, cha, skill);
            if (effect != null) {
                effect.setCount(eff.getCount());
                effect.setPeriod(eff.getCount() == 1 ? eff.getPeriod() - eff.getTime() : eff.getPeriod());
                return effect;
            }
        }
        return null;
    }

    @Override
    public boolean checkCondition(final SkillEntry skillEntry, final Creature activeChar, final Creature target, final boolean forceUse, final boolean dontMove, final boolean first) {
        if (target == null || !target.isPlayer()) {
            activeChar.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
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

                double baseChance = 25;
                int buffCount = 0;
                int stealCount = 0;
                final int decrement = getId() == 1440 ? 5 : 50;
                final List<Effect> effectsList = new ArrayList<>(target.getEffectList().getAllEffects());
                Collections.sort(effectsList, EffectsComparator.getReverseInstance()); // ToFix: Comparator to HF
                for (final Effect e : effectsList) {
                    if (!canSteal(e)) {
                        continue;
                    }

                    if (Rnd.chance(baseChance)) {
                        final Effect stolenEffect = cloneEffect(activeChar, e);
                        if (stolenEffect != null) {
                            activeChar.getEffectList().addEffect(stolenEffect);
                        }
                        e.exit();

                        if (stealCount >= _stealCount) {
                            break;
                        }
                    }
                    stealCount++;

                    buffCount++;
                    if (buffCount >= _stealCount) {
                        baseChance /= decrement;
                        if (baseChance < 1) {
                            break;
                        }
                    }
                }

                getEffects(skillEntry, activeChar, target, getActivateRate() > 0, false);
            }
        }

        if (isSSPossible()) {
            activeChar.unChargeShots(isMagic());
        }
    }
}