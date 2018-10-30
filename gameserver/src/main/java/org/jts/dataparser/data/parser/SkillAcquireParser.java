package org.jts.dataparser.data.parser;

import org.jts.dataparser.data.common.AbstractDataParser;
import org.jts.dataparser.data.holder.SkillAcquireHolder;

/**
 * @author KilRoy
 */
public class SkillAcquireParser extends AbstractDataParser<SkillAcquireHolder> {
    private static SkillAcquireParser ourInstance = new SkillAcquireParser();

    protected SkillAcquireParser() {
        super(SkillAcquireHolder.getInstance());
    }

    public static SkillAcquireParser getInstance() {
        return ourInstance;
    }

    @Override
    protected String getFileName() {
        return "data/pts_scripts/skillacquire.txt";
    }
}