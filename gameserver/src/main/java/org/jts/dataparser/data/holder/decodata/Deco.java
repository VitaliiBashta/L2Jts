package org.jts.dataparser.data.holder.decodata;

import org.jts.dataparser.data.annotations.array.IntArray;
import org.jts.dataparser.data.annotations.value.IntValue;
import org.jts.dataparser.data.annotations.value.StringValue;

/**
 * @author : Camelion
 * @date : 26.08.12 21:45
 */
public class Deco {
    @IntValue
    public int id;
    @StringValue
    public String name; // Больше нет ни в каких файлах
    @IntValue
    public int type;
    @IntValue
    public int level;
    @IntValue
    public int depth;
    // Задается через DecoObjectFactory
    public DecoFunc func;
    public double func_param; // Может отсутствовать для func = none
    @IntArray(splitter = ":")
    public int[] cost;

    public static enum DecoFunc {
        none,
        hp_regen,
        mp_regen,
        exp_restore,
    }
}
