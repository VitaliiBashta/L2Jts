package org.mmocore.gameserver.data.xml.parser;

import org.jdom2.Element;
import org.mmocore.commons.data.xml.AbstractFileParser;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.data.xml.holder.StaticObjectHolder;
import org.mmocore.gameserver.templates.StaticObjectTemplate;
import org.mmocore.gameserver.templates.StatsSet;

import java.io.File;

/**
 * @author VISTALL
 * @date 22:21/09.03.2011
 */
public final class StaticObjectParser extends AbstractFileParser<StaticObjectHolder> {
    private static final StaticObjectParser _instance = new StaticObjectParser();

    private StaticObjectParser() {
        super(StaticObjectHolder.getInstance());
    }

    public static StaticObjectParser getInstance() {
        return _instance;
    }

    @Override
    public File getXMLFile() {
        return new File(ServerConfig.DATAPACK_ROOT, "data/xmlscript/objects/staticobjects.xml");
    }

    @Override
    public String getDTDFileName() {
        return "../dtd/staticobjects.dtd";
    }

    @Override
    protected void readData(final StaticObjectHolder holder, final Element rootElement) {
        for (final Element staticObjectElement : rootElement.getChildren("staticobject")) {
            final StatsSet set = new StatsSet();
            set.set("uid", staticObjectElement.getAttributeValue("id"));
            set.set("stype", staticObjectElement.getAttributeValue("stype"));
            set.set("path", staticObjectElement.getAttributeValue("path"));
            set.set("map_x", staticObjectElement.getAttributeValue("map_x"));
            set.set("map_y", staticObjectElement.getAttributeValue("map_y"));
            set.set("name", staticObjectElement.getAttributeValue("name"));
            set.set("x", staticObjectElement.getAttributeValue("x"));
            set.set("y", staticObjectElement.getAttributeValue("y"));
            set.set("z", staticObjectElement.getAttributeValue("z"));
            set.set("spawn", staticObjectElement.getAttributeValue("spawn"));

            holder.addTemplate(new StaticObjectTemplate(set));
        }
    }
}
