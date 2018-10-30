package org.jts.dataparser.data.holder.areadata.area;

import org.jts.dataparser.data.annotations.class_annotations.ParseSuper;
import org.jts.dataparser.data.annotations.value.DoubleValue;
import org.jts.dataparser.data.annotations.value.ObjectValue;

/**
 * @author : Camelion
 * @date : 24.08.12  23:43
 * <p/>
 * Водные области
 */
@ParseSuper
public class WaterArea extends DefaultArea {
    @ObjectValue(canBeNull = false)
    private WaterRange water_range;

    public WaterArea() {
    }

    public WaterArea(DefaultArea setting) {
        super(setting);
    }

    public static class WaterRange {
        // Min range
        @DoubleValue(withoutName = true)
        private double min_x;
        @DoubleValue(withoutName = true)
        private double min_y;
        @DoubleValue(withoutName = true)
        private double min_z;

        // Max range
        @DoubleValue(withoutName = true)
        private double max_x;
        @DoubleValue(withoutName = true)
        private double max_y;
        @DoubleValue(withoutName = true)
        private double max_z;
    }
}
