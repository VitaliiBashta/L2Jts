package org.mmocore.gameserver.stats.conditions;

import org.jts.dataparser.data.holder.setting.common.PlayerSex;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.skills.SkillEntry;

import java.util.Optional;
import java.util.OptionalDouble;

/**
 * @author : Mangol
 */
public class Condition_check_sex extends Condition {
    private final Type sex;

    public Condition_check_sex(final String val) {
        sex = Type.valueOf(val);
    }

    @Override
    public boolean testImpl(final Creature creature, final Optional<Creature> optionalTarget, final Optional<SkillEntry> skill, final Optional<ItemInstance> item, final OptionalDouble initialValue) {
        final Player player = (Player) creature;
        {
            switch (sex) {
                case M: {
                    return creature != null & player.getSex() == PlayerSex.MALE.ordinal();
                }
                case F: {
                    return creature != null && player.getSex() == PlayerSex.FEMALE.ordinal();
                }
            }
        }
        return false;
    }

    private enum Type {
        M,
        F
    }
}