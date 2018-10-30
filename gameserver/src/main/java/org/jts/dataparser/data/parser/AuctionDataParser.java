package org.jts.dataparser.data.parser;

import org.jts.dataparser.data.common.AbstractDataParser;
import org.jts.dataparser.data.holder.AuctionDataHolder;

/**
 * @author : Camelion
 * @date : 25.08.12 18:50
 */
public class AuctionDataParser extends AbstractDataParser<AuctionDataHolder> {
    private static final AuctionDataParser ourInstance = new AuctionDataParser();

    private AuctionDataParser() {
        super(AuctionDataHolder.getInstance());
    }

    public static AuctionDataParser getInstance() {
        return ourInstance;
    }

    @Override
    protected String getFileName() {
        return "data/pts_scripts/auctiondata.txt";
    }
}