package org.mmocore.gameserver.data.client.parser;

import org.jdom2.Element;
import org.mmocore.commons.data.xml.AbstractFileParser;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.data.client.holder.WeapongrpLineHolder;
import org.mmocore.gameserver.templates.StatsSet;
import org.mmocore.gameserver.templates.client.WeapongrpLine;

import java.io.File;

/**
 * Create by Mangol on 20.12.2015.
 */
public class WeapongrpLineParser extends AbstractFileParser<WeapongrpLineHolder> {
    private static final WeapongrpLineParser INSTANCE = new WeapongrpLineParser();

    protected WeapongrpLineParser() {
        super(WeapongrpLineHolder.getInstance());
    }

    public static WeapongrpLineParser getInstance() {
        return INSTANCE;
    }

    @Override
    public File getXMLFile() {
        return new File(ServerConfig.DATAPACK_ROOT, "data/client/weapongrp/weapongrp.xml");
    }

    @Override
    public String getDTDFileName() {
        return "weapongrp.dtd";
    }

    @Override
    protected void readData(final WeapongrpLineHolder holder, final Element rootElement) throws Exception {
        for (final Element element : rootElement.getChildren()) {
            final int id = Integer.parseInt(element.getAttributeValue("id"));
            final StatsSet statsSet = new StatsSet();
            for (final Element set : element.getChildren("set")) {
                statsSet.set(set.getAttributeValue("name"), set.getAttributeValue("value"));
            }
            final WeapongrpLine weapongrpLine = new WeapongrpLine(id, statsSet);
            holder.addWeapongpr(weapongrpLine);
        }
    }
}
