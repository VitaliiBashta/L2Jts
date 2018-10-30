package org.jts.dataparser.data.parser;

import org.jts.dataparser.data.common.AbstractDataParser;
import org.jts.dataparser.data.holder.FishingDataHolder;

/**
 * @author : Camelion
 * @date : 27.08.12 2:50
 */
public class FishingDataParser extends AbstractDataParser<FishingDataHolder> {
    private static FishingDataParser ourInstance = new FishingDataParser();

    private FishingDataParser() {
        super(FishingDataHolder.getInstance());
    }

    public static FishingDataParser getInstance() {
        return ourInstance;
    }

    @Override
    protected String getFileName() {
        return "data/pts_scripts/fishingdata.txt";
    }
}