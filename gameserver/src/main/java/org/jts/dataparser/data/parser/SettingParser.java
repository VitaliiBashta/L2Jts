package org.jts.dataparser.data.parser;

import org.jts.dataparser.data.common.AbstractDataParser;
import org.jts.dataparser.data.holder.SettingHolder;

/**
 * @author : Camelion
 * @date : 22.08.12 1:34
 */
public class SettingParser extends AbstractDataParser<SettingHolder> {
    private static SettingParser ourInstance = new SettingParser();

    private SettingParser() {
        super(SettingHolder.getInstance());
    }

    public static SettingParser getInstance() {
        return ourInstance;
    }

    @Override
    protected String getFileName() {
        return "data/pts_scripts/setting.txt";
    }
}