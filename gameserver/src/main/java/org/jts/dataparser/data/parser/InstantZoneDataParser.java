package org.jts.dataparser.data.parser;

import org.jts.dataparser.data.common.AbstractDataParser;
import org.jts.dataparser.data.holder.InstantZoneDataHolder;

/**
 * @author : Camelion
 * @date : 27.08.12 13:54
 */
public class InstantZoneDataParser extends AbstractDataParser<InstantZoneDataHolder> {
    private static InstantZoneDataParser ourInstance = new InstantZoneDataParser();

    private InstantZoneDataParser() {
        super(InstantZoneDataHolder.getInstance());
    }

    public static InstantZoneDataParser getInstance() {
        return ourInstance;
    }

    @Override
    protected String getFileName() {
        return "data/pts_scripts/instantzonedata.txt";
    }
}