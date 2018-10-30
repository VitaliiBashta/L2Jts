package org.jts.dataparser.data.parser;

import org.jts.dataparser.data.common.AbstractDataParser;
import org.jts.dataparser.data.holder.CategoryDataHolder;

/**
 * @author : Camelion
 * @date : 24.08.12 21:39
 */
public class CategoryDataParser extends AbstractDataParser<CategoryDataHolder> {
    private static CategoryDataParser ourInstance = new CategoryDataParser();

    private CategoryDataParser() {
        super(CategoryDataHolder.getInstance());
    }

    public static CategoryDataParser getInstance() {
        return ourInstance;
    }

    @Override
    protected String getFileName() {
        return "data/pts_scripts/categorydata.txt";
    }
}