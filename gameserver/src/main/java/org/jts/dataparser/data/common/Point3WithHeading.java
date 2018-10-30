package org.jts.dataparser.data.common;

import org.jts.dataparser.data.annotations.value.IntValue;

/**
 * @author : Camelion
 * @date : 26.08.12 0:20
 */
public class Point3WithHeading {
    @IntValue(withoutName = true)
    public int x;
    @IntValue(withoutName = true)
    public int y;
    @IntValue(withoutName = true)
    public int z;
    @IntValue(withoutName = true)
    public int heading;
}
