package org.jts.dataparser.data.parser;

import org.jts.dataparser.data.common.AbstractDataParser;
import org.jts.dataparser.data.holder.UserBasicActionHolder;

/**
 * Create by Mangol on 20.10.2015.
 */
public class UserBasicActionParser extends AbstractDataParser<UserBasicActionHolder> {
    private static final UserBasicActionParser ourInstance = new UserBasicActionParser();

    private UserBasicActionParser() {
        super(UserBasicActionHolder.getInstance());
    }

    public static UserBasicActionParser getInstance() {
        return ourInstance;
    }

    @Override
    protected String getFileName() {
        return "data/pts_scripts/custom/userbasicaction.txt";
    }
}
