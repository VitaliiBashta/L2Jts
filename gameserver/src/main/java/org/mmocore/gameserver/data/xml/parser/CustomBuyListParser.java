package org.mmocore.gameserver.data.xml.parser;

import org.jdom2.Element;
import org.mmocore.commons.converter.Converter;
import org.mmocore.commons.data.xml.AbstractFileParser;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.data.xml.holder.CustomBuyListHolder;
import org.mmocore.gameserver.data.xml.holder.ItemTemplateHolder;
import org.mmocore.gameserver.model.buylist.BuyList;
import org.mmocore.gameserver.model.buylist.Product;
import org.mmocore.gameserver.templates.item.ItemTemplate;

import java.io.File;

/**
 * @author Mangol
 * @TODO: Временный для гм шопа админки... запилить генерацию байлиста на лету.
 */
public final class CustomBuyListParser extends AbstractFileParser<CustomBuyListHolder> {
    private static final CustomBuyListParser _instance = new CustomBuyListParser();

    protected CustomBuyListParser() {
        super(CustomBuyListHolder.getInstance());
    }

    public static CustomBuyListParser getInstance() {
        return _instance;
    }

    @Override
    public File getXMLFile() {
        return new File(ServerConfig.DATAPACK_ROOT, "data/xmlscript/custom_buylist/custom_buylist.xml");
    }

    @Override
    public String getDTDFileName() {
        return "custom_buylist.dtd";
    }

    @Override
    protected void readData(final CustomBuyListHolder holder, final Element rootElement) {
        for (final Element npcElement : rootElement.getChildren()) {
            final int npc = Converter.convert(Integer.class, npcElement.getAttributeValue("npc"));
            final int shopId = Converter.convert(Integer.class, npcElement.getAttributeValue("shop"));
            final BuyList tl = new BuyList(shopId, npc);
            for (final Element subListElement : npcElement.getChildren()) {
                final int itemId = Converter.convert(Integer.class, subListElement.getAttributeValue("id"));
                final ItemTemplate temp = ItemTemplateHolder.getInstance().getTemplate(itemId);
                final Product product = new Product();
                product.setPrice(0);
                product.setItem(temp);
                tl.addProduct(product);
            }
            holder.addToBuyList(shopId, tl);
        }
    }
}
