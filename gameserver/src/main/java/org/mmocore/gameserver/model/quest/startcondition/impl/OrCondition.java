package org.mmocore.gameserver.model.quest.startcondition.impl;

import org.mmocore.gameserver.model.quest.startcondition.ConditionList;
import org.mmocore.gameserver.model.quest.startcondition.ICheckStartCondition;
import org.mmocore.gameserver.object.Player;

/**
 * Created by Hack
 * Date: 22.06.2016 22:23
 */
public class OrCondition implements ICheckStartCondition {
    ICheckStartCondition[] conditions;

    public OrCondition(ICheckStartCondition... conds) {
        conditions = conds;
    }

    @Override
    public ConditionList checkCondition(Player player) {
        for (ICheckStartCondition cond : conditions) {
            if (cond.checkCondition(player) == ConditionList.NONE) {
                return ConditionList.NONE;
            }
        }
        return ConditionList.OR;
    }
}
