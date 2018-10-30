package org.jts.dataparser.data.parser;

import org.jts.dataparser.data.common.AbstractDataParser;
import org.jts.dataparser.data.holder.ManorDataHolder;

/**
 * @author : Camelion
 * @date : 30.08.12 13:13
 */
public class ManorDataParser extends AbstractDataParser<ManorDataHolder> {
    private static final ManorDataParser ourInstance = new ManorDataParser();

    private ManorDataParser() {
        super(ManorDataHolder.getInstance());
    }

    public static ManorDataParser getInstance() {
        return ourInstance;
    }

    @Override
    protected String getFileName() {
        return "data/pts_scripts/manordata.txt";
    }
}