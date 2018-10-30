package org.jts.dataparser.data.holder.areadata.area;

import org.jts.dataparser.data.annotations.array.EnumArray;
import org.jts.dataparser.data.annotations.class_annotations.ParseSuper;
import org.jts.dataparser.data.common.PlayerAction;

/**
 * @author : Camelion
 * @date : 25.08.12  0:31
 */
@ParseSuper
public class PeaceZone extends DefaultArea {
    // активно не во всех peace_zone областях
    @EnumArray
    private PlayerAction[] blocked_actions; // Запрещенные в этой зоне действия

    public PeaceZone() {
    }

    public PeaceZone(DefaultArea defaultSetting) {
        super(defaultSetting);
        blocked_actions = ((PeaceZone) defaultSetting).blocked_actions;
    }
}
