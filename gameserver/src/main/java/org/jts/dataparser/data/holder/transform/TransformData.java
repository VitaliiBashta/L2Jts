package org.jts.dataparser.data.holder.transform;

import org.jts.dataparser.data.annotations.Element;
import org.jts.dataparser.data.annotations.value.EnumValue;
import org.jts.dataparser.data.annotations.value.IntValue;
import org.jts.dataparser.data.holder.transform.type.TransformType;

import java.util.List;

/**
 * @author : Mangol
 */
public class TransformData {
    @IntValue
    public int id; // ID трансформы
    @EnumValue
    public TransformType type; // Тип трансформы
    @IntValue
    public int can_swim; // Может ли находится в воде
    @IntValue
    public int spawn_height; //Поднимаем на позицию выше.
    @IntValue
    public int normal_attackable; // 1 означает что игрок приобретает
    // неуязвимость
    @Element(start = "female_begin", end = "female_end")
    public List<TOptionsSex> female_begin;
    @Element(start = "male_begin", end = "male_end")
    public List<TOptionsSex> male_begin;
}
