package org.jts.dataparser.data.parser;

import org.jts.dataparser.data.common.AbstractDataParser;
import org.jts.dataparser.data.holder.CursedWeaponDataHolder;

/**
 * @author : Camelion
 * @date : 26.08.12 21:35
 */
public class CursedWeaponDataParser extends AbstractDataParser<CursedWeaponDataHolder> {
    private static CursedWeaponDataParser ourInstance = new CursedWeaponDataParser();

    private CursedWeaponDataParser() {
        super(CursedWeaponDataHolder.getInstance());
    }

    public static CursedWeaponDataParser getInstance() {
        return ourInstance;
    }

    @Override
    protected String getFileName() {
        return "data/pts_scripts/cursedweapondata.txt";
    }
}