package org.jts.dataparser.data.parser;

import org.jts.dataparser.data.common.AbstractDataParser;
import org.jts.dataparser.data.holder.DyeDataHolder;

/**
 * @author : Camelion
 * @date : 27.08.12 1:36
 */
public class DyeDataParser extends AbstractDataParser<DyeDataHolder> {
    private static final DyeDataParser ourInstance = new DyeDataParser();

    private DyeDataParser() {
        super(DyeDataHolder.getInstance());
    }

    public static DyeDataParser getInstance() {
        return ourInstance;
    }

    @Override
    protected String getFileName() {
        return "data/pts_scripts/dyedata.txt";
    }
}