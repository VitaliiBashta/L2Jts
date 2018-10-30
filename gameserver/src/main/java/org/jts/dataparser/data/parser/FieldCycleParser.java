package org.jts.dataparser.data.parser;

import org.jts.dataparser.data.common.AbstractDataParser;
import org.jts.dataparser.data.holder.FieldCycleHolder;

/**
 * @author : Camelion
 * @date : 27.08.12 2:19
 */
public class FieldCycleParser extends AbstractDataParser<FieldCycleHolder> {
    private static FieldCycleParser ourInstance = new FieldCycleParser();

    private FieldCycleParser() {
        super(FieldCycleHolder.getInstance());
    }

    public static FieldCycleParser getInstance() {
        return ourInstance;
    }

    @Override
    protected String getFileName() {
        return "data/pts_scripts/fieldcycle.txt";
    }
}