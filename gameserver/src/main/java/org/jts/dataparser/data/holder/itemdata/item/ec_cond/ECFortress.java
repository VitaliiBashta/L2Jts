package org.jts.dataparser.data.holder.itemdata.item.ec_cond;

import org.jts.dataparser.data.annotations.class_annotations.ParseSuper;
import org.jts.dataparser.data.annotations.value.IntValue;

/**
 * @author : Camelion
 * @date : 28.08.12  12:36
 */
@ParseSuper
public class ECFortress extends DefaultEquipCond {
    @IntValue(withoutName = true)
    public int fortress_id;
}
