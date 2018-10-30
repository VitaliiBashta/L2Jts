package org.jts.dataparser.data.holder.categorydata;

import org.jts.dataparser.data.annotations.array.LinkedArray;
import org.jts.dataparser.data.annotations.value.StringValue;

/**
 * @author : Camelion
 * @date : 26.08.12 12:12
 */
public class CategoryDefine {
    @StringValue
    public String name; // Название категории, совпадает с category_pch.txt
    @LinkedArray
    public int[] category;// Список классов, попадающих в категорию ( совпадает
    // с manual_pch.txt)
}
