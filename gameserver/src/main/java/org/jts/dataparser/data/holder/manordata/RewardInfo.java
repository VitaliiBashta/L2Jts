package org.jts.dataparser.data.holder.manordata;

import org.jts.dataparser.data.annotations.value.IntValue;
import org.jts.dataparser.data.annotations.value.StringValue;

/**
 * @author KilRoy
 */
public class RewardInfo {
    @IntValue(withoutName = true)
    public int reward_id;
    @StringValue(withoutName = true, withoutBounds = true)
    public String reward_item_name;
}