package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.cache.CrestCache;
import org.mmocore.gameserver.network.lineage.serverpackets.ExPledgeCrestLarge;
import org.mmocore.gameserver.object.Player;

public class RequestPledgeCrestLarge extends L2GameClientPacket {
    // format: chd
    private int _crestId;

    @Override
    protected void readImpl() {
        _crestId = readD();
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }
        if (_crestId == 0) {
            return;
        }
        final byte[] data = CrestCache.getInstance().getCrestData(CrestCache.CrestType.PLEDGE_LARGE, _crestId);
        if (data != null) {
            sendPacket(new ExPledgeCrestLarge(_crestId, data));
        }
    }
}