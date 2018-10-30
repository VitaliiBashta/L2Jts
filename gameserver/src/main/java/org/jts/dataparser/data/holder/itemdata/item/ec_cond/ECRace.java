package org.jts.dataparser.data.holder.itemdata.item.ec_cond;

import org.jts.dataparser.data.annotations.array.IntArray;
import org.jts.dataparser.data.annotations.class_annotations.ParseSuper;

/**
 * @author : Camelion
 * @date : 28.08.12  12:22
 */
@ParseSuper
public class ECRace extends DefaultEquipCond {
    @IntArray(withoutName = true)
    public int[] races;
}
