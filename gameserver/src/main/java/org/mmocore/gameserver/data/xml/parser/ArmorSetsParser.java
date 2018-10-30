package org.mmocore.gameserver.data.xml.parser;

import org.jdom2.Element;
import org.mmocore.commons.data.xml.AbstractFileParser;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.data.xml.holder.ArmorSetsHolder;
import org.mmocore.gameserver.model.ArmorSet;

import java.io.File;

public final class ArmorSetsParser extends AbstractFileParser<ArmorSetsHolder> {
    private static final ArmorSetsParser _instance = new ArmorSetsParser();

    private ArmorSetsParser() {
        super(ArmorSetsHolder.getInstance());
    }

    public static ArmorSetsParser getInstance() {
        return _instance;
    }

    @Override
    public File getXMLFile() {
        return new File(ServerConfig.DATAPACK_ROOT, "data/xmlscript/items/armor_sets.xml");
    }

    @Override
    public String getDTDFileName() {
        return "../dtd/armor_sets.dtd";
    }

    @Override
    protected void readData(final ArmorSetsHolder holder, final Element rootElement) throws Exception {
        for (final Element setElement : rootElement.getChildren("set")) {
            String[] chest = null, legs = null, head = null, gloves = null, feet = null, skills = null, shield = null, shield_skills = null, enchant6skills = null;
            int id = Integer.parseInt(setElement.getAttributeValue("id"));

            if (setElement.getAttributeValue("chest") != null) {
                chest = setElement.getAttributeValue("chest").split(";");
            }
            if (setElement.getAttributeValue("legs") != null) {
                legs = setElement.getAttributeValue("legs").split(";");
            }
            if (setElement.getAttributeValue("head") != null) {
                head = setElement.getAttributeValue("head").split(";");
            }
            if (setElement.getAttributeValue("gloves") != null) {
                gloves = setElement.getAttributeValue("gloves").split(";");
            }
            if (setElement.getAttributeValue("feet") != null) {
                feet = setElement.getAttributeValue("feet").split(";");
            }
            if (setElement.getAttributeValue("skills") != null) {
                skills = setElement.getAttributeValue("skills").split(";");
            }
            if (setElement.getAttributeValue("shield") != null) {
                shield = setElement.getAttributeValue("shield").split(";");
            }
            if (setElement.getAttributeValue("shield_skills") != null) {
                shield_skills = setElement.getAttributeValue("shield_skills").split(";");
            }
            if (setElement.getAttributeValue("enchant6skills") != null) {
                enchant6skills = setElement.getAttributeValue("enchant6skills").split(";");
            }

            holder.addArmorSet(new ArmorSet(id, chest, legs, head, gloves, feet, skills, shield, shield_skills, enchant6skills));
        }
    }
}