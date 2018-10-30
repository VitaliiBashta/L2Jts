package org.jts.dataparser.data.holder.fishingdata;

import org.jts.dataparser.data.annotations.array.ObjectArray;
import org.jts.dataparser.data.annotations.value.EnumValue;
import org.jts.dataparser.data.annotations.value.IntValue;

/**
 * @author : Camelion
 * @date : 27.08.12 2:54
 */
public class FishingDistribution {
    @IntValue
    public int distribution_id;
    @ObjectArray
    public Distribution[] default_distribution;
    @ObjectArray
    public Distribution[] night_fishing_distribution;

    public static enum FishGroup {
        easy_wide,
        easy_swift,
        easy_ugly,
        wide,
        swift,
        ugly,
        fish_box,
        hard_wide,
        hard_swift,
        hard_ugly,
        hs_fish
    }

    public static class Distribution {
        @EnumValue(withoutName = true)
        public FishGroup fish_group;
        @IntValue(withoutName = true)
        public int unknown;
    }
}
