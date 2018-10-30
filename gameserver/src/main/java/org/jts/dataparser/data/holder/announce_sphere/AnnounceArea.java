package org.jts.dataparser.data.holder.announce_sphere;

import org.jts.dataparser.data.annotations.array.ObjectArray;
import org.jts.dataparser.data.annotations.value.StringValue;
import org.jts.dataparser.data.common.Point4;

/**
 * @author : Camelion
 * @date : 24.08.12 21:28
 */
public class AnnounceArea {
    @StringValue(withoutName = true)
    public String string_id; // Id, по которому можно найти строку. Передается в
    // функции АИ gg::ShowMsgInTerritory
    @ObjectArray(withoutName = true)
    public Point4[] area_range; // Точки, которыми ограничена область
}
