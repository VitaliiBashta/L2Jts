package org.jts.dataparser.data.holder.convertdata;

import org.jts.dataparser.data.annotations.value.StringValue;

/**
 * @author : Camelion
 * @date : 26.08.12 12:46
 */
public class ConvertData {
    @StringValue
    public String input_item; // Что конвртируем
    @StringValue
    public String output_item; // Во что конвертируем
}
