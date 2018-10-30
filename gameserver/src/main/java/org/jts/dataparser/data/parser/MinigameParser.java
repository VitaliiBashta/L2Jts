package org.jts.dataparser.data.parser;

import org.jts.dataparser.data.common.AbstractDataParser;
import org.jts.dataparser.data.holder.MinigameHolder;

/**
 * @author : Camelion
 * @date : 30.08.12 13:32
 */
public class MinigameParser extends AbstractDataParser<MinigameHolder> {
    private static final MinigameParser ourInstance = new MinigameParser();

    private MinigameParser() {
        super(MinigameHolder.getInstance());
    }

    public static MinigameParser getInstance() {
        return ourInstance;
    }

    @Override
    protected String getFileName() {
        return "data/pts_scripts/minigame.txt";
    }
}