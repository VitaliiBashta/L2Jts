package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.gameserver.network.lineage.serverpackets.ExUISetting;
import org.mmocore.gameserver.object.Player;

/**
 * format: (ch)db
 */
public class RequestSaveKeyMapping extends L2GameClientPacket {
    private byte[] _data;

    @Override
    protected void readImpl() {
        final int length = readD();
        if (length > _buf.remaining() || length > Short.MAX_VALUE || length < 0) {
            _data = null;
            return;
        }
        _data = new byte[length];
        readB(_data);
    }

    @Override
    protected void runImpl() {
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null || _data == null) {
            return;
        }
        activeChar.setKeyBindings(_data);
        activeChar.sendPacket(new ExUISetting(activeChar));
    }
}