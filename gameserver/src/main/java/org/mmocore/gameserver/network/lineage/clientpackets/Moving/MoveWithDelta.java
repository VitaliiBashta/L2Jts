package org.mmocore.gameserver.network.lineage.clientpackets.Moving;

import org.mmocore.gameserver.network.lineage.clientpackets.L2GameClientPacket;

/**
 * Format: (c) ddd
 * d: dx
 * d: dy
 * d: dz
 */
public class MoveWithDelta extends L2GameClientPacket {

    @Override
    protected void readImpl() {
        int _dx = readD();
        int _dy = readD();
        int _dz = readD();
    }

    @Override
    protected void runImpl() {
        // TODO this
    }
}