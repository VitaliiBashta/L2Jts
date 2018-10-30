package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.cache.CrestCache;
import org.mmocore.gameserver.network.lineage.serverpackets.AllianceCrest;

public class RequestAllyCrest extends L2GameClientPacket {
    // format: cd

    private int _crestId;

    @Override
    protected void readImpl() {
        _crestId = readD();
    }

    @Override
    protected void runImpl() {
        if (_crestId == 0) {
            return;
        }
        final byte[] data = CrestCache.getInstance().getCrestData(CrestCache.CrestType.ALLY, _crestId);
        if (data != null)
            sendPacket(new AllianceCrest(_crestId, data));
    }
}