package org.jts.dataparser.data.parser;

import org.jts.dataparser.data.common.AbstractDataParser;
import org.jts.dataparser.data.holder.AirshipHolder;

/**
 * @author : Camelion
 * @date : 24.08.12 12:23
 */
public class AirshipParser extends AbstractDataParser<AirshipHolder> {
    private static AirshipParser ourInstance = new AirshipParser();

    private AirshipParser() {
        super(AirshipHolder.getInstance());
    }

    public static AirshipParser getInstance() {
        return ourInstance;
    }

    @Override
    protected String getFileName() {
        return "data/pts_scripts/airship.txt";
    }
}