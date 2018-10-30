package org.jts.dataparser.data.holder.fieldcycle;

import org.jts.dataparser.data.annotations.Element;
import org.jts.dataparser.data.annotations.array.IntArray;
import org.jts.dataparser.data.annotations.array.ObjectArray;
import org.jts.dataparser.data.annotations.array.StringArray;
import org.jts.dataparser.data.annotations.value.IntValue;
import org.jts.dataparser.data.annotations.value.ObjectValue;
import org.jts.dataparser.data.annotations.value.StringValue;
import org.jts.dataparser.data.common.Point3;
import org.jts.dataparser.data.common.Point4;

import java.util.List;

/**
 * @author : Camelion
 * @date : 27.08.12 2:20
 */
public class FieldCycle {
    @IntValue
    public int id; // Id цикла, по нему выполняется обращение к циклу
    @Element(start = "step_begin", end = "step_end")
    public List<Step> steps; // Стадии цикла

    public static class Step {
        @IntValue
        public int step;// номер стадии
        @IntValue
        public int step_point; // количество очков цикла, до перехода на
        // следующую стадию
        @IntValue
        public int lock_time; // Какое-то время
        @IntValue
        public int drop_time; // Неизвестно
        @IntValue
        public int interval_time; // Неизвестно
        @IntValue
        public int interval_point; // Неизвестно
        @StringArray
        public String[] open_door; // Список дверей, открывающихся на данной
        // стадии
        @StringArray
        public String[] area_on; // Список зон, включающихся на этой стадии
        @Element(start = "restart_begin", end = "restart_end")
        public StepRestart step_restart;
        // Следующие параметры могут отсутствовать
        @ObjectValue
        public Point3 map_point; // Точка карты
        @IntValue
        public int map_string; // Номер из SysteMessage.txt
        @ObjectValue
        public StepChangeTime step_change_time; // Время перехода на следующий
        // цикл
    }

    public static class StepChangeTime {
        @StringValue(withoutName = true)
        public String day_of_week; // День недели
        @IntArray(withoutName = true, splitter = ":")
        public int[] time; // время {часы:минуты}
    }

    public static class StepRestart {
        @ObjectArray
        public Point4[] range; // Какая-то область
        @ObjectValue
        public Point3 normal_point;
        @ObjectValue
        public Point3 chao_point;
    }
}
