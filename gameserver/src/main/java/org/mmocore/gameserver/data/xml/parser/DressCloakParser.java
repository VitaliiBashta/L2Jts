package org.mmocore.gameserver.data.xml.parser;

import org.jdom2.Element;
import org.mmocore.commons.data.xml.AbstractFileParser;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.data.xml.holder.DressCloakHolder;
import org.mmocore.gameserver.model.dress.DressCloakData;

import java.io.File;

/**
 * Created by Hack
 * Date: 17.08.2016 20:58
 */
public final class DressCloakParser extends AbstractFileParser<DressCloakHolder> {
    private static final DressCloakParser _instance = new DressCloakParser();

    private DressCloakParser() {
        super(DressCloakHolder.getInstance());
    }

    public static DressCloakParser getInstance() {
        return _instance;
    }

    @Override
    public File getXMLFile() {
        return new File(ServerConfig.DATAPACK_ROOT, "data/xmlscript/dress/cloak.xml");
    }

    @Override
    public String getDTDFileName() {
        return "cloak.dtd";
    }

    @Override
    protected void readData(DressCloakHolder holder, Element rootElement) {
        for (Element dress : rootElement.getChildren("cloak")) {
            String name = null;
            int id, number, itemId;
            long itemCount;
            number = Integer.parseInt(dress.getAttributeValue("number"));
            id = Integer.parseInt(dress.getAttributeValue("id"));
            name = dress.getAttributeValue("name");

            Element price = dress.getChild("price");
            itemId = Integer.parseInt(price.getAttributeValue("id"));
            itemCount = Long.parseLong(price.getAttributeValue("count"));

            holder.addCloak(new DressCloakData(number, id, name, itemId, itemCount));
        }
    }
}