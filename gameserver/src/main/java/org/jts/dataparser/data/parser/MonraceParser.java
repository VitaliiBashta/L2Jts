package org.jts.dataparser.data.parser;

import org.jts.dataparser.data.common.AbstractDataParser;
import org.jts.dataparser.data.holder.MonraceHolder;

/**
 * @author : Camelion
 * @date : 30.08.12 14:03
 */
public class MonraceParser extends AbstractDataParser<MonraceHolder> {
    private static MonraceParser ourInstance = new MonraceParser();

    private MonraceParser() {
        super(MonraceHolder.getInstance());
    }

    public static MonraceParser getInstance() {
        return ourInstance;
    }

    @Override
    protected String getFileName() {
        return "data/pts_scripts/monrace.txt";
    }
}