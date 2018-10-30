package org.mmocore.gameserver.model.quest.startcondition.impl;

import org.apache.commons.lang3.ArrayUtils;
import org.mmocore.gameserver.model.base.ClassId;
import org.mmocore.gameserver.model.quest.startcondition.ConditionList;
import org.mmocore.gameserver.model.quest.startcondition.ICheckStartCondition;
import org.mmocore.gameserver.object.Player;

/**
 * @author KilRoy
 */
public final class ClassIdCondition implements ICheckStartCondition {
    private ClassId[] classId;

    public ClassIdCondition(final ClassId... classId) {
        this.classId = classId;
    }

    @Override
    public final ConditionList checkCondition(final Player player) {
        if (ArrayUtils.contains(classId, player.getPlayerClassComponent().getClassId()))
            return ConditionList.NONE;
        return ConditionList.CLASS_ID;
    }
}