package org.jts.dataparser.data.parser;

import org.jts.dataparser.data.common.AbstractDataParser;
import org.jts.dataparser.data.holder.VariationDataHolder;

/**
 * @author : Mangol
 */
public class VariationParser extends AbstractDataParser<VariationDataHolder> {
    private static VariationParser ourInstance = new VariationParser();

    private VariationParser() {
        super(VariationDataHolder.getInstance());
    }

    public static VariationParser getInstance() {
        return ourInstance;
    }

    @Override
    protected String getFileName() {
        return "data/pts_scripts/variationdata.txt";
    }
}
