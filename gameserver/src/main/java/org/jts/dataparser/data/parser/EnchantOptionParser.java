package org.jts.dataparser.data.parser;

import org.jts.dataparser.data.common.AbstractDataParser;
import org.jts.dataparser.data.holder.EnchantOptionHolder;

/**
 * @author : Camelion
 * @date : 27.08.12 2:01
 */
public class EnchantOptionParser extends AbstractDataParser<EnchantOptionHolder> {
    private static EnchantOptionParser ourInstance = new EnchantOptionParser();

    private EnchantOptionParser() {
        super(EnchantOptionHolder.getInstance());
    }

    public static EnchantOptionParser getInstance() {
        return ourInstance;
    }

    @Override
    protected String getFileName() {
        return "data/pts_scripts/enchantoption.txt";
    }
}