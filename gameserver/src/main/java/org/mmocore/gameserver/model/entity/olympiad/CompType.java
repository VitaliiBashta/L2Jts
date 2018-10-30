package org.mmocore.gameserver.model.entity.olympiad;

import org.mmocore.gameserver.configuration.config.OlympiadConfig;

public enum CompType {
    TEAM(OlympiadConfig.ALT_OLY_TEAM_RITEM_C, false),
    NON_CLASSED(OlympiadConfig.ALT_OLY_NONCLASSED_RITEM_C, true),
    CLASSED(OlympiadConfig.ALT_OLY_CLASSED_RITEM_C, true);

    private final int _minSize;
    private final int _reward;
    private final int _looseMult;
    private final boolean _hasBuffer;

    CompType(final int reward, final boolean hasBuffer) {
        _minSize = 2;
        _reward = reward;
        _looseMult = 5;
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