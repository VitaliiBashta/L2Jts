package org.jts.dataparser.data.holder.manordata;

import org.jts.dataparser.data.annotations.array.ObjectArray;
import org.jts.dataparser.data.annotations.value.StringValue;

/**
 * @author KilRoy
 */
public class ProcureInfo {
    @StringValue(withoutName = true, withoutBounds = true)
    public String procure_name;
    @ObjectArray(withoutName = true)
    public RewardInfo[] procure_reward;
}