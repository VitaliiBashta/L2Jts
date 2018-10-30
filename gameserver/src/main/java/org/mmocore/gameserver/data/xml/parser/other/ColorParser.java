package org.mmocore.gameserver.data.xml.parser.other;

import org.jdom2.Element;
import org.mmocore.commons.data.xml.AbstractDirParser;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.data.xml.holder.other.ColorHolder;
import org.mmocore.gameserver.templates.other.ColorTemplate;

import java.io.File;

/**
 * @author Mangol
 * @since 19.02.2016
 */
public class ColorParser extends AbstractDirParser<ColorHolder> {
    private static final ColorParser _instance = new ColorParser();

    private ColorParser() {
        super(ColorHolder.getInstance());
    }

    public static ColorParser getInstance() {
        return _instance;
    }

    @Override
    public File getXMLDir() {
        return new File(ServerConfig.DATAPACK_ROOT, "data/xmlscript/community/service/color/");
    }

    @Override
    public boolean isIgnored(File f) {
        return false;
    }

    @Override
    public String getDTDFileName() {
        return "color.dtd";
    }

    @Override
    protected void readData(final ColorHolder holder, final Element rootElement) {
        rootElement.getChildren().stream().forEach(serviceElement -> {
            final String service = serviceElement.getAttributeValue("name");
            serviceElement.getChildren().stream().forEach(element -> {
                final String color = element.getAttributeValue("rgb");
                final String nameRu = element.getAttributeValue("nameRu");
                final String nameEn = element.getAttributeValue("nameEn");
                holder.addColorTemplate(service, new ColorTemplate(color, nameEn, nameRu));
            });
        });
    }
}
