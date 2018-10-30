package org.jts.dataparser.data.holder.expdata;

import org.jts.dataparser.data.annotations.value.IntValue;
import org.jts.dataparser.data.annotations.value.LongValue;

/**
 * @author KilRoy
 */
public class ExpDataTable {
    @IntValue
    private int level;
    @LongValue
    private long exp;

    public int getLevel() {
        return level;
    }

    public long getExp() {
        return exp;
    }
}