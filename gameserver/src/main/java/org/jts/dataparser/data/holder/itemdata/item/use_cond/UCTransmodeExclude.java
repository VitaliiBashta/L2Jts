package org.jts.dataparser.data.holder.itemdata.item.use_cond;

import org.jts.dataparser.data.annotations.array.EnumArray;
import org.jts.dataparser.data.annotations.class_annotations.ParseSuper;

/**
 * @author : Camelion
 * @date : 28.08.12  4:46
 */
@ParseSuper
public class UCTransmodeExclude extends DefaultUseCond {
    @EnumArray(withoutName = true)
    public Mode[] modes;


    public static enum Mode {
        tt_flying, tt_pure_stat, tt_non_transform
    }
}
