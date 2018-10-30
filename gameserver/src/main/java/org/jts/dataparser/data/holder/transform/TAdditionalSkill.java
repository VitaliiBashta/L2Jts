package org.jts.dataparser.data.holder.transform;

import org.jts.dataparser.data.annotations.array.StringArray;
import org.jts.dataparser.data.annotations.value.IntValue;

/**
 * Create by Mangol on 02.10.2015.
 */
public class TAdditionalSkill {
    @IntValue(withoutName = true)
    public int level;
    @StringArray(withoutName = true, bounds = {"", ""})
    public String[] skill_name;
}
