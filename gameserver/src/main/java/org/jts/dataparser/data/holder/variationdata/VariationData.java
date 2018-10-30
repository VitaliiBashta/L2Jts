package org.jts.dataparser.data.holder.variationdata;

import org.jts.dataparser.data.annotations.array.ObjectArray;
import org.jts.dataparser.data.annotations.value.DoubleValue;
import org.jts.dataparser.data.annotations.value.EnumValue;
import org.jts.dataparser.data.annotations.value.StringValue;

/**
 * @author : Mangol
 */
public class VariationData {
    @EnumValue
    public WeaponType weapon_type;
    @StringValue
    public String mineral;
    @ObjectArray
    public VariationGroup[] variation1 = new VariationGroup[0];
    @ObjectArray
    public VariationGroup[] variation2 = new VariationGroup[0];

    public static enum WeaponType {
        warrior,
        mage;
    }

    public static class VariationGroup {
        @ObjectArray(withoutName = true)
        public VariationInfo[] option;
        @DoubleValue(withoutName = true)
        public double group_chance;
    }

    public static class VariationInfo {
        @StringValue(withoutName = true)
        public String option_name;
        @DoubleValue(withoutName = true)
        public double chance;
    }
}
