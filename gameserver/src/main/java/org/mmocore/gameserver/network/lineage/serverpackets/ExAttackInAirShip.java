package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class ExAttackInAirShip extends GameServerPacket {
    /*
     * заготовка!!!
     * Format: dddcddddh[ddc]
     * ExAttackInAirShip AttackerID:%d DefenderID:%d Damage:%d bMiss:%d bCritical:%d AirShipID:%d
     */

    @Override
    protected final void writeData() {
    }
}