package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.cache.CrestCache;
import org.mmocore.gameserver.model.pledge.Alliance;
import org.mmocore.gameserver.object.Player;

public class RequestSetAllyCrest extends L2GameClientPacket {
    private byte[] _data;

    @Override
    protected void readImpl() {
        int _length = readD();
        if (_length == CrestCache.ALLY_CREST_SIZE && _length == _buf.remaining()) {
            _data = new byte[_length];
            readB(_data);
        }
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }

        final Alliance ally = activeChar.getAlliance();
        if (ally != null && activeChar.isAllyLeader()) {
            int crestId = 0;

            if (_data != null) {
                crestId = CrestCache.getInstance().saveCrest(CrestCache.CrestType.ALLY, ally.getAllyId(), _data);
            } else if (ally.hasAllyCrest()) {
                CrestCache.getInstance().removeCrest(CrestCache.CrestType.ALLY, ally.getAllyId());
            }

            ally.setAllyCrestId(crestId);
            ally.broadcastAllyStatus();
        }
    }
}