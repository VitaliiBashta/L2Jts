package org.jts.dataparser.data.holder.fishingdata;

import org.jts.dataparser.data.annotations.array.IntArray;
import org.jts.dataparser.data.annotations.value.DoubleValue;

/**
 * @author : Camelion
 * @date : 27.08.12 3:22
 */
public class FishingMonster {
    @IntArray
    public int[] user_level;
    @DoubleValue
    public double monster_probability;
    // Устанавливается через фабрику объектов
    public String[] fishingmonsters;
}
