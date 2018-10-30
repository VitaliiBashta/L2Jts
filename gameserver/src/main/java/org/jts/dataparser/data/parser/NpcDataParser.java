package org.jts.dataparser.data.parser;

import org.jts.dataparser.data.common.AbstractDataParser;
import org.jts.dataparser.data.holder.NpcDataHolder;

/**
 * @author : Camelion
 * @date : 30.08.12 14:44
 */
public class NpcDataParser extends AbstractDataParser<NpcDataHolder> {
    private static NpcDataParser ourInstance = new NpcDataParser();

    private NpcDataParser() {
        super(NpcDataHolder.getInstance());
    }

    public static NpcDataParser getInstance() {
        return ourInstance;
    }

    @Override
    protected String getFileName() {
        return "data/pts_scripts/npcdata.txt";
    }
}