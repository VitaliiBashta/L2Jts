package org.jts.dataparser.data.holder.fishingdata;

import org.jts.dataparser.data.annotations.value.DoubleValue;
import org.jts.dataparser.data.annotations.value.EnumValue;
import org.jts.dataparser.data.annotations.value.IntValue;
import org.jts.dataparser.data.annotations.value.StringValue;

/**
 * @author : Camelion
 * @date : 27.08.12 3:13
 */
public class Fish {
    @IntValue
    public int fish_id;
    @IntValue
    public int item_id;
    @StringValue
    public String item_name;
    @EnumValue
    public FishingDistribution.FishGroup fish_group;
    @IntValue
    public int fish_level;
    @DoubleValue
    public double fish_bite_rate;
    @DoubleValue
    public double fish_guts;
    @IntValue
    public int fish_hp;
    @IntValue
    public int fish_max_length;
    @DoubleValue
    public double fish_length_rate;
    @DoubleValue
    public double hp_regen;
    @IntValue
    public int start_combat_time;
    @IntValue
    public int combat_duration;
    @IntValue
    public int guts_check_time;
    @DoubleValue
    public double guts_check_probability;
    @DoubleValue
    public double cheating_prob;
    @EnumValue
    public FishGrade fish_grade;

    public static enum FishGrade {
        fish_normal,
        fish_easy,
        fish_hard
    }
}
