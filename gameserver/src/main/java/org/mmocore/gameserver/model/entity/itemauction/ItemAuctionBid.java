package org.mmocore.gameserver.model.entity.itemauction;

import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.world.GameObjectsStorage;

/**
 * @author n0nam3
 */
public final class ItemAuctionBid {
    private final int _charId;
    private long _lastBid;

    public ItemAuctionBid(final int charId, final long lastBid) {
        _charId = charId;
        _lastBid = lastBid;
    }

    public final int getCharId() {
        return _charId;
    }

    public final long getLastBid() {
        return _lastBid;
    }

    final void setLastBid(final long lastBid) {
        _lastBid = lastBid;
    }

    final void cancelBid() {
        _lastBid = -1;
    }

    final boolean isCanceled() {
        return _lastBid == -1;
    }

    final Player getPlayer() {
        return GameObjectsStorage.getPlayer(_charId);
    }
}