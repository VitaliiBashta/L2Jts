package org.mmocore.gameserver.stats.conditions;

import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Inventory;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.skills.SkillEntry;

import java.util.Optional;
import java.util.OptionalDouble;

/**
 * @author : Mangol
 * @date : 25.03.14  07:23
 */
public class Condition_op_agathion_energy extends Condition {
    private final int _val;

    public Condition_op_agathion_energy(final int val) {
        _val = val;
    }

    @Override
    public boolean testImpl(final Creature creature, final Optional<Creature> optionalTarget, final Optional<SkillEntry> skill, final Optional<ItemInstance> item, final OptionalDouble initialValue) {
        final Player player = creature.getPlayer();
        if (player == null) {
            return false;
        }
        final ItemInstance itemInstance = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LBRACELET);
        if (itemInstance != null && itemInstance.getAgathionEnergy() >= _val) {
            return true;
        } else {
            player.sendPacket(new SystemMessage(SystemMsg.THE_SKILL_HAS_BEEN_CANCELED_BECAUSE_YOU_HAVE_INSUFFICIENT_ENERGY));
            return false;
        }
    }
}
