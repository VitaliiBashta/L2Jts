package org.jts.dataparser.data.holder.skilldata.skillacquire;

import org.jts.dataparser.data.annotations.value.LongValue;
import org.jts.dataparser.data.annotations.value.StringValue;

/**
 * @author KilRoy
 */
public class AcquireItemNeeded {
    @StringValue(withoutName = true)
    public String item_name;
    @LongValue(withoutName = true)
    public long count;
}