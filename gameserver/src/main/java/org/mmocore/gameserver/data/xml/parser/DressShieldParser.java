package org.mmocore.gameserver.data.xml.parser;

import org.jdom2.Element;
import org.mmocore.commons.data.xml.AbstractFileParser;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.data.xml.holder.DressShieldHolder;
import org.mmocore.gameserver.model.dress.DressShieldData;

import java.io.File;

/**
 * Created by Hack
 * Date: 17.08.2016 21:07
 */
public class DressShieldParser extends AbstractFileParser<DressShieldHolder> {
    private static final DressShieldParser _instance = new DressShieldParser();

    private DressShieldParser() {
        super(DressShieldHolder.getInstance());
    }

    public static DressShieldParser getInstance() {
        return _instance;
    }

    @Override
    public File getXMLFile() {
        return new File(ServerConfig.DATAPACK_ROOT, "data/xmlscript/dress/shield.xml");
    }

    @Override
    public String getDTDFileName() {
        return "shield.dtd";
    }

    @Override
    protected void readData(DressShieldHolder holder, Element rootElement) {
        for (Element dress : rootElement.getChildren("shield")) {
            String name = null;
            int id, number, itemId;
            long itemCount;
            number = Integer.parseInt(dress.getAttributeValue("number"));
            id = Integer.parseInt(dress.getAttributeValue("id"));
            name = dress.getAttributeValue("name");

            Element price = dress.getChild("price");
            itemId = Integer.parseInt(price.getAttributeValue("id"));
            itemCount = Long.parseLong(price.getAttributeValue("count"));

            holder.addShield(new DressShieldData(number, id, name, itemId, itemCount));
        }
    }
}
