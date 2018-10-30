package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.model.entity.itemauction.ItemAuction;
import org.mmocore.gameserver.model.entity.itemauction.ItemAuctionBid;
import org.mmocore.gameserver.model.entity.itemauction.ItemAuctionState;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

/**
 * @author n0nam3
 */
public class ExItemAuctionInfo extends GameServerPacket {
    private boolean refresh;
    private int timeRemaining;
    private ItemAuction currentAuction;
    private ItemAuction nextAuction;

    public ExItemAuctionInfo(final boolean refresh, final ItemAuction currentAuction, final ItemAuction nextAuction) {
        if (currentAuction == null) {
            throw new NullPointerException();
        }

        if (currentAuction.getAuctionState() != ItemAuctionState.STARTED) {
            timeRemaining = 0;
        } else {
            timeRemaining = (int) (currentAuction.getFinishingTimeRemaining() / 1000); // in seconds
        }

        this.refresh = refresh;
        this.currentAuction = currentAuction;
        this.nextAuction = nextAuction;
    }

    @Override
    protected void writeData() {
        writeC(refresh ? 0x00 : 0x01);
        writeD(currentAuction.getInstanceId());

        final ItemAuctionBid highestBid = currentAuction.getHighestBid();
        writeQ(highestBid != null ? highestBid.getLastBid() : currentAuction.getAuctionInitBid());

        writeD(timeRemaining);
        writeItemInfo(currentAuction.getAuctionItem());

        if (nextAuction != null) {
            writeQ(nextAuction.getAuctionInitBid());
            writeD((int) (nextAuction.getStartingTime() / 1000L)); // unix time in seconds
            writeItemInfo(nextAuction.getAuctionItem());
        }
    }
}