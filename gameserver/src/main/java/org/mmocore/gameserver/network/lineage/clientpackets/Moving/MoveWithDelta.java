package org.mmocore.gameserver.network.lineage.clientpackets.Moving;

import org.mmocore.gameserver.network.lineage.clientpackets.L2GameClientPacket;

/**
 * Format: (c) ddd
 * d: dx
 * d: dy
 * d: dz
 */
public class MoveWithDelta extends L2GameClientPacket {
    @SuppressWarnings("unused")
    private int _dx, _dy, _dz;

    @Override
    protected void readImpl() {
        _dx = readD();
        _dy = readD();
        _dz = readD();
    }

    @Override
    protected void runImpl() {
        // TODO this
    }
}