package org.mmocore.gameserver.model.quest.startcondition.impl;

import org.mmocore.gameserver.model.quest.startcondition.ConditionList;
import org.mmocore.gameserver.model.quest.startcondition.ICheckStartCondition;
import org.mmocore.gameserver.object.Player;

public final class ClassLevelCondition implements ICheckStartCondition {
    private int classLevels;

    public ClassLevelCondition(final int classLevels) {
        this.classLevels = classLevels;
    }

    @Override
    public final ConditionList checkCondition(final Player player) {
        return player.getPlayerClassComponent().getClassId().getLevel().ordinal() >= classLevels ? ConditionList.NONE : ConditionList.CLASS_LEVEL;
    }
}