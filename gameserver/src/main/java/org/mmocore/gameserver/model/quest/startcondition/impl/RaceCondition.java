package org.mmocore.gameserver.model.quest.startcondition.impl;

import org.apache.commons.lang3.ArrayUtils;
import org.jts.dataparser.data.holder.setting.common.PlayerRace;
import org.mmocore.gameserver.model.quest.startcondition.ConditionList;
import org.mmocore.gameserver.model.quest.startcondition.ICheckStartCondition;
import org.mmocore.gameserver.object.Player;

/**
 * @author KilRoy
 */
public final class RaceCondition implements ICheckStartCondition {
    private final PlayerRace[] race;

    public RaceCondition(final PlayerRace... race) {
        this.race = race;
    }

    @Override
    public final ConditionList checkCondition(final Player player) {
        if (ArrayUtils.contains(race, player.getPlayerTemplateComponent().getPlayerRace()))
            return ConditionList.NONE;
        return ConditionList.RACE;
    }
}