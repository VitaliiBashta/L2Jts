package org.jts.dataparser.data.holder.transform;

import org.jts.dataparser.data.annotations.array.DoubleArray;
import org.jts.dataparser.data.annotations.array.IntArray;
import org.jts.dataparser.data.annotations.array.ObjectArray;
import org.jts.dataparser.data.annotations.array.StringArray;
import org.jts.dataparser.data.annotations.value.EnumValue;
import org.jts.dataparser.data.annotations.value.IntValue;
import org.jts.dataparser.data.holder.transform.type.TBaseAttackType;

import java.util.Optional;

/**
 * Create by Mangol on 02.10.2015.
 */
public class TCommon {
    @DoubleArray
    public double[] collision_box; // Всегда имеет значние (Not null),Колизии
    @DoubleArray
    public double[] moving_speed;
    // 0-Скорость ходьбы,
    // 1-Скорость бега,
    // 2-Скорость ходьбы под водой,
    // 3-Скорость бега под водой
    // 4-Скорость ходьбы в полёте,
    // 5-Скорость бега в полёте
    @StringArray
    public String[] skill;
    @ObjectArray
    public TAdditionalSkill[] additional_skill = new TAdditionalSkill[0];
    @IntArray
    public int[] action; // Список социальных действий которые будут
    // видны/можно использовать
    @EnumValue //TODO: сделать
    public TBaseAttackType base_attack_type;
    @IntValue
    public int base_attack_range; // Базовый радиус атаки
    @IntValue
    public int base_random_damage; // Базовый рандомный урон
    @IntValue
    public int base_attack_speed; // Базовая скорость атаки
    @IntValue
    public int base_critical_prob; // Базовый физ. шанс крита
    @IntValue
    public int base_physical_attack; // Базовая физ. атака
    @IntValue
    public int base_magical_attack; // Базовая маг. атака
    public Optional<TItemCheck> item_check = Optional.empty();

    @Override
    public TCommon clone() {
        final TCommon common = new TCommon();
        common.collision_box = collision_box.clone();
        common.moving_speed = moving_speed.clone();
        common.skill = skill.clone();
        common.additional_skill = additional_skill.clone();
        common.action = action.clone();
        common.base_attack_type = base_attack_type;
        common.base_attack_range = base_attack_range;
        common.base_random_damage = base_random_damage;
        common.base_attack_speed = base_attack_speed;
        common.base_critical_prob = base_critical_prob;
        common.base_physical_attack = base_physical_attack;
        common.base_magical_attack = base_magical_attack;
        common.item_check = item_check;
        return common;
    }
}
