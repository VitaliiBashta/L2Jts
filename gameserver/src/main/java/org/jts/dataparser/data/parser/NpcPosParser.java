package org.jts.dataparser.data.parser;

import org.jts.dataparser.data.common.AbstractDataParser;
import org.jts.dataparser.data.holder.NpcPosHolder;

/**
 * @author : Camelion
 * @date : 30.08.12 20:10
 */
public class NpcPosParser extends AbstractDataParser<NpcPosHolder> {
    private static NpcPosParser ourInstance = new NpcPosParser();

    private NpcPosParser() {
        super(NpcPosHolder.getInstance());
    }

    public static NpcPosParser getInstance() {
        return ourInstance;
    }

    @Override
    protected String getFileName() {
        return "data/pts_scripts/npcpos.txt";
    }
}