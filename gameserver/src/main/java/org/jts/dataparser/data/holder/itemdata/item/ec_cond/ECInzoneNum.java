package org.jts.dataparser.data.holder.itemdata.item.ec_cond;

import org.jts.dataparser.data.annotations.array.IntArray;
import org.jts.dataparser.data.annotations.class_annotations.ParseSuper;

/**
 * @author : Camelion
 * @date : 28.08.12  12:46
 */
@ParseSuper
public class ECInzoneNum extends DefaultEquipCond {
    @IntArray(withoutName = true)
    public int[] instant_zone_nums;
}
