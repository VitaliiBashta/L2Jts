package org.jts.dataparser.data.parser;

import org.jts.dataparser.data.common.AbstractDataParser;
import org.jts.dataparser.data.holder.CastleDataHolder;

/**
 * @author : Camelion
 * @date : 25.08.12 22:54
 */
public class CastleDataParser extends AbstractDataParser<CastleDataHolder> {
    private static CastleDataParser ourInstance = new CastleDataParser();

    private CastleDataParser() {
        super(CastleDataHolder.getInstance());
    }

    public static CastleDataParser getInstance() {
        return ourInstance;
    }

    @Override
    protected String getFileName() {
        return "data/pts_scripts/castledata.txt";
    }
}