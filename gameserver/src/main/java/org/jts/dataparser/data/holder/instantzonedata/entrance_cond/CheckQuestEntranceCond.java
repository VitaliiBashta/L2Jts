package org.jts.dataparser.data.holder.instantzonedata.entrance_cond;

import org.jts.dataparser.data.annotations.class_annotations.ParseSuper;
import org.jts.dataparser.data.annotations.value.IntValue;

/**
 * @author : Camelion
 * @date : 27.08.12  15:19
 */
@ParseSuper
public class CheckQuestEntranceCond extends DefaultEntranceCond {
    @IntValue(withoutName = true)
    public int quest_id;
}
