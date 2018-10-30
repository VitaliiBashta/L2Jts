package org.jts.dataparser.data.holder;

import org.jts.dataparser.data.annotations.Element;
import org.jts.dataparser.data.holder.auctiondata.Auction;
import org.mmocore.commons.data.AbstractHolder;

import java.util.List;

/**
 * @author : Camelion
 * @date : 25.08.12 18:50
 */
public class AuctionDataHolder extends AbstractHolder {
    private static AuctionDataHolder ourInstance = new AuctionDataHolder();
    @Element(start = "auction_begin", end = "auction_end")
    private List<Auction> auctions;

    private AuctionDataHolder() {
    }

    public static AuctionDataHolder getInstance() {
        return ourInstance;
    }

    @Override
    public int size() {
        return auctions.size();
    }

    public List<Auction> getAuctions() {
        return auctions;
    }

    @Override
    public void clear() {
        // TODO Auto-generated method stub
    }
}