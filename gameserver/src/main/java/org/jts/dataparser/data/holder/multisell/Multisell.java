package org.jts.dataparser.data.holder.multisell;

import org.jts.dataparser.data.annotations.array.ObjectArray;
import org.jts.dataparser.data.annotations.array.StringArray;
import org.jts.dataparser.data.annotations.value.IntValue;
import org.jts.dataparser.data.annotations.value.StringValue;
import org.jts.dataparser.data.common.ItemName_Count;

/**
 * @author : Camelion
 * @date : 30.08.12 14:24
 */
public class Multisell {
    @StringValue(withoutName = true)
    public String multisell_name;
    @IntValue(withoutName = true)
    public int multisell_id;
    @StringArray(name = "required_npc")
    public String[] required_npcs = new String[0];
    @IntValue
    public int is_dutyfree = 0;
    @IntValue
    public int is_show_all = 1;
    @IntValue
    public int keep_enchanted = 0;
    @IntValue
    public int show_variation_item = 0;
    @ObjectArray
    public SellInfo[] selllist;

    public static class SellInfo {
        @ObjectArray(withoutName = true)
        public ItemName_Count[] product_infos;
        @ObjectArray(withoutName = true)
        public ItemName_Count[] ingredient_infos;
        @IntValue(withoutName = true)
        public int count;
    }
}
