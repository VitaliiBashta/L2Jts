package org.mmocore.gameserver.data.xml.parser;

import org.jdom2.Element;
import org.mmocore.commons.data.xml.AbstractFileParser;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.data.xml.holder.AirshipDockHolder;
import org.mmocore.gameserver.model.entity.events.objects.BoatPoint;
import org.mmocore.gameserver.network.lineage.components.SceneMovie;
import org.mmocore.gameserver.templates.AirshipDock;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mmocore.gameserver.templates.AirshipDock.AirshipPlatform;

/**
 * Author: VISTALL
 * Date:  10:50/14.12.2010
 */
public final class AirshipDockParser extends AbstractFileParser<AirshipDockHolder> {
    private static final AirshipDockParser _instance = new AirshipDockParser();

    private AirshipDockParser() {
        super(AirshipDockHolder.getInstance());
    }

    public static AirshipDockParser getInstance() {
        return _instance;
    }

    @Override
    public File getXMLFile() {
        return new File(ServerConfig.DATAPACK_ROOT, "data/xmlscript/static/airship_docks.xml");
    }

    @Override
    public String getDTDFileName() {
        return "../dtd/airship_docks.dtd";
    }

    @Override
    protected void readData(final AirshipDockHolder holder, final Element rootElement) throws Exception {
        for (final Element dockElement : rootElement.getChildren()) {
            final int id = Integer.parseInt(dockElement.getAttributeValue("id"));

            final List<BoatPoint> teleportList = parsePoints(dockElement.getChild("teleportlist"));
            for (final BoatPoint point : teleportList) {
                point.setTeleport(true);
                point.setSpeed1(-1);
                point.setSpeed2(-1);
            }

            final List<AirshipPlatform> platformList = new ArrayList<>(2);
            for (final Element platforms : dockElement.getChildren("platform")) {
                final SceneMovie movie = SceneMovie.valueOf(platforms.getAttributeValue("movie"));
                final BoatPoint oustLoc = BoatPoint.parse(platforms.getChild("oust"));
                final BoatPoint spawnLoc = BoatPoint.parse(platforms.getChild("spawn"));
                final List<BoatPoint> arrivalList = parsePoints(platforms.getChild("arrival"));
                final List<BoatPoint> departList = parsePoints(platforms.getChild("depart"));

                platformList.add(new AirshipPlatform(movie, oustLoc, spawnLoc, arrivalList, departList));
            }

            holder.addDock(new AirshipDock(id, teleportList, platformList));
        }
    }

    private List<BoatPoint> parsePoints(final Element listElement) {
        if (listElement == null) {
            return Collections.emptyList();
        }

        final List<BoatPoint> list = new ArrayList<>(5);
        for (final Element point : listElement.getChildren()) {
            list.add(BoatPoint.parse(point));
        }

        return list;
    }
}
