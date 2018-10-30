package org.jts.dataparser.data.common;

import org.jts.dataparser.data.annotations.value.LongValue;
import org.jts.dataparser.data.annotations.value.StringValue;

public class ItemName_Count {
    @StringValue(withoutName = true)
    public String itemName; // Название предмета
    @LongValue(withoutName = true)
    public long count; // Количество
}