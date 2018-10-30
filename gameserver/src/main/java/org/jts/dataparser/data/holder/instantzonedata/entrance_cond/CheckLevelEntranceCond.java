package org.jts.dataparser.data.holder.instantzonedata.entrance_cond;

import org.jts.dataparser.data.annotations.class_annotations.ParseSuper;
import org.jts.dataparser.data.annotations.value.IntValue;

/**
 * @author : Camelion
 * @date : 27.08.12  15:20
 */
@ParseSuper
public class CheckLevelEntranceCond extends DefaultEntranceCond {
    @IntValue(withoutName = true)
    public int min_level; // минимальный уровень
    @IntValue(withoutName = true)
    public int max_level; // максимальный уровень
}
