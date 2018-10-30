package org.jts.dataparser.data.parser;

import org.jts.dataparser.data.common.AbstractDataParser;
import org.jts.dataparser.data.holder.FreewayInfoHolder;

/**
 * @author : Camelion
 * @date : 27.08.12 13:14
 */
public class FreewayInfoParser extends AbstractDataParser<FreewayInfoHolder> {
    private static FreewayInfoParser ourInstance = new FreewayInfoParser();

    private FreewayInfoParser() {
        super(FreewayInfoHolder.getInstance());
    }

    public static FreewayInfoParser getInstance() {
        return ourInstance;
    }

    @Override
    protected String getFileName() {
        return "data/pts_scripts/freewayinfo.txt";
    }
}