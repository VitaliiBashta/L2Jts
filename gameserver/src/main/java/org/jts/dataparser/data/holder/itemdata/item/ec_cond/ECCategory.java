package org.jts.dataparser.data.holder.itemdata.item.ec_cond;

import org.jts.dataparser.data.annotations.array.LinkedArray;
import org.jts.dataparser.data.annotations.class_annotations.ParseSuper;

/**
 * @author : Camelion
 * @date : 28.08.12  12:24
 */
@ParseSuper
public class ECCategory extends DefaultEquipCond {
    @LinkedArray(withoutName = true)
    public int[] categories;
}
