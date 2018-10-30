package org.mmocore.gameserver.model.quest.startcondition.impl;

import org.mmocore.gameserver.model.quest.startcondition.ConditionList;
import org.mmocore.gameserver.model.quest.startcondition.ICheckStartCondition;
import org.mmocore.gameserver.object.Player;

public final class QuestCompletedCondition implements ICheckStartCondition {
    private final int questId;

    public QuestCompletedCondition(final int questId) {
        this.questId = questId;
    }

    @Override
    public final ConditionList checkCondition(final Player player) {
        if (player.isQuestCompleted(questId))
            return ConditionList.NONE;
        return ConditionList.QUEST;
    }
}