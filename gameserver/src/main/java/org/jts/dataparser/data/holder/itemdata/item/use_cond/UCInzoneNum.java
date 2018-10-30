package org.jts.dataparser.data.holder.itemdata.item.use_cond;

import org.jts.dataparser.data.annotations.array.IntArray;
import org.jts.dataparser.data.annotations.class_annotations.ParseSuper;

/**
 * @author : Camelion
 * @date : 28.08.12  4:57
 */
@ParseSuper
public class UCInzoneNum extends DefaultUseCond {
    @IntArray(withoutName = true)
    public int[] zone_ids;
}
