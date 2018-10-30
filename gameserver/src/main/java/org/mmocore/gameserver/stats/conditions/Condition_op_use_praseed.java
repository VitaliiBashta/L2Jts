package org.mmocore.gameserver.stats.conditions;

import org.jts.dataparser.data.holder.cubicdata.Agathion;
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
 * Create by Mangol on 25.09.2015.
 */
public class Condition_op_use_praseed extends Condition {
    private final int praseed;

    public Condition_op_use_praseed(final int praseed) {
        this.praseed = praseed;
    }

    @Override
    protected boolean testImpl(final Creature creature, final Optional<Creature> optionalTarget, final Optional<SkillEntry> skill, final Optional<ItemInstance> item, final OptionalDouble initialValue) {
        final Player player = creature.getPlayer();
        if (player.getAgathion() == null) {
            player.sendPacket(new SystemMessage(SystemMsg.YOUR_ENERGY_CANNOT_BE_REPLENISHED_BECAUSE_CONDITIONS_ARE_NOT_MET));
            return false;
        }
        final ItemInstance itemInstance = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LBRACELET);
        final Agathion agathion = player.getAgathion().getTemplate();
        final int item_id = agathion.item_ids.length > 0 ? agathion.item_ids[0] : 0;
        if (itemInstance != null && itemInstance.getTemplate().getItemId() != item_id) {
            player.sendPacket(new SystemMessage(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(skill.get()));
            return false;
        }
        if (itemInstance != null && itemInstance.getAgathionEnergy() + praseed > itemInstance.getAgathionMaxEnergy()) {
            player.sendPacket(new SystemMessage(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(skill.get()));
            return false;
        }
        return itemInstance != null && itemInstance.getAgathionEnergy() + praseed < itemInstance.getAgathionMaxEnergy();
    }
}
