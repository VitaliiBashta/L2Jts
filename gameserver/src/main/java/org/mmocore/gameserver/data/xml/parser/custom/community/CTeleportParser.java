package org.mmocore.gameserver.data.xml.parser.custom.community;

import org.jdom2.Element;
import org.mmocore.commons.data.xml.AbstractFileParser;
import org.mmocore.gameserver.data.xml.holder.custom.community.CTeleportHolder;
import org.mmocore.gameserver.object.components.player.community.TeleportPoint;
import org.mmocore.gameserver.utils.Location;

import java.io.File;

/**
 * Create by Mangol on 12.12.2015.
 */
public class CTeleportParser extends AbstractFileParser<CTeleportHolder> {
    private static final CTeleportParser INSTANCE = new CTeleportParser();

    protected CTeleportParser() {
        super(CTeleportHolder.getInstance());
    }

    public static CTeleportParser getInstance() {
        return INSTANCE;
    }

    @Override
    public File getXMLFile() {
        return new File("data/xmlscript/community/teleport/teleport.xml");
    }

    @Override
    public String getDTDFileName() {
        return "teleport.dtd";
    }

    @Override
    protected void readData(final CTeleportHolder holder, final Element rootElement) throws Exception {
        for (final Element element : rootElement.getChildren()) {
            final int id = Integer.parseInt(element.getAttributeValue("id"));
            final String name = element.getAttributeValue("name");
            final int minLevel = Integer.parseInt(element.getAttributeValue("minLevel"));
            final int maxLevel = Integer.parseInt(element.getAttributeValue("maxLevel"));
            final boolean pk = Boolean.parseBoolean(element.getAttributeValue("pk"));
            final boolean isPremiumPoint = Boolean.parseBoolean(element.getAttributeValue("isPremiumPoint"));
            final boolean confirm = Boolean.parseBoolean(element.getAttributeValue("confirm"));
            final Element cost = element.getChild("cost");
            final int itemId = Integer.parseInt(cost.getAttributeValue("itemId"));
            final int count = Integer.parseInt(cost.getAttributeValue("count"));
            final int premiumItemId = Integer.parseInt(cost.getAttributeValue("premiumItemId"));
            final int premiumCount = Integer.parseInt(cost.getAttributeValue("premiumCount"));
            final Element coordinates = element.getChild("coordinates");
            final int x = Integer.parseInt(coordinates.getAttributeValue("x"));
            final int y = Integer.parseInt(coordinates.getAttributeValue("y"));
            final int z = Integer.parseInt(coordinates.getAttributeValue("z"));
            final TeleportPoint teleportPoint = new TeleportPoint(id, name, itemId, count, minLevel, maxLevel, pk, isPremiumPoint, premiumItemId, premiumCount, new Location(x, y, z), confirm);
            holder.addTeleportPoint(teleportPoint);
        }
    }
}
