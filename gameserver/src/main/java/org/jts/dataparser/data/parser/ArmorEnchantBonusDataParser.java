package org.jts.dataparser.data.parser;

import org.jts.dataparser.data.common.AbstractDataParser;
import org.jts.dataparser.data.holder.ArmorEnchantBonusDataHolder;

/**
 * @author : Camelion
 * @date : 24.08.12 21:39
 */
public class ArmorEnchantBonusDataParser extends AbstractDataParser<ArmorEnchantBonusDataHolder> {
    private static ArmorEnchantBonusDataParser ourInstance = new ArmorEnchantBonusDataParser();

    private ArmorEnchantBonusDataParser() {
        super(ArmorEnchantBonusDataHolder.getInstance());
    }

    public static ArmorEnchantBonusDataParser getInstance() {
        return ourInstance;
    }

    @Override
    protected String getFileName() {
        return "data/pts_scripts/armorenchantbonusdata.txt";
    }
}