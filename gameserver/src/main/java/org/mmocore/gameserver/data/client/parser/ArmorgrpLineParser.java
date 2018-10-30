package org.mmocore.gameserver.data.client.parser;

import org.jdom2.Element;
import org.mmocore.commons.data.xml.AbstractFileParser;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.data.client.holder.ArmorgrpLineHolder;
import org.mmocore.gameserver.templates.StatsSet;
import org.mmocore.gameserver.templates.client.ArmorgrpLine;

import java.io.File;

/**
 * Create by Mangol on 20.12.2015.
 */
public class ArmorgrpLineParser extends AbstractFileParser<ArmorgrpLineHolder> {
    private static final ArmorgrpLineParser INSTANCE = new ArmorgrpLineParser();

    protected ArmorgrpLineParser() {
        super(ArmorgrpLineHolder.getInstance());
    }

    public static ArmorgrpLineParser getInstance() {
        return INSTANCE;
    }

    @Override
    public File getXMLFile() {
        return new File(ServerConfig.DATAPACK_ROOT, "data/client/armorgrp/armorgrp.xml");
    }

    @Override
    public String getDTDFileName() {
        return "armorgrp.dtd";
    }

    @Override
    protected void readData(final ArmorgrpLineHolder holder, final Element rootElement) throws Exception {
        for (final Element element : rootElement.getChildren()) {
            final int id = Integer.parseInt(element.getAttributeValue("id"));
            final StatsSet statsSet = new StatsSet();
            for (final Element set : element.getChildren("set")) {
                statsSet.set(set.getAttributeValue("name"), set.getAttributeValue("value"));
            }
            final ArmorgrpLine armorgrpLine = new ArmorgrpLine(id, statsSet);
            holder.addArmorgpr(armorgrpLine);
        }
    }
}
