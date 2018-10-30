package org.jts.dataparser.data.holder.fishingdata;

import org.jts.dataparser.data.annotations.array.ObjectArray;
import org.jts.dataparser.data.annotations.value.DoubleValue;
import org.jts.dataparser.data.annotations.value.EnumValue;
import org.jts.dataparser.data.annotations.value.IntValue;

/**
 * @author : Camelion
 * @date : 27.08.12 3:09
 */
public class Lure {
    @IntValue
    public int lure_id;
    @IntValue
    public int lure_item_id;
    @DoubleValue
    public double revision_number;
    @IntValue
    public int length_bonus;
    @DoubleValue
    public double length_rate_bonus;
    @EnumValue
    public LureType lure_type;
    @ObjectArray
    public FishingDistribution.Distribution[] fish_group_preference;

    public static enum LureType {
        normal_lure,
        night_lure
    }
}
