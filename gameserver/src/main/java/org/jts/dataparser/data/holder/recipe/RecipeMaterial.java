package org.jts.dataparser.data.holder.recipe;

import org.jts.dataparser.data.annotations.value.IntValue;
import org.jts.dataparser.data.annotations.value.StringValue;
import org.jts.dataparser.data.pch.LinkerFactory;

/**
 * Create by Mangol on 21.12.2015.
 */
public class RecipeMaterial {
    private static final LinkerFactory linkerFactory = LinkerFactory.getInstance();
    @StringValue(withoutName = true)
    private String nameMaterial;
    @IntValue(withoutName = true)
    private int count;

    public int getIdMaterial() {
        return linkerFactory.findClearValue(nameMaterial);
    }

    public int getCount() {
        return count;
    }
}
