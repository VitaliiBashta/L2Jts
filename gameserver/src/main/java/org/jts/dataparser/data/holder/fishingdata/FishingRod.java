package org.jts.dataparser.data.holder.fishingdata;

import org.jts.dataparser.data.annotations.value.DoubleValue;
import org.jts.dataparser.data.annotations.value.IntValue;
import org.jts.dataparser.data.annotations.value.StringValue;

/**
 * @author : Camelion
 * @date : 27.08.12 3:20
 */
public class FishingRod {
    @IntValue
    public int fishingrod_id;
    @IntValue
    public int fishingrod_item_id;
    @IntValue
    public int fishingrod_level;
    @StringValue
    public String fishingrod_name;
    @DoubleValue
    public double fishingrod_damage;
}
