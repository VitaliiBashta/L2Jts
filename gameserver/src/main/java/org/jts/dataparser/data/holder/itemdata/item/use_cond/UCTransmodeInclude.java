package org.jts.dataparser.data.holder.itemdata.item.use_cond;

import org.jts.dataparser.data.annotations.array.EnumArray;
import org.jts.dataparser.data.annotations.class_annotations.ParseSuper;

/**
 * @author : Camelion
 * @date : 28.08.12  4:59
 */
@ParseSuper
public class UCTransmodeInclude extends DefaultUseCond {
    @EnumArray(withoutName = true)
    public UCTransmodeExclude.Mode[] modes;
}
