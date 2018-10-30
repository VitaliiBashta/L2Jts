package org.mmocore.gameserver.model.quest.startcondition;

import org.mmocore.gameserver.object.Player;

@FunctionalInterface
public interface ICheckStartCondition {
    ConditionList checkCondition(Player player);
}