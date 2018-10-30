package org.jts.dataparser.data.parser;

import org.jts.dataparser.data.common.AbstractDataParser;
import org.jts.dataparser.data.holder.CubicDataHolder;

/**
 * @author : Camelion
 * @date : 26.08.12 13:13
 */
public class CubicDataParser extends AbstractDataParser<CubicDataHolder> {
    private static CubicDataParser ourInstance = new CubicDataParser();

    private CubicDataParser() {
        super(CubicDataHolder.getInstance());
    }

    public static CubicDataParser getInstance() {
        return ourInstance;
    }

    @Override
    protected String getFileName() {
        return "data/pts_scripts/cubicdata.txt";
    }
}