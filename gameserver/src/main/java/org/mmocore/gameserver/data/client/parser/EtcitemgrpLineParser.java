package org.mmocore.gameserver.data.client.parser;

import org.jdom2.Element;
import org.mmocore.commons.data.xml.AbstractFileParser;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.data.client.holder.EtcitemgrpLineHolder;
import org.mmocore.gameserver.templates.StatsSet;
import org.mmocore.gameserver.templates.client.EtcitemgrpLine;

import java.io.File;

/**
 * Create by Mangol on 20.12.2015.
 */
public class EtcitemgrpLineParser extends AbstractFileParser<EtcitemgrpLineHolder> {
    private static final EtcitemgrpLineParser INSTANCE = new EtcitemgrpLineParser();

    protected EtcitemgrpLineParser() {
        super(EtcitemgrpLineHolder.getInstance());
    }

    public static EtcitemgrpLineParser getInstance() {
        return INSTANCE;
    }

    @Override
    public File getXMLFile() {
        return new File(ServerConfig.DATAPACK_ROOT, "data/client/etcitemgrp/etcitemgrp.xml");
    }

    @Override
    public String getDTDFileName() {
        return "etcitemgrp.dtd";
    }

    @Override
    protected void readData(final EtcitemgrpLineHolder holder, final Element rootElement) throws Exception {
        for (final Element element : rootElement.getChildren()) {
            final int id = Integer.parseInt(element.getAttributeValue("id"));
            final StatsSet statsSet = new StatsSet();
            for (final Element set : element.getChildren("set")) {
                statsSet.set(set.getAttributeValue("name"), set.getAttributeValue("value"));
            }
            final EtcitemgrpLine etcitemgrpLine = new EtcitemgrpLine(id, statsSet);
            holder.addEtcitemgrp(etcitemgrpLine);
        }
    }
}

