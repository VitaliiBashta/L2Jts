package org.mmocore.gameserver.network.lineage.serverpackets;

import java.nio.ByteBuffer;

/**
 * @author Akumu
 * @date 27.03.2016 13:40
 */
public class RawPacket extends L2GameServerPacket {
    byte[] data;

    public RawPacket(ByteBuffer byteBuffer) {
        data = new byte[byteBuffer.position() + 1];
        byteBuffer.position(0);
        byteBuffer.get(data, 0, data.length);
    }

    @Override
    protected void writeImpl() {
        writeB(data);
    }
}
