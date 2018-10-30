package org.mmocore.gameserver.data.xml.parser;

import org.jdom2.Element;
import org.mmocore.commons.data.xml.AbstractFileParser;
import org.mmocore.commons.geometry.CustomPolygon;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.data.xml.holder.PhantomSpawnHolder;
import org.mmocore.gameserver.model.Territory;
import org.mmocore.gameserver.phantoms.ai.PhantomAiType;
import org.mmocore.gameserver.phantoms.template.PhantomSpawnTemplate;
import org.mmocore.gameserver.templates.item.ItemTemplate;

import java.io.File;
import java.util.List;

/**
 * Created by Hack
 * Date: 22.08.2016 3:55
 */
public class PhantomSpawnParser extends AbstractFileParser<PhantomSpawnHolder> {
    private static final PhantomSpawnParser instance = new PhantomSpawnParser();

    private PhantomSpawnParser() {
        super(PhantomSpawnHolder.getInstance());
    }

    public static PhantomSpawnParser getInstance() {
        return instance;
    }

    @Override
    public File getXMLFile() {
        return new File(ServerConfig.DATAPACK_ROOT, "data/xmlscript/phantoms/spawn.xml");
    }

    @Override
    public String getDTDFileName() {
        return "spawn.dtd";
    }

    @Override
    protected void readData(PhantomSpawnHolder holder, Element rootElement) throws Exception {
        for (Element spawn : rootElement.getChildren("spawn")) {
            PhantomSpawnTemplate template = new PhantomSpawnTemplate();
            template.setType(PhantomAiType.valueOf(spawn.getAttributeValue("type")));
            template.setCount(Integer.parseInt(spawn.getAttributeValue("count")));
            template.setGradeMin(ItemTemplate.Grade.valueOf(spawn.getAttributeValue("gradeMin")));
            template.setGradeMax(ItemTemplate.Grade.valueOf(spawn.getAttributeValue("gradeMax")));
            List<Element> coords = spawn.getChild("territory").getChildren("add");
            CustomPolygon poly = new CustomPolygon(coords.size());

            // CustomPolygon can hold only 2d points. Z-coordinates contains only min and max value for whole shape.
            int zmin = Integer.MAX_VALUE;
            int zmax = Integer.MIN_VALUE;
            for (Element add : coords) {
                poly.add(Integer.parseInt(add.getAttributeValue("x")), Integer.parseInt(add.getAttributeValue("y")));
                zmin = Math.min(zmin, Integer.parseInt(add.getAttributeValue("zmin")));
                zmax = Math.max(zmax, Integer.parseInt(add.getAttributeValue("zmax")));
            }
            poly.setZmin(zmin);
            poly.setZmax(zmax);
            template.setTerritory(new Territory().add(poly));
            holder.addSpawn(template);
        }
    }
}
