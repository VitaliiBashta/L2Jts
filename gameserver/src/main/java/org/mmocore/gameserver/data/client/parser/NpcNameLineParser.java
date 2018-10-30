package org.mmocore.gameserver.data.client.parser;

import org.apache.commons.lang3.StringUtils;
import org.jdom2.Element;
import org.mmocore.commons.data.xml.AbstractDirParser;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.data.client.holder.NpcNameLineHolder;
import org.mmocore.gameserver.templates.client.NpcNameLine;
import org.mmocore.gameserver.utils.Language;

import java.io.File;

public class NpcNameLineParser extends AbstractDirParser<NpcNameLineHolder> {
    private static final NpcNameLineParser INSTANCE = new NpcNameLineParser();

    private NpcNameLineParser() {
        super(NpcNameLineHolder.getInstance());
    }

    public static NpcNameLineParser getInstance() {
        return INSTANCE;
    }

    @Override
    public File getXMLDir() {
        return new File(ServerConfig.DATAPACK_ROOT, "data/client/npcname");
    }

    @Override
    public boolean isIgnored(File f) {
        return false;
    }

    @Override
    public String getDTDFileName() {
        return "npcname.dtd";
    }

    @Override
    protected void readData(final NpcNameLineHolder holder, final Element rootElement) throws Exception {
        final Element options = rootElement.getChild("options");
        final Language lang = Language.valueOf(options.getAttributeValue("lang"));
        for (final Element dataElement : options.getChildren("data")) {
            final int npcId = Integer.parseInt(dataElement.getAttributeValue("npc_id"));
            final String name = dataElement.getAttributeValue("name").equals(StringUtils.EMPTY) ? "NpcNameEmpty" : dataElement.getAttributeValue("name");
            String description = "";
            if (dataElement.getAttributeValue("description") != null) {
                description = dataElement.getAttributeValue("description");
            }
            holder.put(lang, new NpcNameLine(npcId, name, description));
        }
    }
}
