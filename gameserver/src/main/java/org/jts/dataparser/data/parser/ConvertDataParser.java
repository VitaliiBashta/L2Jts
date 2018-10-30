package org.jts.dataparser.data.parser;

import org.jts.dataparser.data.common.AbstractDataParser;
import org.jts.dataparser.data.holder.ConvertDataHolder;

/**
 * @author : Camelion
 * @date : 25.08.12 22:47
 */
public class ConvertDataParser extends AbstractDataParser<ConvertDataHolder> {
    private static ConvertDataParser ourInstance = new ConvertDataParser();

    private ConvertDataParser() {
        super(ConvertDataHolder.getInstance());
    }

    public static ConvertDataParser getInstance() {
        return ourInstance;
    }

    @Override
    protected String getFileName() {
        return "data/pts_scripts/convertdata.txt";
    }
}