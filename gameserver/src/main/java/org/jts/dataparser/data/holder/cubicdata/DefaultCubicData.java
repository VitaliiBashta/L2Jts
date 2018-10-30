package org.jts.dataparser.data.holder.cubicdata;

import org.jts.dataparser.data.annotations.value.DoubleValue;
import org.jts.dataparser.data.annotations.value.IntValue;
import org.mmocore.commons.lang.ArrayUtils;

/**
 * @author : Camelion
 * @date : 26.08.12 13:17
 * <p/>
 * Данные, которые присутствуют как для кубиков, так и для агатионов
 */
public class DefaultCubicData {
    @IntValue
    public int id; // ID кубика
    @IntValue
    public int level; // Уровень кубика (равен уровню скила, призывающего кубик)
    @IntValue
    public int slot; // какой-то слот, не может быть одновременно два помощника
    // с одинаковыими слотами.
    @IntValue
    public int duration = -1; // Продолжительность (по умолчанию -1, без
    // времени)
    @IntValue
    public int delay; // Какая-то задержка, возможно между действиями
    // кубика\агатиона
    @IntValue
    public int max_count = 0; // какое-то количество. Присутствует там, где
    // delay > 0
    @IntValue
    public int use_up; // Неизвестно, всегда 0
    @DoubleValue
    public double power; // Сила помощника
    // Поля, обрабатываемые CubicDataObjectFactory
    public CubicDataTargetType target_type; // Тип цели, если by_skill -
    // определяется скилом
    public CubicDataOpCond op_cond; // Какие-то условия
    public CubicDataSkill skill1; // Первый скилл
    public CubicDataSkill skill2; // Второй скилл
    public CubicDataSkill skill3; // Третий скилл

    public static enum TargetType {
        target,
        heal,
        master,
        by_skill // Не используется при установке target_type в самом скиле
    }

    public static class CubicDataTargetType {
        public TargetType type; // target - кубик действует по цели владельца,
        // heal - лечит самона/владельца, by_skill - в
        // зависимости от скила
        // Только для type = heal, указывает, какой скил будет использоваться
        // Например в массиве (90;60;30;0). 100-90 - не используется, 90-60 -
        // первый скилл, 60-30 - второй скил, 0-30 - третий скил
        public int[] heal_params;

        public CubicDataTargetType(TargetType type) {
            this.type = type;
            heal_params = ArrayUtils.EMPTY_INT_ARRAY;
        }

        // Конструктор для кубиков типа heal
        public CubicDataTargetType(TargetType type, int[] heal_params) {
            this.type = type;
            this.heal_params = heal_params;
        }
    }

    public static class CubicDataOpCond {
        public boolean isDebuff;
        public int[] cond;

        // Конструктор для типа debuff
        public CubicDataOpCond() {
            isDebuff = true;
        }

        // Конструктор для типа {0;30%;1000}
        public CubicDataOpCond(int[] cond) {
            isDebuff = false;
            this.cond = cond;
        }
    }

    public static class CubicDataSkill {
        // Может отсутствовать
        public int skillChance; // Шанс того, что помощник будет использовать
        // именно этот скилл, отсутствует для скила типа
        // TargetType.heal
        // Параметры, присущие каждому скилу
        public String skill_name; // Название скила
        public int useChance; // Шанс использования этого скила, если прошел
        // skillChance
        public int targetStaticObject; // 1 = true, 0 = false
        // Может отсутствовать
        // Если CubicData.target_type = by_skill, то для определения цели
        // используются эти параметры
        public TargetType skill_target_type;
        public CubicDataOpCond skill_op_cond;
    }
}