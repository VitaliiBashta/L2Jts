package org.mmocore.gameserver.stats.conditions;

import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.skills.SkillEntry;

import java.util.Optional;
import java.util.OptionalDouble;

/**
 * Create by Mangol on 25.09.2015.
 */
public class Condition_op_need_agathion extends Condition {
    private final boolean agathion;

    public Condition_op_need_agathion(final boolean agathion) {
        this.agathion = agathion;
    }

    @Override
    protected boolean testImpl(Creature creature, Optional<Creature> optionalTarget, Optional<SkillEntry> skill, Optional<ItemInstance> item, OptionalDouble initialValue) {
        final Player player = creature.getPlayer();
        if (player.getAgathion() == null) {
            player.sendPacket(new SystemMessage(SystemMsg.AGATHION_SKILLS_CAN_BE_USED_ONLY_WHEN_YOUR_AGATHION_IS_SUMMONED));
            return false;
        }
        return agathion;
    }
}
