package org.mmocore.gameserver.templates.mapregion;


import org.jts.dataparser.data.holder.setting.common.PlayerRace;
import org.mmocore.gameserver.model.Territory;

import java.util.Map;

public class RestartArea implements RegionData {
    private final Territory _territory;
    private final Map<PlayerRace, RestartPoint> _restarts;

    public RestartArea(final Territory territory, final Map<PlayerRace, RestartPoint> restarts) {
        _territory = territory;
        _restarts = restarts;
    }

    @Override
    public Territory getTerritory() {
        return _territory;
    }

    public Map<PlayerRace, RestartPoint> getRestartPoint() {
        return _restarts;
    }
}
