package org.jts.dataparser.data.holder.doordata;

import org.jts.dataparser.data.annotations.array.IntArray;
import org.jts.dataparser.data.annotations.class_annotations.ParseSuper;
import org.jts.dataparser.data.annotations.value.IntValue;
import org.jts.dataparser.data.annotations.value.StringValue;

/**
 * @author : Camelion
 * @date : 26.08.12 22:31
 */
@ParseSuper
public class SignBoard extends DefaultSignBoard {
    @IntValue
    public int editor_id;
    @StringValue
    public String html; // Есть не у всех
    @StringValue
    public String texture_name;// Есть не у всех
    @IntArray
    public int[] map_pos; // Есть не у всех
}
