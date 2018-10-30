package org.jts.dataparser.data.parser;

import org.jts.dataparser.data.common.AbstractDataParser;
import org.jts.dataparser.data.holder.AreaDataHolder;

/**
 * @author : Camelion
 * @date : 24.08.12 23:23
 */
public class AreaDataParser extends AbstractDataParser<AreaDataHolder> {
    private static AreaDataParser ourInstance = new AreaDataParser();

    private AreaDataParser() {
        super(AreaDataHolder.getInstance());
    }

    public static AreaDataParser getInstance() {
        return ourInstance;
    }

    @Override
    protected String getFileName() {
        return "data/pts_scripts/areadata.txt";
    }
}