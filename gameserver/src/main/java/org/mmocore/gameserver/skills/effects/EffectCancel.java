package org.mmocore.gameserver.skills.effects;

import org.apache.commons.lang3.ArrayUtils;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.configuration.config.OtherConfig;
import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.stats.Formulas;
import org.mmocore.gameserver.stats.Stats;
import org.mmocore.gameserver.utils.EffectsComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author pchayka
 */

public class EffectCancel extends Effect {
    private final int _minChance;
    private final int _maxChance;
    private final int _cancelRate;
    private final String[] _stackTypes;
    private final int _negateCount;

    /*
     * cancelRate is skill dependant constant:
     * Cancel - 25
     * Touch of Death/Insane Crusher - 25
     * Mage/Warrior Bane - 80
     * Mass Mage/Warrior Bane - 40
     * Infinity Spear - 10
     */

    public EffectCancel(final Creature creature, final Creature target, final SkillEntry skill, final EffectTemplate template) {
        super(creature, target, skill, template);
        _cancelRate = template.getParam().getInteger("cancelRate", 0);
        _minChance = template.getParam().getInteger("minChance", 25);
        _maxChance = template.getParam().getInteger("maxChance", 75);
        _negateCount = template.getParam().getInteger("negateCount", 5);
        final String st = template.getParam().getString("negateStackTypes", null);
        _stackTypes = st != null ? st.split(";") : null;
    }

    @Override
    public void onStart() {
        if (_effected.getEffectList().isEmpty()) {
            return;
        }

        final List<Effect> effectList = new ArrayList<>(_effected.getEffectList().getAllEffects());
        Collections.sort(effectList, EffectsComparator.getReverseInstance());

        final List<Effect> buffList = new ArrayList<>();

        for (final Effect e : effectList) {
            if (!e.isCancelable() || e.getSkill().getTemplate().isToggle() || e.isOffensive()) {
                continue;
            }

            if (_stackTypes != null) {
                if (!ArrayUtils.contains(_stackTypes, e.getStackType()) && !ArrayUtils.contains(_stackTypes, e.getStackType2())) {
                    continue;
                }
            }

            buffList.add(e);
        }

        if (buffList.isEmpty()) {
            return;
        }

        final boolean[] debug = Formulas.isDebugEnabled(_effector, _effected);
        final boolean debugGlobal = debug[0];
        final boolean debugCaster = debug[1];
        final boolean debugTarget = debug[2];
        StringBuilder stat = null;

        final double cancel_res_multiplier = Math.max(1 - _effected.calcStat(Stats.CANCEL_RESIST, 0, null, null) / 100., 0);
        final int magicLevel = getSkill().getTemplate().getMagicLevel();
        double prelimChance;
        int eml, dml, buffTime;
        boolean result;

        if (debugGlobal) {
            stat = new StringBuilder(100);
            stat.append("Cancel power:");
            stat.append(_cancelRate);
            stat.append(" res:");
            stat.append(String.format("%1.1f", cancel_res_multiplier));
            stat.append('\n');
        }

        int negated = 0;
        for (final Effect e : buffList) {
            if (negated >= _negateCount) {
                break;
            }

            eml = e.getSkill().getTemplate().getMagicLevel();
            dml = magicLevel - (eml == 0 ? _effected.getLevel() : eml);
            if (OtherConfig.CANCEL_PVP_BUFF_TIME) {
                buffTime = 10;
            } else {
                buffTime = e.getTimeLeft() / 120;
            }

            prelimChance = (2. * dml + _cancelRate + buffTime) * cancel_res_multiplier; // retail formula
            prelimChance = Math.max(Math.min(prelimChance, _maxChance), _minChance);
            result = Rnd.chance(prelimChance);

            if (result) {
                negated++;
                _effected.sendPacket(new SystemMessage(SystemMsg.THE_EFFECT_OF_S1_HAS_BEEN_REMOVED).addSkillName(e.getSkill().getId(), e.getSkill().getLevel()));
                e.exit();
            }

            if (debugGlobal) {
                stat.append(e.getSkill().getName());
                stat.append(" Lvl:");
                stat.append(eml);
                stat.append(" Delta:");
                stat.append(dml);
                stat.append(" Time:");
                stat.append(buffTime);
                stat.append(" Chance:");
                stat.append(String.format("%1.1f", prelimChance));
                if (!result) {
                    stat.append(" failed");
                }
                stat.append('\n');

            }
        }

        if (debugGlobal) {
            if (debugCaster) {
                _effector.getPlayer().sendMessage(stat.toString());
            }
            if (debugTarget) {
                _effected.getPlayer().sendMessage(stat.toString());
            }
        }

        buffList.clear();
    }

    @Override
    protected boolean onActionTime() {
        return false;
    }
}