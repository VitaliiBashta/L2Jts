package org.jts.dataparser.data.parser;

import org.jts.dataparser.data.common.AbstractDataParser;
import org.jts.dataparser.data.holder.RecipeHolder;

/**
 * Create by Mangol on 21.12.2015.
 */
public class RecipeParser extends AbstractDataParser<RecipeHolder> {
    private static final RecipeParser ourInstance = new RecipeParser();

    private RecipeParser() {
        super(RecipeHolder.getInstance());
    }

    public static RecipeParser getInstance() {
        return ourInstance;
    }

    @Override
    protected String getFileName() {
        return "data/pts_scripts/recipe.txt";
    }
}
