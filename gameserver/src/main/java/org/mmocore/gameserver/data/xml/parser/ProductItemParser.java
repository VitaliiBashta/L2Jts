package org.mmocore.gameserver.data.xml.parser;

import org.jdom2.Element;
import org.mmocore.commons.data.xml.AbstractFileParser;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.configuration.config.ServicesConfig;
import org.mmocore.gameserver.data.xml.holder.ProductItemHolder;
import org.mmocore.gameserver.database.dao.impl.CharacterProductDAO;
import org.mmocore.gameserver.object.components.items.premium.ProductItemComponent;
import org.mmocore.gameserver.templates.item.ProductItemTemplate;
import org.mmocore.gameserver.templates.item.ProductItemTemplate.EventFlag;
import org.mmocore.gameserver.utils.TimeUtils;

import java.io.File;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * @author VISTALL
 * @author KilRoy
 * @author Java-man
 */
public final class ProductItemParser extends AbstractFileParser<ProductItemHolder> {
    private static final ProductItemParser INSTANCE = new ProductItemParser();

    protected ProductItemParser() {
        super(ProductItemHolder.getInstance());
    }

    public static ProductItemParser getInstance() {
        return INSTANCE;
    }

    private static ZonedDateTime getMillisecondsFromString(final String datetime) {
        try {
            return TimeUtils.DATE_TIME_FORMATTER.withZone(ZoneId.systemDefault()).parse(datetime, ZonedDateTime::from);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public File getXMLFile() {
        return new File(ServerConfig.DATAPACK_ROOT, "data/premiumShop/" + ServicesConfig.SERVICES_PREMIUMSHOP_PREMIUMSHOP_FILENAME);
    }

    @Override
    public String getDTDFileName() {
        return "product.dtd";
    }

    @Override
    protected void readData(final ProductItemHolder holder, final Element rootElement) throws Exception {
        for (final Element productElement : rootElement.getChildren("product")) {
            Boolean on_sale = productElement.getAttributeValue("on_sale") != null && Boolean.parseBoolean(productElement.getAttributeValue("on_sale"));
            if (!on_sale)
                continue;
            final ZonedDateTime end_sale_date = productElement.getAttributeValue("end_sale_date") != null
                    ? getMillisecondsFromString(productElement.getAttributeValue("end_sale_date"))
                    : ProductItemTemplate.DEFAULT_END_SALE_DATE;
            if (end_sale_date.isBefore(ZonedDateTime.now()))
                continue;
            final int id = Integer.parseInt(productElement.getAttributeValue("id"));
            final int category = Integer.parseInt(productElement.getAttributeValue("category"));
            final int price = Integer.parseInt(productElement.getAttributeValue("price"));
            final boolean is_event = productElement.getAttributeValue("is_event") != null ? Boolean.parseBoolean(productElement.getAttributeValue("is_event")) : ProductItemTemplate.DEFAULT_IS_EVENT;
            final int max_stock = productElement.getAttributeValue("max_stock") != null ? Integer.parseInt(productElement.getAttributeValue("max_stock")) : ProductItemTemplate.DEFAULT_MAX_STOCK;
            final ZonedDateTime start_sale_date = productElement.getAttributeValue("start_sale_date") != null
                    ? getMillisecondsFromString(productElement.getAttributeValue("start_sale_date"))
                    : ProductItemTemplate.DEFAULT_START_SALE_DATE;
            final int day_of_week = productElement.getAttributeValue("day_of_week") != null ? Integer.parseInt(productElement.getAttributeValue("day_of_week")) : Byte.MAX_VALUE;
            final EventFlag event = is_event ? EventFlag.EVENT : EventFlag.NONE;

            final ProductItemTemplate template = new ProductItemTemplate(id, category, price, day_of_week, start_sale_date, end_sale_date, max_stock, event);

            for (final Element itemList : productElement.getChildren("item")) {
                final ProductItemComponent comp = new ProductItemComponent(Integer.parseInt(itemList.getAttributeValue("id")), Integer.parseInt(itemList.getAttributeValue("count")));
                template.getComponents().add(comp);
            }
            holder.addTemplate(template);
        }
        CharacterProductDAO.getInstance().loadBoughtProducts();
    }
}