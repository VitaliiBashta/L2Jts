package org.mmocore.gameserver.skills.skillclasses;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.configuration.config.FormulasConfig;
import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.EffectType;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.stats.Formulas;
import org.mmocore.gameserver.templates.StatsSet;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NegateEffects extends Skill {
    private final Map<EffectType, Integer> _negateEffects = new EnumMap<>(EffectType.class);
    private final Map<String, Integer> _negateStackType = new HashMap<>();
    private final boolean _onlyPhysical;
    private final boolean _negateDebuffs;

    public NegateEffects(final StatsSet set) {
        super(set);

        final String[] negateEffectsString = set.getString("negateEffects", "").split(";");
        for (int i = 0; i < negateEffectsString.length; i++) {
            if (!negateEffectsString[i].isEmpty()) {
                final String[] entry = negateEffectsString[i].split(":");
                _negateEffects.put(Enum.valueOf(EffectType.class, entry[0]), entry.length > 1 ? Integer.decode(entry[1]) : Integer.MAX_VALUE);
            }
        }

        final String[] negateStackTypeString = set.getString("negateStackType", "").split(";");
        for (int i = 0; i < negateStackTypeString.length; i++) {
            if (!negateStackTypeString[i].isEmpty()) {
                final String[] entry = negateStackTypeString[i].split(":");
                _negateStackType.put(entry[0], entry.length > 1 ? Integer.decode(entry[1]) : Integer.MAX_VALUE);
            }
        }

        _onlyPhysical = set.getBool("onlyPhysical", false);
        _negateDebuffs = set.getBool("negateDebuffs", true);
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

                if (!_negateDebuffs && !success) {
                    activeChar.sendPacket(new SystemMessage(SystemMsg.C1_HAS_RESISTED_YOUR_S2).addString(target.getName()).addSkillName(getId(), getLevel()));
                    continue;
                }

                if (!_negateEffects.isEmpty()) {
                    for (final Map.Entry<EffectType, Integer> e : _negateEffects.entrySet()) {
                        negateEffectAtPower(target, e.getKey(), e.getValue());
                    }
                }

                if (!_negateStackType.isEmpty()) {
                    for (final Map.Entry<String, Integer> e : _negateStackType.entrySet()) {
                        negateEffectAtPower(target, e.getKey(), e.getValue());
                    }
                }

                getEffects(skillEntry, activeChar, target, getActivateRate() > 0, false);
            }
        }

        if (isSSPossible()) {
            activeChar.unChargeShots(isMagic());
        }
    }

    private void negateEffectAtPower(final Creature target, final EffectType type, final int power) {
        for (final Effect e : target.getEffectList().getAllEffects()) {
            final SkillEntry skill = e.getSkill();
            if (_onlyPhysical && skill.getTemplate().isMagic() || !skill.getTemplate().isCancelable() || skill.getTemplate().isOffensive() && !_negateDebuffs) {
                continue;
            }
            // Если у бафа выше уровень чем у скилла Cancel, то есть шанс, что этот баф не снимется
            if (!skill.getTemplate().isOffensive() && skill.getTemplate().getMagicLevel() > getMagicLevel() && Rnd.chance(skill.getTemplate().getMagicLevel() - getMagicLevel())) {
                continue;
            }
            if (e.getEffectType() == type && e.getStackOrder() <= power) {
                e.exit();
            }
        }
    }

    private void negateEffectAtPower(final Creature target, final String stackType, final int power) {
        for (final Effect e : target.getEffectList().getAllEffects()) {
            final SkillEntry skill = e.getSkill();
            if (_onlyPhysical && skill.getTemplate().isMagic() || !skill.getTemplate().isCancelable() || skill.getTemplate().isOffensive() && !_negateDebuffs) {
                continue;
            }
            // Если у бафа выше уровень чем у скилла Cancel, то есть шанс, что этот баф не снимется
            if (!skill.getTemplate().isOffensive() && skill.getTemplate().getMagicLevel() > getMagicLevel() && Rnd.chance(skill.getTemplate().getMagicLevel() - getMagicLevel())) {
                continue;
            }
            if (e.checkStackType(stackType) && e.getStackOrder() <= power) {
                e.exit();
            }
        }
    }
}