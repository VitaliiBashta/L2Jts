package org.mmocore.gameserver.templates;

import org.mmocore.gameserver.model.entity.events.objects.BoatPoint;
import org.mmocore.gameserver.network.lineage.components.SceneMovie;
import org.mmocore.gameserver.utils.Location;

import java.util.Collections;
import java.util.List;

/**
 * @author VISTALL
 */
public class AirshipDock {
    private final int _id;
    private List<BoatPoint> _teleportList = Collections.emptyList();
    private List<AirshipPlatform> _platformList = Collections.emptyList();
    public AirshipDock(final int id, final List<BoatPoint> teleport, final List<AirshipPlatform> platformList) {
        _id = id;
        _teleportList = teleport;
        _platformList = platformList;
    }

    public int getId() {
        return _id;
    }

    public List<BoatPoint> getTeleportList() {
        return _teleportList;
    }

    public AirshipPlatform getPlatform(final int id) {
        return _platformList.get(id);
    }

    public static class AirshipPlatform {
        private final SceneMovie _oustMovie;
        private final Location _oustLoc;
        private final Location _spawnLoc;
        private List<BoatPoint> _arrivalPoints = Collections.emptyList();
        private List<BoatPoint> _departPoints = Collections.emptyList();

        public AirshipPlatform(final SceneMovie movie, final Location oustLoc, final Location spawnLoc, final List<BoatPoint> arrival, final List<BoatPoint> depart) {
            _oustMovie = movie;
            _oustLoc = oustLoc;
            _spawnLoc = spawnLoc;
            _arrivalPoints = arrival;
            _departPoints = depart;
        }

        public SceneMovie getOustMovie() {
            return _oustMovie;
        }

        public Location getOustLoc() {
            return _oustLoc;
        }

        public Location getSpawnLoc() {
            return _spawnLoc;
        }

        public List<BoatPoint> getArrivalPoints() {
            return _arrivalPoints;
        }

        public List<BoatPoint> getDepartPoints() {
            return _departPoints;
        }
    }
}
