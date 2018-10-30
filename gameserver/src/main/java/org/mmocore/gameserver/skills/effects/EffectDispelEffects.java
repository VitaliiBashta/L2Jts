package org.mmocore.gameserver.skills.effects;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.utils.EffectsComparator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author pchayka
 */

public class EffectDispelEffects extends Effect {
    private final int _cancelRate;
    private final int _negateCount;

    public EffectDispelEffects(final Creature creature, final Creature target, final SkillEntry skill, final EffectTemplate template) {
        super(creature, target, skill, template);
        _cancelRate = template.getParam().getInteger("cancelRate", 0);
        _negateCount = template.getParam().getInteger("negateCount", 5);
    }

    @Override
    public void onStart() {
        if (_effected.getEffectList().isEmpty()) {
            return;
        }

        final List<Effect> effectList = new ArrayList<>(_effected.getEffectList().getAllEffects());
        Collections.sort(effectList, EffectsComparator.getReverseInstance());

        final Collection<Effect> buffList = new ArrayList<>();

        for (final Effect e : effectList) {
            if (e.isOffensive() && e.isCancelable()) {
                buffList.add(e);
            }
        }

        if (buffList.isEmpty()) {
            return;
        }

        int negated = 0;
        for (final Effect e : buffList) {
            if (negated >= _negateCount) {
                break;
            }

            if (Rnd.chance(_cancelRate)) {
                negated++;
                _effected.sendPacket(new SystemMessage(SystemMsg.THE_EFFECT_OF_S1_HAS_BEEN_REMOVED).addSkillName(e.getSkill().getId(), e.getSkill().getLevel()));
                e.exit();
            }
        }

        buffList.clear();
    }

    @Override
    protected boolean onActionTime() {
        return false;
    }
}