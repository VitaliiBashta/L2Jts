package org.mmocore.gameserver.geoengine;

import org.mmocore.commons.geometry.Shape;

public interface GeoCollision {
    Shape getShape();

    byte[][] getGeoAround();

    void setGeoAround(byte[][] geo);

    boolean isConcrete();
}