package org.mmocore.gameserver.network.lineage.serverpackets;

import ru.akumu.smartguard.core.SmartCore;

/**
 * @author Akumu
 * @date 26.10.13
 */
public class KeyPacket extends L2GameServerPacket {
    private static final String _S__01_KEYPACKET = "[S] 00 KeyPacket (SmartGuard)";
    private byte[] _key;

    public KeyPacket(byte[] key) {
        _key = key;
        SmartCore.cryptInternalKey(_key);
    }

    @Override
    public void writeImpl() {
        writeC(0x00);
        writeC(0x01);

        for (int i = 0; i < 8; i++)
            writeC(_key[i]);

        writeD(0x01);
        writeC(0x00);
    }

    @Override
    public String getType() {
        return _S__01_KEYPACKET;
    }
}
