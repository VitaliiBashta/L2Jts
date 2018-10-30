package org.jts.dataparser.data.holder.airship;

import org.jts.dataparser.data.annotations.Element;
import org.jts.dataparser.data.annotations.array.IntArray;
import org.jts.dataparser.data.annotations.array.ObjectArray;
import org.jts.dataparser.data.annotations.factory.IObjectFactory;
import org.jts.dataparser.data.annotations.value.EnumValue;
import org.jts.dataparser.data.annotations.value.IntValue;
import org.jts.dataparser.data.annotations.value.ObjectValue;
import org.jts.dataparser.data.annotations.value.StringValue;
import org.jts.dataparser.data.common.ItemName_Count;
import org.jts.dataparser.data.common.Point3;

/**
 * @author : Camelion
 * @date : 24.08.12 18:37
 * <p/>
 * Недостаточно хорошо обработана переменная path, для её обработки нужен
 * какой-либо механизм условий для объектов
 */
public class AirShip {
    @EnumValue
    public AirshipType airship_type; // Тип корабля
    @ObjectArray(name = "collision", canBeNull = false)
    public AirshipCollision[] collisions; // Коллизии корабля.
    // Null для кораблей типа PLEDGE
    @ObjectArray(name = "path", objectFactory = AirshipPathObjectFactory.class)
    public AirshipPathPoint[] pathPoints; // Путь, по которому движется корабль.
    // AirshipPathPoint может принимать
    // значения MovePoint, AirportPoint,
    // TeleportPoint.
    // Null для кораблей типа PLEDGE
    @ObjectValue
    public AirshipStartPos start_pos; // Возможно, стартовая позиция корабля
    // Null для кораблей типа PLEDGE
    @EnumValue
    public AirshipMoveType return_to_start; // Каким образом корабль будет
    // возвращаться к стартовой позиции
    // (tel = teleport)
    // Null для кораблей типа REGULAR
    @Element(start = "steering_begin", end = "steering_end")
    public Steering steering;// Что-то связанное с водителем, для кораблей типа
    // Отсутствует у кораблей типа REGULAR
    @ObjectValue(canBeNull = true)
    public ItemName_Count buy_cost; // Стоимость покупки?
    // Отсутствует у кораблей типа REGULAR
    @ObjectValue(canBeNull = true)
    public ItemName_Count summon_cost; // Стоимость призыва?
    @IntValue
    private int id; // ID Корабля
    @IntArray
    private int[] speed; // Скорость корабля {1;2;3}
    // PLEDGE
    // Отсутствует у кораблей типа REGULAR
    @IntValue
    private int fuel_max; // Запас топлива для кораблей типа PLEDGE
    // Отсутствует у кораблей типа REGULAR
    @IntValue
    private int fuel_consume; // Запас топлива для кораблей типа PLEDGE

    public static enum AirshipMoveType {
        tel,
        move,
        airport
    }

    public static enum AirshipType {
        REGULAR,
        PLEDGE
    }

    public static class AirshipStartPos {
        @ObjectValue(withoutName = true, canBeNull = false)
        private Point3 point;
        @IntValue(withoutName = true)
        private int unknown;
    }

    public static class AirshipCollision { // Коллизии корабля
        @ObjectValue(withoutName = true, canBeNull = false)
        private Point3 point; // Точка, относительно координат корабля
        @IntValue(withoutName = true)
        private int radius; // Радиус в данной точке
        @IntValue(withoutName = true)
        private int height; // Высота в данной точке
    }

    public static class AirshipPathPoint {
    }

    public static class MovePoint extends AirshipPathPoint { // move
        @ObjectValue(withoutName = true, canBeNull = false)
        private Point3 point; // Точка, в которую необходимо двигаться
    }

    public static class AirportPoint extends AirshipPathPoint { // airport
        @IntValue(withoutName = true)
        private int airport_id;// id аэропорта
    }

    public static class TeleportPoint extends AirshipPathPoint { // tel
        @ObjectValue(withoutName = true, canBeNull = false)
        private Point3 point;// Точка, в которую необходимо телепортироваться
        @IntValue(withoutName = true)
        private int heading;// угол поворота
    }

    public static class AirshipPathObjectFactory implements IObjectFactory<AirshipPathPoint> {
        @Override
        public AirshipPathPoint createObjectFor(StringBuilder data) {
            String stringData = data.toString();
            if (stringData.startsWith("move"))
                return new MovePoint();
            else if (stringData.startsWith("airport"))
                return new AirportPoint();
            else if (stringData.startsWith("tel"))
                return new TeleportPoint();
            return new AirshipPathPoint();
        }

        @Override
        public void setFieldClass(Class<?> clazz) {
            // Ignored
        }
    }

    // Какие-то параметры управления
    public static class Steering {
        @ObjectValue(canBeNull = false)
        private Point3 steering_pos; // Какая то позиция (относительно корабля)
        @ObjectValue(canBeNull = false)
        private Point3 driver_pos; // Позиция водителя (относительно корабля)
        @StringValue
        private String steering_item; // Управляющий предмет
    }
}
