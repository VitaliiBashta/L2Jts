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
 * Create by Mangol on 18.10.2015.
 */
public class Condition_can_untransform extends Condition {
    private final boolean _transform;

    public Condition_can_untransform(final boolean transform) {
        _transform = transform;
    }

    @Override
    protected boolean testImpl(Creature creature, Optional<Creature> optionalTarget, Optional<SkillEntry> skill, Optional<ItemInstance> item, OptionalDouble initialValue) {
        final Player player = creature.getPlayer();
        // Нельзя отменять летающую трансформу слишком высоко над землей
        if (player.isInFlyingTransform() && Math.abs(player.getZ() - player.getLoc().correctGeoZ().z) > 333) {
            player.sendPacket(new SystemMessage(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(skill.get()));
            return false;
        }
        return _transform;
    }
}
