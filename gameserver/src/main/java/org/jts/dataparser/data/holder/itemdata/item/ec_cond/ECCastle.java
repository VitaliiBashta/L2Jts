package org.jts.dataparser.data.holder.itemdata.item.ec_cond;

import org.jts.dataparser.data.annotations.class_annotations.ParseSuper;
import org.jts.dataparser.data.annotations.value.IntValue;

/**
 * @author : Camelion
 * @date : 28.08.12  12:27
 */
@ParseSuper
public class ECCastle extends DefaultEquipCond {
    @IntValue(withoutName = true)
    public int have_castle;
}
