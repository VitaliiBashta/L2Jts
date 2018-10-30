package org.mmocore.gameserver.stats.conditions;

import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Inventory;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.skills.SkillEntry;

import java.util.Optional;
import java.util.OptionalDouble;

public final class ConditionSlotItemId extends ConditionInventory {
    private final int _itemId;

    private final int _enchantLevel;

    public ConditionSlotItemId(final int slot, final int itemId, final int enchantLevel) {
        super(slot);
        _itemId = itemId;
        _enchantLevel = enchantLevel;
    }

    @Override
    public boolean testImpl(final Creature creature, final Optional<Creature> optionalTarget, final Optional<SkillEntry> skill,
                            final Optional<ItemInstance> item, final OptionalDouble initialValue) {
        final Player player = creature.getPlayer();

        if (player == null) {
            return false;
        }

        final Inventory inv = player.getInventory();
        final ItemInstance itemInstance = inv.getPaperdollItem(_slot);
        if (itemInstance == null) {
            return _itemId == 0;
        }

        return itemInstance.getItemId() == _itemId && itemInstance.getEnchantLevel() >= _enchantLevel;
    }
}
