package org.jts.dataparser.data.parser;

import org.jts.dataparser.data.common.AbstractDataParser;
import org.jts.dataparser.data.holder.ExpDataHolder;

/**
 * @author KilRoy
 */
public class ExpDataParser extends AbstractDataParser<ExpDataHolder> {
    private static ExpDataParser ourInstance = new ExpDataParser();

    private ExpDataParser() {
        super(ExpDataHolder.getInstance());
    }

    public static ExpDataParser getInstance() {
        return ourInstance;
    }

    @Override
    protected String getFileName() {
        return "data/pts_scripts/expdata.txt";
    }
}