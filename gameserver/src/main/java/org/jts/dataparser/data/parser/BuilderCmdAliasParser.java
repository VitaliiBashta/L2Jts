package org.jts.dataparser.data.parser;

import org.jts.dataparser.data.common.AbstractDataParser;
import org.jts.dataparser.data.holder.BuilderCmdAliasHolder;

/**
 * @author : Camelion
 * @date : 25.08.12 22:47
 */
public class BuilderCmdAliasParser extends AbstractDataParser<BuilderCmdAliasHolder> {
    private static BuilderCmdAliasParser ourInstance = new BuilderCmdAliasParser();

    private BuilderCmdAliasParser() {
        super(BuilderCmdAliasHolder.getInstance());
    }

    public static BuilderCmdAliasParser getInstance() {
        return ourInstance;
    }

    @Override
    protected String getFileName() {
        return "data/pts_scripts/buildercmdalias.txt";
    }
}