package org.jts.dataparser.data.holder.areadata.area;

import org.jts.dataparser.data.annotations.array.ObjectArray;
import org.jts.dataparser.data.annotations.class_annotations.ParseSuper;
import org.jts.dataparser.data.common.Point3;

/**
 * @author : Camelion
 * @date : 25.08.12  14:19
 */
@ParseSuper
public class TeleportZone extends DefaultArea {
    // Обязательно должно присутствовать для всех зон телепорта
    @ObjectArray(canBeNull = false)
    private Point3[] teleport_points; // Точки телепорта (если больше одной, то выбирается случайным образом)

    public TeleportZone(DefaultArea defaultSetting) {
        super(defaultSetting);
        teleport_points = ((TeleportZone) defaultSetting).teleport_points;
    }

    public TeleportZone() {

    }
}
