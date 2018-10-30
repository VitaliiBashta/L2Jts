package org.jts.dataparser.data.holder.skilldata.skillacquire;

import org.jts.dataparser.data.annotations.array.IntArray;
import org.jts.dataparser.data.annotations.array.ObjectArray;
import org.jts.dataparser.data.annotations.array.StringArray;
import org.jts.dataparser.data.annotations.value.IntValue;
import org.jts.dataparser.data.annotations.value.ObjectValue;
import org.jts.dataparser.data.annotations.value.StringValue;

/**
 * @author KilRoy
 */
public class SkillAcquireInfo {
    @StringValue
    private String skill_name;
    @IntValue
    private int get_lv;
    @IntValue
    private int lv_up_sp;
    @StringValue
    private boolean auto_get;
    @ObjectValue
    private AcquireItemNeeded item_needed;
    @IntArray
    private int[] quest_needed;
    @IntValue
    private int social_class;
    @StringValue
    private String residence_skill;
    @StringValue
    private String pledge_type;
    @ObjectArray
    private AcquireRace[] race = new AcquireRace[0];
    @StringArray
    private String[] prerequisite_skill;

    public String getSkillName() {
        return skill_name;
    }

    public int getNeedLevel() {
        return get_lv;
    }

    public int getLvlUpSP() {
        return lv_up_sp;
    }

    public boolean isAutoGet() {
        return auto_get;
    }

    public AcquireItemNeeded getItemNeeded() {
        return item_needed;
    }

    public int[] getQuestNeeded() {
        return quest_needed;
    }

    public int getSocialClass() {
        return social_class;
    }

    public String getResidenceSkill() {
        return residence_skill;
    }

    public String getPledgeType() {
        return pledge_type;
    }

    public AcquireRace[] getRace() {
        return race;
    }

    public String[] getPrerequisiteSkill() {
        return prerequisite_skill;
    }
}