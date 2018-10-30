package org.jts.dataparser.data.holder.doordata;

import org.jts.dataparser.data.annotations.value.IntValue;
import org.jts.dataparser.data.annotations.value.ObjectValue;
import org.jts.dataparser.data.annotations.value.StringValue;
import org.jts.dataparser.data.common.Point3;

/**
 * @author : Camelion
 * @date : 27.08.12 0:57
 */
public class Chair {
    @StringValue(withoutName = true)
    public String name; // название
    @IntValue
    public int editor_id;
    @ObjectValue
    public Point3 pos; // Позиция
}
