package org.mmocore.gameserver.data.xml.parser;

import org.jdom2.Element;
import org.mmocore.commons.data.xml.AbstractFileParser;
import org.mmocore.commons.geometry.CustomPolygon;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.manager.MapRegionManager;
import org.mmocore.gameserver.model.Territory;
import org.mmocore.gameserver.templates.mapregion.DomainArea;

import java.io.File;

public class DomainParser extends AbstractFileParser<MapRegionManager> {
    private static final DomainParser _instance = new DomainParser();

    protected DomainParser() {
        super(MapRegionManager.getInstance());
    }

    public static DomainParser getInstance() {
        return _instance;
    }

    @Override
    public File getXMLFile() {
        return new File(ServerConfig.DATAPACK_ROOT, "data/mapregion/domains.xml");
    }

    @Override
    public String getDTDFileName() {
        return "domains.dtd";
    }

    @Override
    protected void readData(final MapRegionManager holder, final Element rootElement) throws Exception {
        for (final Element domainElement : rootElement.getChildren()) {
            final int id = Integer.parseInt(domainElement.getAttributeValue("id"));
            final String name = domainElement.getAttributeValue("name");

            Territory territory = null;
            for (final Element polygonElement : domainElement.getChildren("polygon")) {
                final CustomPolygon shape = ZoneParser.parsePolygon(polygonElement);

                if (!shape.validate()) {
                    error("DomainParser: invalid territory data : " + shape + '!');
                }

                if (territory == null) {
                    territory = new Territory();
                }

                territory.add(shape);
            }

            if (territory == null) {
                throw new RuntimeException("DomainParser: empty territory for " + name + " domain id:" + id + '!');
            }

            holder.addRegionData(new DomainArea(id, territory));
        }
    }
}
