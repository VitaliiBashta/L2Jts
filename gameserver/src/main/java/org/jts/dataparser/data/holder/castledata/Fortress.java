package org.jts.dataparser.data.holder.castledata;

import org.jts.dataparser.data.annotations.array.ObjectArray;
import org.jts.dataparser.data.annotations.array.StringArray;
import org.jts.dataparser.data.annotations.class_annotations.ParseSuper;
import org.jts.dataparser.data.annotations.value.EnumValue;
import org.jts.dataparser.data.annotations.value.IntValue;
import org.jts.dataparser.data.annotations.value.ObjectValue;
import org.jts.dataparser.data.annotations.value.StringValue;
import org.jts.dataparser.data.common.Point3;

/**
 * @author : Camelion
 * @date : 26.08.12 0:41
 */
@ParseSuper
public class Fortress extends Residence {
    @EnumValue
    public Scale fortress_scale; // Размер форта
    @StringArray
    public String[] fortress_related_castle; // Замки, к которым относится форт
    @ObjectValue
    public FortFlagpole fortress_flagpole; // Точка, куда устанавливает флаг
    @StringValue
    public String fortress_flag; // Название итема - флага. (есть в
    // itemdata.txt)
    @ObjectArray(withoutName = true)
    public Point3[] flag_points; // Точки установки флага

    public static enum Scale {
        small,
        large
    }

    public static class FortFlagpole {
        @StringValue(withoutName = true)
        public String fort_flagpole; // всегда=flag_pole. если добавить имя
        // форта(без _fort), то получится имя
        // NPC, соответствующего форту
        @ObjectValue(withoutName = true)
        public Point3 point; // всегда=flag_pole. если добавить имя форта(без
        // _fort), то получится имя NPC,
        // соответствующего форту
        @IntValue(withoutName = true)
        public int id; // Какой-то идентификатор, скорее всего чтоб не могли
        // ставить флаги от других фортов.
    }
}
