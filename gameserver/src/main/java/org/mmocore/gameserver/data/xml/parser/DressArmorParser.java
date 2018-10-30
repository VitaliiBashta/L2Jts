package org.mmocore.gameserver.data.xml.parser;

import org.jdom2.Element;
import org.mmocore.commons.data.xml.AbstractFileParser;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.data.xml.holder.DressArmorHolder;
import org.mmocore.gameserver.model.dress.DressArmorData;

import java.io.File;

/**
 * Created by Hack
 * Date: 17.08.2016 19:08
 */
public final class DressArmorParser extends AbstractFileParser<DressArmorHolder> {
    private static final DressArmorParser _instance = new DressArmorParser();

    private DressArmorParser() {
        super(DressArmorHolder.getInstance());
    }

    public static DressArmorParser getInstance() {
        return _instance;
    }

    @Override
    public File getXMLFile() {
        return new File(ServerConfig.DATAPACK_ROOT, "data/xmlscript/dress/armor.xml");
    }

    @Override
    public String getDTDFileName() {
        return "armor.dtd";
    }

    @Override
    protected void readData(DressArmorHolder holder, Element rootElement) {
        for (Element dress : rootElement.getChildren("dress")) {
            String name = null;
            int id, chest = 0, legs = 0, gloves = 0, feet = 0, itemId = 0;
            long itemCount = 0L;
            id = Integer.parseInt(dress.getAttributeValue("id"));
            name = dress.getAttributeValue("name");
            String costumeAtt = dress.getAttributeValue("isCostume");
            boolean isCostume = costumeAtt != null && costumeAtt.equalsIgnoreCase("true");

            Element set = dress.getChild("set");

            chest = Integer.parseInt(set.getAttributeValue("chest"));
            legs = Integer.parseInt(set.getAttributeValue("legs"));
            gloves = Integer.parseInt(set.getAttributeValue("gloves"));
            feet = Integer.parseInt(set.getAttributeValue("feet"));

            Element price = dress.getChild("price");
            itemId = Integer.parseInt(price.getAttributeValue("id"));
            itemCount = Long.parseLong(price.getAttributeValue("count"));

            holder.addDress(new DressArmorData(id, name, chest, legs, gloves, feet, itemId, itemCount, isCostume));
        }
    }
}