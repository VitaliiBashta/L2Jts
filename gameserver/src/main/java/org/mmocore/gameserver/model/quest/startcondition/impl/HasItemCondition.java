package org.mmocore.gameserver.model.quest.startcondition.impl;

import org.mmocore.gameserver.model.quest.startcondition.ConditionList;
import org.mmocore.gameserver.model.quest.startcondition.ICheckStartCondition;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;

/**
 * Created by Hack
 * Date: 22.06.2016 22:02
 */
public final class HasItemCondition implements ICheckStartCondition {
    final int[] itemIds;

    public HasItemCondition(int... ids) {
        itemIds = ids;
    }

    @Override
    public ConditionList checkCondition(Player player) {
        for (int id : itemIds) {
            ItemInstance item = player.getInventory().getItemByItemId(id);
            if (item == null || item.getCount() < 1)
                return ConditionList.ITEM;
        }
        return ConditionList.NONE;
    }
}
