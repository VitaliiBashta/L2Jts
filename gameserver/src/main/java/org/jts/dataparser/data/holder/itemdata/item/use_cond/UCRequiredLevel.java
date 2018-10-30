package org.jts.dataparser.data.holder.itemdata.item.use_cond;

import org.jts.dataparser.data.annotations.class_annotations.ParseSuper;
import org.jts.dataparser.data.annotations.value.IntValue;

/**
 * @author : Camelion
 * @date : 28.08.12  4:54
 */
@ParseSuper
public class UCRequiredLevel extends DefaultUseCond {
    @IntValue(withoutName = true)
    public int level;
}
