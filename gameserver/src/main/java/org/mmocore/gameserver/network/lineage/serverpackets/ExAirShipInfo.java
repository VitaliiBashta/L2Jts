package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.AirShip;
import org.mmocore.gameserver.object.ClanAirShip;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.Location;

public class ExAirShipInfo extends GameServerPacket {
    private final int objId;
    private final int speed1;
    private final int speed2;
    private final Location loc;
    private int fuel;
    private int maxFuel;
    private int driverObjId;
    private int controlKey;

    public ExAirShipInfo(final AirShip ship) {
        objId = ship.getObjectId();
        loc = ship.getLoc();
        speed1 = ship.getRunSpeed();
        speed2 = ship.getRotationSpeed();
        if (ship.isClanAirShip()) {
            fuel = ((ClanAirShip) ship).getCurrentFuel();
            maxFuel = ((ClanAirShip) ship).getMaxFuel();
            final Player driver = ((ClanAirShip) ship).getDriver();
            driverObjId = driver == null ? 0 : driver.getObjectId();
            controlKey = ((ClanAirShip) ship).getControlKey().getObjectId();
        }
    }

    @Override
    protected final void writeData() {
        writeD(objId);
        writeD(loc.x);
        writeD(loc.y);
        writeD(loc.z);
        writeD(loc.h);
        writeD(driverObjId); // object id of player who control ship
        writeD(speed1);
        writeD(speed2);
        writeD(controlKey);

        if (controlKey != 0) {
            writeD(0x16e); // Controller X
            writeD(0x00); // Controller Y
            writeD(0x6b); // Controller Z
            writeD(0x15c); // Captain X
            writeD(0x00); // Captain Y
            writeD(0x69); // Captain Z
        } else {
            writeD(0x00);
            writeD(0x00);
            writeD(0x00);
            writeD(0x00);
            writeD(0x00);
            writeD(0x00);
        }

        writeD(fuel); // current fuel
        writeD(maxFuel); // max fuel
    }
}