package org.jts.dataparser.data.parser;

import org.jts.dataparser.data.common.AbstractDataParser;
import org.jts.dataparser.data.holder.FormationInfoHolder;

/**
 * @author : Camelion
 * @date : 27.08.12 13:04
 */
public class FormationInfoParser extends AbstractDataParser<FormationInfoHolder> {
    private static FormationInfoParser ourInstance = new FormationInfoParser();

    private FormationInfoParser() {
        super(FormationInfoHolder.getInstance());
    }

    public static FormationInfoParser getInstance() {
        return ourInstance;
    }

    @Override
    protected String getFileName() {
        return "data/pts_scripts/formationinfo.txt";
    }
}