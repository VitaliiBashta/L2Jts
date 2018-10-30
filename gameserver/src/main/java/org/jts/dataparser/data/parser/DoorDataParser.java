package org.jts.dataparser.data.parser;

import org.jts.dataparser.data.common.AbstractDataParser;
import org.jts.dataparser.data.holder.DoorDataHolder;

/**
 * @author : Camelion
 * @date : 26.08.12 22:29
 */
public class DoorDataParser extends AbstractDataParser<DoorDataHolder> {
    private static DoorDataParser ourInstance = new DoorDataParser();

    private DoorDataParser() {
        super(DoorDataHolder.getInstance());
    }

    public static DoorDataParser getInstance() {
        return ourInstance;
    }

    @Override
    protected String getFileName() {
        return "data/pts_scripts/doordata.txt";
    }
}