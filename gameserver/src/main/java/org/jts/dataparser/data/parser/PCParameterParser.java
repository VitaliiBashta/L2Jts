package org.jts.dataparser.data.parser;

import org.jts.dataparser.data.common.AbstractDataParser;
import org.jts.dataparser.data.holder.PCParameterHolder;

public class PCParameterParser extends AbstractDataParser<PCParameterHolder> {
    private static final PCParameterParser ourInstance = new PCParameterParser();

    private PCParameterParser() {
        super(PCParameterHolder.getInstance());
    }

    public static PCParameterParser getInstance() {
        return ourInstance;
    }

    @Override
    protected String getFileName() {
        return "data/pts_scripts/pc_parameter.txt";
    }
}