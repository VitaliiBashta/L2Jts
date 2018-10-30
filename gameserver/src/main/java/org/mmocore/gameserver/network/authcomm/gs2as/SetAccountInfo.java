package org.mmocore.gameserver.network.authcomm.gs2as;

import org.mmocore.gameserver.network.authcomm.SendablePacket;

/**
 * @author VISTALL
 * @date 21:07/25.03.2011
 */
public class SetAccountInfo extends SendablePacket {
    private final String _account;
    private final int _size;
    private final int[] _deleteChars;

    public SetAccountInfo(final String account, final int size, final int[] deleteChars) {
        _account = account;
        _size = size;
        _deleteChars = deleteChars;
    }

    @Override
    protected void writeImpl() {
        writeC(0x05);
        writeS(_account);
        writeC(_size);
        writeD(_deleteChars.length);
        for (final int i : _deleteChars) {
            writeD(i);
        }
    }
}
