package org.jts.dataparser.data.holder.variationdata;

import org.jts.dataparser.data.annotations.array.StringArray;
import org.jts.dataparser.data.annotations.value.IntValue;
import org.jts.dataparser.data.annotations.value.StringValue;

/**
 * @author : Mangol
 */
public class VariationItemData {
    @StringValue(withoutName = true)
    public String item_group;
    @IntValue(withoutName = true)
    public int id;
    @StringArray
    public String[] item_list = new String[0];
}
