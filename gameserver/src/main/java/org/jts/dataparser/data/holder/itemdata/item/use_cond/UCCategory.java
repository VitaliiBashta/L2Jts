package org.jts.dataparser.data.holder.itemdata.item.use_cond;

import org.jts.dataparser.data.annotations.array.LinkedArray;
import org.jts.dataparser.data.annotations.class_annotations.ParseSuper;

/**
 * @author : Camelion
 * @date : 28.08.12  4:51
 */
@ParseSuper
public class UCCategory extends DefaultUseCond {
    @LinkedArray(withoutName = true)
    public int[] categories;
}
