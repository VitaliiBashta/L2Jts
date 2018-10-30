package org.mmocore.gameserver.data.xml.parser;

import org.jdom2.Element;
import org.mmocore.commons.data.xml.AbstractFileParser;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.data.xml.holder.DressWeaponHolder;
import org.mmocore.gameserver.model.dress.DressWeaponData;

import java.io.File;

/**
 * Created by Hack
 * Date: 17.08.2016 21:11
 */
public class DressWeaponParser extends AbstractFileParser<DressWeaponHolder> {
    private static final DressWeaponParser _instance = new DressWeaponParser();

    private DressWeaponParser() {
        super(DressWeaponHolder.getInstance());
    }

    public static DressWeaponParser getInstance() {
        return _instance;
    }

    @Override
    public File getXMLFile() {
        return new File(ServerConfig.DATAPACK_ROOT, "data/xmlscript/dress/weapon.xml");
    }

    @Override
    public String getDTDFileName() {
        return "weapon.dtd";
    }

    @Override
    protected void readData(DressWeaponHolder holder, Element rootElement) {
        for (Element dress : rootElement.getChildren("weapon")) {
            String name, type;
            int id, itemId;
            long itemCount;
            id = Integer.parseInt(dress.getAttributeValue("id"));
            name = dress.getAttributeValue("name");
            type = dress.getAttributeValue("type");

            Element price = dress.getChild("price");
            itemId = Integer.parseInt(price.getAttributeValue("id"));
            itemCount = Long.parseLong(price.getAttributeValue("count"));

            holder.addWeapon(new DressWeaponData(id, name, type, itemId, itemCount));
        }
    }

}
