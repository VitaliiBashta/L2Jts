package org.mmocore.gameserver.geoengine;

import org.mmocore.commons.geometry.Shape;

public interface GeoCollision {
    public Shape getShape();

    public byte[][] getGeoAround();

    public void setGeoAround(byte[][] geo);

    public boolean isConcrete();
}