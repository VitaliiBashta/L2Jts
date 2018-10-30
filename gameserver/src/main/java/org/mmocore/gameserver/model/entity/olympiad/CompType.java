package org.mmocore.gameserver.model.entity.olympiad;

import org.mmocore.gameserver.configuration.config.OlympiadConfig;

public enum CompType {
    TEAM(2, OlympiadConfig.ALT_OLY_TEAM_RITEM_C, 5, false),
    NON_CLASSED(2, OlympiadConfig.ALT_OLY_NONCLASSED_RITEM_C, 5, true),
    CLASSED(2, OlympiadConfig.ALT_OLY_CLASSED_RITEM_C, 5, true);

    private final int _minSize;
    private final int _reward;
    private final int _looseMult;
    private final boolean _hasBuffer;

    CompType(final int minSize, final int reward, final int looseMult, final boolean hasBuffer) {
        _minSize = minSize;
        _reward = reward;
        _looseMult = looseMult;
        _hasBuffer = hasBuffer;
    }

    public int getMinSize() {
        return _minSize;
    }

    public int getReward() {
        return _reward;
    }

    public int getLooseMult() {
        return _looseMult;
    }

    public boolean hasBuffer() {
        return _hasBuffer;
    }
}