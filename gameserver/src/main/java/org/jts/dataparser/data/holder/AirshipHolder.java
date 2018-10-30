package org.jts.dataparser.data.holder;

import org.jts.dataparser.data.annotations.Element;
import org.jts.dataparser.data.annotations.ElementArray;
import org.jts.dataparser.data.holder.airship.AirPort;
import org.jts.dataparser.data.holder.airship.AirShip;
import org.jts.dataparser.data.holder.airship.AirshipArea;
import org.mmocore.commons.data.AbstractHolder;

import java.util.List;

/**
 * @author : Camelion
 * @date : 24.08.12 12:23
 * <p/>
 * Воздушные корабли и воздушные аэропорты
 */
public class AirshipHolder extends AbstractHolder {
    private static final AirshipHolder ourInstance = new AirshipHolder();
    @Element(start = "airport_begin", end = "airport_end")
    private List<AirPort> airPorts;
    @Element(start = "airship_begin", end = "airship_end")
    private List<AirShip> airShips;
    @ElementArray(start = "airship_area_begin", end = "airship_area_end")
    private AirshipArea[] airshipAreas;

    private AirshipHolder() {
    }

    public static AirshipHolder getInstance() {
        return ourInstance;
    }

    @Override
    public int size() {
        return airPorts.size() + airShips.size() + airshipAreas.length;
    }

    public List<AirPort> getAirPorts() {
        return airPorts;
    }

    public List<AirShip> getAirShips() {
        return airShips;
    }

    public AirshipArea[] getAirshipAreas() {
        return airshipAreas;
    }

    @Override
    public void clear() {
        airPorts.clear();
        airShips.clear();
        airshipAreas = null;
    }
}