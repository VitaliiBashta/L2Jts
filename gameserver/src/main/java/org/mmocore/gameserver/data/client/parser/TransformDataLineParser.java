package org.mmocore.gameserver.data.client.parser;

import org.jdom2.Element;
import org.jts.dataparser.data.holder.setting.common.PlayerSex;
import org.mmocore.commons.data.xml.AbstractFileParser;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.data.client.holder.TransformDataLineHolder;
import org.mmocore.gameserver.templates.client.TransformDataLine;

import java.io.File;

/**
 * Create by Mangol on 19.10.2015.
 */
public class TransformDataLineParser extends AbstractFileParser<TransformDataLineHolder> {
    private static final TransformDataLineParser INSTANCE = new TransformDataLineParser();

    private TransformDataLineParser() {
        super(TransformDataLineHolder.getInstance());
    }

    public static TransformDataLineParser getInstance() {
        return INSTANCE;
    }

    @Override
    public File getXMLFile() {
        return new File(ServerConfig.DATAPACK_ROOT, "data/client/transformdata/transformdata.xml");
    }

    @Override
    public String getDTDFileName() {
        return "transformdata.dtd";
    }

    @Override
    protected void readData(final TransformDataLineHolder holder, final Element rootElement) {
        for (final Element dataElement : rootElement.getChildren()) {
            final int id = Integer.parseInt(dataElement.getAttributeValue("id"));
            for (final Element setting : dataElement.getChildren()) {
                final PlayerSex sex = PlayerSex.valueOf(setting.getAttributeValue("sex"));
                final int npc_id = setting.getAttributeValue("npc_id") == null ? 0 : Integer.parseInt(setting.getAttributeValue("npc_id"));
                final int item_id = setting.getAttributeValue("item_id") == null ? 0 : Integer.parseInt(setting.getAttributeValue("item_id"));
                holder.put(new TransformDataLine(sex, id, npc_id, item_id));
            }
        }
    }
}
