package org.jts.dataparser.data.holder.enchantoption;

import org.jts.dataparser.data.annotations.ElementArray;
import org.jts.dataparser.data.annotations.array.StringArray;
import org.jts.dataparser.data.annotations.value.IntValue;
import org.jts.dataparser.data.annotations.value.StringValue;

/**
 * @author : Camelion
 * @date : 27.08.12 2:02
 */
public class EnchantOption {
    @StringValue
    public String item; // Название предмета, есть в itemdata.txt
    @ElementArray(start = "option_begin", end = "option_end")
    public Option[] options;

    public static class Option {
        @IntValue
        public int enchant;// Уровень заточки
        @StringArray(name = "option")
        public String[] option_names; // Какие-то параметры. Есть в
        // optiondata.txt
    }
}
