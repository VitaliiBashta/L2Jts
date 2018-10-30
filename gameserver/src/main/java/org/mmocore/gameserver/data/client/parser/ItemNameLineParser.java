package org.mmocore.gameserver.data.client.parser;

import org.apache.commons.lang3.StringUtils;
import org.jdom2.Element;
import org.mmocore.commons.data.xml.AbstractDirParser;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.data.client.holder.ItemNameLineHolder;
import org.mmocore.gameserver.templates.client.ItemNameLine;
import org.mmocore.gameserver.utils.Language;

import java.io.File;

/**
 * @author xblx
 */
public class ItemNameLineParser extends AbstractDirParser<ItemNameLineHolder> {
    private static final ItemNameLineParser INSTANCE = new ItemNameLineParser();

    private ItemNameLineParser() {
        super(ItemNameLineHolder.getInstance());
    }

    public static ItemNameLineParser getInstance() {
        return INSTANCE;
    }

    @Override
    public File getXMLDir() {
        return new File(ServerConfig.DATAPACK_ROOT, "data/client/itemname");
    }

    @Override
    public String getDTDFileName() {
        return "itemname.dtd";
    }

    @Override
    public boolean isIgnored(File f) {
        return false;
    }

    @Override
    protected void readData(final ItemNameLineHolder holder, final Element rootElement) {
        final Element options = rootElement.getChild("options");
        final Language lang = Language.valueOf(options.getAttributeValue("lang"));
        for (final Element dataElement : options.getChildren("data")) {
            final int item_id = Integer.parseInt(dataElement.getAttributeValue("item_id"));
            final String name = dataElement.getAttributeValue("name").equals(StringUtils.EMPTY) ? "NpcNameEmpty" : dataElement.getAttributeValue("name");
            final String add_name = dataElement.getAttributeValue("add_name") == null ? "" : dataElement.getAttributeValue("add_name");
            final String description = dataElement.getAttributeValue("description") == null ? "" : dataElement.getAttributeValue("description");
            final int color = dataElement.getAttributeValue("color") != null ? Integer.parseInt(dataElement.getAttributeValue("color")) : 0;
            final ItemNameLine template = new ItemNameLine(lang, item_id, name, add_name, description, color);
            for (final Element custom : dataElement.getChildren("custom")) {
                final String function = custom.getAttributeValue("function");
                final String nameCustom = custom.getAttributeValue("name") == null ? name : custom.getAttributeValue("name");
                final String addNameCustom = custom.getAttributeValue("add_name") == null ? add_name : custom.getAttributeValue("add_name");
                final String descriptionCustom = custom.getAttributeValue("description") == null ? description : custom.getAttributeValue("description");
                final int colorCustom = custom.getAttributeValue("color") == null ? color : Integer.parseInt(custom.getAttributeValue("color"));
                final ItemNameLine templateCustom = new ItemNameLine(lang, item_id, nameCustom, addNameCustom, descriptionCustom, colorCustom);
                template.addFunction(function, templateCustom);
            }
            holder.put(lang, template);
        }
    }
}
