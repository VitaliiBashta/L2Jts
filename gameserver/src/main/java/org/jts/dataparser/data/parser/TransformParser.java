package org.jts.dataparser.data.parser;

import org.jts.dataparser.data.common.AbstractDataParser;
import org.jts.dataparser.data.holder.TransformHolder;

/**
 * @author : Mangol
 */
public class TransformParser extends AbstractDataParser<TransformHolder> {
    private static TransformParser ourInstance = new TransformParser();

    private TransformParser() {
        super(TransformHolder.getInstance());
    }

    public static TransformParser getInstance() {
        return ourInstance;
    }

    @Override
    protected String getFileName() {
        return "data/pts_scripts/transform.txt";
    }
}
