package org.jts.dataparser.data.holder.doordata;

import org.jts.dataparser.data.annotations.value.ObjectValue;
import org.jts.dataparser.data.annotations.value.StringValue;
import org.jts.dataparser.data.common.Point3;

/**
 * @author : Camelion
 * @date : 26.08.12 22:31
 */
public class DefaultSignBoard {
    @StringValue(withoutName = true)
    public String signBoardName; // Название, есть у всех
    @ObjectValue
    public Point3 pos; // Позиция, есть у всех
}
