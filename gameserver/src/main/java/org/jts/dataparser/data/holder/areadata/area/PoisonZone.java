package org.jts.dataparser.data.holder.areadata.area;

import org.jts.dataparser.data.annotations.array.LinkedArray;
import org.jts.dataparser.data.annotations.class_annotations.ParseSuper;
import org.jts.dataparser.data.annotations.value.EnumValue;
import org.jts.dataparser.data.annotations.value.IntValue;
import org.jts.dataparser.data.annotations.value.StringValue;
import org.jts.dataparser.data.holder.areadata.SkillActionType;

import static org.jts.dataparser.data.holder.areadata.area.DefaultArea.OnOffZoneParam.off;

/**
 * @author : Camelion
 * @date : 25.08.12  2:36
 */
@ParseSuper
public class PoisonZone extends DefaultArea {
    // Активно не для всех poison зон
    @LinkedArray
    private int[] skill_list; // Список скилов

    // Активно не для всех poison зон
    @EnumValue
    private SkillActionType skill_action_type; // Неизвестно, вероятно обозначение того, как будут использоваться скилы из skill_list

    // Активно не для всех poison зон (но присутствует в параметрах по умолчанию)
    @StringValue
    private String skill_name; // название скила, используемого зоной

    @IntValue
    private int skill_prob; // неизвестно


    // Активно не для всех poison зон
    @EnumValue
    private OnOffZoneParam show_dangerzone = off; // Показывать или нет значок опасной зоны?

    public PoisonZone(DefaultArea defaultSetting) {
        super(defaultSetting);
        skill_list = ((PoisonZone) defaultSetting).skill_list;
        skill_action_type = ((PoisonZone) defaultSetting).skill_action_type;
        skill_name = ((PoisonZone) defaultSetting).skill_name;
        skill_prob = ((PoisonZone) defaultSetting).skill_prob;
    }

    public PoisonZone() {

    }
}
