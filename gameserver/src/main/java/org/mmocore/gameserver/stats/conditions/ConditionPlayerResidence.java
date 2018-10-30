package org.mmocore.gameserver.stats.conditions;

import org.mmocore.gameserver.model.entity.residence.Residence;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.skills.SkillEntry;

import java.util.Optional;
import java.util.OptionalDouble;

/**
 * @author VISTALL
 * @date 8:13/31.01.2011
 */
public class ConditionPlayerResidence extends Condition {
    private final int _id;
    private final Class<? extends Residence> _type;

    @SuppressWarnings("unchecked")
    public ConditionPlayerResidence(final int id, final String type) {
        _id = id;
        try {
            _type = (Class<? extends Residence>) Class.forName("org.mmocore.gameserver.model.entity.residence." + type);
        } catch (ClassNotFoundException e) {
            throw new Error(e);
        }
    }

    @Override
    public boolean testImpl(final Creature creature, final Optional<Creature> optionalTarget, final Optional<SkillEntry> skill,
                            final Optional<ItemInstance> item, final OptionalDouble initialValue) {
        final Player player = creature.getPlayer();

        if (player == null) {
            return false;
        }

        final Clan clan = player.getClan();

        if (clan == null) {
            return false;
        }

        final int residenceId = clan.getResidenceId(_type);

        return _id > 0 ? residenceId == _id : residenceId > 0;
    }
}
