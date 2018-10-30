package org.jts.dataparser.data.holder.fishingdata;

import org.jts.dataparser.data.annotations.array.ObjectArray;
import org.jts.dataparser.data.annotations.value.EnumValue;
import org.jts.dataparser.data.annotations.value.IntValue;
import org.jts.dataparser.data.common.Point4;

/**
 * @author : Camelion
 * @date : 27.08.12 3:03
 */
public class FishingPlace {
    @IntValue
    public int fishing_place_id;
    @ObjectArray
    public Point4[] territory;
    @IntValue
    public int limit_grid;
    @IntValue
    public int distribution_id;
    @EnumValue
    public FishingPlaceType fishing_place_type;
    @IntValue
    public int maintain_distribution_time;

    public static enum FishingPlaceType {
        fishing_place_default,
        fishing_place_type1,
        fishing_place_type2
    }
}
