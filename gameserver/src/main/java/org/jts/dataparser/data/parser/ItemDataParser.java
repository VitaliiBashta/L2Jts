package org.jts.dataparser.data.parser;

import org.jts.dataparser.data.common.AbstractDataParser;
import org.jts.dataparser.data.holder.ItemDataHolder;

/**
 * @author : Camelion
 * @date : 27.08.12 17:09
 */
public class ItemDataParser extends AbstractDataParser<ItemDataHolder> {
    private static final ItemDataParser ourInstance = new ItemDataParser();

    private ItemDataParser() {
        super(ItemDataHolder.getInstance());
    }

    public static ItemDataParser getInstance() {
        return ourInstance;
    }

    @Override
    protected String getFileName() {
        return "data/pts_scripts/itemdata.txt";
    }
}