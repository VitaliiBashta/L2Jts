package org.jts.dataparser.data.parser;

import org.jts.dataparser.data.common.AbstractDataParser;
import org.jts.dataparser.data.holder.PetDataHolder;

/**
 * @author KilRoy
 */
public class PetDataParser extends AbstractDataParser<PetDataHolder> {
    private static PetDataParser INSTANCE = new PetDataParser();

    private PetDataParser() {
        super(PetDataHolder.getInstance());
    }

    public static PetDataParser getInstance() {
        return INSTANCE;
    }

    @Override
    protected String getFileName() {
        return "data/pts_scripts/petdata.txt";
    }
}