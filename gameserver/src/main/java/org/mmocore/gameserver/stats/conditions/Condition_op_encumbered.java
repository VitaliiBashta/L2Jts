package org.mmocore.gameserver.stats.conditions;

import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.skills.SkillEntry;

import java.util.Optional;
import java.util.OptionalDouble;

/**
 * @author : Mangol
 * @date : 25.03.14 21:34
 */
public class Condition_op_encumbered extends Condition {
    private final int _size;
    private final int _weight;

    public Condition_op_encumbered(final int valS, final int valW) {
        _size = valS;
        _weight = valW;
    }

    @Override
    public boolean testImpl(final Creature creature, final Optional<Creature> optionalTarget, final Optional<SkillEntry> skill, final Optional<ItemInstance> item, final OptionalDouble initialValue) {
        final Player player = creature.getPlayer();
        if (player == null) {
            return false;
        }
        final boolean size = player.getInventory().getSize() <= player.getInventoryLimit() - _size;
        final double maxLoad = player.getMaxLoad() / 100 * _weight;
        final boolean weight = maxLoad + player.getCurrentLoad() < player.getMaxLoad();
        if (!size) {
            player.sendPacket(SystemMsg.YOUR_INVENTORY_IS_FULL);
            return false;
        }
        if (!weight) {
            player.sendPacket(SystemMsg.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
            return false;
        }
        return true;
    }
}
