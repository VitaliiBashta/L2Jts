package org.jts.dataparser.data.holder.npcpos;

import org.jts.dataparser.data.annotations.array.ObjectArray;
import org.jts.dataparser.data.annotations.value.IntValue;
import org.jts.dataparser.data.annotations.value.StringValue;
import org.jts.dataparser.data.common.Point4;

/**
 * @author : Camelion
 * @date : 30.08.12 20:12
 */
public class Domain {
    @StringValue(withoutName = true)
    public String name;
    @IntValue
    public int domain_id;
    @ObjectArray(withoutName = true)
    public Point4[] territory;
}
