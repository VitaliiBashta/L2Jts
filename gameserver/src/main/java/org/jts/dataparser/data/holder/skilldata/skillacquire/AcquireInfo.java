package org.jts.dataparser.data.holder.skilldata.skillacquire;

import org.jts.dataparser.data.annotations.Element;
import org.jts.dataparser.data.annotations.value.StringValue;

import java.util.List;

/**
 * @author KilRoy
 */
public class AcquireInfo {
    @StringValue(withoutName = true)
    private String parrent_class;
    @Element(start = "skill_begin", end = "skill_end")
    private List<SkillAcquireInfo> skillAcquireInfo;

    public List<SkillAcquireInfo> getSkillAcquireInfo() {
        return skillAcquireInfo;
    }

    public SkillAcquireInfo getSkillInfo(final String skillName) {
        return skillAcquireInfo.stream().filter(s -> s.getSkillName() == skillName).findFirst().get();
    }
}