package org.jts.dataparser.data.holder.npcpos;

import org.jts.dataparser.data.annotations.array.ObjectArray;
import org.jts.dataparser.data.annotations.value.StringValue;
import org.jts.dataparser.data.common.Point4;

/**
 * @author : Camelion
 * @date : 30.08.12 20:15
 */
public class PosTerritory {
    @StringValue(withoutName = true)
    private String name;
    @ObjectArray(withoutName = true)
    private Point4[] points;

    public String getPosTerritoryName() {
        return name;
    }

    public Point4[] getPoints() {
        return points;
    }
}