package org.mmocore.gameserver.network.lineage.serverpackets;


import org.mmocore.gameserver.model.entity.events.objects.BoatPoint;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.ClanAirShip;

import java.util.Collections;
import java.util.List;


public class ExAirShipTeleportList extends GameServerPacket {
    private final int fuel;
    private List<BoatPoint> airports = Collections.emptyList();

    public ExAirShipTeleportList(final ClanAirShip ship) {
        fuel = ship.getCurrentFuel();
        airports = ship.getDock().getTeleportList();
    }

    @Override
    protected void writeData() {
        writeD(fuel); // current fuel
        writeD(airports.size());

        for (int i = 0; i < airports.size(); i++) {
            final BoatPoint point = airports.get(i);
            writeD(i - 1); // AirportID
            writeD(point.getFuel()); // need fuel
            writeD(point.x); // Airport x
            writeD(point.y); // Airport y
            writeD(point.z); // Airport z
        }
    }
}