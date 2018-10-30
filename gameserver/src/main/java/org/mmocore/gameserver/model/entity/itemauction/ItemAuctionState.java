package org.mmocore.gameserver.model.entity.itemauction;

import org.mmocore.commons.lang.ArrayUtils;

public enum ItemAuctionState {
    CREATED,
    STARTED,
    FINISHED;

    public static ItemAuctionState stateForStateId(final int stateId) {
        return ArrayUtils.valid(values(), stateId);
    }
}