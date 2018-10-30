package org.jts.dataparser.data.parser;

import org.jts.dataparser.data.common.AbstractDataParser;
import org.jts.dataparser.data.holder.SkillDataHolder;

/**
 * @author KilRoy
 */
public class SkillDataParser extends AbstractDataParser<SkillDataHolder> {
    private static final SkillDataParser ourInstance = new SkillDataParser();

    private SkillDataParser() {
        super(SkillDataHolder.getInstance());
    }

    public static SkillDataParser getInstance() {
        return ourInstance;
    }

    @Override
    protected String getFileName() {
        return "data/pts_scripts/skilldata.txt";
    }
}