package org.mmocore.gameserver.stats.triggers;

import org.mmocore.commons.lang.ArrayUtils;
import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.stats.conditions.Condition;

/**
 * @author VISTALL
 * @date 15:03/22.01.2011
 */
public class TriggerInfo extends Skill.AddedSkill {
    private final TriggerType _type;
    private final double _chance;
    private Condition[] _conditions = Condition.EMPTY_ARRAY;

    public TriggerInfo(final int id, final int level, final TriggerType type, final double chance) {
        super(id, level);
        _type = type;
        _chance = chance;
    }

    public final void addCondition(final Condition c) {
        _conditions = ArrayUtils.add(_conditions, c);
    }

    public boolean checkCondition(final Creature actor, final Creature target, final Creature aimTarget, final SkillEntry owner, final double damage) {
        // Скилл проверяется и кастуется на aimTarget
        if (getSkill().checkTarget(actor, aimTarget, aimTarget, false, false) != null) {
            return false;
        }

        for (final Condition c : _conditions) {
            if (!c.test(actor, target, owner, damage)) {
                return false;
            }
        }
        return true;
    }

    public TriggerType getType() {
        return _type;
    }

    public double getChance() {
        return _chance;
    }
}
