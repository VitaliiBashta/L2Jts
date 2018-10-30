package org.jts.dataparser.data.holder.setting;

import org.jts.dataparser.data.annotations.Element;
import org.jts.dataparser.data.annotations.array.ObjectArray;
import org.jts.dataparser.data.annotations.array.StringArray;
import org.jts.dataparser.data.annotations.value.IntValue;
import org.jts.dataparser.data.common.Point3;
import org.jts.dataparser.data.common.Point4;

import java.util.List;

/**
 * @author : Camelion
 * @date : 23.08.12 0:07
 * <p/>
 * Содержит в себе информацию об олимпиадных аренах
 */
public class OlympiadArena {
    @Element(start = "arena_begin", end = "arena_end")
    public List<Arena> arenas; // Список олимпиадных арен

    public static class Arena {
        @IntValue
        public int id; // ID арены
        @ObjectArray(name = "range")
        public Point4[] ranges;// Список точек, составляющих область арены
        // Список точек, которыми окружена область.
        @ObjectArray(name = "point1")
        public Point3[] points1;// Список точек, телепорта первого игрока(ов)
        // (три координаты, возможно для боев 3x3)
        @ObjectArray(name = "point2")
        public Point3[] points2;// Список точек, телепорта первого игрока(ов)
        // (три координаты, возможно для боев 3x3)
        @StringArray(name = "olympiad_door", canBeNull = false)
        public String[] olympiad_doors;
    }
}
