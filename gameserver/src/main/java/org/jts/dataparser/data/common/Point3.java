package org.jts.dataparser.data.common;

import org.jts.dataparser.data.annotations.value.IntValue;

public class Point3 {
    @IntValue(withoutName = true)
    public int x;
    @IntValue(withoutName = true)
    public int y;
    @IntValue(withoutName = true)
    public int z;
}