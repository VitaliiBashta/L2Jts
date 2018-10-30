package org.jts.dataparser.data.holder.monrace;

import org.jts.dataparser.data.annotations.array.ObjectArray;
import org.jts.dataparser.data.annotations.value.IntValue;
import org.jts.dataparser.data.annotations.value.ObjectValue;
import org.jts.dataparser.data.annotations.value.StringValue;
import org.jts.dataparser.data.common.Point3;

/**
 * @author : Camelion
 * @date : 30.08.12 14:05
 */
public class MonRace {
    @IntValue
    public int return_rate;
    @IntValue
    public int residenceid;
    @StringValue
    public String begin_music;
    @StringValue
    public String begin_sound;
    @ObjectArray(name = "race_area")
    public MonArea[] race_areas;

    public static class MonArea {
        @IntValue(withoutName = true)
        public int unknown;
        @ObjectValue(withoutName = true)
        public Point3 point1;
        @ObjectValue(withoutName = true)
        public Point3 point2;
        @ObjectValue(withoutName = true)
        public Point3 point3;
        @ObjectValue(withoutName = true)
        public Point3 point4;
    }
}
