package org.jts.dataparser.data.holder.itemdata.item.ec_cond;

import org.jts.dataparser.data.annotations.class_annotations.ParseSuper;
import org.jts.dataparser.data.annotations.value.IntValue;

/**
 * @author : Camelion
 * @date : 28.08.12  12:31
 */
@ParseSuper
public class ECNobless extends DefaultEquipCond {
    @IntValue(withoutName = true)
    public int is_nobless;
}
