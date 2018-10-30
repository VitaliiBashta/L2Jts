package org.jts.dataparser.data.holder.doordata;

import org.jts.dataparser.data.annotations.array.StringArray;
import org.jts.dataparser.data.annotations.value.EnumValue;
import org.jts.dataparser.data.annotations.value.IntValue;
import org.jts.dataparser.data.annotations.value.ObjectValue;
import org.jts.dataparser.data.annotations.value.StringValue;
import org.jts.dataparser.data.common.Point3;

/**
 * @author : Camelion
 * @date : 27.08.12 1:39
 */
public class ControlTower {
    @StringValue(withoutName = true)
    public String name;
    @EnumValue
    public ControlTowerType controltower_type; // Тип контрольной башни
    @EnumValue
    public ControlTowerToggle toggle; // Неизвестно
    @ObjectValue
    public Point3 pos; // позиция башни
    @IntValue
    public int hp; // HP башни
    @IntValue
    public int physical_defence; // P.Def башни
    @IntValue
    public int magical_defence; // M.Def башни
    @StringValue
    public String display_npc_working; // NPC, который отображается на месте
    // работающей башни
    @StringValue
    public String display_npc_not_working; // NPC, который отображается на месте
    // не работающей башни
    @StringArray
    public String[] control_area; // Список каких-то зон, отсутвует для башен
    // типа life_control

    public static enum ControlTowerType {
        support_control,
        life_control,
        trap_control
    }

    public static enum ControlTowerToggle {
        flase
    }
}
