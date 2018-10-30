package org.jts.dataparser.data.holder.areadata.area;

import org.jts.dataparser.data.annotations.class_annotations.ParseSuper;
import org.jts.dataparser.data.annotations.value.EnumValue;
import org.jts.dataparser.data.annotations.value.IntValue;

/**
 * @author : Camelion
 * @date : 25.08.12  0:22
 */
@ParseSuper
public class MotherTreeZone extends DefaultArea {
    // активно не во всех mother_tree областях
    @IntValue
    public int hp_regen_bonus; // Бонус к восстановлению hp.
    @EnumValue
    private AffectRace affect_race; // Раса, на которую направлен эффект зоны, либо all-все расы
    @IntValue
    private int entering_message_no; // ID сообщения(из SystemMessage.dat), отображаемого при входе в зону
    @IntValue
    private int leaving_message_no; // ID сообщения(из SystemMessage.dat), отображаемого при выходе из зоны
    // активно не во всех mother_tree областях
    @IntValue
    private int mp_regen_bonus; // Бонус к восстановлению mp.

    public MotherTreeZone() {
        super();
    }

    public MotherTreeZone(DefaultArea defaultSetting) {
        super(defaultSetting);
        affect_race = ((MotherTreeZone) defaultSetting).affect_race;
        entering_message_no = ((MotherTreeZone) defaultSetting).entering_message_no;
        leaving_message_no = ((MotherTreeZone) defaultSetting).leaving_message_no;
        hp_regen_bonus = ((MotherTreeZone) defaultSetting).hp_regen_bonus;
        mp_regen_bonus = ((MotherTreeZone) defaultSetting).mp_regen_bonus;
    }

    public static enum AffectRace {
        human,
        elf,
        darkelf,
        orc,
        dwarf,
        kamael,
        all
    }
}
