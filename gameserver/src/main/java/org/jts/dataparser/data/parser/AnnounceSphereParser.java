package org.jts.dataparser.data.parser;

import org.jts.dataparser.data.common.AbstractDataParser;
import org.jts.dataparser.data.holder.AnnounceSphereHolder;

/**
 * @author : Camelion
 * @date : 24.08.12 21:26
 */
public class AnnounceSphereParser extends AbstractDataParser<AnnounceSphereHolder> {
    private static AnnounceSphereParser ourInstance = new AnnounceSphereParser();

    private AnnounceSphereParser() {
        super(AnnounceSphereHolder.getInstance());
    }

    public static AnnounceSphereParser getInstance() {
        return ourInstance;
    }

    @Override
    protected String getFileName() {
        return "data/pts_scripts/announce_sphere.txt";
    }
}