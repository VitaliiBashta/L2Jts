package org.mmocore.gameserver.data.xml.parser;

import org.jdom2.Element;
import org.mmocore.commons.data.xml.AbstractFileParser;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.data.xml.holder.SoulCrystalHolder;
import org.mmocore.gameserver.templates.SoulCrystal;

import java.io.File;

/**
 * @author VISTALL
 * @date 10:55/08.12.2010
 */
public final class SoulCrystalParser extends AbstractFileParser<SoulCrystalHolder> {
    private static final SoulCrystalParser _instance = new SoulCrystalParser();

    private SoulCrystalParser() {
        super(SoulCrystalHolder.getInstance());
    }

    public static SoulCrystalParser getInstance() {
        return _instance;
    }

    @Override
    public File getXMLFile() {
        return new File(ServerConfig.DATAPACK_ROOT, "data/xmlscript/items/soul_crystals.xml");
    }

    @Override
    public String getDTDFileName() {
        return "../dtd/soul_crystals.dtd";
    }

    @Override
    protected void readData(final SoulCrystalHolder holder, final Element rootElement) throws Exception {
        for (final Element crystalElement : rootElement.getChildren("crystal")) {
            final int itemId = Integer.parseInt(crystalElement.getAttributeValue("item_id"));
            final int level = Integer.parseInt(crystalElement.getAttributeValue("level"));
            final int nextItemId = Integer.parseInt(crystalElement.getAttributeValue("next_item_id"));
            final int cursedNextItemId = Integer.parseInt(crystalElement.getAttributeValue("cursed_next_item_id", "0"));

            holder.addCrystal(new SoulCrystal(itemId, level, nextItemId, cursedNextItemId));
        }
    }
}
