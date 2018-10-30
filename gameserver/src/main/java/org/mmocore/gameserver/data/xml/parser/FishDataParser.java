package org.mmocore.gameserver.data.xml.parser;

import org.jdom2.Attribute;
import org.jdom2.Element;
import org.mmocore.commons.collections.MultiValueSet;
import org.mmocore.commons.data.xml.AbstractFileParser;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.data.xml.holder.FishDataHolder;
import org.mmocore.gameserver.templates.item.support.FishGroup;
import org.mmocore.gameserver.templates.item.support.FishTemplate;
import org.mmocore.gameserver.templates.item.support.LureTemplate;
import org.mmocore.gameserver.templates.item.support.LureType;

import java.io.File;
import java.util.EnumMap;
import java.util.Map;

/**
 * @author VISTALL
 * @date 8:34/19.07.2011
 */
public class FishDataParser extends AbstractFileParser<FishDataHolder> {
    private static final FishDataParser _instance = new FishDataParser();

    private FishDataParser() {
        super(FishDataHolder.getInstance());
    }

    public static FishDataParser getInstance() {
        return _instance;
    }

    @Override
    public File getXMLFile() {
        return new File(ServerConfig.DATAPACK_ROOT, "data/xmlscript/static/fishdata.xml");
    }

    @Override
    public String getDTDFileName() {
        return "../dtd/fishdata.dtd";
    }

    @Override
    protected void readData(final FishDataHolder holder, final Element rootElement) throws Exception {
        for (final Element element : rootElement.getChildren()) {
            if ("fish".equals(element.getName())) {
                final MultiValueSet<String> map = new MultiValueSet<>();
                for (final Attribute attribute : element.getAttributes()) {
                    map.put(attribute.getName(), attribute.getValue());
                }

                holder.addFish(new FishTemplate(map));
            } else if ("lure".equals(element.getName())) {
                final MultiValueSet<String> map = new MultiValueSet<>();
                for (final Attribute attribute : element.getAttributes()) {
                    map.put(attribute.getName(), attribute.getValue());
                }

                final Map<FishGroup, Integer> chances = new EnumMap<>(FishGroup.class);
                for (final Element chanceElement : element.getChildren()) {
                    chances.put(FishGroup.valueOf(chanceElement.getAttributeValue("type")), Integer.parseInt(chanceElement.getAttributeValue("value")));
                }

                map.put("chances", chances);
                holder.addLure(new LureTemplate(map));
            } else if ("distribution".equals(element.getName())) {
                final int id = Integer.parseInt(element.getAttributeValue("id"));

                for (final Element forLureElement : element.getChildren()) {
                    final LureType lureType = LureType.valueOf(forLureElement.getAttributeValue("type"));
                    final Map<FishGroup, Integer> chances = new EnumMap<>(FishGroup.class);

                    for (final Element chanceElement : forLureElement.getChildren()) {
                        chances.put(FishGroup.valueOf(chanceElement.getAttributeValue("type")), Integer.parseInt(chanceElement.getAttributeValue("value")));
                    }

                    holder.addDistribution(id, lureType, chances);
                }
            }
        }
    }
}
