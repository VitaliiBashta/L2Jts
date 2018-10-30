package org.jts.dataparser.data.holder;

import org.jts.dataparser.data.annotations.Element;
import org.jts.dataparser.data.holder.skilldata.SkillData;
import org.mmocore.commons.data.AbstractHolder;

import java.util.List;

/**
 * @author KilRoy
 */
public class SkillDataHolder extends AbstractHolder {
    private static SkillDataHolder ourInstance = new SkillDataHolder();
    @Element(start = "skill_begin", end = "skill_end")
    private List<SkillData> skill;

    private SkillDataHolder() {
    }

    public static SkillDataHolder getInstance() {
        return ourInstance;
    }

    @Override
    public int size() {
        return skill.size();
    }

    @Override
    public void clear() {
        skill.clear();
    }
}