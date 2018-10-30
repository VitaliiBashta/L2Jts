package org.mmocore.gameserver.model.entity.events.objects;

import org.mmocore.gameserver.model.pledge.Clan;

import java.time.Instant;

/**
 * @author VISTALL
 * @date 2:23/16.06.2011
 */
public class AuctionSiegeClanObject extends SiegeClanObject {
    private long _bid;

    public AuctionSiegeClanObject(final String type, final Clan clan, final long param) {
        this(type, clan, param, Instant.now());
    }

    public AuctionSiegeClanObject(final String type, final Clan clan, final long param, final Instant date) {
        super(type, clan, param, date);
        _bid = param;
    }

    @Override
    public long getParam() {
        return _bid;
    }

    public void setParam(final long param) {
        _bid = param;
    }
}
