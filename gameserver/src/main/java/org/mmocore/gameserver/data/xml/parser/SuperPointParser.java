package org.mmocore.gameserver.data.xml.parser;

import org.jdom2.Element;
import org.mmocore.commons.data.xml.AbstractFileParser;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.data.xml.holder.SuperPointHolder;
import org.mmocore.gameserver.network.lineage.components.ChatType;
import org.mmocore.gameserver.object.components.npc.superPoint.SuperPoinCoordinate;
import org.mmocore.gameserver.object.components.npc.superPoint.SuperPoint;
import org.mmocore.gameserver.object.components.npc.superPoint.SuperPointType;

import java.io.File;

/**
 * @author: Felixx Company: J Develop Station
 */
public final class SuperPointParser extends AbstractFileParser<SuperPointHolder> {
    private static SuperPointParser instance;

    public SuperPointParser() {
        super(SuperPointHolder.getInstance());
    }

    public static SuperPointParser getInstance() {
        if (instance == null)
            instance = new SuperPointParser();

        return instance;
    }

    @Override
    public File getXMLFile() {
        return new File(ServerConfig.DATAPACK_ROOT, "data/xmlscript/static/superpointinfo.xml");
    }

    @Override
    public String getDTDFileName() {
        return "../dtd/superpointinfo.dtd";
    }

    @Override
    protected void readData(final SuperPointHolder holder, final Element rootElement) {
        for (final Element superPointElement : rootElement.getChildren("superpoint")) {
            superPointElement.sortAttributes((o1, o2) -> 0);
            final String pointName = superPointElement.getAttributeValue("name");
            final boolean isRunning = Boolean.parseBoolean(superPointElement.getAttributeValue("running"));
            final SuperPoint point = new SuperPoint();
            point.setRunning(isRunning);
            point.setType(SuperPointType.valueOf(superPointElement.getAttributeValue("moveType", "NONE")));

            for (final Element pointElement : superPointElement.getChildren("point")) {
                final int x = Integer.parseInt(pointElement.getAttributeValue("x"));
                final int y = Integer.parseInt(pointElement.getAttributeValue("y"));
                final int z = Integer.parseInt(pointElement.getAttributeValue("z"));

                final SuperPoinCoordinate coords = new SuperPoinCoordinate(x, y, z);

                final Element mgsElement = pointElement.getChild("msg");
                if (mgsElement != null) {
                    coords.setMsgId(Integer.parseInt(mgsElement.getAttributeValue("id", "0")));
                    coords.setMsgChatType(ChatType.valueOf(mgsElement.getAttributeValue("chat", "ALL")));
                    coords.setMsgRadius(Integer.parseInt(mgsElement.getAttributeValue("radius", "1500")));
                }

                final Element delayElement = pointElement.getChild("delay");
                if (delayElement != null)
                    coords.setDelayInSec(Integer.parseInt(delayElement.getAttributeValue("sec", "0")));

                final Element socialElement = pointElement.getChild("social");
                if (socialElement != null)
                    coords.setSocialId(Integer.parseInt(socialElement.getAttributeValue("id", "0")));

                point.addMoveCoordinats(coords);
            }

            holder.addSuperPoints(pointName, point);
        }
    }
}