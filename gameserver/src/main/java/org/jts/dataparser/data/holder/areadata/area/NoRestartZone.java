package org.jts.dataparser.data.holder.areadata.area;

import org.jts.dataparser.data.annotations.class_annotations.ParseSuper;
import org.jts.dataparser.data.annotations.value.IntValue;
import org.jts.dataparser.data.annotations.value.TimeValue;

/**
 * @author : Camelion
 * @date : 25.08.12  1:25
 * <p/>
 * Зоны, в которых запрещён рестарт
 */
@ParseSuper
public class NoRestartZone extends DefaultArea {
    @IntValue
    private int restart_time; // Неизвестно

    // Активно не во всех no_restart областях
    @TimeValue
    private long restart_allowed_time; // Неизвестно

    public NoRestartZone(DefaultArea defaultSetting) {
        super(defaultSetting);
        restart_time = ((NoRestartZone) defaultSetting).restart_time;
    }

    public NoRestartZone() {

    }
}
