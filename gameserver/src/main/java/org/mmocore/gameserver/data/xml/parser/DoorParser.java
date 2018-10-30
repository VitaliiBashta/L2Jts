package org.mmocore.gameserver.data.xml.parser;

import org.jdom2.Element;
import org.mmocore.commons.data.xml.AbstractDirParser;
import org.mmocore.commons.geometry.CustomPolygon;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.data.xml.holder.DoorHolder;
import org.mmocore.gameserver.templates.DoorTemplate;
import org.mmocore.gameserver.templates.StatsSet;
import org.mmocore.gameserver.templates.item.WeaponTemplate;
import org.mmocore.gameserver.utils.Location;

import java.io.File;

public final class DoorParser extends AbstractDirParser<DoorHolder> {
    private DoorParser() {
        super(DoorHolder.getInstance());
    }

    public static DoorParser getInstance() {
        return LazyHolder.INSTANCE;
    }

    @Override
    public File getXMLDir() {
        return new File(ServerConfig.DATAPACK_ROOT, "data/doors/");
    }

    @Override
    public boolean isIgnored(final File f) {
        return false;
    }

    @Override
    public String getDTDFileName() {
        return "doors.dtd";
    }

    private StatsSet initBaseStats() {
        final StatsSet baseDat = new StatsSet();
        baseDat.set("level", 0);
        baseDat.set("baseSTR", 0);
        baseDat.set("baseCON", 0);
        baseDat.set("baseDEX", 0);
        baseDat.set("baseINT", 0);
        baseDat.set("baseWIT", 0);
        baseDat.set("baseMEN", 0);
        baseDat.set("baseShldDef", 0);
        baseDat.set("baseShldRate", 0);
        baseDat.set("baseAccCombat", 38);
        baseDat.set("baseEvasRate", 38);
        baseDat.set("baseCritRate", 38);
        baseDat.set("baseAtkRange", 0);
        baseDat.set("baseMpMax", 0);
        baseDat.set("baseCpMax", 0);
        baseDat.set("basePAtk", 0);
        baseDat.set("baseMAtk", 0);
        baseDat.set("basePAtkSpd", 0);
        baseDat.set("baseMAtkSpd", 0);
        baseDat.set("baseWalkSpd", 0);
        baseDat.set("baseRunSpd", 0);
        baseDat.set("baseHpReg", 0);
        baseDat.set("baseCpReg", 0);
        baseDat.set("baseMpReg", 0);
        baseDat.set("baseAtkType", WeaponTemplate.WeaponType.SWORD);

        return baseDat;
    }

    @Override
    protected void readData(final DoorHolder holder, final Element rootElement) throws Exception {
        for (final Element doorElement : rootElement.getChildren("door")) {
            final StatsSet doorSet = initBaseStats();
            StatsSet aiParams = null;

            doorSet.set("door_type", doorElement.getAttributeValue("type"));

            final Element posElement = doorElement.getChild("pos");
            final Location doorPos;
            final int x = Integer.parseInt(posElement.getAttributeValue("x"));
            final int y = Integer.parseInt(posElement.getAttributeValue("y"));
            final int z = Integer.parseInt(posElement.getAttributeValue("z"));
            doorSet.set("pos", doorPos = new Location(x, y, z));

            final CustomPolygon shape = new CustomPolygon(4);
            int minz = 0, maxz = 0;

            final Element shapeElement = doorElement.getChild("shape");
            minz = Integer.parseInt(shapeElement.getAttributeValue("minz"));
            maxz = Integer.parseInt(shapeElement.getAttributeValue("maxz"));
            shape.add(Integer.parseInt(shapeElement.getAttributeValue("ax")), Integer.parseInt(shapeElement.getAttributeValue("ay")));
            shape.add(Integer.parseInt(shapeElement.getAttributeValue("bx")), Integer.parseInt(shapeElement.getAttributeValue("by")));
            shape.add(Integer.parseInt(shapeElement.getAttributeValue("cx")), Integer.parseInt(shapeElement.getAttributeValue("cy")));
            shape.add(Integer.parseInt(shapeElement.getAttributeValue("dx")), Integer.parseInt(shapeElement.getAttributeValue("dy")));
            shape.setZmin(minz);
            shape.setZmax(maxz);
            doorSet.set("shape", shape);

            doorPos.setZ(minz + 32); //фактическая координата двери в мире

            for (final Element nextElement : doorElement.getChildren()) {
                if ("set".equals(nextElement.getName())) {
                    doorSet.set(nextElement.getAttributeValue("name"), nextElement.getAttributeValue("value"));
                } else if ("ai_params".equals(nextElement.getName())) {
                    if (aiParams == null) {
                        aiParams = new StatsSet();
                        doorSet.set("ai_params", aiParams);
                    }

                    for (final Element aiParamElement : nextElement.getChildren("set")) {
                        aiParams.set(aiParamElement.getAttributeValue("name"), aiParamElement.getAttributeValue("value"));
                    }
                }
            }

            doorSet.set("uid", doorElement.getAttributeValue("id"));
            doorSet.set("name", doorElement.getAttributeValue("name"));
            doorSet.set("baseHpMax", doorElement.getAttributeValue("hp"));
            doorSet.set("basePDef", doorElement.getAttributeValue("pdef"));
            doorSet.set("baseMDef", doorElement.getAttributeValue("mdef"));

            doorSet.set("collision_height", maxz - minz & 0xfff0);
            doorSet.set("collision_radius", Math.max(50, Math.min(doorPos.x - shape.getXmin(), doorPos.y - shape.getYmin())));

            final DoorTemplate template = new DoorTemplate(doorSet);
            holder.addTemplate(template);
        }
    }

    private static class LazyHolder {
        private static final DoorParser INSTANCE = new DoorParser();
    }
}
